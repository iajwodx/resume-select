package com.resume.service;

import com.resume.dto.ResumeFilterDTO;
import com.resume.entity.Resume;
import org.springframework.web.multipart.MultipartFile;

import java.util.Map;

/**
 * Service for resume business logic.
 */
public interface ResumeService {

    /**
     * Upload a PDF resume, parse it with AI, and save to database.
     */
    Resume uploadResume(MultipartFile file) throws Exception;

    /**
     * Get resume list with optional filter conditions.
     *
     * @param filter the filter parameters
     * @param userId current user id (from session), used for per-user favorite
     * @return map containing "list", "total", "page", "size", "fittedPositions"
     */
    Map<String, Object> listResumes(ResumeFilterDTO filter, Long userId);

    /**
     * Get a single resume by id.
     *
     * @param id     the resume id
     * @param userId current user id (from session), used for per-user favorite
     * @return the resume entity with favorite info
     */
    Map<String, Object> getResume(Long id, Long userId);

    /**
     * Update a resume.
     */
    boolean updateResume(Resume resume);

    /**
     * Delete a resume by id.
     */
    boolean deleteResume(Long id);

    /**
     * Toggle favorite: add or remove favorite, optionally update fitted position.
     *
     * @param userId         current user id
     * @param resumeId       the resume id
     * @param isFavorite     true to favorite, false to unfavorite
     * @param fittedPosition fitted position (only used when isFavorite is true)
     */
    boolean updateFavorite(Long userId, Long resumeId, Boolean isFavorite, String fittedPosition);
}
