package com.medical.service;

import com.medical.entity.Examination;
import com.medical.entity.Patient;
import com.medical.repository.ExaminationRepository;
import com.medical.repository.SickLeaveRepository;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.math.BigDecimal;
import java.time.LocalDate;

import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
class ExaminationServiceTest {

    @Mock
    private ExaminationRepository examinationRepository;

    @Mock
    private SickLeaveRepository sickLeaveRepository;

    @InjectMocks
    private ExaminationService examinationService;

    @Test
    void save_insuredPatient_priceIsSetToZero() {
        Patient insuredPatient = Patient.builder()
                .id(1L)
                .name("Jane Doe")
                .egn("1234567890")
                .isInsured(true)
                .build();

        Examination examination = Examination.builder()
                .date(LocalDate.now())
                .patient(insuredPatient)
                .price(new BigDecimal("50.00"))
                .treatment("Rest and fluids")
                .build();

        when(examinationRepository.save(any(Examination.class))).thenAnswer(inv -> inv.getArgument(0));

        Examination saved = examinationService.save(examination);

        assertThat(saved.getPrice()).isEqualByComparingTo(BigDecimal.ZERO);
    }

    @Test
    void save_uninsuredPatient_priceRemainsAsSet() {
        Patient uninsuredPatient = Patient.builder()
                .id(2L)
                .name("John Uninsured")
                .egn("0987654321")
                .isInsured(false)
                .build();

        Examination examination = Examination.builder()
                .date(LocalDate.now())
                .patient(uninsuredPatient)
                .fee(new BigDecimal("75.50"))
                .treatment("Medication prescribed")
                .build();

        when(examinationRepository.save(any(Examination.class))).thenAnswer(inv -> inv.getArgument(0));

        Examination saved = examinationService.save(examination);

        assertThat(saved.getPrice()).isEqualByComparingTo(new BigDecimal("75.50"));
    }

    @Test
    void save_nullPatient_priceRemainsAsSet() {
        Examination examination = Examination.builder()
                .date(LocalDate.now())
                .patient(null)
                .fee(new BigDecimal("30.00"))
                .treatment("Checkup")
                .build();

        when(examinationRepository.save(any(Examination.class))).thenAnswer(inv -> inv.getArgument(0));

        Examination saved = examinationService.save(examination);

        assertThat(saved.getPrice()).isEqualByComparingTo(new BigDecimal("30.00"));
    }
}
