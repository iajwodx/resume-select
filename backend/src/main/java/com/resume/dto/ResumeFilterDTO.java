package com.resume.dto;

/**
 * DTO for resume filtering parameters.
 */
public class ResumeFilterDTO {

    /** 期望工作地点(逗号分隔, 符合其一) */
    private String locations;

    /** 最低工作年限 */
    private Integer minWorkYears;

    /** 学历(逗号分隔, 符合其一) */
    private String educations;

    /** 期望薪资下限(千元) */
    private Integer salaryMin;

    /** 期望薪资上限(千元) */
    private Integer salaryMax;

    /** 求职状态: 在职/离职 */
    private String jobStatus;

    /** 是否收藏: true-已收藏 */
    private Boolean isFavorite;

    /** 适配岗位 */
    private String fittedPosition;

    /** 关键词(用于AI匹配) */
    private String keyword;

    /** 排序方式: matchScore-按匹配度排序, updateTime-按更新时间排序 */
    private String sortBy;

    /** 页码(从1开始) */
    private Integer page = 1;

    /** 每页数量 */
    private Integer size = 10;

    public String getLocations() {
        return locations;
    }

    public void setLocations(String locations) {
        this.locations = locations;
    }

    public Integer getMinWorkYears() {
        return minWorkYears;
    }

    public void setMinWorkYears(Integer minWorkYears) {
        this.minWorkYears = minWorkYears;
    }

    public String getEducations() {
        return educations;
    }

    public void setEducations(String educations) {
        this.educations = educations;
    }

    public Integer getSalaryMin() {
        return salaryMin;
    }

    public void setSalaryMin(Integer salaryMin) {
        this.salaryMin = salaryMin;
    }

    public Integer getSalaryMax() {
        return salaryMax;
    }

    public void setSalaryMax(Integer salaryMax) {
        this.salaryMax = salaryMax;
    }

    public String getJobStatus() {
        return jobStatus;
    }

    public void setJobStatus(String jobStatus) {
        this.jobStatus = jobStatus;
    }

    public Boolean getIsFavorite() {
        return isFavorite;
    }

    public void setIsFavorite(Boolean isFavorite) {
        this.isFavorite = isFavorite;
    }

    public String getFittedPosition() {
        return fittedPosition;
    }

    public void setFittedPosition(String fittedPosition) {
        this.fittedPosition = fittedPosition;
    }

    public String getKeyword() {
        return keyword;
    }

    public void setKeyword(String keyword) {
        this.keyword = keyword;
    }

    public String getSortBy() {
        return sortBy;
    }

    public void setSortBy(String sortBy) {
        this.sortBy = sortBy;
    }

    public Integer getPage() {
        return page;
    }

    public void setPage(Integer page) {
        this.page = page;
    }

    public Integer getSize() {
        return size;
    }

    public void setSize(Integer size) {
        this.size = size;
    }
}
