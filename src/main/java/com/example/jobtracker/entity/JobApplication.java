package com.example.jobtracker.entity;

import jakarta.persistence.*;
import lombok.*;
import java.time.LocalDate;

@Entity
@Table(name = "job_applications")
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class JobApplication {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    private String companyName;

    private String position;

    private String status;

    private LocalDate appliedDate;

    private LocalDate updatedDate;

    private String location;

    private String source;

    @Column(length = 500)
    private String notes;

    @Version
    private Long version;


    // Optional: JPA lifecycle hooks so updatedDate is always set by backend
    @PrePersist
    public void onCreate() {
        // If client doesn’t send appliedDate, default to today
        if (this.appliedDate == null) {
            this.appliedDate = LocalDate.now();
        }
        this.updatedDate = LocalDate.now();
    }

    @PreUpdate
    public void onUpdate() {
        this.updatedDate = LocalDate.now();
    }
}
