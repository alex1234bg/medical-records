package com.medical;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.boot.autoconfigure.domain.EntityScan;
import org.springframework.data.jpa.repository.config.EnableJpaRepositories;

@SpringBootApplication
@EntityScan("com.medical.entity")
@EnableJpaRepositories("com.medical")
public class MedicalRecordsApplication {

	public static void main(String[] args) {
		SpringApplication.run(MedicalRecordsApplication.class, args);
	}

}
