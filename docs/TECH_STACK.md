# Technology Stack

## Overview

This project uses modern, industry-standard technologies to demonstrate best practices in full-stack development.

## Backend

### Language: Kotlin

**Why Kotlin?**

- ✅ Concise, expressive syntax
- ✅ Null safety (reduces NPE bugs)
- ✅ Excellent Java interop
- ✅ First-class Spring support
- ✅ Data classes (reduced boilerplate)

**Alternatives Considered:**

- Java: More verbose, lacks modern features
- Node.js: Good for I/O, less suitable for complex business logic

### Framework: Spring Boot 3.2

**Why Spring Boot?**

- ✅ Industry standard for enterprise Java/Kotlin
- ✅ Auto-configuration reduces boilerplate
- ✅ Comprehensive ecosystem (Data, Security, Web)
- ✅ Production-ready features (metrics, health checks)
- ✅ Excellent documentation

**Key Dependencies:**

- `spring-boot-starter-web`: REST API
- `spring-boot-starter-data-jpa`: Database access
- `spring-boot-starter-validation`: Input validation
- `flyway-core`: Database migrations
- `aws-java-sdk-s3`: S3 integration

### Database: PostgreSQL 16

**Why PostgreSQL?**

- ✅ ACID compliance
- ✅ Advanced features (triggers, check constraints)
- ✅ Excellent performance
- ✅ JSON support (future extensibility)
- ✅ Free and open-source

**Alternatives Considered:**

- MySQL: Less feature-rich
- MongoDB: Not suitable for relational data

### ORM: JPA/Hibernate

**Why JPA?**

- ✅ Standard Java persistence API
- ✅ Database-agnostic (can switch DB easily)
- ✅ Reduces boilerplate SQL
- ✅ Built-in Spring support

### Migrations: Flyway

**Why Flyway?**

- ✅ Version control for database schema
- ✅ Repeatable, reliable migrations
- ✅ Team collaboration friendly
- ✅ Production-safe rollback support

### Storage: AWS S3 / LocalStack

**Why S3?**

- ✅ Scalable object storage
- ✅ Industry standard
- ✅ Cost-effective
- ✅ LocalStack for local dev (no AWS account needed)

---

## Frontend

### Framework: React 18

**Why React?**

- ✅ Most popular frontend framework
- ✅ Component-based architecture
- ✅ Large ecosystem
- ✅ Excellent developer experience
- ✅ Strong job market demand

**Alternatives Considered:**

- Vue: Smaller ecosystem
- Angular: Steeper learning curve

### Language: TypeScript

**Why TypeScript?**

- ✅ Type safety catches bugs early
- ✅ Better IDE support (autocomplete, refactoring)
- ✅ Self-documenting code
- ✅ Industry trend (95% of new projects)

### Build Tool: Vite

**Why Vite?**

- ✅ Lightning-fast HMR (Hot Module Replacement)
- ✅ Modern, optimized builds
- ✅ Better DX than Create React App
- ✅ Native ESM support

### State Management: TanStack Query

**Why TanStack Query?**

- ✅ Server state management (caching, refetching)
- ✅ Reduces boilerplate vs Redux
- ✅ Automatic background updates
- ✅ Optimistic updates support
- ✅ Error/loading states built-in

**Alternatives Considered:**

- Redux: Too much boilerplate for this use case
- Context API: Not suitable for server state

### HTTP Client: Axios

**Why Axios?**

- ✅ Simple, intuitive API
- ✅ Interceptor support (error handling)
- ✅ Automatic JSON transformation
- ✅ Better browser support than Fetch

### Styling: Tailwind CSS

**Why Tailwind?**

- ✅ Utility-first approach (rapid development)
- ✅ No CSS file switching
- ✅ Tree-shaking (only used styles in bundle)
- ✅ Consistent design system
- ✅ Mobile-first responsive design

---

## Infrastructure

### Containerization: Docker Compose

