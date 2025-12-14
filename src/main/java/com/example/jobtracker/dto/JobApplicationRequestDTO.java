package com.example.jobtracker.dto;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Size;

public record JobApplicationRequestDTO(

        @NotBlank(message = "Company name cannot be empty")
        @Size(max = 100, message = "Company name must be <= 100 characters")
        String companyName,

        @NotBlank(message = "Position cannot be empty")
        @Size(max = 100, message = "Position must be <= 100 characters")
        String position,

        @NotBlank(message = "Status cannot be empty")
        String status,

        @NotBlank(message = "Location is required")
        String location,

        @NotBlank(message = "Source is required")
        String source,

        @Size(max = 500, message = "Notes must be at most 500 characters")
        String notes
) {}
