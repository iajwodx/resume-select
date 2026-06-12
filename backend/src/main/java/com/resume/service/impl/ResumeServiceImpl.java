package com.resume.service.impl;

import com.resume.dto.DeepSeekResultDTO;
import com.resume.dto.KeywordMatchResultDTO;
import com.resume.dto.ResumeFilterDTO;
import com.resume.entity.Resume;
import com.resume.entity.UserResumeFavorite;
import com.resume.mapper.ResumeMapper;
import com.resume.mapper.UserResumeFavoriteMapper;
import com.resume.service.DeepSeekService;
import com.resume.service.PdfParseService;
import com.resume.service.ResumeService;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Service;
import org.springframework.web.multipart.MultipartFile;

import java.time.LocalDateTime;
import java.util.*;
import java.util.concurrent.CompletableFuture;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.stream.Collectors;

/**
 * Implementation of ResumeService.
 */
@Service
public class ResumeServiceImpl implements ResumeService {

    private static final Logger log = LoggerFactory.getLogger(ResumeServiceImpl.class);

    private final PdfParseService pdfParseService;
    private final DeepSeekService deepSeekService;
    private final ResumeMapper resumeMapper;
    private final UserResumeFavoriteMapper favoriteMapper;

    /** Thread pool for concurrent DeepSeek API calls during keyword matching */
    private final ExecutorService matchExecutor = Executors.newFixedThreadPool(5);

    public ResumeServiceImpl(PdfParseService pdfParseService,
                             DeepSeekService deepSeekService,
                             ResumeMapper resumeMapper,
                             UserResumeFavoriteMapper favoriteMapper) {
        this.pdfParseService = pdfParseService;
        this.deepSeekService = deepSeekService;
        this.resumeMapper = resumeMapper;
        this.favoriteMapper = favoriteMapper;
    }

    @Override
    public Resume uploadResume(MultipartFile file) throws Exception {
        String pdfText = pdfParseService.extractText(file.getInputStream());
        if (pdfText == null || pdfText.isBlank()) {
            throw new RuntimeException("无法从PDF中提取文本内容");
        }

        DeepSeekResultDTO aiResult = deepSeekService.analyzeResume(pdfText);

        Resume resume = convertToEntity(aiResult);
        resume.setUpdateTime(LocalDateTime.now());

        if (resume.getName() != null && resume.getContact() != null) {
            List<Resume> existing = resumeMapper.selectByNameAndContact(
                    resume.getName(), resume.getContact());
            if (!existing.isEmpty()) {
                Resume merged = mergeResume(existing.get(0), resume);
                resumeMapper.updateById(merged);
                log.info("Merged resume for: {}", merged.getName());
                return resumeMapper.selectById(merged.getId());
            }
        }

        resumeMapper.insert(resume);
        log.info("Inserted new resume for: {}", resume.getName());
        return resume;
    }

