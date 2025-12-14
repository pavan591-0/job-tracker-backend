package com.example.jobtracker.dto;

import java.time.LocalDate;

public record JobApplicationResponseDTO(
        Long id,
        String companyName,
        String position,
        String status,
        String location,
        String source,
        String notes,
        LocalDate appliedDate,
        LocalDate updatedDate
) {}
