package com.example.jobtracker.spec;

import com.example.jobtracker.dto.JobFilterDTO;
import com.example.jobtracker.entity.JobApplication;
import org.springframework.data.jpa.domain.Specification;

import java.time.LocalDate;

public class JobApplicationSpecification {

    public static Specification<JobApplication> filter(JobFilterDTO filter){
        return Specification.where(status(filter.status()))
                .and(company(filter.company()))
                .and(location(filter.location()))
                .and(source(filter.source()))
                .and(appliedDateFrom(filter.minDate()))
                .and(appliedDateTo(filter.maxDate()));
    }

    private static Specification<JobApplication> appliedDateTo(LocalDate date) {
        return date == null? null :
                ((root, query, criteriaBuilder) ->
                        criteriaBuilder.lessThanOrEqualTo(root.get("appliedDate"), date));
    }

    private static Specification<JobApplication> appliedDateFrom(LocalDate date) {
        return  date == null ? null :
                ((root, query, criteriaBuilder) ->
                        criteriaBuilder.greaterThanOrEqualTo(root.get("appliedDate"), date));
    }

    private static Specification<JobApplication> source(String value) {
        return (value == null || value.isBlank()) ? null :
                ((root, query, criteriaBuilder) ->
                        criteriaBuilder.like(criteriaBuilder.lower(root.get("source")), "%"+value.toLowerCase()+"%"));
    }

    private static Specification<JobApplication> location(String value) {
        return (value == null || value.isBlank()) ? null :
                ((root, query, criteriaBuilder) ->
                        criteriaBuilder.like(criteriaBuilder.lower(root.get("location")), "%"+value.toLowerCase()+"%"));
    }

    private static Specification<JobApplication> status(String value){
        return (value == null || value.isBlank()) ? null :
                ((root, query, criteriaBuilder) ->
                        criteriaBuilder.equal(criteriaBuilder.lower(root.get("status")), value.toLowerCase()));

    }

    private static Specification<JobApplication> company(String value) {
        return (value == null || value.isBlank()) ? null :
                ((root, query, criteriaBuilder) ->
                        criteriaBuilder.like(criteriaBuilder.lower(root.get("companyName")), "%"+value.toLowerCase()+"%"));
    }

}
