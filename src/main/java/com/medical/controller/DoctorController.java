package com.medical.controller;

import com.medical.dto.DoctorDTO;
import com.medical.service.DoctorService;
import com.medical.service.MapperService;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.validation.BindingResult;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@Controller
@RequestMapping("/doctors")
@RequiredArgsConstructor
public class DoctorController {

    private final DoctorService doctorService;
    private final MapperService mapperService;

    @GetMapping
    public String listDoctors(Model model) {
        List<DoctorDTO> doctors = doctorService.findAll().stream()
                .map(mapperService::toDTO)
                .toList();
        model.addAttribute("doctors", doctors);
        return "doctors/list";
    }

    @GetMapping("/new")
    public String showAddForm(Model model) {
        model.addAttribute("doctor", new DoctorDTO());
        return "doctors/form";
    }

    @PostMapping("/save")
    public String saveDoctor(@Valid @ModelAttribute("doctor") DoctorDTO dto, BindingResult result) {
        if (result.hasErrors()) {
            return "doctors/form";
        }
        doctorService.save(mapperService.toEntity(dto));
        return "redirect:/doctors";
    }

    @GetMapping("/{id}/edit")
    public String showEditForm(@PathVariable Long id, Model model) {
        model.addAttribute("doctor", mapperService.toDTO(doctorService.findById(id)));
        return "doctors/form";
    }

    @PostMapping("/{id}/update")
    public String updateDoctor(@PathVariable Long id, @Valid @ModelAttribute("doctor") DoctorDTO dto,
                               BindingResult result) {
        if (result.hasErrors()) {
            return "doctors/form";
        }
        doctorService.update(id, mapperService.toEntity(dto));
        return "redirect:/doctors";
    }

    @PostMapping("/{id}/delete")
    public String deleteDoctor(@PathVariable Long id) {
        doctorService.delete(id);
        return "redirect:/doctors";
    }
}
