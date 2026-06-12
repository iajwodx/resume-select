package com.resume.entity;

/**
 * User-Resume favorite relationship entity.
 */
public class UserResumeFavorite {

    private Long userId;

    private Long resumeId;

    /** 适配岗位 */
    private String fittedPosition;

    public Long getUserId() {
        return userId;
    }

    public void setUserId(Long userId) {
        this.userId = userId;
    }

    public Long getResumeId() {
        return resumeId;
    }

    public void setResumeId(Long resumeId) {
        this.resumeId = resumeId;
    }

    public String getFittedPosition() {
        return fittedPosition;
    }

    public void setFittedPosition(String fittedPosition) {
        this.fittedPosition = fittedPosition;
    }
}
