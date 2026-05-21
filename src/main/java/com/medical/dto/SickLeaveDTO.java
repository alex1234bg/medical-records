package com.medical.dto;

import jakarta.validation.constraints.Max;
import jakarta.validation.constraints.Min;
import jakarta.validation.constraints.NotNull;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.LocalDate;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class SickLeaveDTO {

    private Long id;

    @NotNull(message = "Start date is required")
    private LocalDate startDate;

    @Min(value = 1, message = "Must be at least 1 day")
    @Max(value = 365, message = "Cannot exceed 365 days")
    private int days;

    @NotNull(message = "Examination is required")
    private Long examinationId;

    private LocalDate examinationDate;
    private Long   doctorId;
    private String patientName;
    private String doctorName;
    private String diagnosisCode;
}
