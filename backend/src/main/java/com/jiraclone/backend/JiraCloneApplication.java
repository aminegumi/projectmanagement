package com.jiraclone.backend;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.scheduling.annotation.EnableScheduling;

@SpringBootApplication
@EnableScheduling
public class JiraCloneApplication {

    public static void main(String[] args) {
        SpringApplication.run(JiraCloneApplication.class, args);
        System.out.println("Application is running...");
    }
}