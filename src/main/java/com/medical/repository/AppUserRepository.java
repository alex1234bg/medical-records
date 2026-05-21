package com.medical.repository;

import com.medical.entity.AppUser;
import com.medical.entity.Doctor;
import com.medical.entity.Patient;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.Optional;

public interface AppUserRepository extends JpaRepository<AppUser, Long> {

    Optional<AppUser> findByUsername(String username);

    Optional<AppUser> findByPatient(Patient patient);

    Optional<AppUser> findByDoctor(Doctor doctor);

    @Query("SELECT u.patient FROM AppUser u WHERE u.username = :username")
    Optional<Patient> findPatientByUsername(@Param("username") String username);

    @Query("SELECT u.doctor FROM AppUser u WHERE u.username = :username")
    Optional<Doctor> findDoctorByUsername(@Param("username") String username);
}
