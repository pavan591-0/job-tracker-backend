package com.example.jobtracker.service;

import com.example.jobtracker.dto.JobApplicationPageResponseDTO;
import com.example.jobtracker.dto.JobApplicationRequestDTO;
import com.example.jobtracker.dto.JobApplicationResponseDTO;
import com.example.jobtracker.dto.JobFilterDTO;
import com.example.jobtracker.dto.JobStatusHistoryDTO;
import org.springframework.data.domain.Page;

import java.util.List;

public interface JobApplicationService {

    JobApplicationResponseDTO create(JobApplicationRequestDTO request);

    List<JobApplicationResponseDTO> getAll();

    JobApplicationResponseDTO getById(Long id);

    JobApplicationResponseDTO update(Long id, JobApplicationRequestDTO request);

    void delete(Long id);

    Page<JobApplicationResponseDTO> getAll(int page, int size, String sortBy, String order);

    Page<JobApplicationResponseDTO> search(String status, String source, int page, int size, String sortBy, String order);

    JobApplicationResponseDTO updateStatus(Long id, String status);

    JobApplicationPageResponseDTO filter(String status, String company, int page, int size);

    JobApplicationPageResponseDTO advancedFilter(JobFilterDTO filter, int page, int size, String sortBy, String order);

    List<JobStatusHistoryDTO> getHistory(Long id);

    JobApplicationResponseDTO undoLastStatus(Long id);
}
