package com.medical.service;

import com.medical.entity.Examination;
import com.medical.entity.Patient;
import com.medical.entity.SickLeave;
import com.medical.repository.AppUserRepository;
import com.medical.repository.ExaminationRepository;
import com.medical.repository.PatientRepository;
import com.medical.repository.SickLeaveRepository;
import jakarta.persistence.EntityNotFoundException;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.util.List;
import java.util.Optional;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class PatientServiceTest {

    @Mock
    private PatientRepository patientRepository;

    @Mock
    private ExaminationRepository examinationRepository;

    @Mock
    private SickLeaveRepository sickLeaveRepository;

    @Mock
    private AppUserRepository appUserRepository;

    @InjectMocks
    private PatientService patientService;

    private Patient patient;

    @BeforeEach
    void setUp() {
        patient = Patient.builder()
                .id(1L)
                .name("Jane Doe")
                .egn("1234567890")
                .isInsured(true)
                .build();
    }

    @Test
    void findAll_returnsAllPatients() {
        when(patientRepository.findAll()).thenReturn(List.of(patient));

        List<Patient> result = patientService.findAll();

        assertThat(result).hasSize(1);
        assertThat(result.get(0).getName()).isEqualTo("Jane Doe");
        verify(patientRepository).findAll();
    }

    @Test
    void findById_existingId_returnsPatient() {
        when(patientRepository.findById(1L)).thenReturn(Optional.of(patient));

        Patient result = patientService.findById(1L);

        assertThat(result.getEgn()).isEqualTo("1234567890");
    }

    @Test
    void findById_nonExistingId_throwsEntityNotFoundException() {
        when(patientRepository.findById(99L)).thenReturn(Optional.empty());

        assertThatThrownBy(() -> patientService.findById(99L))
                .isInstanceOf(EntityNotFoundException.class)
                .hasMessageContaining("99");
    }

    @Test
    void save_delegatesToRepository() {
        when(patientRepository.save(patient)).thenReturn(patient);

        Patient result = patientService.save(patient);

        assertThat(result).isEqualTo(patient);
        verify(patientRepository).save(patient);
    }

    @Test
    void delete_withLinkedExaminationAndSickLeave_cascadesCorrectly() {
        Examination exam = Examination.builder().id(10L).patient(patient).build();
        SickLeave sickLeave = SickLeave.builder().id(5L).examination(exam).build();

        when(patientRepository.findById(1L)).thenReturn(Optional.of(patient));
        when(appUserRepository.findByPatient(patient)).thenReturn(Optional.empty());
        when(examinationRepository.findByPatient(patient)).thenReturn(List.of(exam));
        when(sickLeaveRepository.findByExamination(exam)).thenReturn(Optional.of(sickLeave));

        patientService.delete(1L);

        verify(sickLeaveRepository).delete(sickLeave);
        verify(examinationRepository).deleteAll(List.of(exam));
        verify(patientRepository).delete(patient);
    }

    @Test
    void delete_noExaminations_deletesPatientDirectly() {
        when(patientRepository.findById(1L)).thenReturn(Optional.of(patient));
        when(appUserRepository.findByPatient(patient)).thenReturn(Optional.empty());
        when(examinationRepository.findByPatient(patient)).thenReturn(List.of());

        patientService.delete(1L);

        verify(examinationRepository).deleteAll(List.of());
        verify(patientRepository).delete(patient);
        verifyNoInteractions(sickLeaveRepository);
    }
}