**Why Docker Compose?**

- ✅ Consistent dev environment across machines
- ✅ Easy service orchestration (DB + LocalStack)
- ✅ One-command startup
- ✅ Production parity

### Local Cloud: LocalStack

**Why LocalStack?**

- ✅ Simulate AWS services locally
- ✅ No AWS account needed for development
- ✅ Faster iteration (no internet calls)
- ✅ Cost-free development

---

## Development Tools

### Build: Gradle

**Why Gradle?**

- ✅ Kotlin DSL (build.gradle.kts)
- ✅ Faster than Maven
- ✅ Better dependency management
- ✅ Spring Boot's recommended build tool

### Package Manager: npm

**Why npm?**

- ✅ Default for Node.js ecosystem
- ✅ Largest package registry
- ✅ Lock file for reproducible builds

---

## Testing

### Backend Testing Stack

| Tool                         | Purpose                                  |
|------------------------------|------------------------------------------|
| **JUnit 5**                  | Test framework                           |
| **Mockito + mockito-kotlin** | Mocking dependencies                     |
| **Spring Test**              | Spring context testing                   |
| **Testcontainers**           | Integration testing with real PostgreSQL |
| **JSONAssert**               | Flexible JSON comparison                 |
| **JaCoCo**                   | Code coverage reporting                  |

**Why Mockito?**

- ✅ Industry standard for Java/Kotlin mocking
- ✅ Clean API with mockito-kotlin extensions
- ✅ Excellent documentation and community support
- ✅ Built-in with spring-boot-starter-test

**Why Testcontainers?**

- ✅ Real database testing (PostgreSQL in Docker)
- ✅ Tests run against actual Flyway migrations
- ✅ Validates queries and transactions work correctly
- ✅ Seamless Spring Boot integration with @ServiceConnection

**Why JaCoCo?**

- ✅ Industry standard for JVM code coverage
- ✅ Generates HTML and XML reports
- ✅ Integrates with Gradle seamlessly
- ✅ Supports coverage thresholds enforcement

**Test Coverage:**

- Unit tests across services
- Integration tests (REST API + DB verification)
- Mocked repository layer for isolated unit testing

---

## Not Included (Out of Scope)

These are production-ready features intentionally excluded to focus on core functionality:

### Authentication & Authorization

- **Would use:** Spring Security + JWT
- **Reason:** Demo focused on business logic, not auth flow

### Observability

- **Would use:** Spring Actuator + Prometheus + Grafana
- **Reason:** Demo environment, not production monitoring

### CI/CD

- **Would use:** GitHub Actions + Docker Registry
- **Reason:** Manual deployment for demo

See [FUTURE_ENHANCEMENTS.md](FUTURE_ENHANCEMENTS.md) for complete list.

---

## Dependency Versions

### Backend

```kotlin
Kotlin: 2.3.0
Spring Boot : 4.0.2
PostgreSQL Driver :(managed by Spring)
Flyway: (managed by Spring)
AWS SDK S3: 2.29.0

// Testing
JUnit 5: (managed by Spring)
Mockito - Kotlin: 5.4.0
JaCoCo: 0.8.12
```

### Frontend

```json
- React: 18.3.1
- TypeScript: 5.3.3
- Vite: 5.0.11
- TanStack Query: 5.17.19
- Axios: 1.6.5
- Tailwind CSS: 3.4.1
```

---

## Learning Resources

For those unfamiliar with this stack:

**Kotlin:**

- [Official Kotlin Docs](https://kotlinlang.org/docs/home.html)
- Kotlin for Java Developers course

**Spring Boot:**

- [Spring Guides](https://spring.io/guides)
- Spring Boot in Action (book)

**React + TypeScript:**

- [React Beta Docs](https://react.dev)
- [TypeScript Handbook](https://www.typescriptlang.org/docs/)

**TanStack Query:**

- [Official Docs](https://tanstack.com/query/latest)