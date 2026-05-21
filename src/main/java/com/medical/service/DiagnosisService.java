package com.medical.service;

import com.medical.entity.Diagnosis;
import com.medical.entity.Examination;
import com.medical.repository.DiagnosisRepository;
import com.medical.repository.ExaminationRepository;
import com.medical.repository.SickLeaveRepository;
import jakarta.persistence.EntityNotFoundException;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

@Service
@RequiredArgsConstructor
public class DiagnosisService {

    private final DiagnosisRepository diagnosisRepository;
    private final ExaminationRepository examinationRepository;
    private final SickLeaveRepository sickLeaveRepository;

    public List<Diagnosis> findAll() {
        return diagnosisRepository.findAll();
    }

    public Diagnosis findById(Long id) {
        return diagnosisRepository.findById(id)
                .orElseThrow(() -> new EntityNotFoundException("Diagnosis not found with id: " + id));
    }

    @Transactional
    public Diagnosis save(Diagnosis diagnosis) {
        return diagnosisRepository.save(diagnosis);
    }

    @Transactional
    public Diagnosis update(Long id, Diagnosis updated) {
        Diagnosis existing = findById(id);
        existing.setCode(updated.getCode());
        existing.setDescription(updated.getDescription());
        return diagnosisRepository.save(existing);
    }

    @Transactional
    public void delete(Long id) {
        Diagnosis diagnosis = findById(id);
        List<Examination> examinations = examinationRepository.findByDiagnosis(diagnosis);
        for (Examination exam : examinations) {
            sickLeaveRepository.findByExamination(exam)
                    .ifPresent(sickLeaveRepository::delete);
        }
        examinationRepository.deleteAll(examinations);
        diagnosisRepository.delete(diagnosis);
    }
}
