package com.medical.service;

import com.medical.entity.Doctor;
import com.medical.entity.Examination;
import com.medical.entity.Patient;
import com.medical.repository.ExaminationRepository;
import com.medical.repository.SickLeaveRepository;
import jakarta.persistence.EntityNotFoundException;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.math.BigDecimal;
import java.util.List;

@Service
@RequiredArgsConstructor
public class ExaminationService {

    private final ExaminationRepository examinationRepository;
    private final SickLeaveRepository sickLeaveRepository;

    public List<Examination> findAll() {
        return examinationRepository.findAll();
    }

    public List<Examination> findByPatient(Patient patient) {
        return examinationRepository.findByPatient(patient);
    }

    public List<Examination> findByDoctor(Doctor doctor) {
        return examinationRepository.findByDoctor(doctor);
    }

    public Examination findById(Long id) {
        return examinationRepository.findById(id)
                .orElseThrow(() -> new EntityNotFoundException("Examination not found with id: " + id));
    }

    @Transactional
    public Examination save(Examination examination) {
        applyPricePolicy(examination);
        return examinationRepository.save(examination);
    }

    @Transactional
    public Examination update(Long id, Examination updated) {
        Examination existing = findById(id);
        existing.setDate(updated.getDate());
        existing.setDoctor(updated.getDoctor());
        existing.setPatient(updated.getPatient());
        existing.setDiagnosis(updated.getDiagnosis());
        existing.setTreatment(updated.getTreatment());
        existing.setFee(updated.getFee());
        applyPricePolicy(existing);
        return examinationRepository.save(existing);
    }

    @Transactional
    public void delete(Long id) {
        Examination examination = findById(id);
        sickLeaveRepository.findByExamination(examination)
                .ifPresent(sickLeaveRepository::delete);
        examinationRepository.delete(examination);
    }

    private void applyPricePolicy(Examination examination) {
        if (examination.getPatient() != null && examination.getPatient().isInsured()) {
            examination.setPrice(BigDecimal.ZERO);
        } else {
            examination.setPrice(examination.getFee());
        }
    }
}
