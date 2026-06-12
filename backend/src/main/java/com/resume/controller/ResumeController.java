package com.resume.controller;

import com.resume.dto.ResumeFilterDTO;
import com.resume.dto.Result;
import com.resume.entity.Resume;
import com.resume.service.ResumeService;
import jakarta.servlet.http.HttpSession;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;

import java.util.Map;

/**
 * REST controller for resume operations.
 */
@RestController
@RequestMapping("/api/resume")
public class ResumeController {

    private static final Logger log = LoggerFactory.getLogger(ResumeController.class);

    private final ResumeService resumeService;

    public ResumeController(ResumeService resumeService) {
        this.resumeService = resumeService;
    }

    /**
     * Upload a PDF resume for AI analysis and storage.
     */
    @PostMapping("/upload")
    public Result<Resume> uploadResume(@RequestParam("file") MultipartFile file) {
        try {
            if (file.isEmpty()) {
                return Result.error(400, "请选择要上传的文件");
            }
            String contentType = file.getContentType();
            if (contentType == null || !contentType.equals("application/pdf")) {
                return Result.error(400, "仅支持PDF格式文件");
            }
            Resume resume = resumeService.uploadResume(file);
            return Result.success("简历分析完成", resume);
        } catch (Exception e) {
            log.error("Upload resume failed", e);
            return Result.error("简历上传分析失败: " + e.getMessage());
        }
    }

    /**
     * Get resume list with optional filter conditions.
     * Session provides userId for per-user favorite info.
     */
    @GetMapping("/list")
    public Result<Map<String, Object>> listResumes(ResumeFilterDTO filter, HttpSession session) {
        try {
            Long userId = getUserIdFromSession(session);
            Map<String, Object> result = resumeService.listResumes(filter, userId);
            return Result.success(result);
        } catch (Exception e) {
            log.error("List resumes failed", e);
            return Result.error("获取简历列表失败: " + e.getMessage());
        }
    }

    /**
     * Get a single resume by id.
     * Session provides userId for per-user favorite info.
     */
    @GetMapping("/{id}")
    public Result<Map<String, Object>> getResume(@PathVariable Long id, HttpSession session) {
        try {
            Long userId = getUserIdFromSession(session);
            Map<String, Object> resume = resumeService.getResume(id, userId);
            if (resume == null) {
                return Result.error(404, "简历不存在");
            }
            return Result.success(resume);
        } catch (Exception e) {
            log.error("Get resume failed", e);
            return Result.error("获取简历详情失败: " + e.getMessage());
        }
    }

    /**
     * Update a resume.
     */
    @PutMapping("/{id}")
    public Result<Void> updateResume(@PathVariable Long id, @RequestBody Resume resume) {
        try {
            resume.setId(id);
            boolean success = resumeService.updateResume(resume);
            if (success) {
                return Result.success("更新成功", null);
            }
            return Result.error("更新失败");
        } catch (Exception e) {
            log.error("Update resume failed", e);
            return Result.error("更新简历失败: " + e.getMessage());
        }
    }

    /**
     * Delete a resume by id.
     */
    @DeleteMapping("/{id}")
    public Result<Void> deleteResume(@PathVariable Long id) {
        try {
            boolean success = resumeService.deleteResume(id);
            if (success) {
                return Result.success("删除成功", null);
            }
            return Result.error("删除失败");
        } catch (Exception e) {
            log.error("Delete resume failed", e);
            return Result.error("删除简历失败: " + e.getMessage());
        }
    }

    /**
     * Toggle favorite status for a resume, with optional fitted position.
     * Session provides userId for per-user favorite.
     */
    @PutMapping("/{id}/favorite")
    public Result<Void> toggleFavorite(@PathVariable Long id, @RequestBody Map<String, Object> body, HttpSession session) {
        try {
            Long userId = getUserIdFromSession(session);
            if (userId == null) {
                return Result.error(401, "未登录");
            }

            Boolean isFavorite = null;
            Object isFav = body.get("isFavorite");
            if (isFav != null) {
                isFavorite = Boolean.TRUE.equals(isFav);
            }

            String fittedPosition = null;
            Object fittedPos = body.get("fittedPosition");
            if (fittedPos != null && !fittedPos.toString().isEmpty()) {
                fittedPosition = fittedPos.toString();
            }

            boolean success = resumeService.updateFavorite(userId, id, isFavorite, fittedPosition);
            if (success) {
                return Result.success("操作成功", null);
            }
            return Result.error("操作失败");
        } catch (Exception e) {
            log.error("Toggle favorite failed", e);
            return Result.error("收藏操作失败: " + e.getMessage());
        }
    }

    /**
     * Extract userId from session.
     */
    private Long getUserIdFromSession(HttpSession session) {
        Object userId = session.getAttribute("userId");
        if (userId == null) {
            return null;
        }
        if (userId instanceof Long) {
            return (Long) userId;
        }
        return Long.valueOf(userId.toString());
    }
}
