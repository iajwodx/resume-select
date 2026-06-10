package com.resume.service;

import com.resume.dto.DeepSeekResultDTO;
import com.resume.dto.KeywordMatchResultDTO;
import com.resume.entity.Resume;

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

    /**
     * Match a keyword against a resume and compute relevance score.
     *
     * @param keyword the keyword(s) to match (space or comma separated)
     * @param resume  the resume to evaluate
     * @return matching result with score and matched text snippets
     * @throws Exception if API call fails
     */
    KeywordMatchResultDTO matchKeyword(String keyword, Resume resume) throws Exception;
}
