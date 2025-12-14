package com.example.jobtracker.dto;

import java.time.LocalDateTime;

public record JobStatusHistoryDTO(
        String oldStatus,
        String newStatus,
        LocalDateTime changedAt
) {}
