package com.medical.repository;

import com.medical.entity.Doctor;
import com.medical.entity.Examination;
import com.medical.entity.Patient;
import com.medical.entity.SickLeave;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import java.util.List;
import java.util.Optional;

public interface SickLeaveRepository extends JpaRepository<SickLeave, Long> {

    Optional<SickLeave> findByExamination(Examination examination);

    List<SickLeave> findByExamination_Patient(Patient patient);

    List<SickLeave> findByExamination_Doctor(Doctor doctor);

    @Query("SELECT COUNT(s) FROM SickLeave s WHERE MONTH(s.startDate) = :month AND YEAR(s.startDate) = :year")
    long countByMonth(@Param("month") int month, @Param("year") int year);

    @Query("""
            SELECT s.examination.doctor
            FROM SickLeave s
            GROUP BY s.examination.doctor
            ORDER BY COUNT(s) DESC
            LIMIT 1
            """)
    Optional<Doctor> findDoctorWithMostSickLeaves();

    @Query("""
            SELECT MONTH(s.startDate), YEAR(s.startDate)
            FROM SickLeave s
            GROUP BY YEAR(s.startDate), MONTH(s.startDate)
            ORDER BY COUNT(s) DESC
            LIMIT 1
            """)
    List<Object[]> findMonthWithMostSickLeaves();
}
