package com.resume.service.impl;

import com.resume.dto.DeepSeekResultDTO;
import com.resume.dto.ResumeFilterDTO;
import com.resume.entity.Resume;
import com.resume.mapper.ResumeMapper;
import com.resume.service.DeepSeekService;
import com.resume.service.PdfParseService;
import com.resume.service.ResumeService;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Service;
import org.springframework.web.multipart.MultipartFile;

import java.time.LocalDateTime;
import java.util.HashMap;
import java.util.LinkedHashSet;
import java.util.List;
import java.util.Map;
import java.util.Set;

/**
 * Implementation of ResumeService.
 */
@Service
public class ResumeServiceImpl implements ResumeService {

    private static final Logger log = LoggerFactory.getLogger(ResumeServiceImpl.class);

    private final PdfParseService pdfParseService;
    private final DeepSeekService deepSeekService;
    private final ResumeMapper resumeMapper;

    public ResumeServiceImpl(PdfParseService pdfParseService,
                             DeepSeekService deepSeekService,
                             ResumeMapper resumeMapper) {
        this.pdfParseService = pdfParseService;
        this.deepSeekService = deepSeekService;
        this.resumeMapper = resumeMapper;
    }

    @Override
    public Resume uploadResume(MultipartFile file) throws Exception {
        // Step 1: Extract text from PDF
        String pdfText = pdfParseService.extractText(file.getInputStream());
        if (pdfText == null || pdfText.isBlank()) {
            throw new RuntimeException("无法从PDF中提取文本内容");
        }

        // Step 2: Call DeepSeek AI to analyze resume
        DeepSeekResultDTO aiResult = deepSeekService.analyzeResume(pdfText);

        // Step 3: Convert DTO to entity
        Resume resume = convertToEntity(aiResult);
        resume.setUpdateTime(LocalDateTime.now());

        // Step 4: Check for duplicate (same name and contact) and merge
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

        // Step 5: Insert new resume
        resumeMapper.insert(resume);
        log.info("Inserted new resume for: {}", resume.getName());
        return resume;
    }

    @Override
    public Map<String, Object> listResumes(ResumeFilterDTO filter) {
        int page = filter.getPage() != null ? filter.getPage() : 1;
        int size = filter.getSize() != null ? filter.getSize() : 10;
        int offset = (page - 1) * size;

        List<Resume> list = resumeMapper.selectWithFilter(
                filter.getLocations(),
                filter.getMinWorkYears(),
                filter.getEducations(),
                filter.getSalaryMin(),
                filter.getSalaryMax(),
                filter.getJobStatus(),
                offset,
                size
        );

        int total = resumeMapper.countWithFilter(
                filter.getLocations(),
                filter.getMinWorkYears(),
                filter.getEducations(),
                filter.getSalaryMin(),
                filter.getSalaryMax(),
                filter.getJobStatus()
        );

        Map<String, Object> result = new HashMap<>();
        result.put("list", list);
        result.put("total", total);
        result.put("page", page);
        result.put("size", size);
        return result;
    }

    @Override
    public Resume getResume(Long id) {
        return resumeMapper.selectById(id);
    }

    @Override
    public boolean updateResume(Resume resume) {
        resume.setModifyTime(LocalDateTime.now());
        return resumeMapper.updateById(resume) > 0;
    }

    @Override
    public boolean deleteResume(Long id) {
        return resumeMapper.deleteById(id) > 0;
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

    /**
     * Merge two comma-separated strings, deduplicating values.
     */
    private String mergeCommaSeparated(String existing, String newData) {
        if (existing == null || existing.isBlank()) {
            return newData;
        }
        if (newData == null || newData.isBlank()) {
            return existing;
        }
        Set<String> merged = new LinkedHashSet<>();
        for (String part : existing.split(",")) {
            String trimmed = part.trim();
            if (!trimmed.isEmpty()) {
                merged.add(trimmed);
            }
        }
        for (String part : newData.split(",")) {
            String trimmed = part.trim();
            if (!trimmed.isEmpty()) {
                merged.add(trimmed);
            }
        }
        return String.join(",", merged);
    }
}
