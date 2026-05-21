package com.medical.dto;

import jakarta.validation.constraints.DecimalMin;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Size;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.math.BigDecimal;
import java.time.LocalDate;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class ExaminationDTO {

    private Long id;

    @NotNull(message = "Date is required")
    private LocalDate date;

    @NotNull(message = "Doctor is required")
    private Long doctorId;

    private String doctorName;

    @NotNull(message = "Patient is required")
    private Long patientId;

    private String patientName;

    @NotNull(message = "Diagnosis is required")
    private Long diagnosisId;

    private String diagnosisCode;

    private String diagnosisDescription;

    @NotBlank(message = "Treatment is required")
    @Size(min = 3, max = 500, message = "Treatment must be between 3 and 500 characters")
    private String treatment;

    @NotNull(message = "Doctor fee is required")
    @DecimalMin(value = "0.0", message = "Doctor fee cannot be negative")
    private BigDecimal fee;

    private BigDecimal price;
}
