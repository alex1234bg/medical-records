package com.medical.config;

import com.medical.repository.DiagnosisRepository;
import com.medical.repository.DoctorRepository;
import com.medical.repository.ExaminationRepository;
import com.medical.repository.PatientRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.context.annotation.Configuration;
import org.springframework.format.FormatterRegistry;
import org.springframework.web.servlet.config.annotation.WebMvcConfigurer;

@Configuration
@RequiredArgsConstructor
public class WebConfig implements WebMvcConfigurer {

    private final DoctorRepository doctorRepository;
    private final PatientRepository patientRepository;
    private final DiagnosisRepository diagnosisRepository;
    private final ExaminationRepository examinationRepository;

    @Override
    public void addFormatters(FormatterRegistry registry) {
        registry.addConverter(String.class, com.medical.entity.Doctor.class,
                id -> (id == null || id.isBlank()) ? null
                        : doctorRepository.findById(Long.parseLong(id)).orElse(null));

        registry.addConverter(String.class, com.medical.entity.Patient.class,
                id -> (id == null || id.isBlank()) ? null
                        : patientRepository.findById(Long.parseLong(id)).orElse(null));

        registry.addConverter(String.class, com.medical.entity.Diagnosis.class,
                id -> (id == null || id.isBlank()) ? null
                        : diagnosisRepository.findById(Long.parseLong(id)).orElse(null));

        registry.addConverter(String.class, com.medical.entity.Examination.class,
                id -> (id == null || id.isBlank()) ? null
                        : examinationRepository.findById(Long.parseLong(id)).orElse(null));
    }
}
