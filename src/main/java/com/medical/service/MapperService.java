package com.medical.service;

import com.medical.dto.*;
import com.medical.entity.*;
import com.medical.repository.*;
import jakarta.persistence.EntityNotFoundException;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

@Service
@RequiredArgsConstructor
public class MapperService {

    private final DoctorRepository doctorRepository;
    private final PatientRepository patientRepository;
    private final DiagnosisRepository diagnosisRepository;
    private final ExaminationRepository examinationRepository;

    public DoctorDTO toDTO(Doctor doctor) {
        DoctorDTO dto = new DoctorDTO();
        dto.setId(doctor.getId());
        dto.setUniqueId(doctor.getUniqueId());
        dto.setName(doctor.getName());
        dto.setSpecialty(doctor.getSpecialty());
        dto.setGP(doctor.isGP());
        return dto;
    }

    public Doctor toEntity(DoctorDTO dto) {
        Doctor doctor = new Doctor();
        doctor.setId(dto.getId());
        doctor.setUniqueId(dto.getUniqueId());
        doctor.setName(dto.getName());
        doctor.setSpecialty(dto.getSpecialty());
        doctor.setGP(dto.isGP());
        return doctor;
    }

    public PatientDTO toDTO(Patient patient) {
        PatientDTO dto = new PatientDTO();
        dto.setId(patient.getId());
        dto.setName(patient.getName());
        dto.setEgn(patient.getEgn());
        dto.setInsured(patient.isInsured());
        if (patient.getPersonalDoctor() != null) {
            dto.setPersonalDoctorId(patient.getPersonalDoctor().getId());
            dto.setPersonalDoctorName(patient.getPersonalDoctor().getName());
        }
        return dto;
    }

    public Patient toEntity(PatientDTO dto) {
        Patient patient = new Patient();
        patient.setId(dto.getId());
        patient.setName(dto.getName());
        patient.setEgn(dto.getEgn());
        patient.setInsured(dto.isInsured());
        if (dto.getPersonalDoctorId() != null) {
            Doctor doctor = doctorRepository.findById(dto.getPersonalDoctorId())
                    .orElseThrow(() -> new EntityNotFoundException(
                            "Doctor not found with id: " + dto.getPersonalDoctorId()));
            patient.setPersonalDoctor(doctor);
        }
        return patient;
    }

    public DiagnosisDTO toDTO(Diagnosis diagnosis) {
        DiagnosisDTO dto = new DiagnosisDTO();
        dto.setId(diagnosis.getId());
        dto.setCode(diagnosis.getCode());
        dto.setDescription(diagnosis.getDescription());
        return dto;
    }

    public Diagnosis toEntity(DiagnosisDTO dto) {
        Diagnosis diagnosis = new Diagnosis();
        diagnosis.setId(dto.getId());
        diagnosis.setCode(dto.getCode());
        diagnosis.setDescription(dto.getDescription());
        return diagnosis;
    }

    public ExaminationDTO toDTO(Examination examination) {
        ExaminationDTO dto = new ExaminationDTO();
        dto.setId(examination.getId());
        dto.setDate(examination.getDate());
        dto.setTreatment(examination.getTreatment());
        dto.setFee(examination.getFee());
        dto.setPrice(examination.getPrice());
        if (examination.getDoctor() != null) {
            dto.setDoctorId(examination.getDoctor().getId());
            dto.setDoctorName(examination.getDoctor().getName());
        }
        if (examination.getPatient() != null) {
            dto.setPatientId(examination.getPatient().getId());
            dto.setPatientName(examination.getPatient().getName());
        }
        if (examination.getDiagnosis() != null) {
            dto.setDiagnosisId(examination.getDiagnosis().getId());
            dto.setDiagnosisCode(examination.getDiagnosis().getCode());
            dto.setDiagnosisDescription(examination.getDiagnosis().getDescription());
        }
        return dto;
    }

    public Examination toEntity(ExaminationDTO dto) {
        Examination examination = new Examination();
        examination.setId(dto.getId());
        examination.setDate(dto.getDate());
        examination.setTreatment(dto.getTreatment());
        examination.setFee(dto.getFee());
        if (dto.getDoctorId() != null) {
            examination.setDoctor(doctorRepository.findById(dto.getDoctorId())
                    .orElseThrow(() -> new EntityNotFoundException(
                            "Doctor not found with id: " + dto.getDoctorId())));
        }
        if (dto.getPatientId() != null) {
            examination.setPatient(patientRepository.findById(dto.getPatientId())
                    .orElseThrow(() -> new EntityNotFoundException(
                            "Patient not found with id: " + dto.getPatientId())));
        }
        if (dto.getDiagnosisId() != null) {
            examination.setDiagnosis(diagnosisRepository.findById(dto.getDiagnosisId())
                    .orElseThrow(() -> new EntityNotFoundException(
                            "Diagnosis not found with id: " + dto.getDiagnosisId())));
        }
        return examination;
    }

    public SickLeaveDTO toDTO(SickLeave sickLeave) {
        SickLeaveDTO dto = new SickLeaveDTO();
        dto.setId(sickLeave.getId());
        dto.setStartDate(sickLeave.getStartDate());
        dto.setDays(sickLeave.getDays());
        if (sickLeave.getExamination() != null) {
            Examination exam = sickLeave.getExamination();
            dto.setExaminationId(exam.getId());
            dto.setExaminationDate(exam.getDate());
            if (exam.getPatient() != null)   dto.setPatientName(exam.getPatient().getName());
            if (exam.getDoctor() != null) {
                dto.setDoctorId(exam.getDoctor().getId());
                dto.setDoctorName(exam.getDoctor().getName());
            }
            if (exam.getDiagnosis() != null) dto.setDiagnosisCode(exam.getDiagnosis().getCode());
        }
        return dto;
    }

    public SickLeave toEntity(SickLeaveDTO dto) {
        SickLeave sickLeave = new SickLeave();
        sickLeave.setId(dto.getId());
        sickLeave.setStartDate(dto.getStartDate());
        sickLeave.setDays(dto.getDays());
        if (dto.getExaminationId() != null) {
            sickLeave.setExamination(examinationRepository.findById(dto.getExaminationId())
                    .orElseThrow(() -> new EntityNotFoundException(
                            "Examination not found with id: " + dto.getExaminationId())));
        }
        return sickLeave;
    }

    public AppUserDTO toDTO(AppUser user) {
        AppUserDTO dto = new AppUserDTO();
        dto.setId(user.getId());
        dto.setUsername(user.getUsername());
        dto.setRole(user.getRole());
        return dto;
    }
}
