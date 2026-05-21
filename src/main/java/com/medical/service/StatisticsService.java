package com.medical.service;

import com.medical.entity.Diagnosis;
import com.medical.entity.Doctor;
import com.medical.entity.Examination;
import com.medical.entity.Patient;
import com.medical.repository.DoctorRepository;
import com.medical.repository.ExaminationRepository;
import com.medical.repository.PatientRepository;
import com.medical.repository.SickLeaveRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.math.BigDecimal;
import java.time.YearMonth;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;
import java.util.Optional;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
@Transactional(readOnly = true)
public class StatisticsService {

    private final ExaminationRepository examinationRepository;
    private final PatientRepository patientRepository;
    private final DoctorRepository doctorRepository;
    private final SickLeaveRepository sickLeaveRepository;

    public List<Patient> getPatientsWithDiagnosis(Diagnosis diagnosis) {
        return patientRepository.findByDiagnosis(diagnosis);
    }

    public Optional<Diagnosis> getMostCommonDiagnosis() {
        return examinationRepository.findMostCommonDiagnosis();
    }

    public Map<Doctor, List<Patient>> getPatientsPerGP() {
        return doctorRepository.findAll().stream()
                .filter(Doctor::isGP)
                .collect(Collectors.toMap(
                        gp -> gp,
                        gp -> patientRepository.findByPersonalDoctor(gp),
                        (a, b) -> a,
                        LinkedHashMap::new
                ));
    }

    public BigDecimal getTotalCostPaidByPatients() {
        BigDecimal total = examinationRepository.findTotalCostPaidByPatients();
        return total != null ? total : BigDecimal.ZERO;
    }

    public Map<Doctor, BigDecimal> getCostsPerDoctor() {
        return examinationRepository.findCostPerDoctor().stream()
                .filter(row -> row != null && row.length >= 2 && row[0] != null)
                .collect(Collectors.toMap(
                        row -> (Doctor) row[0],
                        row -> row[1] != null ? (BigDecimal) row[1] : BigDecimal.ZERO,
                        (a, b) -> a,
                        LinkedHashMap::new
                ));
    }

    public Map<Doctor, Long> getVisitCountPerDoctor() {
        return examinationRepository.findVisitCountPerDoctor().stream()
                .filter(row -> row != null && row.length >= 2 && row[0] != null)
                .collect(Collectors.toMap(
                        row -> (Doctor) row[0],
                        row -> row[1] != null ? (Long) row[1] : 0L,
                        (a, b) -> a,
                        LinkedHashMap::new
                ));
    }

    public List<Examination> getPatientVisitHistory(Patient patient) {
        return examinationRepository.findByPatient(patient);
    }

    public List<Examination> getExamsByDoctorAndPeriod(
            Doctor doctor,
            java.time.LocalDate from,
            java.time.LocalDate to) {

        if (doctor != null && from != null && to != null) {
            return examinationRepository.findByDoctorAndDateBetween(doctor, from, to);
        }
        if (doctor != null) {
            return examinationRepository.findByDoctor(doctor);
        }
        if (from != null && to != null) {
            return examinationRepository.findByDateBetween(from, to);
        }
        return examinationRepository.findAll();
    }

    public Optional<YearMonth> getMonthWithMostSickLeaves() {
        List<Object[]> results = sickLeaveRepository.findMonthWithMostSickLeaves();
        if (results.isEmpty()) {
            return Optional.empty();
        }
        Object[] row = results.get(0);
        if (row == null || row.length < 2 || row[0] == null || row[1] == null) {
            return Optional.empty();
        }
        return Optional.of(YearMonth.of(((Number) row[1]).intValue(), ((Number) row[0]).intValue()));
    }

    public Optional<Doctor> getDoctorWithMostSickLeaves() {
        return sickLeaveRepository.findDoctorWithMostSickLeaves();
    }
}
