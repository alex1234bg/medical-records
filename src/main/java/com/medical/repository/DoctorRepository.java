package com.medical.repository;

import com.medical.entity.Doctor;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;

import java.util.List;
import java.util.Optional;

public interface DoctorRepository extends JpaRepository<Doctor, Long> {

    Optional<Doctor> findByUniqueId(String uniqueId);

    @Query("SELECT d FROM Doctor d WHERE d NOT IN (SELECT u.doctor FROM AppUser u WHERE u.doctor IS NOT NULL)")
    List<Doctor> findUnlinkedDoctors();
}
