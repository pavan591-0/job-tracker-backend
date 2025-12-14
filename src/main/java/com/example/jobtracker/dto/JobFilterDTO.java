package com.example.jobtracker.dto;

import java.time.LocalDate;

public record JobFilterDTO(
        String status,
        String company,
        String location,
        String source,
        LocalDate minDate,
        LocalDate maxDate
) {}
