package com.example.jobtracker.service.impl;

import com.example.jobtracker.dto.*;
import com.example.jobtracker.entity.JobStatusHistory;
import com.example.jobtracker.exception.BadRequestException;
import com.example.jobtracker.exception.NotFoundException;
import com.example.jobtracker.mapper.JobApplicationMapper;
import com.example.jobtracker.entity.JobApplication;
import com.example.jobtracker.repo.JobApplicationRepository;
import com.example.jobtracker.repo.JobStatusHistoryRepository;
import com.example.jobtracker.service.JobApplicationService;
import com.example.jobtracker.spec.JobApplicationSpecification;
import jakarta.transaction.Transactional;
import org.springframework.cglib.core.Local;
import org.springframework.data.domain.*;
import org.springframework.stereotype.Service;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.Comparator;
import java.util.List;
import java.util.stream.Stream;

@Service
public class JobApplicationServiceImpl implements JobApplicationService {

    private final JobApplicationRepository repository;
    private final JobStatusHistoryRepository statusHistory;

    public JobApplicationServiceImpl(JobApplicationRepository repository, JobStatusHistoryRepository statusHistory) {
        this.repository = repository;
        this.statusHistory = statusHistory;
    }

    @Override
    public JobApplicationResponseDTO create(JobApplicationRequestDTO request) {
        JobApplication entity = JobApplicationMapper.toEntity(request);
        JobApplication saved = repository.save(entity);
        return JobApplicationMapper.toResponse(saved);
    }

    @Override
    public List<JobApplicationResponseDTO> getAll() {
        return repository.findAll().stream()
                .map(JobApplicationMapper::toResponse)
                .toList();
    }

    @Override
    public JobApplicationResponseDTO getById(Long id) {
        JobApplication entity = repository.findById(id)
                .orElseThrow(() -> new NotFoundException("Job not found with id "+id));

        return JobApplicationMapper.toResponse(entity);
    }

    @Override
    @Transactional
    public JobApplicationResponseDTO update(Long id, JobApplicationRequestDTO request) {

        JobApplication entity = repository.findById(id)
                .orElseThrow(() -> new RuntimeException("Job not found"));

        entity.setCompanyName(request.companyName());
        entity.setPosition(request.position());
        entity.setStatus(request.status());
        entity.setSource(request.source());
        entity.setLocation(request.location());
        entity.setNotes(request.notes());
        entity.setUpdatedDate(LocalDate.now());

        return JobApplicationMapper.toResponse(entity);
    }


    @Override
    @Transactional
    public void delete(Long id) {

        if (!repository.existsById(id)) {
            throw new NotFoundException("Job not found with id: "+id);
        }

        repository.deleteById(id);
    }


    @Override
    public Page<JobApplicationResponseDTO> getAll(int page, int size, String sortBy, String order) {

        Sort sort = order.equalsIgnoreCase("desc")
                ? Sort.by(sortBy).descending()
                : Sort.by(sortBy).ascending();

        Pageable pageable = PageRequest.of(page, size, sort);

        Page<JobApplication> result = repository.findAll(pageable);

        return result.map(JobApplicationMapper::toResponse);
    }

    @Override
    public Page<JobApplicationResponseDTO> search(String status, String source, int page, int size, String sortBy, String order) {

        Sort sort = order.equalsIgnoreCase("desc")
                ? Sort.by(sortBy).descending()
                : Sort.by(sortBy).ascending();

        Pageable pageable = PageRequest.of(page, size, sort);

        Page<JobApplication> result = repository.findAll(pageable);

        Page<JobApplicationResponseDTO> response = result.map(JobApplicationMapper::toResponse);

        List<JobApplicationResponseDTO> filtered = response.getContent();

        if(status != null && !status.isBlank()){
            filtered = filtered.stream()
                    .filter(j -> status.equalsIgnoreCase(j.status()))
                    .toList();
        }

        if(source!= null && !source.isBlank()){
            filtered = filtered.stream()
                    .filter(j -> source.equalsIgnoreCase(j.source()))
                    .toList();
        }

        return new PageImpl<>(filtered, pageable, filtered.size());

    }

    @Override
    @Transactional
    public JobApplicationResponseDTO updateStatus(Long id, String newStatus) {
        JobApplication entity = repository.findById(id)
                .orElseThrow(() -> new NotFoundException("Job not found"));

        String oldStatus = entity.getStatus();

        if(oldStatus.equals(newStatus)){
            return JobApplicationMapper.toResponse(entity);
        }

        entity.setStatus(newStatus);
        entity.setUpdatedDate(LocalDate.now());

        JobStatusHistory history = JobStatusHistory.builder()
                .jobApplication(entity)
                .oldStatus(oldStatus)
                .newStatus(newStatus)
                .changedAt(LocalDateTime.now())
                .build();

        statusHistory.save(history);

        return JobApplicationMapper.toResponse(entity);
    }

