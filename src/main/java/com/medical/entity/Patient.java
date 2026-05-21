package com.medical.entity;

import jakarta.persistence.*;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Pattern;
import jakarta.validation.constraints.Size;
import lombok.*;

@Entity
@Table(name = "patients")
@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class Patient {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @NotBlank(message = "Name is required")
    @Size(min = 2, max = 100, message = "Name must be between 2 and 100 characters")
    @Pattern(regexp = "[a-zA-Z\\s]+", message = "Name must contain only letters and spaces")
    @Column(nullable = false)
    private String name;

    @NotBlank(message = "EGN is required")
    @Size(min = 10, max = 10, message = "EGN must be exactly 10 digits")
    @Pattern(regexp = "[0-9]{10}", message = "EGN must contain exactly 10 digits")
    @Column(unique = true, nullable = false)
    private String egn;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "personal_doctor_id")
    private Doctor personalDoctor;

    @Column(nullable = false)
    private boolean isInsured;
}
