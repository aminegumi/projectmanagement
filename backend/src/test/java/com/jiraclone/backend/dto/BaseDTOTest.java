package com.jiraclone.backend.dto;

import java.time.LocalDateTime;
import org.junit.jupiter.api.BeforeEach;

public abstract class BaseDTOTest {
    protected LocalDateTime now;

    @BeforeEach
    void setUp() {
        now = LocalDateTime.now();
    }
}