# Setup Guide

Complete guide to running the Animal Shelter Adoption Portal locally.

## Prerequisites

### Required Software

| Software       | Version | Download                                           |
|----------------|---------|----------------------------------------------------|
| Java           | 21+     | [https://adoptium.net/](https://adoptium.net/)     |
| Node.js        | 18+     | [https://nodejs.org/](https://nodejs.org/)         |
| Docker         | 20+     | [https://www.docker.com/](https://www.docker.com/) |
| Docker Compose | 2.0+    | Included with Docker Desktop                       |
| Git            | Latest  | [https://git-scm.com/](https://git-scm.com/)       |

**Optional but Recommended:**

- IntelliJ IDEA (for backend development)
- VS Code (for frontend development)
- Postman (for API testing)

---

## Quick Start

### 1. Clone Repository

```bash
git clone https://github.com/yourusername/animal-shelter-adoption.git
cd animal-shelter-adoption
```

### 2. Start Infrastructure

```bash
# Start PostgreSQL and LocalStack
docker-compose up -d

# Verify containers are running
docker ps
```

**Expected output:**

```
CONTAINER ID   IMAGE                       STATUS
abc123...      postgres:16-alpine          Up 10 seconds
def456...      localstack/localstack       Up 10 seconds
```

### 3. Start Backend

```bash
cd backend

# Linux/Mac
./gradlew bootRun

# Windows
gradlew.bat bootRun
```

**Expected output:**

```
Started AnimalShelterApplicationKt in 5.234 seconds
```

Backend should be running on `http://localhost:8080`

### 4. Start Frontend

**Open a new terminal:**

```bash
cd frontend

# Install dependencies (first time only)
npm install

# Start dev server
npm run dev
```

**Expected output:**

```
  VITE v5.0.11  ready in 823 ms

  ➜  Local:   http://localhost:5173/
  ➜  Network: use --host to expose
```

### 5. Access Application

Open browser to:

- **Frontend:** http://localhost:5173
- **Backend API:** http://localhost:8080/api/pets
- **Health Check:** http://localhost:8080/actuator/health

---

## Detailed Setup

### Backend Setup

#### 1. Verify Java Installation

```bash
java -version
# Should show: openjdk version "21.x.x"
```

#### 2. Build Project

```bash
cd backend
./gradlew build
```

This will:

- Download dependencies
- Compile Kotlin code
- Run unit tests (76 service layer tests)
- Generate JaCoCo coverage report
- Create executable JAR

#### 3. Database Migrations

Flyway migrations run automatically on startup. To manually check:

```bash
./gradlew flywayInfo
```

To manually migrate:

```bash
./gradlew flywayMigrate
```

#### 4. Run Application

```bash
./gradlew bootRun
```

**Alternative: Run JAR directly**

```bash
./gradlew build
java -jar build/libs/animal-shelter-backend-0.0.1-SNAPSHOT.jar
```

#### 5. Verify Backend

```bash
# Health check
curl http://localhost:8080/actuator/health

# Get pets
curl http://localhost:8080/api/pets

# Get species
curl http://localhost:8080/api/species
```

---

### Frontend Setup

#### 1. Verify Node Installation

```bash
node --version
# Should show: v18.x.x or higher

npm --version
# Should show: 9.x.x or higher
```

#### 2. Install Dependencies

```bash
cd frontend
npm install
```

This installs:

- React, React Router
- TypeScript
- Vite
- TanStack Query
- Tailwind CSS
- Axios

#### 3. Configure Environment

Create `.env` file:

```bash
cd frontend
cat > .env << EOF
VITE_API_BASE_URL=http://localhost:8080/api
EOF
```

#### 4. Run Development Server

```bash
npm run dev
```

Frontend runs on `http://localhost:5173` with hot reload enabled.

#### 5. Build for Production

```bash
npm run build
```

Output in `frontend/dist/`

#### 6. Preview Production Build

```bash
npm run preview
```

Serves production build on `http://localhost:4173`

---

### Infrastructure Setup

#### Docker Compose Services

**postgres:**

- Image: `postgres:16-alpine`
- Port: `5432`
- Database: animal_shelter
- User: shelter_user
- Password: shelter_pass

**localstack:**

- Image: `localstack/localstack:latest`
- Port: `4566`
- Services: S3
- Bucket: shelter-images

#### Manual Setup (if not using Docker)

**PostgreSQL:**

```bash
# Install PostgreSQL 16

# Create database
createdb animal_shelter

# Create user
psql -c "CREATE USER shelter_user WITH PASSWORD 'shelter_pass';"
psql -c "GRANT ALL PRIVILEGES ON DATABASE animal_shelter TO shelter_user;"
```

**Update backend config:**

```yaml
# backend/src/main/resources/application.yml
spring:
  datasource:
    url: jdbc:postgresql://localhost:5432/animal_shelter
    username: shelter_user
    password: shelter_pass
```

---

## Troubleshooting

### Backend Issues

**Problem: Port 8080 already in use**

```bash
# Find process using port 8080
lsof -i :8080 # Mac/Linux
netstat -ano | findstr :8080 # Windows

# Kill process or change port in application.yml
```

**Problem: Database connection failed**

```bash
# Check if PostgreSQL is running
docker ps | grep postgres

# Restart docker-compose
docker-compose down
docker-compose up -d

# Check logs
docker logs animal-shelter-postgres
```

**Problem: Flyway migration failed**

```bash
# Reset database (CAUTION: deletes all data)
docker-compose down -v
docker-compose up -d

# Restart backend
./gradlew bootRun
```

**Problem: Build fails**

```bash
# Clean build
./gradlew clean build

# Check Gradle version
./gradlew --version
```

### Frontend Issues

**Problem: npm install fails**

```bash
# Clear npm cache
npm cache clean --force

# Delete node_modules and reinstall
rm -rf node_modules package-lock.json
npm install
```

**Problem: Port 5173 already in use**

```bash
# Kill process on port 5173
lsof -i :5173 # Mac/Linux
netstat -ano | findstr :5173 # Windows

# Or change port in vite.config.ts
```

**Problem: API calls fail (CORS error)**

- Check backend is running on port 8080
- Check .env file has correct VITE_API_BASE_URL
- Restart frontend dev server

**Problem: Build errors**

```bash
# Check TypeScript
npm run build

# If errors, check tsconfig.json
```

### Docker Issues

**Problem: Containers won't start**

```bash
# Check Docker is running
docker info

# Restart Docker Desktop

# Check disk space
docker system df

# Prune unused resources
docker system prune -a
```

**Problem: Cannot connect to LocalStack S3**

```bash
# Check LocalStack logs
docker logs animal-shelter-localstack

# Restart LocalStack
docker-compose restart localstack

# Test S3 access
aws --endpoint-url=http://localhost:4566 s3 ls
```

---

## Development Workflow

### Making Changes

**Backend:**

```bash
# Make code changes

# Backend auto-reloads with Spring DevTools (if configured)

# Or restart: Ctrl+C then ./gradlew bootRun
```

**Frontend:**

```bash
# Make code changes

# Vite auto-reloads (HMR)

# No restart needed
```

**Database:**

```bash
# Create new migration
# backend/src/main/resources/db/migration/V8__your_change.sql

# Restart backend to apply
./gradlew bootRun
```

### Running Tests

**Backend:**

```bash
cd backend

# Run all tests
./gradlew test

# Run tests with coverage report (auto-generated)
./gradlew test jacocoTestReport

# Run specific test class
./gradlew test --tests "com.animalshelter.service.PetServiceTest"

# Run specific test method
./gradlew test --tests "com.animalshelter.service.PetServiceTest.FindAll*"
```

**View Coverage Reports:**

```bash
# After running tests, open in browser:
open build/reports/jacoco/html/index.html   # Mac
xdg-open build/reports/jacoco/html/index.html  # Linux
start build/reports/jacoco/html/index.html  # Windows

# Test results HTML report
open build/reports/tests/test/index.html
```

**Verify Coverage Thresholds:**

```bash
# Fails build if coverage < 70%
./gradlew jacocoTestCoverageVerification
```

**Frontend:**

```bash
cd frontend
npm run test # If tests configured
```

### Code Formatting

**Backend:**

```bash
# Kotlin formatting with ktlint (if configured)
./gradlew ktlintFormat
```

**Frontend:**

```bash
# Prettier formatting (if configured)
npm run format
```

---

## Stopping the Application

### Stop Frontend

```bash
# In terminal running `npm run dev`
Ctrl+C
```

### Stop Backend

```bash
# In terminal running `./gradlew bootRun`
Ctrl+C
```

### Stop Infrastructure

```bash
# Stop but keep data
docker-compose stop

# Stop and remove containers (keeps volumes)
docker-compose down

# Stop and remove everything including data
docker-compose down -v
```

---

## Data Management

### Resetting Database

```bash
# Stop all services
docker-compose down -v

# Start fresh
docker-compose up -d
cd backend && ./gradlew bootRun
```

### Seeding Test Data

Initial species are seeded automatically via `V7__seed_initial_species.sql`.

To add test pets/applications:

```bash
# Use Postman collection

# Or create SQL seed file: V8__seed_test_data.sql
```

---

## IDE Setup

### IntelliJ IDEA (Backend)

1. Open `backend` folder
2. Right-click `build.gradle.kts` → "Link Gradle Project"
3. Wait for indexing
4. Run configurations:
    - Main class: `com.animalshelter.AnimalShelterApplicationKt`
    - VM options: (none needed)

### VS Code (Frontend)

**Recommended Extensions:**

- ESLint
- Prettier
- Tailwind CSS IntelliSense
- TypeScript Vue Plugin (Volar)

**Settings:**

```json
{
  "editor.formatOnSave": true,
  "editor.defaultFormatter": "esbenp.prettier-vscode"
}
```

---

## Next Steps

After successful setup:

- ✅ Browse pets at http://localhost:5173
- ✅ Submit test application
- ✅ Access admin panel at http://localhost:5173/admin
- ✅ Review applications and test approval workflow
- ✅ Read `BUSINESS_RULES.md` for domain logic
- ✅ Check `API_DOCUMENTATION.md` for endpoints

---

## Production Deployment (Future)

See `FUTURE_ENHANCEMENTS.md` for:

- AWS deployment guide
- Kubernetes configuration
- CI/CD pipeline setup
- Environment configuration