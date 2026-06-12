CREATE DATABASE IF NOT EXISTS resume_select DEFAULT CHARSET utf8mb4 COLLATE utf8mb4_unicode_ci;

USE resume_select;

CREATE TABLE IF NOT EXISTS resume (
    id                BIGINT AUTO_INCREMENT PRIMARY KEY,
    name              VARCHAR(50) NOT NULL COMMENT '姓名',
    contact           VARCHAR(200) COMMENT '联系方式(多个用逗号分隔)',
    expected_locations VARCHAR(500) COMMENT '期望工作地点(多个用逗号分隔)',
    work_years        INT COMMENT '工作年限',
    education         VARCHAR(50) COMMENT '学历',
    salary_min        INT COMMENT '期望薪资下限(千元)',
    salary_max        INT COMMENT '期望薪资上限(千元)',
    skills            TEXT COMMENT '技能',
    project_experience TEXT COMMENT '项目经验',
    job_status        VARCHAR(20) COMMENT '求职状态: 在职/离职/随时到岗',
    is_favorite       TINYINT(1) DEFAULT 0 COMMENT '是否收藏: 0-未收藏, 1-已收藏',
    fitted_position   VARCHAR(100) DEFAULT NULL COMMENT '适配岗位',
    update_time       DATETIME COMMENT '简历更新时间',
    create_time       DATETIME DEFAULT CURRENT_TIMESTAMP,
    modify_time       DATETIME DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP
);

CREATE TABLE IF NOT EXISTS user (
    id          BIGINT AUTO_INCREMENT PRIMARY KEY,
    username    VARCHAR(50) NOT NULL UNIQUE,
    password    VARCHAR(100) NOT NULL,
    role        VARCHAR(20) NOT NULL COMMENT 'admin/user',
    invite_code VARCHAR(50) DEFAULT NULL COMMENT '权限码'
);
