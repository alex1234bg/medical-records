package com.medical.entity;

import jakarta.persistence.*;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Pattern;
import jakarta.validation.constraints.Size;
import lombok.*;

@Entity
@Table(name = "doctors")
@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class Doctor {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @NotBlank(message = "Unique ID is required")
    @Pattern(regexp = "[0-9]+", message = "Unique ID must contain only numbers")
    @Column(unique = true, nullable = false)
    private String uniqueId;

    @NotBlank(message = "Name is required")
    @Size(min = 2, max = 100, message = "Name must be between 2 and 100 characters")
    @Pattern(regexp = "[a-zA-Z\\s]+", message = "Name must contain only letters and spaces")
    @Column(nullable = false)
    private String name;

    @NotBlank(message = "Specialty is required")
    @Pattern(regexp = "[a-zA-Z\\s]+", message = "Specialty must contain only letters and spaces")
    private String specialty;

    @Column(nullable = false)
    private boolean isGP;
}
