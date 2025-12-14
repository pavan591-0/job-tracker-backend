package com.example.jobtracker.controller;

import com.example.jobtracker.dto.*;
import com.example.jobtracker.service.JobApplicationService;
import jakarta.validation.Valid;

import org.springframework.data.domain.Page;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/job-applications")
public class JobApplicationController {

    private final JobApplicationService jobApplicationService;

    public JobApplicationController(JobApplicationService jobApplicationService) {
        this.jobApplicationService = jobApplicationService;
    }

    @PostMapping
    public ResponseEntity<JobApplicationResponseDTO> createJobApplication(
            @Valid @RequestBody JobApplicationRequestDTO request) {
        JobApplicationResponseDTO response = jobApplicationService.create(request);
        return ResponseEntity.status(HttpStatus.CREATED).body(response);
    }


    @GetMapping
    public ResponseEntity<List<JobApplicationResponseDTO>> getAllJobApplications() {
        List<JobApplicationResponseDTO> response = jobApplicationService.getAll();
        return ResponseEntity.ok(response);
    }

    // GET single job by id (optional but useful)
    @GetMapping("/{id}")
    public ResponseEntity<JobApplicationResponseDTO> getJobApplicationById(@PathVariable Long id) {
        JobApplicationResponseDTO response = jobApplicationService.getById(id);
        return ResponseEntity.ok(response);
    }

    @GetMapping("/paged")
    public ResponseEntity<Page<JobApplicationResponseDTO>> getPaginated(
            @RequestParam(defaultValue = "0") int page,
            @RequestParam(defaultValue = "10") int size,
            @RequestParam(defaultValue = "id") String sortBy,
            @RequestParam(defaultValue = "asc") String order
    ) {
        Page<JobApplicationResponseDTO> result =
                jobApplicationService.getAll(page, size, sortBy, order);

        return ResponseEntity.ok(result);
    }

    @GetMapping("/search")
    public ResponseEntity<Page<JobApplicationResponseDTO>> search(
            @RequestParam(required = false) String status,
            @RequestParam(required = false) String source,
            @RequestParam(defaultValue = "0") int page,
            @RequestParam(defaultValue = "10") int size,
            @RequestParam(defaultValue = "asc") String order,
            @RequestParam(defaultValue = "id") String sortBy
    ) {
        Page<JobApplicationResponseDTO> result =
                jobApplicationService.search(status, source, page, size, sortBy, order);

        return ResponseEntity.ok(result);
    }

    @PatchMapping("/{id}/status")
    public ResponseEntity<JobApplicationResponseDTO> updateStatus(
            @PathVariable Long id,
            @RequestParam String value
    ) {
        JobApplicationResponseDTO response = jobApplicationService.updateStatus(id, value);
        return ResponseEntity.ok(response);
    }

    @PutMapping("/{id}")
    public ResponseEntity<JobApplicationResponseDTO> update(
            @PathVariable Long id,
            @Valid @RequestBody JobApplicationRequestDTO request
    ) {
        JobApplicationResponseDTO response = jobApplicationService.update(id, request);
        return ResponseEntity.ok(response);
    }

    @DeleteMapping("/{id}")
    public ResponseEntity<Void> delete(@PathVariable Long id) {
        jobApplicationService.delete(id);
        return ResponseEntity.noContent().build();
    }

    @GetMapping("/filter")
    public ResponseEntity<?> filter(
            @RequestParam(required = false) String status,
            @RequestParam(required = false) String company,
            @RequestParam(defaultValue = "0") int page,
            @RequestParam(defaultValue = "10") int size
    ){
        var response = jobApplicationService.filter(status, company, page, size);
        return ResponseEntity.ok(response);
    }

    @GetMapping("/filter/advanced")
    public ResponseEntity<JobApplicationPageResponseDTO> advancedfilter(
            @ModelAttribute JobFilterDTO filter,
            @RequestParam(defaultValue = "0") int page,
            @RequestParam(defaultValue = "10") int size,
            @RequestParam(defaultValue = "id") String sortBy,
            @RequestParam(defaultValue = "asc") String order
    ) {
        var response = jobApplicationService.advancedFilter(filter, page, size, sortBy, order);
        return ResponseEntity.ok(response);
    }

    @GetMapping("/{id}/history")
    public ResponseEntity<List<JobStatusHistoryDTO>> getHistory(@PathVariable Long id) {
        var history = jobApplicationService.getHistory(id);
        return ResponseEntity.ok(history);
    }

    @PostMapping("/{id}/undo-status")
    public ResponseEntity<JobApplicationResponseDTO> undoStatus(@PathVariable Long id) {
        var response = jobApplicationService.undoLastStatus(id);
        return ResponseEntity.ok(response);
    }



}

