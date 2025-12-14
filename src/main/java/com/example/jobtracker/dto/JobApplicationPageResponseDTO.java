package com.example.jobtracker.dto;


import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;

import java.util.List;

@Data
@Builder
@AllArgsConstructor
public class JobApplicationPageResponseDTO {

    private List<JobApplicationResponseDTO> jobs;
    private int page;
    private int size;
    private long total;

}
