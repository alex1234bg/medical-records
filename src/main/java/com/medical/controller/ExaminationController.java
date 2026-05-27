package com.medical.controller;

import com.medical.dto.DiagnosisDTO;
import com.medical.dto.DoctorDTO;
import com.medical.dto.ExaminationDTO;
import com.medical.dto.PatientDTO;
import com.medical.entity.Doctor;
import com.medical.repository.AppUserRepository;
import com.medical.service.DiagnosisService;
import com.medical.service.DoctorService;
import com.medical.service.ExaminationService;
import com.medical.service.MapperService;
import com.medical.service.PatientService;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.security.access.AccessDeniedException;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.validation.BindingResult;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@Controller
@RequestMapping("/examinations")
@RequiredArgsConstructor
public class ExaminationController {

    private final ExaminationService  examinationService;
    private final DoctorService       doctorService;
    private final PatientService      patientService;
    private final DiagnosisService    diagnosisService;
    private final MapperService       mapperService;
    private final AppUserRepository   appUserRepository;

    @GetMapping
    public String listExaminations(Model model) {
        Authentication auth = SecurityContextHolder.getContext().getAuthentication();
        boolean isPatient = hasRole(auth, "ROLE_PATIENT");
        boolean isAdmin   = hasRole(auth, "ROLE_ADMIN");
        Long linkedDoctorId = resolveLinkedDoctorId(auth);

        List<ExaminationDTO> examinations;
        if (isPatient) {
            examinations = appUserRepository.findPatientByUsername(auth.getName())
                    .map(p -> examinationService.findByPatient(p).stream()
                            .map(mapperService::toDTO).toList())
                    .orElse(List.of());
        } else if (hasRole(auth, "ROLE_DOCTOR")) {
            examinations = appUserRepository.findDoctorByUsername(auth.getName())
                    .map(d -> examinationService.findByDoctor(d).stream()
                            .map(mapperService::toDTO).toList())
                    .orElse(List.of());
        } else {
            examinations = examinationService.findAll().stream()
                    .map(mapperService::toDTO).toList();
        }

        model.addAttribute("examinations", examinations);
        model.addAttribute("isPatient", isPatient);
        model.addAttribute("isAdmin", isAdmin);
        model.addAttribute("linkedDoctorId", linkedDoctorId);
        return "examinations/list";
    }

    @GetMapping("/new")
    public String showAddForm(Model model) {
        Authentication auth = SecurityContextHolder.getContext().getAuthentication();
        ExaminationDTO dto = new ExaminationDTO();

        if (hasRole(auth, "ROLE_DOCTOR")) {
            appUserRepository.findDoctorByUsername(auth.getName())
                    .ifPresent(d -> dto.setDoctorId(d.getId()));
        }

        model.addAttribute("examination", dto);
        populateFormModel(model);
        return "examinations/form";
    }

    @PostMapping("/save")
    public String saveExamination(@Valid @ModelAttribute("examination") ExaminationDTO dto,
                                  BindingResult result, Model model) {
        if (result.hasErrors()) {
            populateFormModel(model);
            return "examinations/form";
        }
        examinationService.save(mapperService.toEntity(dto));
        return "redirect:/examinations";
    }

    @GetMapping("/{id}/edit")
    public String showEditForm(@PathVariable Long id, Model model) {
        Authentication auth = SecurityContextHolder.getContext().getAuthentication();
        ExaminationDTO dto = mapperService.toDTO(examinationService.findById(id));
        checkDoctorOwnership(auth, dto.getDoctorId());

        model.addAttribute("examination", dto);
        populateFormModel(model);
        return "examinations/form";
    }

    @PostMapping("/{id}/update")
    public String updateExamination(@PathVariable Long id,
                                    @Valid @ModelAttribute("examination") ExaminationDTO dto,
                                    BindingResult result, Model model) {
        Authentication auth = SecurityContextHolder.getContext().getAuthentication();
        ExaminationDTO persisted = mapperService.toDTO(examinationService.findById(id));
        checkDoctorOwnership(auth, persisted.getDoctorId());

        if (result.hasErrors()) {
            populateFormModel(model);
            return "examinations/form";
        }
        examinationService.update(id, mapperService.toEntity(dto));
        return "redirect:/examinations";
    }

    @PostMapping("/{id}/delete")
    public String deleteExamination(@PathVariable Long id) {
        Authentication auth = SecurityContextHolder.getContext().getAuthentication();
        ExaminationDTO persisted = mapperService.toDTO(examinationService.findById(id));
        checkDoctorOwnership(auth, persisted.getDoctorId());

        examinationService.delete(id);
        return "redirect:/examinations";
    }

    private void populateFormModel(Model model) {
        List<DoctorDTO> doctors = doctorService.findAll().stream()
                .map(mapperService::toDTO).toList();
        List<PatientDTO> patients = patientService.findAll().stream()
                .map(mapperService::toDTO).toList();
        List<DiagnosisDTO> diagnoses = diagnosisService.findAll().stream()
                .map(mapperService::toDTO).toList();
        model.addAttribute("doctors", doctors);
        model.addAttribute("patients", patients);
        model.addAttribute("diagnoses", diagnoses);
    }

    private void checkDoctorOwnership(Authentication auth, Long examinationDoctorId) {
        if (hasRole(auth, "ROLE_ADMIN")) return;
        if (hasRole(auth, "ROLE_DOCTOR")) {
            Long myId = resolveLinkedDoctorId(auth);
            if (myId == null || !myId.equals(examinationDoctorId)) {
                throw new AccessDeniedException("You can only edit your own examinations");
            }
        }
    }

    private Long resolveLinkedDoctorId(Authentication auth) {
        if (!hasRole(auth, "ROLE_DOCTOR")) return null;
        return appUserRepository.findDoctorByUsername(auth.getName())
                .map(Doctor::getId).orElse(null);
    }

    private boolean hasRole(Authentication auth, String role) {
        return auth.getAuthorities().stream()
                .anyMatch(a -> a.getAuthority().equals(role));
    }
}
