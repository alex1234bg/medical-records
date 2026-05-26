package com.medical.controller;

import com.medical.dto.RegistrationDTO;
import com.medical.entity.AppUser;
import com.medical.entity.Doctor;
import com.medical.entity.Patient;
import com.medical.repository.AppUserRepository;
import com.medical.repository.DoctorRepository;
import com.medical.repository.PatientRepository;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Controller;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.ui.Model;
import org.springframework.validation.BindingResult;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.ModelAttribute;
import org.springframework.web.bind.annotation.PostMapping;

@Controller
@RequiredArgsConstructor
public class AuthController {

    private final AppUserRepository appUserRepository;
    private final PatientRepository patientRepository;
    private final DoctorRepository  doctorRepository;
    private final PasswordEncoder   passwordEncoder;

    @GetMapping("/login")
    public String loginPage() {
        return "auth/login";
    }

    @GetMapping("/register")
    public String showRegisterForm(Model model) {
        model.addAttribute("registration", new RegistrationDTO());
        model.addAttribute("doctors", doctorRepository.findAll());
        model.addAttribute("unlinkedDoctors", doctorRepository.findUnlinkedDoctors());
        model.addAttribute("unlinkedPatients", patientRepository.findUnlinkedPatients());
        return "auth/register";
    }

    @Transactional
    @PostMapping("/register")
    public String processRegistration(
            @Valid @ModelAttribute("registration") RegistrationDTO dto,
            BindingResult result,
            Model model) {

        if (!dto.getPassword().equals(dto.getConfirmPassword())) {
            result.rejectValue("confirmPassword", "passwords.mismatch", "Passwords do not match");
        }
        if (appUserRepository.findByUsername(dto.getUsername()).isPresent()) {
            result.rejectValue("username", "username.exists", "Username is already taken");
        }

        if ("PATIENT".equals(dto.getRole())) {
            if (dto.getExistingPatientId() == null) {
                if (dto.getPatientName() == null || dto.getPatientName().isBlank()) {
                    result.rejectValue("patientName", "field.required", "Name is required");
                }
                if (dto.getPatientEgn() == null || !dto.getPatientEgn().matches("[0-9]{10}")) {
                    result.rejectValue("patientEgn", "field.invalid", "EGN must be exactly 10 digits");
                }
            }
        } else if ("DOCTOR".equals(dto.getRole())) {
            if (dto.getExistingDoctorId() == null) {
                result.rejectValue("existingDoctorId", "field.required", "Please select your doctor profile");
            }
        }

        if (result.hasErrors()) {
            model.addAttribute("doctors", doctorRepository.findAll());
            model.addAttribute("unlinkedDoctors", doctorRepository.findUnlinkedDoctors());
            model.addAttribute("unlinkedPatients", patientRepository.findUnlinkedPatients());
            return "auth/register";
        }

        if ("PATIENT".equals(dto.getRole())) {
            Patient patient;
            if (dto.getExistingPatientId() != null) {
                patient = patientRepository.findById(dto.getExistingPatientId())
                        .orElseThrow(() -> new IllegalArgumentException("Patient not found"));
            } else {
                Doctor personalDoctor = dto.getPersonalDoctorId() != null
                        ? doctorRepository.findById(dto.getPersonalDoctorId()).orElse(null)
                        : null;
                patient = patientRepository.save(Patient.builder()
                        .name(dto.getPatientName())
                        .egn(dto.getPatientEgn())
                        .personalDoctor(personalDoctor)
                        .isInsured(dto.isPatientIsInsured())
                        .build());
            }

            appUserRepository.save(AppUser.builder()
                    .username(dto.getUsername())
                    .password(passwordEncoder.encode(dto.getPassword()))
                    .role("PATIENT")
                    .patient(patient)
                    .build());

        } else if ("DOCTOR".equals(dto.getRole())) {
            Doctor doctor = doctorRepository.findById(dto.getExistingDoctorId())
                    .orElseThrow(() -> new IllegalArgumentException("Doctor not found"));

            appUserRepository.save(AppUser.builder()
                    .username(dto.getUsername())
                    .password(passwordEncoder.encode(dto.getPassword()))
                    .role("DOCTOR")
                    .doctor(doctor)
                    .build());
        }

        return "redirect:/login?registered";
    }
}
