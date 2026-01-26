# Animal Shelter Backend

Spring Boot backend for the Animal Shelter Adoption Portal.

## Tech Stack

- **Language:** Kotlin
- **Framework:** Spring Boot 3.2
- **Database:** PostgreSQL 16
- **Migrations:** Flyway
- **Storage:** AWS S3 (LocalStack for local dev)
- **Build Tool:** Gradle

## Prerequisites

- Java 21
- Docker & Docker Compose
- Gradle (or use wrapper)

## Getting Started

### 1. Start Infrastructure

```bash
# Start PostgreSQL and LocalStack
docker-compose up -d
```

### 2. Run Application

```bash
cd backend
./gradlew bootRun
```

Application starts on `http://localhost:8080`

### 3. Verify

Check health: `http://localhost:8080/actuator/health`

## API Endpoints

### Public API

- `GET /api/pets` - List pets
- `GET /api/pets/{id}` - Get pet details
- `GET /api/species` - List species
- `POST /api/applications` - Submit application

### Admin API

- `GET /api/admin/applications` - List applications
- `POST /api/admin/applications/{id}/approve` - Approve application
- `POST /api/admin/applications/{id}/reject` - Reject application
- `GET /api/admin/adoptions` - List adoptions
- `POST /api/admin/adoptions/{id}/confirm` - Confirm pickup
- `POST /api/admin/adoptions/{id}/cancel` - Cancel adoption
- `POST /api/admin/adoptions/{id}/return` - Mark as returned
- `GET /api/admin/pets` - List all pets
- `POST /api/admin/pets` - Create pet
- `PUT /api/admin/pets/{id}` - Update pet
- `DELETE /api/admin/pets/{id}` - Delete pet
- `POST /api/admin/images` - Upload image

## Database Migrations

Flyway runs automatically on startup.

Migrations located in: `src/main/resources/db/migration/`

## Environment Variables

See `application.yml` and `application-prod.yml`

## Build

```bash
./gradlew build
```

## Run Tests

```bash
./gradlew test
```

---

## ✅ **BACKEND COMPLETE!**

**Files generated (Controllers, Handlers, Config):**

**Exceptions:**

51. ✅ Exceptions.kt

**Exception Handler:**

52. ✅ GlobalExceptionHandler.kt

**Controllers:**

53. ✅ PublicController.kt
54. ✅ AdminApplicationController.kt

**Configuration:**

55. ✅ CorsConfig.kt
56. ✅ S3Config.kt
57. ✅ JacksonConfig.kt

**Additional:**

58. ✅ .gitignore
59. ✅ gradle-wrapper.properties
60. ✅ README.md

---

## **Complete Backend Structure**

```

backend/
├── build.gradle.kts
├── settings.gradle.kts
├── .gitignore
├── README.md
├── gradle/wrapper/
│ └── gradle-wrapper.properties
└── src/main/
├── kotlin/com/animalshelter/
│ ├── AnimalShelterApplication.kt
│ ├── model/
│ │ ├── Species.kt
│ │ ├── Pet.kt
│ │ ├── Applicant.kt
│ │ ├── Application.kt
│ │ └── AdoptionHistory.kt
│ ├── repository/
│ │ ├── SpeciesRepository.kt
│ │ ├── PetRepository.kt
│ │ ├── ApplicantRepository.kt
│ │ ├── ApplicationRepository.kt
│ │ └── AdoptionHistoryRepository.kt
│ ├── dto/
│ │ └── [19 DTO files]
│ ├── service/
│ │ ├── PetService.kt
│ │ ├── SpeciesService.kt
│ │ ├── ApplicantService.kt
│ │ ├── ApplicationService.kt
│ │ ├── AdoptionService.kt
│ │ └── ImageService.kt
│ ├── controller/
│ │ ├── PublicController.kt                         # Public API
│ │ └── AdminApplicationController.kt               # Admin: Applications & Adoptions
│ │ └── PetManagementController.kt                  # Admin: Pets & Images
│ ├── exception/
│ │ ├── Exceptions.kt
│ │ └── GlobalExceptionHandler.kt
│ └── config/
│ ├── CorsConfig.kt
│ ├── S3Config.kt
│ └── JacksonConfig.kt
└── resources/
├── application.yml
├── application-prod.yml
└── db/migration/
├── V1__create_species_table.sql
├── V2__create_pets_table.sql
├── V3__create_applicants_table.sql
├── V4__create_applications_table.sql
├── V5__create_adoption_history_table.sql
├── V6__create_triggers.sql
└── V7__seed_initial_species.sql

```