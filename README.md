# Medical Records System

A web application for managing medical records built with Spring Boot, Spring Security, and Thymeleaf.

## Features

- **Role-based access control** — three roles: Admin, Doctor, Patient
- **Doctors** — manage doctor profiles (Admin only)
- **Patients** — manage patient records with EGN, insurance status, and personal GP
- **Examinations** — create and manage medical examinations with diagnosis, treatment, and fees
- **Sick Leaves** — issue and track sick leave certificates linked to examinations
- **Diagnoses** — manage a catalog of diagnosis codes
- **Doctor Dashboard** — each doctor sees only their own examinations, revenue, and sick leaves
- **Patient Portal** — patients view their own medical history and manage their profile
- **Statistics** — admin-only reports: visits per doctor, revenue, patients per GP, most common diagnosis, sick leave trends

## Tech Stack

| Layer | Technology |
|---|---|
| Language | Java 21 |
| Framework | Spring Boot 3.5 |
| Security | Spring Security 6 (BCrypt, form login) |
| Persistence | Spring Data JPA / Hibernate |
| Database | PostgreSQL |
| Templates | Thymeleaf 3 + thymeleaf-extras-springsecurity6 |
| UI | Bootstrap 5.3 |
| Build | Maven |
| Utilities | Lombok |

## Roles & Permissions

| Feature | Admin | Doctor | Patient |
|---|---|---|---|
| Manage doctors | ✅ | ❌ | ❌ |
| Manage patients | ✅ | ❌ | ❌ |
| Manage examinations | ✅ | Own only | View own |
| Manage sick leaves | ✅ | Own only | View own |
| Manage diagnoses | ✅ | Add only | ❌ |
| Statistics dashboard | ✅ | ❌ | ❌ |
| Personal dashboard | ❌ | ✅ | ❌ |
| My medical history | ❌ | ❌ | ✅ |
| My profile | ❌ | ❌ | ✅ |

## Getting Started

### Prerequisites

- Java 21+
- Maven 3.9+
- PostgreSQL 14+

### Database Setup

Create a PostgreSQL database:

```sql
CREATE DATABASE medical_records;
```

### Configuration

Edit `src/main/resources/application.properties`:

```properties
spring.datasource.url=jdbc:postgresql://localhost:5432/medical_records
spring.datasource.username=your_username
spring.datasource.password=your_password
```

### Run

```bash
mvn spring-boot:run
```

The application starts at `http://localhost:8080`.

### Default Admin Account

On first startup, a default admin account is created automatically:

| Username | Password |
|---|---|
| `admin` | `admin123` |

## Registration Flow

- **Patients** register directly — a new patient record is created during registration.
- **Doctors** register by selecting an existing doctor profile added by an Admin. The admin must create the doctor record first before the doctor can register an account.

## Fee & Insurance Model

Each examination has two price fields:

- **Doctor Fee** — what the doctor receives (always set by the doctor)
- **Patient Pays** — calculated automatically: `0` if the patient is insured (NHIF covers it), otherwise equal to the doctor fee