    @Override
    public Map<String, Object> listResumes(ResumeFilterDTO filter, Long userId) {
        int page = filter.getPage() != null ? filter.getPage() : 1;
        int size = filter.getSize() != null ? filter.getSize() : 10;

        String keyword = filter.getKeyword();
        boolean hasKeyword = keyword != null && !keyword.isBlank();

        // Get distinct fitted positions for the current user (merged into one response)
        List<String> fittedPositions = userId != null
                ? favoriteMapper.selectDistinctPositions(userId)
                : Collections.emptyList();

        if (hasKeyword) {
            List<Map<String, Object>> allResults = resumeMapper.selectWithFilterNoPaging(
                    userId,
                    filter.getLocations(),
                    filter.getMinWorkYears(),
                    filter.getEducations(),
                    filter.getSalaryMin(),
                    filter.getSalaryMax(),
                    filter.getJobStatus(),
                    filter.getIsFavorite(),
                    filter.getFittedPosition()
            );

            int total = allResults.size();
            if (total == 0) {
                Map<String, Object> result = new HashMap<>();
                result.put("list", Collections.emptyList());
                result.put("total", 0);
                result.put("page", page);
                result.put("size", size);
                result.put("fittedPositions", fittedPositions);
                return result;
            }

            // Build Resume objects from map results for AI matching
            List<Resume> allResumes = allResults.stream()
                    .map(this::mapToResume)
                    .collect(Collectors.toList());

            List<Map<String, Object>> enrichedResults = computeMatchScores(keyword, allResumes);

            // Merge isFavorite and fittedPosition back
            for (int i = 0; i < enrichedResults.size(); i++) {
                Map<String, Object> srcRow = allResults.get(i);
                enrichedResults.get(i).put("isFavorite", srcRow.get("is_favorite"));
                enrichedResults.get(i).put("fittedPosition", srcRow.get("fitted_position"));
            }

            String sortBy = filter.getSortBy();
            if ("matchScore".equals(sortBy)) {
                enrichedResults.sort((a, b) -> {
                    Integer scoreA = (Integer) a.get("matchScore");
                    Integer scoreB = (Integer) b.get("matchScore");
                    if (scoreA == null) scoreA = 0;
                    if (scoreB == null) scoreB = 0;
                    return scoreB.compareTo(scoreA);
                });
            } else {
                enrichedResults.sort((a, b) -> {
                    Resume ra = (Resume) a.get("resume");
                    Resume rb = (Resume) b.get("resume");
                    if (ra.getUpdateTime() == null && rb.getUpdateTime() == null) return 0;
                    if (ra.getUpdateTime() == null) return 1;
                    if (rb.getUpdateTime() == null) return -1;
                    return rb.getUpdateTime().compareTo(ra.getUpdateTime());
                });
            }

            int fromIndex = (page - 1) * size;
            int toIndex = Math.min(fromIndex + size, enrichedResults.size());
            List<Map<String, Object>> pageData;
            if (fromIndex >= enrichedResults.size()) {
                pageData = Collections.emptyList();
            } else {
                pageData = enrichedResults.subList(fromIndex, toIndex);
            }

            List<Map<String, Object>> listData = pageData.stream().map(item -> {
                Resume resume = (Resume) item.get("resume");
                Map<String, Object> resumeMap = new LinkedHashMap<>();
                resumeMap.put("id", resume.getId());
                resumeMap.put("name", resume.getName());
                resumeMap.put("contact", resume.getContact());
                resumeMap.put("expectedLocations", resume.getExpectedLocations());
                resumeMap.put("workYears", resume.getWorkYears());
                resumeMap.put("education", resume.getEducation());
                resumeMap.put("salaryMin", resume.getSalaryMin());
                resumeMap.put("salaryMax", resume.getSalaryMax());
                resumeMap.put("skills", resume.getSkills());
                resumeMap.put("projectExperience", resume.getProjectExperience());
                resumeMap.put("jobStatus", resume.getJobStatus());
                resumeMap.put("isFavorite", item.get("isFavorite"));
                resumeMap.put("fittedPosition", item.get("fittedPosition"));
                resumeMap.put("updateTime", resume.getUpdateTime());
                resumeMap.put("createTime", resume.getCreateTime());
                resumeMap.put("modifyTime", resume.getModifyTime());
                resumeMap.put("matchScore", item.get("matchScore"));
                resumeMap.put("matchedTexts", item.get("matchedTexts"));
                return resumeMap;
            }).collect(Collectors.toList());

            Map<String, Object> result = new HashMap<>();
            result.put("list", listData);
            result.put("total", total);
            result.put("page", page);
            result.put("size", size);
            result.put("fittedPositions", fittedPositions);
            return result;

        } else {
            int offset = (page - 1) * size;

            List<Map<String, Object>> list = resumeMapper.selectWithFilter(
                    userId,
                    filter.getLocations(),
                    filter.getMinWorkYears(),
                    filter.getEducations(),
                    filter.getSalaryMin(),
                    filter.getSalaryMax(),
                    filter.getJobStatus(),
                    filter.getIsFavorite(),
                    filter.getFittedPosition(),
                    offset,
                    size
            );

            int total = resumeMapper.countWithFilter(
                    userId,
                    filter.getLocations(),
                    filter.getMinWorkYears(),
                    filter.getEducations(),
                    filter.getSalaryMin(),
                    filter.getSalaryMax(),
                    filter.getJobStatus(),
                    filter.getIsFavorite(),
                    filter.getFittedPosition()
            );

            // Convert column names from snake_case to camelCase for frontend
            List<Map<String, Object>> listData = list.stream()
                    .map(this::convertRowKeys)
                    .collect(Collectors.toList());

            Map<String, Object> result = new HashMap<>();
            result.put("list", listData);
            result.put("total", total);
            result.put("page", page);
            result.put("size", size);
            result.put("fittedPositions", fittedPositions);
            return result;
        }
    }

