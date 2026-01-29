# 🐾 Animal Shelter Adoption Portal

A full-stack web application demonstrating modern software engineering practices through a pet adoption management
system.

[![Tech Stack](https://img.shields.io/badge/Stack-Kotlin%20%7C%20Spring%20Boot%20%7C%20React%20%7C%20PostgreSQL-blue)](#tech-stack)
[![License](https://img.shields.io/badge/License-Portfolio%20Project-green)](#)

---

## 📖 Table of Contents

- [Overview](#overview)
- [Features](#features)
- [Tech Stack](#tech-stack)
- [Architecture](#architecture)
- [Getting Started](#getting-started)
- [Project Structure](#project-structure)
- [Documentation](#documentation)
- [Screenshots](#screenshots)
- [Future Enhancements](#future-enhancements)
- [Learning Outcomes](#learning-outcomes)
- [Author](#author)

---

## 🎯 Overview

This project simulates a real-world animal shelter management system where:

- **Public users** can browse available pets and submit adoption applications
- **Admin staff** can manage the entire adoption lifecycle from application review to pet pickup
- The system enforces complex business rules and maintains data integrity throughout the adoption process

**Built to showcase:**

- Full-stack development capabilities
- Complex state machine implementation
- RESTful API design
- Modern frontend architecture
- Database design and optimization
- Professional development practices

---

## ✨ Features

### Public Features

- 🔍 Browse pets with filtering (species, availability status)
- 📋 View detailed pet profiles with images
- 📝 Submit adoption applications with validation
- 🎨 Responsive design (mobile-friendly)

### Admin Features

- 📊 Dashboard with real-time statistics
- ✅ Review and approve/reject applications
- 🏠 Manage adoption lifecycle:
    - Approve application → Creates adoption record (PENDING_PICKUP)
    - Confirm pickup → Marks pet as ADOPTED
    - Handle no-shows → Cancel adoption
    - Process returns → Pet back to AVAILABLE
- 🐕 Pet inventory management (CRUD)
- 📸 Image upload to cloud storage (S3)
- 🔒 **Business rule enforcement** (only one approved application per pet)

### Key Business Rules

- ✅ Only **one approved application** allowed per pet at a time
- ✅ Pet status transitions: AVAILABLE → PENDING → ADOPTED
- ✅ Adoption workflow: Application → Approval → Pickup → Active
- ✅ Supports pet returns and re-adoption
- ✅ Soft delete for data preservation

See [BUSINESS_RULES.md](docs/BUSINESS_RULES.md) for complete rules.

---

## 🛠️ Tech Stack

### Backend

| Technology          | Purpose                              |
|---------------------|--------------------------------------|
| **Kotlin**          | Modern JVM language with null safety |
| **Spring Boot 3.2** | Enterprise Java framework            |
| **PostgreSQL 16**   | Relational database                  |
| **JPA/Hibernate**   | ORM for database access              |
| **Flyway**          | Database version control             |
| **AWS S3 SDK**      | Cloud storage for images             |

### Frontend

| Technology         | Purpose                 |
|--------------------|-------------------------|
| **React 18**       | UI framework            |
| **TypeScript**     | Type-safe JavaScript    |
| **Vite**           | Fast build tool         |
| **TanStack Query** | Server state management |
| **Tailwind CSS**   | Utility-first styling   |
| **Axios**          | HTTP client             |

### Infrastructure

| Technology         | Purpose                         |
|--------------------|---------------------------------|
| **Docker Compose** | Local development orchestration |
| **LocalStack**     | Local AWS S3 simulation         |
| **Gradle**         | Build automation                |

See [TECH_STACK.md](docs/TECH_STACK.md) for detailed rationale.

---

## 🏗️ Architecture

**Pattern:** Layered Architecture (MVC + Service Layer)

```
┌──────────────┐
│  React SPA   │  ← Frontend (TypeScript)
└──────┬───────┘
       │ REST API
┌──────▼───────┐
│ Controllers  │
├──────────────┤
│  Services    │  ← Business Logic
├──────────────┤
│ Repositories │
└──────┬───────┘
       │ JDBC
┌──────▼───────┐
│  PostgreSQL  │
└──────────────┘
```

**Data Flow Example:**

```
User submits application
  → Frontend validates
  → POST /api/applications
  → ApplicationController
  → ApplicationService (business logic)
    - Find/create applicant
    - Check for duplicates
    - Create application record
    - Update pet status
  → ApplicationRepository
  → PostgreSQL
```

See [ARCHITECTURE.md](docs/ARCHITECTURE.md) for detailed diagrams.

---

## 🚀 Getting Started

### Prerequisites

- Java 21+
- Node.js 18+
- Docker & Docker Compose

### Quick Start

```bash
# 1. Clone repository
git clone https://github.com/yourusername/animal-shelter-adoption.git
cd animal-shelter-adoption

# 2. Start infrastructure (PostgreSQL + LocalStack)
docker-compose up -d

# 3. Start backend (new terminal)
cd backend
./gradlew bootRun

# 4. Start frontend (new terminal)
cd frontend
npm install
npm run dev
```

**Access:**

- 🌐 Frontend: http://localhost:5173
- 🔌 Backend API: http://localhost:8080/api
- 🗄️ Database: localhost:5432 (animal_shelter)

See [SETUP.md](docs/SETUP.md) for detailed instructions.

---

## 📁 Project Structure

```
animal-shelter-adoption/
├── backend/                    # Spring Boot backend
│   ├── src/main/kotlin/
│   │   └── com/animalshelter/
│   │       ├── controller/    # REST controllers
│   │       ├── service/       # Business logic
│   │       ├── repository/    # Data access
│   │       ├── model/         # JPA entities
│   │       ├── dto/           # Request/Response objects
│   │       ├── exception/     # Custom exceptions
│   │       └── config/        # Configuration
│   └── src/main/resources/
│       └── db/migration/      # Flyway migrations
├── frontend/                   # React frontend
│   └── src/
│       ├── components/        # React components
│       ├── pages/             # Route pages
│       ├── hooks/             # Custom hooks
│       ├── api/               # API client
│       └── types/             # TypeScript types
├── docs/                       # Documentation
└── docker-compose.yml          # Local infrastructure
```

---

## 📚 Documentation

### Developer Guides

- [🏗️ Architecture](docs/ARCHITECTURE.md) - System design and patterns
- [💾 Database Design](docs/DATABASE_DESIGN.md) - Schema, ERD, migrations
- [🔌 API Documentation](docs/API_DOCUMENTATION.md) - Endpoints and contracts
- [📜 Business Rules](docs/BUSINESS_RULES.md) - Domain logic and constraints
- [⚙️ Setup Guide](docs/SETUP.md) - Installation and troubleshooting

### Technical Decisions

- [🛠️ Tech Stack](docs/TECH_STACK.md) - Technology choices and rationale
- [🔮 Future Enhancements](docs/FUTURE_ENHANCEMENTS.md) - Production features

---

## 📸 Screenshots

### Public Interface

**Pet Listing Page**

```
┌─────────────────────────────────────────┐
│  🐾 Animal Shelter                      │
│  Home | Browse Pets | Admin             │
├─────────────────────────────────────────┤
│  Filters: [All Species ▼] [Available ▼] │
│                                          │
│  ┌──────┐  ┌──────┐  ┌──────┐          │
│  │ 🐕   │  │ 🐈   │  │ 🐰   │          │
│  │Buddy │  │Whisks│  │Fluffy│          │
│  │3 yrs │  │2 yrs │  │1 yr  │          │
│  │[Apply│  │[Apply│  │[Apply│          │
│  └──────┘  └──────┘  └──────┘          │
└─────────────────────────────────────────┘
```

### Admin Dashboard

```
┌─────────────────────────────────────────┐
│  Admin Dashboard                         │
├─────────────────────────────────────────┤
│  Available Pets: 12  Pending Apps: 8    │
│  Active Adoptions: 5  Pending Pickup: 2 │
│                                          │
│  Recent Applications:                    │
│  • John Doe → Buddy (PENDING) [Review]  │
│  • Jane Smith → Whiskers (PENDING)      │
└─────────────────────────────────────────┘
```

---

## 🔮 Future Enhancements

Features intentionally **not implemented** to focus on core functionality:

### Security & Authentication

- JWT-based authentication
- Role-based access control (RBAC)
- API rate limiting

### Observability

- Structured logging (ELK stack)
- Distributed tracing (Jaeger)
- Metrics dashboard (Prometheus + Grafana)

### DevOps

- CI/CD pipeline (GitHub Actions)
- Kubernetes deployment
- Infrastructure as Code (Terraform)

### Features

- Email notifications
- Advanced search (Elasticsearch)
- Scheduled reminders
- Pet medical records
- Multi-language support

### Testing

- Unit tests (JUnit 5)
- Integration tests (TestContainers)
- E2E tests (Playwright)

**Estimated Timeline:** +12 weeks for production-ready system

See [FUTURE_ENHANCEMENTS.md](docs/FUTURE_ENHANCEMENTS.md) for details.

---

## 🎓 Learning Outcomes

This project demonstrates proficiency in:

**Backend Development:**

- ✅ RESTful API design with proper HTTP semantics
- ✅ Complex business logic with state machines
- ✅ Database design with referential integrity
- ✅ Transaction management
- ✅ Exception handling and validation

**Frontend Development:**

- ✅ React component architecture
- ✅ TypeScript for type safety
- ✅ Server state management (TanStack Query)
- ✅ Form validation and error handling
- ✅ Responsive design

**Software Engineering:**

- ✅ Layered architecture
- ✅ Separation of concerns
- ✅ Database versioning (Flyway)
- ✅ API documentation
- ✅ Professional git workflow

**Industry Practices:**

- ✅ Docker for development environment
- ✅ Environment configuration
- ✅ Code organization and naming conventions
- ✅ Comprehensive documentation

---

## 🧪 Testing the Application

### Sample Workflow

1. **Browse Pets**
    - Navigate to http://localhost:5173
    - Filter by species or status

2. **Submit Application**
    - Click on a pet
    - Fill out adoption form
    - Submit (requires 50+ character reason)

3. **Admin Review** (http://localhost:5173/admin)
    - View pending applications
    - Approve application → Creates adoption record
    - Try approving 2nd application for same pet → Error! ✅

4. **Complete Adoption**
    - Go to Adoptions tab
    - Confirm pickup → Pet marked as ADOPTED
    - All other applications auto-rejected

5. **Handle Return**
    - Mark pet as returned
    - Pet becomes AVAILABLE again

---

## 📊 Database Schema

**5 Core Tables:**

- `species` - Reference data (Dog, Cat, etc.)
- `pets` - Pet inventory with status
- `applicants` - People applying to adopt
- `applications` - Adoption applications
- `adoption_history` - Adoption lifecycle tracking

**Key Relationships:**

```sql
pets.species_id → species.id
applications.pet_id → pets.id
applications.applicant_id → applicants.id
adoption_history.pet_id → pets.id
adoption_history.application_id → applications.id
```

See [DATABASE_DESIGN.md](docs/DATABASE_DESIGN.md) for complete schema.

---

## 🌟 Highlights

**What makes this project stand out:**

1. **Complex State Machine**
    - Not just CRUD operations
    - Real-world business logic
    - Multiple interconnected workflows

2. **Data Integrity**
    - Foreign key constraints
    - Check constraints
    - Business rule validation
    - Soft deletes for audit trail

3. **Modern Stack**
    - Cutting-edge technologies
    - Industry best practices
    - Production-ready patterns

4. **Comprehensive Documentation**
    - Well-documented decisions
    - Clear architecture
    - Professional presentation

---

## 🤝 Contributing

This is a portfolio project for demonstration purposes. Not accepting contributions.

---

## 📝 License

This project is created for educational and portfolio purposes.

---

## 👤 Author

**Kamal Gandhi**

---

## 🙏 Acknowledgments

- Built as a demonstration of full-stack development skills
- Inspired by real-world animal shelter management systems
- Technologies chosen to showcase modern development practices

---

## 📞 Contact

For questions about this project, please reach out via:

- 📧 Email: kamalgandhi20@gmail.com

---

**⭐ If you found this project interesting, please consider giving it a star!**