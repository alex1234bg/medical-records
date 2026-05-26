package com.medical.config;

import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.http.HttpMethod;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.annotation.web.configuration.EnableWebSecurity;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.security.web.SecurityFilterChain;

@Configuration
@EnableWebSecurity
public class SecurityConfig {

    @Bean
    public PasswordEncoder passwordEncoder() {
        return new BCryptPasswordEncoder();
    }

    @Bean
    public SecurityFilterChain filterChain(HttpSecurity http) throws Exception {
        http
            .authorizeHttpRequests(auth -> auth
                .requestMatchers("/login", "/register", "/css/**", "/js/**").permitAll()
                .requestMatchers("/my-history").hasRole("PATIENT")
                .requestMatchers("/my-profile/**").hasRole("PATIENT")
                .requestMatchers("/statistics/**").hasRole("ADMIN")
                .requestMatchers("/doctor-dashboard/**").hasRole("DOCTOR")
                .requestMatchers(HttpMethod.GET, "/doctors").hasAnyRole("PATIENT", "DOCTOR", "ADMIN")
                .requestMatchers(HttpMethod.GET, "/doctors/**").hasRole("ADMIN")
                .requestMatchers(HttpMethod.POST, "/doctors/**").hasRole("ADMIN")
                .requestMatchers(HttpMethod.GET, "/patients").hasAnyRole("DOCTOR", "ADMIN")
                .requestMatchers("/patients/**").hasRole("ADMIN")
                .requestMatchers(HttpMethod.GET, "/examinations").hasAnyRole("PATIENT", "DOCTOR", "ADMIN")
                .requestMatchers("/examinations/**").hasAnyRole("DOCTOR", "ADMIN")
                .requestMatchers(HttpMethod.GET, "/diagnoses").hasAnyRole("DOCTOR", "ADMIN")
                .requestMatchers(HttpMethod.GET, "/diagnoses/new").hasAnyRole("DOCTOR", "ADMIN")
                .requestMatchers(HttpMethod.POST, "/diagnoses/save").hasAnyRole("DOCTOR", "ADMIN")
                .requestMatchers("/diagnoses/**").hasRole("ADMIN")
                .requestMatchers(HttpMethod.GET, "/sick-leaves").hasAnyRole("PATIENT", "DOCTOR", "ADMIN")
                .requestMatchers("/sick-leaves/**").hasAnyRole("DOCTOR", "ADMIN")
                .anyRequest().authenticated()
            )
            .formLogin(form -> form
                .loginPage("/login")
                .defaultSuccessUrl("/", true)
                .permitAll()
            )
            .logout(logout -> logout
                .logoutUrl("/logout")
                .logoutSuccessUrl("/login?logout")
                .permitAll()
            );

        return http.build();
    }
}
