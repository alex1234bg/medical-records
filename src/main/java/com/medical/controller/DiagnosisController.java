package com.medical.controller;

import com.medical.dto.DiagnosisDTO;
import com.medical.service.DiagnosisService;
import com.medical.service.MapperService;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.validation.BindingResult;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@Controller
@RequestMapping("/diagnoses")
@RequiredArgsConstructor
public class DiagnosisController {

    private final DiagnosisService diagnosisService;
    private final MapperService mapperService;

    @GetMapping
    public String listDiagnoses(Model model) {
        List<DiagnosisDTO> diagnoses = diagnosisService.findAll().stream()
                .map(mapperService::toDTO)
                .toList();
        var auth = SecurityContextHolder.getContext().getAuthentication();
        boolean isAdmin = auth.getAuthorities().stream()
                .anyMatch(a -> a.getAuthority().equals("ROLE_ADMIN"));
        model.addAttribute("diagnoses", diagnoses);
        model.addAttribute("isAdmin", isAdmin);
        return "diagnoses/list";
    }

    @GetMapping("/new")
    public String showAddForm(Model model) {
        model.addAttribute("diagnosis", new DiagnosisDTO());
        return "diagnoses/form";
    }

    @PostMapping("/save")
    public String saveDiagnosis(@Valid @ModelAttribute("diagnosis") DiagnosisDTO dto, BindingResult result) {
        if (result.hasErrors()) {
            return "diagnoses/form";
        }
        diagnosisService.save(mapperService.toEntity(dto));
        return "redirect:/diagnoses";
    }

    @GetMapping("/{id}/edit")
    public String showEditForm(@PathVariable Long id, Model model) {
        model.addAttribute("diagnosis", mapperService.toDTO(diagnosisService.findById(id)));
        return "diagnoses/form";
    }

    @PostMapping("/{id}/update")
    public String updateDiagnosis(@PathVariable Long id, @Valid @ModelAttribute("diagnosis") DiagnosisDTO dto,
                                  BindingResult result) {
        if (result.hasErrors()) {
            return "diagnoses/form";
        }
        diagnosisService.update(id, mapperService.toEntity(dto));
        return "redirect:/diagnoses";
    }

    @PostMapping("/{id}/delete")
    public String deleteDiagnosis(@PathVariable Long id) {
        diagnosisService.delete(id);
        return "redirect:/diagnoses";
    }
}
