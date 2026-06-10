package com.resume.dto;

/**
 * DTO for DeepSeek AI analysis result.
 */
public class DeepSeekResultDTO {

    /** 姓名 */
    private String name;

    /** 联系方式(多个用逗号分隔) */
    private String contact;

    /** 期望工作地点(多个用逗号分隔) */
    private String expectedLocations;

    /** 工作年限 */
    private Integer workYears;

    /** 学历 */
    private String education;

    /** 期望薪资下限(千元) */
    private Integer salaryMin;

    /** 期望薪资上限(千元) */
    private Integer salaryMax;

    /** 技能 */
    private String skills;

    /** 项目经验 */
    private String projectExperience;

    /** 求职状态 */
    private String jobStatus;

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getContact() {
        return contact;
    }

    public void setContact(String contact) {
        this.contact = contact;
    }

    public String getExpectedLocations() {
        return expectedLocations;
    }

    public void setExpectedLocations(String expectedLocations) {
        this.expectedLocations = expectedLocations;
    }

    public Integer getWorkYears() {
        return workYears;
    }

    public void setWorkYears(Integer workYears) {
        this.workYears = workYears;
    }

    public String getEducation() {
        return education;
    }

    public void setEducation(String education) {
        this.education = education;
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

    public String getSkills() {
        return skills;
    }

    public void setSkills(String skills) {
        this.skills = skills;
    }

    public String getProjectExperience() {
        return projectExperience;
    }

    public void setProjectExperience(String projectExperience) {
        this.projectExperience = projectExperience;
    }

    public String getJobStatus() {
        return jobStatus;
    }

    public void setJobStatus(String jobStatus) {
        this.jobStatus = jobStatus;
    }
}
