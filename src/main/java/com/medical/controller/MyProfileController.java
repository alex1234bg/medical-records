package com.medical.controller;

import com.medical.entity.Doctor;
import com.medical.entity.Patient;
import com.medical.repository.AppUserRepository;
import com.medical.service.DoctorService;
import com.medical.service.MapperService;
import com.medical.service.PatientService;
import jakarta.persistence.EntityNotFoundException;
import lombok.RequiredArgsConstructor;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.servlet.mvc.support.RedirectAttributes;

import java.util.List;

@Controller
@RequestMapping("/my-profile")
@RequiredArgsConstructor
public class MyProfileController {

    private final AppUserRepository appUserRepository;
    private final PatientService    patientService;
    private final DoctorService     doctorService;
    private final MapperService     mapperService;

    @GetMapping
    public String myProfile(Model model) {
        String username = SecurityContextHolder.getContext().getAuthentication().getName();

        Patient patient = appUserRepository.findPatientByUsername(username).orElse(null);

        if (patient == null) {
            model.addAttribute("noRecord", true);
            return "my-profile";
        }

        List<Doctor> gpDoctors = doctorService.findAll().stream()
                .filter(Doctor::isGP)
                .toList();

        model.addAttribute("patient", mapperService.toDTO(patient));
        model.addAttribute("gpDoctors", gpDoctors);
        model.addAttribute("noRecord", false);
        return "my-profile";
    }

    @PostMapping("/update")
    public String update(
            @RequestParam(value = "personalDoctorId", required = false) Long personalDoctorId,
            @RequestParam(value = "insured", defaultValue = "false") boolean insured,
            RedirectAttributes redirectAttributes) {

        String username = SecurityContextHolder.getContext().getAuthentication().getName();

        Patient patient = appUserRepository.findPatientByUsername(username)
                .orElseThrow(() -> new EntityNotFoundException("No patient record linked to current user"));

        Doctor personalDoctor = (personalDoctorId != null) ? doctorService.findById(personalDoctorId) : null;
        patient.setPersonalDoctor(personalDoctor);
        patient.setInsured(insured);
        patientService.save(patient);

        redirectAttributes.addFlashAttribute("successMessage", "Profile updated successfully.");
        return "redirect:/my-profile";
    }
}
