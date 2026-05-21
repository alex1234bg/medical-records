package com.medical.service;

import com.medical.entity.Patient;
import com.medical.entity.SickLeave;
import com.medical.repository.SickLeaveRepository;
import jakarta.persistence.EntityNotFoundException;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

@Service
@RequiredArgsConstructor
public class SickLeaveService {

    private final SickLeaveRepository sickLeaveRepository;

    public List<SickLeave> findAll() {
        return sickLeaveRepository.findAll();
    }

    public List<SickLeave> findByPatient(Patient patient) {
        return sickLeaveRepository.findByExamination_Patient(patient);
    }

    public SickLeave findById(Long id) {
        return sickLeaveRepository.findById(id)
                .orElseThrow(() -> new EntityNotFoundException("SickLeave not found with id: " + id));
    }

    @Transactional
    public SickLeave save(SickLeave sickLeave) {
        return sickLeaveRepository.save(sickLeave);
    }

    @Transactional
    public SickLeave update(Long id, SickLeave updated) {
        SickLeave existing = findById(id);
        existing.setStartDate(updated.getStartDate());
        existing.setDays(updated.getDays());
        existing.setExamination(updated.getExamination());
        return sickLeaveRepository.save(existing);
    }

    @Transactional
    public void delete(Long id) {
        sickLeaveRepository.delete(findById(id));
    }
}
