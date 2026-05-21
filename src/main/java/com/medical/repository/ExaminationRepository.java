package com.medical.repository;

import com.medical.entity.Diagnosis;
import com.medical.entity.Doctor;
import com.medical.entity.Examination;
import com.medical.entity.Patient;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.util.List;
import java.util.Optional;

public interface ExaminationRepository extends JpaRepository<Examination, Long> {

    List<Examination> findByDoctor(Doctor doctor);

    List<Examination> findByPatient(Patient patient);

    List<Examination> findByDateBetween(LocalDate from, LocalDate to);

    List<Examination> findByDiagnosis(Diagnosis diagnosis);

    List<Examination> findByDoctorAndDateBetween(Doctor doctor, LocalDate from, LocalDate to);

    @Query("SELECT e.diagnosis FROM Examination e GROUP BY e.diagnosis ORDER BY COUNT(e) DESC LIMIT 1")
    Optional<Diagnosis> findMostCommonDiagnosis();

    @Query("SELECT COALESCE(SUM(e.price), 0) FROM Examination e WHERE e.patient.isInsured = false")
    BigDecimal findTotalCostPaidByPatients();

    @Query("SELECT e.doctor, SUM(e.fee) FROM Examination e GROUP BY e.doctor")
    List<Object[]> findCostPerDoctor();

    @Query("SELECT e.doctor, COUNT(e) FROM Examination e GROUP BY e.doctor")
    List<Object[]> findVisitCountPerDoctor();
}
