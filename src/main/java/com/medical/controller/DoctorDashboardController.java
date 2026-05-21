package com.medical.controller;

import com.medical.entity.Doctor;
import com.medical.entity.Examination;
import com.medical.entity.SickLeave;
import com.medical.repository.AppUserRepository;
import com.medical.repository.ExaminationRepository;
import com.medical.repository.SickLeaveRepository;
import jakarta.persistence.EntityNotFoundException;
import lombok.RequiredArgsConstructor;
import org.springframework.format.annotation.DateTimeFormat;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.util.List;

@Controller
@RequestMapping("/doctor-dashboard")
@RequiredArgsConstructor
public class DoctorDashboardController {

    private final AppUserRepository appUserRepository;
    private final ExaminationRepository examinationRepository;
    private final SickLeaveRepository sickLeaveRepository;

    @GetMapping
    public String dashboard(
            @RequestParam(required = false) @DateTimeFormat(iso = DateTimeFormat.ISO.DATE) LocalDate from,
            @RequestParam(required = false) @DateTimeFormat(iso = DateTimeFormat.ISO.DATE) LocalDate to,
            Model model) {

        String username = SecurityContextHolder.getContext().getAuthentication().getName();
        Doctor doctor = appUserRepository.findDoctorByUsername(username)
                .orElseThrow(() -> new EntityNotFoundException("No doctor profile linked to this account"));

        List<Examination> examinations;
        if (from != null && to != null) {
            examinations = examinationRepository.findByDoctorAndDateBetween(doctor, from, to);
        } else {
            examinations = examinationRepository.findByDoctor(doctor);
        }

        List<SickLeave> sickLeaves = sickLeaveRepository.findByExamination_Doctor(doctor);

        BigDecimal totalRevenue = examinations.stream()
                .map(e -> e.getFee() != null ? e.getFee() : BigDecimal.ZERO)
                .reduce(BigDecimal.ZERO, BigDecimal::add);

        long uniquePatients = examinations.stream()
                .map(e -> e.getPatient().getId())
                .distinct()
                .count();

        model.addAttribute("doctor", doctor);
        model.addAttribute("examinations", examinations);
        model.addAttribute("sickLeaves", sickLeaves);
        model.addAttribute("totalRevenue", totalRevenue);
        model.addAttribute("uniquePatients", uniquePatients);
        model.addAttribute("from", from);
        model.addAttribute("to", to);

        return "doctor-dashboard";
    }
}
