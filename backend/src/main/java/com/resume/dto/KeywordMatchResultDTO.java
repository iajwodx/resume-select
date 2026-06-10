package com.resume.dto;

import java.util.List;

/**
 * DTO for keyword matching result from AI analysis.
 */
public class KeywordMatchResultDTO {

    /** 匹配度评分(0-100) */
    private Integer matchScore;

    /** 简历中与关键词匹配的文本片段列表 */
    private List<String> matchedTexts;

    public Integer getMatchScore() {
        return matchScore;
    }

    public void setMatchScore(Integer matchScore) {
        this.matchScore = matchScore;
    }

    public List<String> getMatchedTexts() {
        return matchedTexts;
    }

    public void setMatchedTexts(List<String> matchedTexts) {
        this.matchedTexts = matchedTexts;
    }
}
