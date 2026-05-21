package com.medical.dto;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Pattern;
import jakarta.validation.constraints.Size;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class PatientDTO {

    private Long id;

    @NotBlank(message = "Name is required")
    @Size(min = 2, max = 100, message = "Name must be between 2 and 100 characters")
    @Pattern(regexp = "[a-zA-Z\\s]+", message = "Name must contain only letters and spaces")
    private String name;

    @NotBlank(message = "EGN is required")
    @Size(min = 10, max = 10, message = "EGN must be exactly 10 digits")
    @Pattern(regexp = "[0-9]{10}", message = "EGN must contain exactly 10 digits")
    private String egn;

    private Long personalDoctorId;

    private String personalDoctorName;

    private boolean isInsured;
}
