package com.resume;

import org.mybatis.spring.annotation.MapperScan;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;

/**
 * Resume Screening Platform Application.
 */
@SpringBootApplication
@MapperScan("com.resume.mapper")
public class ResumeSelectApplication {

    public static void main(String[] args) {
        SpringApplication.run(ResumeSelectApplication.class, args);
    }
}
