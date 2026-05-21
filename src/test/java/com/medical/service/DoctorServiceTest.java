package com.medical.service;

import com.medical.entity.Doctor;
import com.medical.repository.DoctorRepository;
import com.medical.repository.ExaminationRepository;
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
class DoctorServiceTest {

    @Mock
    private DoctorRepository doctorRepository;

    @Mock
    private ExaminationRepository examinationRepository;

    @Mock
    private SickLeaveRepository sickLeaveRepository;

    @InjectMocks
    private DoctorService doctorService;

    private Doctor doctor;

    @BeforeEach
    void setUp() {
        doctor = Doctor.builder()
                .id(1L)
                .uniqueId("12345")
                .name("John Smith")
                .specialty("General Practice")
                .isGP(true)
                .build();
    }

    @Test
    void findAll_returnsAllDoctors() {
        when(doctorRepository.findAll()).thenReturn(List.of(doctor));

        List<Doctor> result = doctorService.findAll();

        assertThat(result).hasSize(1);
        assertThat(result.get(0).getName()).isEqualTo("John Smith");
        verify(doctorRepository).findAll();
    }

    @Test
    void findById_existingId_returnsDoctor() {
        when(doctorRepository.findById(1L)).thenReturn(Optional.of(doctor));

        Doctor result = doctorService.findById(1L);

        assertThat(result.getId()).isEqualTo(1L);
        assertThat(result.getUniqueId()).isEqualTo("12345");
    }

    @Test
    void findById_nonExistingId_throwsEntityNotFoundException() {
        when(doctorRepository.findById(99L)).thenReturn(Optional.empty());

        assertThatThrownBy(() -> doctorService.findById(99L))
                .isInstanceOf(EntityNotFoundException.class)
                .hasMessageContaining("99");
    }

    @Test
    void save_delegatesToRepository() {
        when(doctorRepository.save(doctor)).thenReturn(doctor);

        Doctor result = doctorService.save(doctor);

        assertThat(result).isEqualTo(doctor);
        verify(doctorRepository).save(doctor);
    }

    @Test
    void delete_noExaminations_deletesDoctor() {
        when(doctorRepository.findById(1L)).thenReturn(Optional.of(doctor));
        when(examinationRepository.findByDoctor(doctor)).thenReturn(List.of());

        doctorService.delete(1L);

        verify(examinationRepository).deleteAll(List.of());
        verify(doctorRepository).delete(doctor);
        verifyNoInteractions(sickLeaveRepository);
    }
}
