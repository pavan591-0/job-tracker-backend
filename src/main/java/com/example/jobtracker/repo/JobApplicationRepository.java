package com.example.jobtracker.repo;

import com.example.jobtracker.entity.JobApplication;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.JpaSpecificationExecutor;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface JobApplicationRepository extends JpaRepository<JobApplication, Long>,
        JpaSpecificationExecutor<JobApplication> {

    List<JobApplication> findByCompanyNameIgnoreCaseAndPositionIgnoreCase(
            String companyName,
            String position
    );
}

