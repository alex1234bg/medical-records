package com.medical.service;

import com.medical.entity.Examination;
import com.medical.entity.Patient;
import com.medical.repository.AppUserRepository;
import com.medical.repository.ExaminationRepository;
import com.medical.repository.PatientRepository;
import com.medical.repository.SickLeaveRepository;
import jakarta.persistence.EntityNotFoundException;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

@Service
@RequiredArgsConstructor
public class PatientService {

    private final PatientRepository patientRepository;
    private final ExaminationRepository examinationRepository;
    private final SickLeaveRepository sickLeaveRepository;
    private final AppUserRepository appUserRepository;

    public List<Patient> findAll() {
        return patientRepository.findAll();
    }

    public Patient findById(Long id) {
        return patientRepository.findById(id)
                .orElseThrow(() -> new EntityNotFoundException("Patient not found with id: " + id));
    }

    @Transactional
    public Patient save(Patient patient) {
        return patientRepository.save(patient);
    }

    @Transactional
    public Patient update(Long id, Patient updated) {
        Patient existing = findById(id);
        existing.setName(updated.getName());
        existing.setEgn(updated.getEgn());
        existing.setPersonalDoctor(updated.getPersonalDoctor());
        existing.setInsured(updated.isInsured());
        return patientRepository.save(existing);
    }

    @Transactional
    public void delete(Long id) {
        Patient patient = findById(id);

        appUserRepository.findByPatient(patient).ifPresent(u -> {
            u.setPatient(null);
            appUserRepository.save(u);
        });

        List<Examination> examinations = examinationRepository.findByPatient(patient);
        for (Examination exam : examinations) {
            sickLeaveRepository.findByExamination(exam)
                    .ifPresent(sickLeaveRepository::delete);
        }
        examinationRepository.deleteAll(examinations);
        patientRepository.delete(patient);
    }

    public boolean isInsured(Long id) {
        return findById(id).isInsured();
    }
}
