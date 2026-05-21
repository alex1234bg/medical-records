package com.medical.repository;

import com.medical.entity.Doctor;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.orm.jpa.DataJpaTest;

import java.util.Optional;

import static org.assertj.core.api.Assertions.assertThat;

@DataJpaTest
class DoctorRepositoryTest {

    @Autowired
    private DoctorRepository doctorRepository;

    @Test
    void save_persistsDoctor() {
        Doctor doctor = Doctor.builder()
                .uniqueId("99001")
                .name("Alice Brown")
                .specialty("Cardiology")
                .isGP(false)
                .build();

        Doctor saved = doctorRepository.save(doctor);

        assertThat(saved.getId()).isNotNull();
        assertThat(saved.getName()).isEqualTo("Alice Brown");
    }

    @Test
    void findByUniqueId_existingUniqueId_returnsDoctor() {
        Doctor doctor = Doctor.builder()
                .uniqueId("77042")
                .name("Bob Green")
                .specialty("General Practice")
                .isGP(true)
                .build();

        doctorRepository.save(doctor);

        Optional<Doctor> result = doctorRepository.findByUniqueId("77042");

        assertThat(result).isPresent();
        assertThat(result.get().getName()).isEqualTo("Bob Green");
        assertThat(result.get().isGP()).isTrue();
    }

    @Test
    void findByUniqueId_nonExistingUniqueId_returnsEmpty() {
        Optional<Doctor> result = doctorRepository.findByUniqueId("00000");

        assertThat(result).isEmpty();
    }
}
