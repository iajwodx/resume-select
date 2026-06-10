package com.resume.entity;

import java.time.LocalDateTime;

/**
 * Resume entity mapping to resume table.
 */
public class Resume {

    private Long id;

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

    /** 求职状态: 在职/离职 */
    private String jobStatus;

    /** 简历更新时间 */
    private LocalDateTime updateTime;

    /** 创建时间 */
    private LocalDateTime createTime;

    /** 修改时间 */
    private LocalDateTime modifyTime;

    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

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

    public LocalDateTime getUpdateTime() {
        return updateTime;
    }

    public void setUpdateTime(LocalDateTime updateTime) {
        this.updateTime = updateTime;
    }

    public LocalDateTime getCreateTime() {
        return createTime;
    }

    public void setCreateTime(LocalDateTime createTime) {
        this.createTime = createTime;
    }

    public LocalDateTime getModifyTime() {
        return modifyTime;
    }

    public void setModifyTime(LocalDateTime modifyTime) {
        this.modifyTime = modifyTime;
    }
}
