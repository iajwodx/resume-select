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
     *
     * @param file the uploaded PDF file
     * @return the saved resume entity
     * @throws Exception if upload or parsing fails
     */
    Resume uploadResume(MultipartFile file) throws Exception;

    /**
     * Get resume list with optional filter conditions.
     *
     * @param filter the filter parameters
     * @return map containing "list" and "total"
     */
    Map<String, Object> listResumes(ResumeFilterDTO filter);

    /**
     * Get a single resume by id.
     *
     * @param id the resume id
     * @return the resume entity
     */
    Resume getResume(Long id);

    /**
     * Update a resume.
     *
     * @param resume the resume entity with updated fields
     * @return true if update succeeded
     */
    boolean updateResume(Resume resume);

    /**
     * Delete a resume by id.
     *
     * @param id the resume id
     * @return true if deletion succeeded
     */
    boolean deleteResume(Long id);
}
