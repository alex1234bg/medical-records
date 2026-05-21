package com.medical.controller;

import com.medical.service.DiagnosisService;
import com.medical.service.DoctorService;
import com.medical.service.PatientService;
import com.medical.service.StatisticsService;
import lombok.RequiredArgsConstructor;
import org.springframework.format.annotation.DateTimeFormat;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;

import java.time.LocalDate;

@Controller
@RequestMapping("/statistics")
@RequiredArgsConstructor
public class StatisticsController {

    private final StatisticsService statisticsService;
    private final DiagnosisService diagnosisService;
    private final DoctorService doctorService;
    private final PatientService patientService;

    @GetMapping
    public String dashboard(
            @RequestParam(required = false) Long diagnosisId,
            @RequestParam(required = false) Long patientId,
            @RequestParam(required = false) Long doctorId,
            @RequestParam(required = false) @DateTimeFormat(iso = DateTimeFormat.ISO.DATE) LocalDate from,
            @RequestParam(required = false) @DateTimeFormat(iso = DateTimeFormat.ISO.DATE) LocalDate to,
            Model model) {

        model.addAttribute("totalCost", statisticsService.getTotalCostPaidByPatients());
        model.addAttribute("mostCommonDiagnosis", statisticsService.getMostCommonDiagnosis().orElse(null));
        model.addAttribute("topSickLeaveDoctor", statisticsService.getDoctorWithMostSickLeaves().orElse(null));
        model.addAttribute("topSickLeaveMonth", statisticsService.getMonthWithMostSickLeaves().orElse(null));
        model.addAttribute("patientsPerGP", statisticsService.getPatientsPerGP());
        model.addAttribute("costsPerDoctor", statisticsService.getCostsPerDoctor());
        model.addAttribute("visitsPerDoctor", statisticsService.getVisitCountPerDoctor());

        model.addAttribute("allDiagnoses", diagnosisService.findAll());
        model.addAttribute("allPatients", patientService.findAll());
        model.addAttribute("allDoctors", doctorService.findAll());

        if (diagnosisId != null) {
            var diagnosis = diagnosisService.findById(diagnosisId);
            model.addAttribute("selectedDiagnosis", diagnosis);
            model.addAttribute("patientsWithDiagnosis", statisticsService.getPatientsWithDiagnosis(diagnosis));
        }

        if (patientId != null) {
            var patient = patientService.findById(patientId);
            model.addAttribute("selectedPatient", patient);
            model.addAttribute("patientHistory", statisticsService.getPatientVisitHistory(patient));
        }

        if (doctorId != null || (from != null && to != null)) {
            var doctor = doctorId != null ? doctorService.findById(doctorId) : null;
            model.addAttribute("selectedDoctor", doctor);
            model.addAttribute("from", from);
            model.addAttribute("to", to);
            model.addAttribute("examsByPeriod", statisticsService.getExamsByDoctorAndPeriod(doctor, from, to));
        }

        return "statistics/dashboard";
    }

    @GetMapping("/patients-by-diagnosis")
    public String patientsByDiagnosis(
            @RequestParam(required = false) Long diagnosisId,
            Model model) {
        model.addAttribute("diagnoses", diagnosisService.findAll());
        if (diagnosisId != null) {
            var diagnosis = diagnosisService.findById(diagnosisId);
            model.addAttribute("selectedDiagnosis", diagnosis);
            model.addAttribute("patients", statisticsService.getPatientsWithDiagnosis(diagnosis));
        }
        return "statistics/patients-by-diagnosis";
    }

    @GetMapping("/most-common-diagnosis")
    public String mostCommonDiagnosis(Model model) {
        model.addAttribute("diagnosis", statisticsService.getMostCommonDiagnosis().orElse(null));
        return "statistics/most-common-diagnosis";
    }

    @GetMapping("/patients-per-gp")
    public String patientsPerGP(Model model) {
        model.addAttribute("patientsPerGP", statisticsService.getPatientsPerGP());
        return "statistics/patients-per-gp";
    }

    @GetMapping("/costs-patients")
    public String totalCostsPaidByPatients(Model model) {
        model.addAttribute("totalCost", statisticsService.getTotalCostPaidByPatients());
        return "statistics/costs-patients";
    }

    @GetMapping("/costs-per-doctor")
    public String costsPerDoctor(Model model) {
        model.addAttribute("costsPerDoctor", statisticsService.getCostsPerDoctor());
        return "statistics/costs-per-doctor";
    }

    @GetMapping("/visits-per-doctor")
    public String visitCountPerDoctor(Model model) {
        model.addAttribute("visitsPerDoctor", statisticsService.getVisitCountPerDoctor());
        return "statistics/visits-per-doctor";
    }

    @GetMapping("/patient-history")
    public String patientVisitHistory(
            @RequestParam(required = false) Long patientId,
            Model model) {
        model.addAttribute("patients", patientService.findAll());
        if (patientId != null) {
            var patient = patientService.findById(patientId);
            model.addAttribute("selectedPatient", patient);
            model.addAttribute("examinations", statisticsService.getPatientVisitHistory(patient));
        }
        return "statistics/patient-history";
    }

    @GetMapping("/exams-by-doctor-period")
    public String examsByDoctorAndPeriod(
            @RequestParam(required = false) Long doctorId,
            @RequestParam(required = false) @DateTimeFormat(iso = DateTimeFormat.ISO.DATE) LocalDate from,
            @RequestParam(required = false) @DateTimeFormat(iso = DateTimeFormat.ISO.DATE) LocalDate to,
            Model model) {
        model.addAttribute("doctors", doctorService.findAll());
        var doctor = doctorId != null ? doctorService.findById(doctorId) : null;
        model.addAttribute("selectedDoctor", doctor);
        model.addAttribute("from", from);
        model.addAttribute("to", to);
        model.addAttribute("examinations", statisticsService.getExamsByDoctorAndPeriod(doctor, from, to));
        return "statistics/exams-by-doctor-period";
    }

    @GetMapping("/month-most-sick-leaves")
    public String monthWithMostSickLeaves(Model model) {
        model.addAttribute("month", statisticsService.getMonthWithMostSickLeaves().orElse(null));
        return "statistics/month-most-sick-leaves";
    }

    @GetMapping("/doctor-most-sick-leaves")
    public String doctorWithMostSickLeaves(Model model) {
        model.addAttribute("doctor", statisticsService.getDoctorWithMostSickLeaves().orElse(null));
        return "statistics/doctor-most-sick-leaves";
    }
}
