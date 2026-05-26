package com.medical.repository;

import com.medical.entity.Diagnosis;
import com.medical.entity.Doctor;
import com.medical.entity.Patient;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import java.util.List;

public interface PatientRepository extends JpaRepository<Patient, Long> {

    List<Patient> findByPersonalDoctor(Doctor personalDoctor);

    @Query("SELECT DISTINCT e.patient FROM Examination e WHERE e.diagnosis = :diagnosis")
    List<Patient> findByDiagnosis(@Param("diagnosis") Diagnosis diagnosis);

    @Query("SELECT p FROM Patient p WHERE p NOT IN (SELECT u.patient FROM AppUser u WHERE u.patient IS NOT NULL)")
    List<Patient> findUnlinkedPatients();
}