    @Override
    public JobApplicationPageResponseDTO filter(String status, String company, int page, int size) {

        List<JobApplication> data = repository.findAll();

        Stream<JobApplication> stream = data.stream();

        if(status!= null && !status.isBlank())
            stream = stream.filter(job -> job.getStatus().equalsIgnoreCase(status));

        if(company != null && !company.isBlank())
            stream = stream.filter(job-> job.getCompanyName().equalsIgnoreCase(company));

        data = stream.toList();

        int start = page * size;
        int end = Math.min(start + size, data.size());

        List<JobApplicationResponseDTO> content = start >= data.size() ? List.of() :
                JobApplicationMapper.toResponseList(data.subList(start, end));


        return JobApplicationPageResponseDTO.builder()
                .jobs(content)
                .page(page)
                .size(size)
                .total(data.size())
                .build();
    }

//    @Override
//    public JobApplicationPageResponseDTO advancedFilter(
//            JobFilterDTO filter,
//            int page,
//            int size,
//            String sortBy,
//            String order
//    ) {
//
//        // 1️⃣ FETCH ALL DATA
//        List<JobApplication> data = repository.findAll();
//
//        // 2️⃣ FILTER USING STREAMS
//        var stream = data.stream();
//
//        if (filter.status() != null && !filter.status().isBlank()) {
//            stream = stream.filter(j -> j.getStatus().equalsIgnoreCase(filter.status()));
//        }
//
//        if (filter.company() != null && !filter.company().isBlank()) {
//            stream = stream.filter(j -> j.getCompanyName().equalsIgnoreCase(filter.company()));
//        }
//
//        if (filter.location() != null && !filter.location().isBlank()) {
//            stream = stream.filter(j -> j.getLocation().equalsIgnoreCase(filter.location()));
//        }
//
//        if (filter.source() != null && !filter.source().isBlank()) {
//            stream = stream.filter(j -> j.getSource().equalsIgnoreCase(filter.source()));
//        }
//
//        if (filter.minDate() != null) {
//            stream = stream.filter(j -> j.getAppliedDate().isAfter(filter.minDate())
//                    || j.getAppliedDate().isEqual(filter.minDate()));
//        }
//
//        if (filter.maxDate() != null) {
//            stream = stream.filter(j -> j.getAppliedDate().isBefore(filter.maxDate())
//                    || j.getAppliedDate().isEqual(filter.maxDate()));
//        }
//
//        List<JobApplication> filtered = stream.toList();
//
//        // 3️⃣ SORTING
//        Comparator<JobApplication> comparator = switch (sortBy) {
//            case "company"     -> Comparator.comparing(JobApplication::getCompanyName);
//            case "status"      -> Comparator.comparing(JobApplication::getStatus);
//            case "location"    -> Comparator.comparing(JobApplication::getLocation);
//            case "source"      -> Comparator.comparing(JobApplication::getSource);
//            case "appliedDate" -> Comparator.comparing(JobApplication::getAppliedDate);
//            default            -> Comparator.comparing(JobApplication::getId);
//        };
//
//
//        if (order.equalsIgnoreCase("desc")) {
//            comparator = comparator.reversed();
//        }
//
//        filtered = filtered.stream().sorted(comparator).toList();
//
//        // 4️⃣ PAGINATION
//        int start = page * size;
//        int end = Math.min(start + size, filtered.size());
//        List<JobApplication> pageContent =
//                start >= filtered.size() ? List.of() : filtered.subList(start, end);
//
//        // 5️⃣ MAP TO RESPONSE DTO
//        List<JobApplicationResponseDTO> content =
//                JobApplicationMapper.toResponseList(pageContent);
//
//        // 6️⃣ RETURN PAGED RESPONSE DTO
//        return JobApplicationPageResponseDTO.builder()
//                .jobs(content)
//                .page(page)
//                .size(size)
//                .total(filtered.size())
//                .build();
//    }
//
//
//}


    @Override
    public JobApplicationPageResponseDTO advancedFilter(
            JobFilterDTO filter,
            int page,
            int size,
            String sortBy,
            String order
    ){
        Pageable pageable = PageRequest.of(
                page,
                size,
                order.equalsIgnoreCase("desc")
                        ? Sort.by(sortBy).descending()
                        : Sort.by(sortBy).ascending()
        );

        var spec = JobApplicationSpecification.filter(filter);

        var result = repository.findAll(spec, pageable);

        List<JobApplicationResponseDTO> dtos= result.getContent()
                .stream()
                .map(JobApplicationMapper::toResponse)
                .toList();

        return JobApplicationPageResponseDTO.builder()
                .jobs(dtos)
                .page(result.getNumber())
                .size(result.getSize())
                .total(result.getTotalElements())
                .build();
    }

    @Override
    public List<JobStatusHistoryDTO> getHistory(Long id) {
        var history = statusHistory.findByJobApplicationIdOrderByChangedAtDesc(id);

        return history.stream()
                .map(job -> new JobStatusHistoryDTO(
                        job.getOldStatus(),
                        job.getNewStatus(),
                        job.getChangedAt()
                )).toList();
    }

    @Override
    @Transactional
    public JobApplicationResponseDTO undoLastStatus(Long id) {
        JobApplication job = repository.findById(id)
                .orElseThrow(() -> new NotFoundException("Job not found"));

        List<JobStatusHistory> history = statusHistory.findByJobApplicationIdOrderByChangedAtDesc(id);

        if(history.isEmpty()){
            throw new BadRequestException("No status history to undo");
        }

        JobStatusHistory lastChange = history.get(0);

        job.setStatus(lastChange.getOldStatus());
        job.setUpdatedDate(LocalDate.now());

        statusHistory.delete(lastChange);

        return JobApplicationMapper.toResponse(job);
    }
}