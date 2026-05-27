package com.medical.repository;

import com.medical.entity.Doctor;
import com.medical.entity.Patient;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.orm.jpa.DataJpaTest;

import java.util.List;

import static org.assertj.core.api.Assertions.assertThat;

@DataJpaTest
class PatientRepositoryTest {

    @Autowired
    private PatientRepository patientRepository;

    @Autowired
    private DoctorRepository doctorRepository;

    @Test
    void save_persistsPatient() {
        Patient patient = Patient.builder()
                .name("Carol White")
                .egn("1111111111")
                .isInsured(true)
                .build();

        Patient saved = patientRepository.save(patient);

        assertThat(saved.getId()).isNotNull();
        assertThat(saved.getEgn()).isEqualTo("1111111111");
        assertThat(saved.isInsured()).isTrue();
    }

    @Test
    void findByPersonalDoctor_returnsAssignedPatients() {
        Doctor doctor = doctorRepository.save(Doctor.builder()
                .uniqueId("55001")
                .name("Dr House")
                .specialty("General Practice")
                .isGP(true)
                .build());

        patientRepository.save(Patient.builder()
                .name("Patient One")
                .egn("2222222222")
                .personalDoctor(doctor)
                .isInsured(false)
                .build());

        patientRepository.save(Patient.builder()
                .name("Patient Two")
                .egn("3333333333")
                .personalDoctor(doctor)
                .isInsured(true)
                .build());

      
        patientRepository.save(Patient.builder()
                .name("Unassigned")
                .egn("4444444444")
                .isInsured(false)
                .build());

        List<Patient> result = patientRepository.findByPersonalDoctor(doctor);

        assertThat(result).hasSize(2);
        assertThat(result).extracting(Patient::getName)
                .containsExactlyInAnyOrder("Patient One", "Patient Two");
    }

    @Test
    void findByPersonalDoctor_noAssignedPatients_returnsEmpty() {
        Doctor doctor = doctorRepository.save(Doctor.builder()
                .uniqueId("66001")
                .name("Dr Nobody")
                .specialty("Dermatology")
                .isGP(false)
                .build());

        List<Patient> result = patientRepository.findByPersonalDoctor(doctor);

        assertThat(result).isEmpty();
    }
}