    @Override
    public Map<String, Object> getResume(Long id, Long userId) {
        Resume resume = resumeMapper.selectById(id);
        if (resume == null) {
            return null;
        }

        Map<String, Object> result = new LinkedHashMap<>();
        result.put("id", resume.getId());
        result.put("name", resume.getName());
        result.put("contact", resume.getContact());
        result.put("expectedLocations", resume.getExpectedLocations());
        result.put("workYears", resume.getWorkYears());
        result.put("education", resume.getEducation());
        result.put("salaryMin", resume.getSalaryMin());
        result.put("salaryMax", resume.getSalaryMax());
        result.put("skills", resume.getSkills());
        result.put("projectExperience", resume.getProjectExperience());
        result.put("jobStatus", resume.getJobStatus());
        result.put("updateTime", resume.getUpdateTime());
        result.put("createTime", resume.getCreateTime());
        result.put("modifyTime", resume.getModifyTime());

        // Look up favorite info for this user
        if (userId != null) {
            UserResumeFavorite fav = favoriteMapper.selectByUserAndResume(userId, id);
            result.put("isFavorite", fav != null);
            result.put("fittedPosition", fav != null ? fav.getFittedPosition() : null);
        } else {
            result.put("isFavorite", false);
            result.put("fittedPosition", null);
        }

        return result;
    }

    @Override
    public boolean updateResume(Resume resume) {
        resume.setModifyTime(LocalDateTime.now());
        return resumeMapper.updateById(resume) > 0;
    }

    @Override
    public boolean deleteResume(Long id) {
        // First clean up all favorite records for this resume
        favoriteMapper.deleteByResumeId(id);
        return resumeMapper.deleteById(id) > 0;
    }

    @Override
    public boolean updateFavorite(Long userId, Long resumeId, Boolean isFavorite, String fittedPosition) {
        if (Boolean.TRUE.equals(isFavorite)) {
            // Add favorite
            UserResumeFavorite existing = favoriteMapper.selectByUserAndResume(userId, resumeId);
            if (existing != null) {
                // Already favorited, just update fitted position if provided
                if (fittedPosition != null) {
                    return favoriteMapper.updateFittedPosition(userId, resumeId, fittedPosition) > 0;
                }
                return true; // Already favorited, nothing to do
            } else {
                UserResumeFavorite fav = new UserResumeFavorite();
                fav.setUserId(userId);
                fav.setResumeId(resumeId);
                fav.setFittedPosition(fittedPosition);
                return favoriteMapper.insert(fav) > 0;
            }
        } else {
            // Remove favorite
            return favoriteMapper.delete(userId, resumeId) > 0;
        }
    }

    /**
     * Convert a DB row map (snake_case keys) to camelCase keys for frontend.
     */
    private Map<String, Object> convertRowKeys(Map<String, Object> row) {
        Map<String, Object> result = new LinkedHashMap<>();
        row.forEach((key, value) -> {
            String camelKey = snakeToCamel(key);
            result.put(camelKey, value);
        });
        return result;
    }

    private String snakeToCamel(String snake) {
        StringBuilder sb = new StringBuilder();
        boolean nextUpper = false;
        for (char c : snake.toCharArray()) {
            if (c == '_') {
                nextUpper = true;
            } else {
                if (nextUpper) {
                    sb.append(Character.toUpperCase(c));
                    nextUpper = false;
                } else {
                    sb.append(c);
                }
            }
        }
        return sb.toString();
    }

    /**
     * Map a DB row result back to a Resume entity (for AI keyword matching).
     */
    private Resume mapToResume(Map<String, Object> row) {
        Resume resume = new Resume();
        resume.setId(toLong(row.get("id")));
        resume.setName(toString(row.get("name")));
        resume.setContact(toString(row.get("contact")));
        resume.setExpectedLocations(toString(row.get("expected_locations")));
        resume.setWorkYears(toInteger(row.get("work_years")));
        resume.setEducation(toString(row.get("education")));
        resume.setSalaryMin(toInteger(row.get("salary_min")));
        resume.setSalaryMax(toInteger(row.get("salary_max")));
        resume.setSkills(toString(row.get("skills")));
        resume.setProjectExperience(toString(row.get("project_experience")));
        resume.setJobStatus(toString(row.get("job_status")));
        resume.setUpdateTime(toLocalDateTime(row.get("update_time")));
        resume.setCreateTime(toLocalDateTime(row.get("create_time")));
        resume.setModifyTime(toLocalDateTime(row.get("modify_time")));
        return resume;
    }

    private Long toLong(Object obj) {
        if (obj == null) return null;
        if (obj instanceof Long) return (Long) obj;
        if (obj instanceof Number) return ((Number) obj).longValue();
        return Long.valueOf(obj.toString());
    }

    private Integer toInteger(Object obj) {
        if (obj == null) return null;
        if (obj instanceof Integer) return (Integer) obj;
        if (obj instanceof Number) return ((Number) obj).intValue();
        return Integer.valueOf(obj.toString());
    }

