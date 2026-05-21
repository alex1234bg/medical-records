package com.medical.entity;

import jakarta.persistence.*;
import jakarta.validation.constraints.Max;
import jakarta.validation.constraints.Min;
import jakarta.validation.constraints.NotNull;
import lombok.*;

import java.time.LocalDate;

@Entity
@Table(name = "sick_leaves")
@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class SickLeave {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @NotNull(message = "Start date is required")
    @Column(nullable = false)
    private LocalDate startDate;

    @Min(value = 1, message = "Must be at least 1 day")
    @Max(value = 365, message = "Cannot exceed 365 days")
    @Column(nullable = false)
    private int days;

    @OneToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "examination_id", nullable = false, unique = true)
    private Examination examination;
}
