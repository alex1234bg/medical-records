package com.medical.controller;

import com.medical.dto.DoctorDTO;
import com.medical.dto.PatientDTO;
import com.medical.repository.AppUserRepository;
import com.medical.service.DoctorService;
import com.medical.service.MapperService;
import com.medical.service.PatientService;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.validation.BindingResult;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@Controller
@RequestMapping("/patients")
@RequiredArgsConstructor
public class PatientController {

    private final PatientService      patientService;
    private final DoctorService       doctorService;
    private final MapperService       mapperService;
    private final AppUserRepository   appUserRepository;

    @GetMapping
    public String listPatients(Model model) {
        Authentication auth = SecurityContextHolder.getContext().getAuthentication();
        boolean isPatient = isPatientRole(auth);

        List<PatientDTO> patients;
        if (isPatient) {
            patients = appUserRepository.findPatientByUsername(auth.getName())
                    .map(p -> List.of(mapperService.toDTO(p)))
                    .orElse(List.of());
        } else {
            patients = patientService.findAll().stream()
                    .map(mapperService::toDTO)
                    .toList();
        }

        model.addAttribute("patients", patients);
        model.addAttribute("isPatient", isPatient);
        return "patients/list";
    }

    @GetMapping("/new")
    public String showAddForm(Model model) {
        model.addAttribute("patient", new PatientDTO());
        populateFormModel(model);
        return "patients/form";
    }

    @PostMapping("/save")
    public String savePatient(@Valid @ModelAttribute("patient") PatientDTO dto,
                              BindingResult result, Model model) {
        if (result.hasErrors()) {
            populateFormModel(model);
            return "patients/form";
        }
        patientService.save(mapperService.toEntity(dto));
        return "redirect:/patients";
    }

    @GetMapping("/{id}/edit")
    public String showEditForm(@PathVariable Long id, Model model) {
        model.addAttribute("patient", mapperService.toDTO(patientService.findById(id)));
        populateFormModel(model);
        return "patients/form";
    }

    @PostMapping("/{id}/update")
    public String updatePatient(@PathVariable Long id,
                                @Valid @ModelAttribute("patient") PatientDTO dto,
                                BindingResult result, Model model) {
        if (result.hasErrors()) {
            populateFormModel(model);
            return "patients/form";
        }
        patientService.update(id, mapperService.toEntity(dto));
        return "redirect:/patients";
    }

    @PostMapping("/{id}/delete")
    public String deletePatient(@PathVariable Long id) {
        patientService.delete(id);
        return "redirect:/patients";
    }

    private void populateFormModel(Model model) {
        List<DoctorDTO> doctors = doctorService.findAll().stream()
                .map(mapperService::toDTO)
                .toList();
        model.addAttribute("doctors", doctors);
    }

    private boolean isPatientRole(Authentication auth) {
        return auth.getAuthorities().stream()
                .anyMatch(a -> a.getAuthority().equals("ROLE_PATIENT"));
    }
}
