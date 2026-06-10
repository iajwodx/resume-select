package com.resume.service;

import com.resume.dto.DeepSeekResultDTO;

/**
 * Service for interacting with DeepSeek AI API.
 */
public interface DeepSeekService {

    /**
     * Analyze resume text and extract structured information.
     *
     * @param resumeText the raw text extracted from a resume PDF
     * @return structured resume data extracted by AI
     * @throws Exception if API call fails
     */
    DeepSeekResultDTO analyzeResume(String resumeText) throws Exception;
}
