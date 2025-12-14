package com.example.jobtracker.mapper;

import com.example.jobtracker.dto.JobApplicationRequestDTO;
import com.example.jobtracker.dto.JobApplicationResponseDTO;
import com.example.jobtracker.entity.JobApplication;

import java.util.List;

public class JobApplicationMapper {

    public static JobApplication toEntity(JobApplicationRequestDTO dto) {
        return JobApplication.builder()
                .companyName(dto.companyName())
                .position(dto.position())
                .status(dto.status())
                .location(dto.location())
                .source(dto.source())
                .notes(dto.notes())
                .build();
    }

    public static JobApplicationResponseDTO toResponse(JobApplication entity) {
        return new JobApplicationResponseDTO(
                entity.getId(),
                entity.getCompanyName(),
                entity.getPosition(),
                entity.getStatus(),
                entity.getLocation(),
                entity.getSource(),
                entity.getNotes(),
                entity.getAppliedDate(),
                entity.getUpdatedDate()
        );
    }

    public static List<JobApplicationResponseDTO> toResponseList(List<JobApplication> entities){
        return entities.stream()
                .map(JobApplicationMapper::toResponse)
                .toList();
    }



}
