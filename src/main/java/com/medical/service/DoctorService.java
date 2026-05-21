package com.medical.service;

import com.medical.entity.Doctor;
import com.medical.entity.Examination;
import com.medical.repository.AppUserRepository;
import com.medical.repository.DoctorRepository;
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
public class DoctorService {

    private final DoctorRepository doctorRepository;
    private final ExaminationRepository examinationRepository;
    private final SickLeaveRepository sickLeaveRepository;
    private final PatientRepository patientRepository;
    private final AppUserRepository appUserRepository;

    public List<Doctor> findAll() {
        return doctorRepository.findAll();
    }

    public Doctor findById(Long id) {
        return doctorRepository.findById(id)
                .orElseThrow(() -> new EntityNotFoundException("Doctor not found with id: " + id));
    }

    @Transactional
    public Doctor save(Doctor doctor) {
        return doctorRepository.save(doctor);
    }

    @Transactional
    public Doctor update(Long id, Doctor updated) {
        Doctor existing = findById(id);
        existing.setUniqueId(updated.getUniqueId());
        existing.setName(updated.getName());
        existing.setSpecialty(updated.getSpecialty());
        existing.setGP(updated.isGP());
        return doctorRepository.save(existing);
    }

    @Transactional
    public void delete(Long id) {
        Doctor doctor = findById(id);

        patientRepository.findByPersonalDoctor(doctor).forEach(p -> {
            p.setPersonalDoctor(null);
            patientRepository.save(p);
        });

        appUserRepository.findByDoctor(doctor).ifPresent(u -> {
            u.setDoctor(null);
            appUserRepository.save(u);
        });

        List<Examination> examinations = examinationRepository.findByDoctor(doctor);
        for (Examination exam : examinations) {
            sickLeaveRepository.findByExamination(exam)
                    .ifPresent(sickLeaveRepository::delete);
        }
        examinationRepository.deleteAll(examinations);
        doctorRepository.delete(doctor);
    }
}
