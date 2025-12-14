package com.example.jobtracker.repo;

import com.example.jobtracker.entity.JobStatusHistory;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;

public interface JobStatusHistoryRepository extends
        JpaRepository<JobStatusHistory, Long> {
    List<JobStatusHistory> findByJobApplicationIdOrderByChangedAtDesc(Long jobId);
}
