package com.medical.controller;

import com.medical.dto.ExaminationDTO;
import com.medical.dto.SickLeaveDTO;
import com.medical.entity.Doctor;
import com.medical.repository.AppUserRepository;
import com.medical.service.ExaminationService;
import com.medical.service.MapperService;
import com.medical.service.SickLeaveService;
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
@RequestMapping("/sick-leaves")
@RequiredArgsConstructor
public class SickLeaveController {

    private final SickLeaveService   sickLeaveService;
    private final ExaminationService examinationService;
    private final MapperService      mapperService;
    private final AppUserRepository  appUserRepository;

    @GetMapping
    public String listSickLeaves(Model model) {
        Authentication auth = SecurityContextHolder.getContext().getAuthentication();
        boolean isPatient = hasRole(auth, "ROLE_PATIENT");
        boolean isAdmin   = hasRole(auth, "ROLE_ADMIN");
        Long linkedDoctorId = resolveLinkedDoctorId(auth);

        List<SickLeaveDTO> sickLeaves;
        if (isPatient) {
            sickLeaves = appUserRepository.findPatientByUsername(auth.getName())
                    .map(p -> sickLeaveService.findByPatient(p).stream()
                            .map(mapperService::toDTO).toList())
                    .orElse(List.of());
        } else {
            sickLeaves = sickLeaveService.findAll().stream()
                    .map(mapperService::toDTO).toList();
        }

        model.addAttribute("sickLeaves", sickLeaves);
        model.addAttribute("isPatient", isPatient);
        model.addAttribute("isAdmin", isAdmin);
        model.addAttribute("linkedDoctorId", linkedDoctorId);
        return "sick-leaves/list";
    }

    @GetMapping("/new")
    public String showAddForm(Model model) {
        model.addAttribute("sickLeave", new SickLeaveDTO());
        populateFormModel(model);
        return "sick-leaves/form";
    }

    @PostMapping("/save")
    public String saveSickLeave(@Valid @ModelAttribute("sickLeave") SickLeaveDTO dto,
                                BindingResult result, Model model) {
        if (result.hasErrors()) {
            populateFormModel(model);
            return "sick-leaves/form";
        }
        sickLeaveService.save(mapperService.toEntity(dto));
        return "redirect:/sick-leaves";
    }

    @GetMapping("/{id}/edit")
    public String showEditForm(@PathVariable Long id, Model model) {
        Authentication auth = SecurityContextHolder.getContext().getAuthentication();
        SickLeaveDTO dto = mapperService.toDTO(sickLeaveService.findById(id));
        checkDoctorOwnership(auth, dto.getDoctorId());

        model.addAttribute("sickLeave", dto);
        populateFormModel(model);
        return "sick-leaves/form";
    }

    @PostMapping("/{id}/update")
    public String updateSickLeave(@PathVariable Long id,
                                  @Valid @ModelAttribute("sickLeave") SickLeaveDTO dto,
                                  BindingResult result, Model model) {
        Authentication auth = SecurityContextHolder.getContext().getAuthentication();
        SickLeaveDTO persisted = mapperService.toDTO(sickLeaveService.findById(id));
        checkDoctorOwnership(auth, persisted.getDoctorId());

        if (result.hasErrors()) {
            populateFormModel(model);
            return "sick-leaves/form";
        }
        sickLeaveService.update(id, mapperService.toEntity(dto));
        return "redirect:/sick-leaves";
    }

    @PostMapping("/{id}/delete")
    public String deleteSickLeave(@PathVariable Long id) {
        Authentication auth = SecurityContextHolder.getContext().getAuthentication();
        SickLeaveDTO persisted = mapperService.toDTO(sickLeaveService.findById(id));
        checkDoctorOwnership(auth, persisted.getDoctorId());

        sickLeaveService.delete(id);
        return "redirect:/sick-leaves";
    }

    private void populateFormModel(Model model) {
        Authentication auth = SecurityContextHolder.getContext().getAuthentication();
        List<ExaminationDTO> examinations;
        if (hasRole(auth, "ROLE_DOCTOR")) {
            examinations = appUserRepository.findDoctorByUsername(auth.getName())
                    .map(d -> examinationService.findByDoctor(d).stream()
                            .map(mapperService::toDTO).toList())
                    .orElse(List.of());
        } else {
            examinations = examinationService.findAll().stream()
                    .map(mapperService::toDTO).toList();
        }
        model.addAttribute("examinations", examinations);
    }

    private void checkDoctorOwnership(Authentication auth, Long sickLeaveDoctorId) {
        if (hasRole(auth, "ROLE_ADMIN")) return;
        if (hasRole(auth, "ROLE_DOCTOR")) {
            Long myId = resolveLinkedDoctorId(auth);
            if (myId == null || !myId.equals(sickLeaveDoctorId)) {
                throw new AccessDeniedException("You can only edit sick leaves linked to your own examinations");
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