    private String toString(Object obj) {
        return obj != null ? obj.toString() : null;
    }

    private LocalDateTime toLocalDateTime(Object obj) {
        if (obj == null) return null;
        if (obj instanceof LocalDateTime) return (LocalDateTime) obj;
        return null;
    }

    /**
     * Compute AI match scores for all resumes concurrently.
     */
    private List<Map<String, Object>> computeMatchScores(String keyword, List<Resume> resumes) {
        List<CompletableFuture<Map<String, Object>>> futures = resumes.stream()
                .map(resume -> CompletableFuture.supplyAsync(() -> {
                    try {
                        KeywordMatchResultDTO matchResult = deepSeekService.matchKeyword(keyword, resume);
                        Map<String, Object> item = new HashMap<>();
                        item.put("resume", resume);
                        item.put("matchScore", matchResult.getMatchScore() != null ? matchResult.getMatchScore() : 0);
                        item.put("matchedTexts", matchResult.getMatchedTexts() != null ? matchResult.getMatchedTexts() : Collections.emptyList());
                        return item;
                    } catch (Exception e) {
                        log.warn("Failed to match keyword for resume id={}, name={}: {}",
                                resume.getId(), resume.getName(), e.getMessage());
                        Map<String, Object> item = new HashMap<>();
                        item.put("resume", resume);
                        item.put("matchScore", 0);
                        item.put("matchedTexts", Collections.emptyList());
                        return item;
                    }
                }, matchExecutor))
                .collect(Collectors.toList());

        return futures.stream()
                .map(CompletableFuture::join)
                .collect(Collectors.toList());
    }

    /**
     * Convert DeepSeek AI result to Resume entity.
     */
    private Resume convertToEntity(DeepSeekResultDTO dto) {
        Resume resume = new Resume();
        resume.setName(dto.getName());
        resume.setContact(dto.getContact());
        resume.setExpectedLocations(dto.getExpectedLocations());
        resume.setWorkYears(dto.getWorkYears());
        resume.setEducation(dto.getEducation());
        resume.setSalaryMin(dto.getSalaryMin());
        resume.setSalaryMax(dto.getSalaryMax());
        resume.setSkills(dto.getSkills());
        resume.setProjectExperience(dto.getProjectExperience());
        resume.setJobStatus(dto.getJobStatus());
        return resume;
    }

    /**
     * Merge new resume data into existing resume, keeping more complete info.
     */
    private Resume mergeResume(Resume existing, Resume newData) {
        if (newData.getContact() != null && !newData.getContact().isBlank()) {
            existing.setContact(mergeCommaSeparated(existing.getContact(), newData.getContact()));
        }
        if (newData.getExpectedLocations() != null && !newData.getExpectedLocations().isBlank()) {
            existing.setExpectedLocations(
                    mergeCommaSeparated(existing.getExpectedLocations(), newData.getExpectedLocations()));
        }
        if (newData.getWorkYears() != null) {
            existing.setWorkYears(newData.getWorkYears());
        }
        if (newData.getEducation() != null && !newData.getEducation().isBlank()) {
            existing.setEducation(newData.getEducation());
        }
        if (newData.getSalaryMin() != null) {
            existing.setSalaryMin(newData.getSalaryMin());
        }
        if (newData.getSalaryMax() != null) {
            existing.setSalaryMax(newData.getSalaryMax());
        }
        if (newData.getSkills() != null && !newData.getSkills().isBlank()) {
            existing.setSkills(mergeCommaSeparated(existing.getSkills(), newData.getSkills()));
        }
        if (newData.getProjectExperience() != null && !newData.getProjectExperience().isBlank()) {
            existing.setProjectExperience(newData.getProjectExperience());
        }
        if (newData.getJobStatus() != null && !newData.getJobStatus().isBlank()) {
            existing.setJobStatus(newData.getJobStatus());
        }
        existing.setUpdateTime(LocalDateTime.now());
        return existing;
    }

    private String mergeCommaSeparated(String existing, String newData) {
        if (existing == null || existing.isBlank()) {
            return newData;
        }
        if (newData == null || newData.isBlank()) {
            return existing;
        }
        Set<String> merged = new LinkedHashSet<>();
        for (String part : existing.split("[,，\\s]+")) {
            String trimmed = part.trim();
            if (!trimmed.isEmpty()) {
                merged.add(trimmed);
            }
        }
        for (String part : newData.split("[,，\\s]+")) {
            String trimmed = part.trim();
            if (!trimmed.isEmpty()) {
                merged.add(trimmed);
            }
        }
        return String.join(",", merged);
    }
}
