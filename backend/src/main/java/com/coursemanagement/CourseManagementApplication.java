package com.coursemanagement;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.boot.autoconfigure.domain.EntityScan;
import org.springframework.data.jpa.repository.config.EnableJpaAuditing;

@SpringBootApplication
@EnableJpaAuditing
@EntityScan("com.coursemanagement.entity")
public class CourseManagementApplication {
    public static void main(String[] args) {
        SpringApplication.run(CourseManagementApplication.class, args);
    }
}
