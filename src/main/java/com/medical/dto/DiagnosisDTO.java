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
public class DiagnosisDTO {

    private Long id;

    @NotBlank(message = "Code is required")
    @Size(max = 10, message = "Code must be at most 10 characters")
    @Pattern(regexp = "[A-Z0-9]+", message = "Code must contain only uppercase letters and numbers")
    private String code;

    @NotBlank(message = "Description is required")
    @Size(min = 3, max = 255, message = "Description must be between 3 and 255 characters")
    private String description;
}
