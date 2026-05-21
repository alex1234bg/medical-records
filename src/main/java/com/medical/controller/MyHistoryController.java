package com.medical.controller;

import com.medical.dto.ExaminationDTO;
import com.medical.dto.PatientDTO;
import com.medical.dto.SickLeaveDTO;
import com.medical.entity.Patient;
import com.medical.repository.AppUserRepository;
import com.medical.service.ExaminationService;
import com.medical.service.MapperService;
import com.medical.service.SickLeaveService;
import lombok.RequiredArgsConstructor;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;

import java.util.List;

@Controller
@RequestMapping("/my-history")
@RequiredArgsConstructor
public class MyHistoryController {

    private final AppUserRepository  appUserRepository;
    private final ExaminationService examinationService;
    private final SickLeaveService   sickLeaveService;
    private final MapperService      mapperService;

    @GetMapping
    public String myHistory(Model model) {
        String username = SecurityContextHolder.getContext().getAuthentication().getName();

        Patient patient = appUserRepository.findPatientByUsername(username)
                .orElse(null);

        if (patient == null) {
            model.addAttribute("noRecord", true);
            return "my-history";
        }

        PatientDTO patientDTO = mapperService.toDTO(patient);

        List<ExaminationDTO> examinations = examinationService.findByPatient(patient).stream()
                .map(mapperService::toDTO)
                .toList();

        List<SickLeaveDTO> sickLeaves = sickLeaveService.findByPatient(patient).stream()
                .map(mapperService::toDTO)
                .toList();

        model.addAttribute("patient", patientDTO);
        model.addAttribute("examinations", examinations);
        model.addAttribute("sickLeaves", sickLeaves);
        model.addAttribute("noRecord", false);
        return "my-history";
    }
}
