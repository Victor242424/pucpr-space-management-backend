# 🏫 Space Management System

[![Java](https://img.shields.io/badge/Java-21-red.svg)](https://openjdk.java.net/)
[![Spring Boot](https://img.shields.io/badge/Spring%20Boot-3.2.0-brightgreen.svg)](https://spring.io/projects/spring-boot)
[![License](https://img.shields.io/badge/License-Apache%202.0-blue.svg)](https://opensource.org/licenses/Apache-2.0)
[![Build Status](https://img.shields.io/badge/build-passing-brightgreen.svg)]()

> REST API for managing educational spaces, student access control, and occupancy reporting

## 📋 Table of Contents

- [Overview](#-overview)
- [Features](#-features)
- [Architecture](#-architecture)
- [Technologies](#-technologies)
- [Prerequisites](#-prerequisites)
- [Installation](#-installation)
- [Configuration](#-configuration)
- [Running the Application](#-running-the-application)
- [API Documentation](#-api-documentation)
- [Testing](#-testing)
- [Monitoring](#-monitoring)
- [Security](#-security)
- [Project Structure](#-project-structure)
- [API Endpoints](#-api-endpoints)
- [Database Schema](#-database-schema)
- [Contributing](#-contributing)
- [License](#-license)

## 🎯 Overview

The **Space Management System** is a comprehensive REST API designed to manage educational facilities, track student access to different spaces (classrooms, laboratories, study rooms), and generate detailed occupancy reports.

### Key Capabilities

- 👥 **Student Management**: Complete CRUD operations for student records
- 🏢 **Space Management**: Manage different types of educational spaces
- 🚪 **Access Control**: Track entry and exit of students in real-time
- 📊 **Analytics**: Generate occupancy reports and usage statistics
- 🔐 **Security**: JWT-based authentication and role-based authorization
- 📈 **Monitoring**: Integrated metrics with Prometheus and actuator endpoints
- 📚 **Documentation**: Interactive API documentation with Swagger UI

## ✨ Features

### Core Features

- **Multi-Role Authentication**
    - Student access with limited permissions
    - Admin access with full management capabilities
    - JWT token-based authentication

- **Real-Time Space Tracking**
    - Automatic occupancy calculation
    - Maximum capacity validation
    - Active access monitoring

- **Comprehensive Reporting**
    - Daily, weekly, and monthly statistics
    - Occupancy rates and trends
    - Average visit duration
    - Space utilization metrics

- **Data Integrity**
    - Soft delete for records with dependencies
    - Validation of business rules
    - Transaction management

### Technical Features

- RESTful API design
- OpenAPI 3.0 specification
- Profile-based configuration (dev, test, prod)
- Internationalized responses
- Exception handling
- Code coverage with JaCoCo
- Code quality analysis with SonarQube

## 🏗️ Architecture

### Layered Architecture

```
┌─────────────────────────────────────┐
│         Presentation Layer          │
│    (Controllers + DTOs)             │
├─────────────────────────────────────┤
│         Business Layer              │
│         (Services)                  │
├─────────────────────────────────────┤
│         Persistence Layer           │
│    (Repositories + Entities)        │
├─────────────────────────────────────┤
│         Database Layer              │
│         (PostgreSQL)                │
└─────────────────────────────────────┘
```

### Design Patterns

- **Repository Pattern**: Data access abstraction
- **DTO Pattern**: Data transfer between layers
- **Service Layer**: Business logic encapsulation
- **Dependency Injection**: Inversion of control
- **Builder Pattern**: Object construction

## 🛠️ Technologies

### Backend

- **Java 21**: Latest LTS version
- **Spring Boot 3.2.0**: Application framework
- **Spring Data JPA**: Data persistence
- **Spring Security**: Authentication & Authorization
- **PostgreSQL**: Production database
- **H2**: In-memory database for testing

### Security

- **JWT (jjwt 0.12.3)**: Token-based authentication
- **BCrypt**: Password hashing
- **Spring Security**: Security framework

### Documentation

- **SpringDoc OpenAPI 3 (2.3.0)**: API documentation
- **Swagger UI**: Interactive API testing

### Monitoring & Metrics

- **Spring Actuator**: Application monitoring
- **Micrometer**: Metrics collection
- **Prometheus**: Metrics storage and querying

### Testing

- **JUnit 5**: Unit testing framework
- **Mockito**: Mocking framework
- **AssertJ**: Fluent assertions
- **Spring Security Test**: Security testing

### Code Quality

- **JaCoCo**: Code coverage
- **SonarQube**: Code quality analysis
- **Lombok**: Boilerplate reduction

### Build & Development

- **Maven**: Build automation
- **Git**: Version control

## 📦 Prerequisites

Before you begin, ensure you have the following installed:

- **Java Development Kit (JDK) 21** or higher
  ```bash
  java -version
  # Should output: openjdk version "21" or higher
  ```

- **Maven 3.8+**
  ```bash
  mvn -version
  # Should output: Apache Maven 3.8.x or higher
  ```

- **PostgreSQL 14+** (for production)
  ```bash
  psql --version
  # Should output: psql (PostgreSQL) 14.x or higher
  ```

- **Git**
  ```bash
  git --version
  ```

## 🚀 Installation

### 1. Clone the Repository

```bash
git clone https://github.com/victor-rivas-dev/space-management.git
cd space-management
```

### 2. Database Setup

#### For Development (PostgreSQL)

```bash
# Create database
createdb education_spaces_db

# Or using psql
psql -U postgres
CREATE DATABASE education_spaces_db;
\q
```

#### For Testing (H2)

No setup required - H2 runs in-memory automatically during tests.

### 3. Install Dependencies

```bash
mvn clean install
```

## ⚙️ Configuration

### Environment Variables

Create a `.env` file in the root directory (for production):

```bash
# Database Configuration
DATABASE_URL=jdbc:postgresql://localhost:5432/education_spaces_db
DATABASE_USERNAME=postgres
DATABASE_PASSWORD=your_password

# JWT Configuration
JWT_SECRET=your_very_secure_jwt_secret_key_at_least_32_characters_long
JWT_EXPIRATION=3600000

# CORS Configuration
CORS_ALLOWED_ORIGIN_1=https://yourdomain.com
CORS_ALLOWED_ORIGIN_2=https://www.yourdomain.com

# Server Configuration
SERVER_PORT=8080

# Swagger Configuration (optional)
SWAGGER_ENABLED=false
```

### Application Profiles

The application supports three profiles:

#### Development Profile (`dev`)

**File**: `src/main/resources/application-dev.yaml`

- Uses PostgreSQL database
- Swagger UI enabled
- Detailed logging
- CORS permissive for local development
- JWT token valid for 24 hours

```bash
# Activate dev profile
mvn spring-boot:run -Dspring-boot.run.profiles=dev
```

#### Test Profile (`test`)

**File**: `src/main/resources/application-test.yaml`

- Uses H2 in-memory database
- Swagger disabled
- Auto-activated during tests
- CORS fully open for testing

```bash
# Run tests
mvn test
```

#### Production Profile (`prod`)

**File**: `src/main/resources/application-prod.yaml`

- Uses PostgreSQL with environment variables
- Swagger disabled by default
- Minimal logging
- Restricted CORS
- JWT token valid for 1 hour
- Connection pooling optimized

```bash
# Run in production
export SPRING_PROFILES_ACTIVE=prod
java -jar target/space-management-0.0.1-SNAPSHOT.jar
```

## 🏃 Running the Application

### Development Mode

```bash
# Using Maven
mvn spring-boot:run -Dspring-boot.run.profiles=dev

# Or
mvn clean package
java -jar target/space-management-0.0.1-SNAPSHOT.jar --spring.profiles.active=dev
```

The application will start on `http://localhost:8081`

### Production Mode

```bash
# Set environment variables
export SPRING_PROFILES_ACTIVE=prod
export JWT_SECRET=your_secret_key
export DATABASE_PASSWORD=your_db_password

# Run application
java -jar target/space-management-0.0.1-SNAPSHOT.jar
```

### Docker (Optional)

```dockerfile
FROM eclipse-temurin:21-jre-alpine
WORKDIR /app
COPY target/*.jar app.jar

ENV SPRING_PROFILES_ACTIVE=prod
ENV SERVER_PORT=8080

EXPOSE 8080
ENTRYPOINT ["java", "-jar", "app.jar"]
```

```bash
# Build image
docker build -t space-management:latest .

# Run container
docker run -p 8080:8080 \
  -e JWT_SECRET=your_secret \
  -e DATABASE_URL=jdbc:postgresql://host.docker.internal:5432/education_spaces_db \
  -e DATABASE_PASSWORD=your_password \
  space-management:latest
```

## 📚 API Documentation

### Swagger UI

Access the interactive API documentation:

```
http://localhost:8081/swagger-ui.html
```

### OpenAPI Specification

- **JSON**: `http://localhost:8081/api-docs`
- **YAML**: `http://localhost:8081/api-docs.yaml`

### Quick Start with Swagger

1. Open Swagger UI in your browser
2. Navigate to **Authentication** → **POST /api/auth/login**
3. Click **"Try it out"**
4. Enter credentials:
   ```json
   {
     "username": "admin",
     "password": "admin123"
   }
   ```
5. Click **"Execute"** and copy the token
6. Click **"Authorize"** button (🔓) at the top
7. Enter: `Bearer {your-token}`
8. Now you can test all protected endpoints!

### Postman Collection

Import the OpenAPI spec into Postman:

```bash
# Download spec
curl http://localhost:8081/api-docs > space-management-api.json

# Import in Postman: File → Import → Upload Files
```

## 🧪 Testing

### Run All Tests

```bash
mvn test
```

### Run Specific Test Class

```bash
mvn test -Dtest=StudentServiceTest
```

### Run Integration Tests

```bash
mvn test -Dtest=*IntegrationTest
```

### Generate Coverage Report

```bash
mvn clean test jacoco:report
```

View report at: `target/site/jacoco/index.html`

### Code Quality Analysis

```bash
# Start SonarQube (if running locally)
docker run -d --name sonarqube -p 9000:9000 sonarqube:latest

# Run analysis
mvn clean verify sonar:sonar
```

View report at: `http://localhost:9000`

### Test Coverage Summary

The project maintains the following coverage thresholds:

- **Line Coverage**: Minimum 50%
- **Excludes**: DTOs, Entities, Configuration classes

## 📊 Monitoring

### Actuator Endpoints

Available at `http://localhost:8081/actuator`

| Endpoint | Description |
|----------|-------------|
| `/actuator/health` | Application health status |
| `/actuator/info` | Application information |
| `/actuator/metrics` | Application metrics |
| `/actuator/prometheus` | Prometheus metrics |
| `/actuator/env` | Environment properties |
| `/actuator/loggers` | Logger configuration |

### Health Check

```bash
curl http://localhost:8081/actuator/health
```

**Response:**
```json
{
  "status": "UP",
  "components": {
    "customHealth": {
      "status": "UP",
      "details": {
        "students": 10,
        "spaces": 5,
        "database": "Connected"
      }
    },
    "db": {
      "status": "UP"
    }
  }
}
```

### Metrics

```bash
# View all metrics
curl http://localhost:8081/actuator/metrics

# Specific metric
curl http://localhost:8081/actuator/metrics/space.entry.total
```

### Prometheus Integration

The application exposes Prometheus-compatible metrics:

```bash
curl http://localhost:8081/actuator/prometheus
```

**Sample metrics:**
- `space_entry_total`: Total number of space entries
- `space_exit_total`: Total number of space exits
- `students_active_total`: Current active students
- `spaces_available_total`: Available spaces
- `access_active_current`: Current active accesses

## 🔐 Security

### Authentication Flow

1. **Register** a new student:
   ```bash
   POST /api/auth/register
   ```

2. **Login** to get JWT token:
   ```bash
   POST /api/auth/login
   ```

3. **Use token** in subsequent requests:
   ```bash
   Authorization: Bearer {token}
   ```

### User Roles

| Role | Permissions |
|------|-------------|
| **STUDENT** | View own data, register access, view reports |
| **ADMIN** | Full access to all endpoints |

### Security Features

- ✅ Password hashing with BCrypt
- ✅ JWT token-based authentication
- ✅ Token expiration (1 hour in prod, 24 hours in dev)
- ✅ Role-based access control (RBAC)
- ✅ CORS protection
- ✅ CSRF protection disabled (stateless REST API)
- ✅ SQL injection prevention (JPA/Hibernate)
- ✅ Input validation

### Default Users

For development/testing purposes:

```json
{
  "username": "admin",
  "password": "admin123",
  "role": "ADMIN"
}
```

**⚠️ Warning**: Change default credentials in production!

## 📁 Project Structure

```
space-management/
├── src/
│   ├── main/
│   │   ├── java/dev/victor_rivas/space_management/
│   │   │   ├── config/              # Configuration classes
│   │   │   │   ├── AppInfoContributor.java
│   │   │   │   ├── CorsProperties.java
│   │   │   │   ├── CustomHealthIndicator.java
│   │   │   │   ├── MetricsConfig.java
│   │   │   │   └── OpenApiConfig.java
│   │   │   ├── constant/            # Constants
│   │   │   │   └── ExceptionMessagesConstants.java
│   │   │   ├── controller/          # REST Controllers
│   │   │   │   ├── AccessRecordController.java
│   │   │   │   ├── AuthController.java
│   │   │   │   ├── ReportController.java
│   │   │   │   ├── SpaceController.java
│   │   │   │   └── StudentController.java
│   │   │   ├── enums/               # Enumerations
│   │   │   │   ├── AccessStatus.java
│   │   │   │   ├── Role.java
│   │   │   │   ├── SpaceStatus.java
│   │   │   │   ├── SpaceType.java
│   │   │   │   └── StudentStatus.java
│   │   │   ├── exception/           # Exception handling
│   │   │   │   ├── BusinessException.java
│   │   │   │   ├── ErrorResponse.java
│   │   │   │   ├── GlobalExceptionHandler.java
│   │   │   │   └── ResourceNotFoundException.java
│   │   │   ├── model/
│   │   │   │   ├── dto/             # Data Transfer Objects
│   │   │   │   │   ├── AccessRecordDTO.java
│   │   │   │   │   ├── ApiResponse.java
│   │   │   │   │   ├── AuthResponse.java
│   │   │   │   │   ├── CreateStudentRequest.java
│   │   │   │   │   ├── EntryRequest.java
│   │   │   │   │   ├── ExitRequest.java
│   │   │   │   │   ├── LoginRequest.java
│   │   │   │   │   ├── OccupancyReportDTO.java
│   │   │   │   │   ├── SpaceDTO.java
│   │   │   │   │   └── StudentDTO.java
│   │   │   │   └── entity/          # JPA Entities
│   │   │   │       ├── AccessRecord.java
│   │   │   │       ├── Space.java
│   │   │   │       ├── Student.java
│   │   │   │       └── User.java
│   │   │   ├── repository/          # Data Access Layer
│   │   │   │   ├── AccessRecordRepository.java
│   │   │   │   ├── SpaceRepository.java
│   │   │   │   ├── StudentRepository.java
│   │   │   │   └── UserRepository.java
│   │   │   ├── security/            # Security Configuration
│   │   │   │   ├── CustomUserDetailsService.java
│   │   │   │   ├── JwtAuthenticationFilter.java
│   │   │   │   ├── JwtTokenProvider.java
│   │   │   │   └── SecurityConfig.java
│   │   │   ├── service/             # Business Logic
│   │   │   │   ├── AccessRecordService.java
│   │   │   │   ├── AuthService.java
│   │   │   │   ├── MetricsService.java
│   │   │   │   ├── ReportService.java
│   │   │   │   ├── SpaceService.java
│   │   │   │   └── StudentService.java
│   │   │   └── SpaceManagementApplication.java
│   │   └── resources/
│   │       ├── application.yaml           # Base configuration
│   │       ├── application-dev.yaml       # Development config
│   │       ├── application-test.yaml      # Test config
│   │       └── application-prod.yaml      # Production config
│   └── test/
│       └── java/dev/victor_rivas/space_management/
│           ├── integration/               # Integration tests
│           │   ├── AccessRecordControllerIntegrationTest.java
│           │   ├── AuthControllerIntegrationTest.java
│           │   ├── ReportControllerIntegrationTest.java
│           │   ├── SpaceControllerIntegrationTest.java
│           │   └── StudentControllerIntegrationTest.java
│           └── SpaceManagementApplicationTests.java
├── .env.example                      # Environment variables template
├── .gitignore
├── pom.xml                          # Maven configuration
├── prometheus.yml                   # Prometheus configuration
├── sonar-project.properties         # SonarQube configuration
└── README.md
```

## 🌐 API Endpoints

### Authentication

| Method | Endpoint | Description | Auth |
|--------|----------|-------------|------|
| POST | `/api/auth/login` | Login and get JWT token | Public |
| POST | `/api/auth/register` | Register new student | Public |

### Students

| Method | Endpoint | Description | Auth |
|--------|----------|-------------|------|
| GET | `/api/students` | Get all students | ADMIN |
| GET | `/api/students/{id}` | Get student by ID | Authenticated |
| PUT | `/api/students/{id}` | Update student | Authenticated |
| DELETE | `/api/students/{id}` | Delete student | ADMIN |

### Spaces

| Method | Endpoint | Description | Auth |
|--------|----------|-------------|------|
| GET | `/api/spaces` | Get all spaces | Authenticated |
| GET | `/api/spaces/{id}` | Get space by ID | Authenticated |
| POST | `/api/spaces` | Create new space | ADMIN |
| PUT | `/api/spaces/{id}` | Update space | ADMIN |
| DELETE | `/api/spaces/{id}` | Delete space | ADMIN |

### Access Records

| Method | Endpoint | Description | Auth |
|--------|----------|-------------|------|
| POST | `/api/access/entry` | Register entry | Authenticated |
| POST | `/api/access/exit` | Register exit | Authenticated |
| GET | `/api/access` | Get all records | ADMIN |
| GET | `/api/access/student/{id}` | Get records by student | Authenticated |
| GET | `/api/access/space/{id}` | Get records by space | Authenticated |
| GET | `/api/access/active` | Get active records | Authenticated |

### Reports

| Method | Endpoint | Description | Auth |
|--------|----------|-------------|------|
| GET | `/api/reports/occupancy` | Get occupancy for all spaces | Authenticated |
| GET | `/api/reports/occupancy/space/{id}` | Get occupancy by space | Authenticated |

### Example Requests

#### Register Student
```bash
curl -X POST http://localhost:8081/api/auth/register \
  -H "Content-Type: application/json" \
  -d '{
    "registrationNumber": "STU001",
    "name": "John Doe",
    "email": "john.doe@university.edu",
    "password": "password123",
    "phoneNumber": "+1234567890"
  }'
```

#### Login
```bash
curl -X POST http://localhost:8081/api/auth/login \
  -H "Content-Type: application/json" \
  -d '{
    "username": "STU001",
    "password": "password123"
  }'
```

#### Create Space (Admin)
```bash
curl -X POST http://localhost:8081/api/spaces \
  -H "Authorization: Bearer {token}" \
  -H "Content-Type: application/json" \
  -d '{
    "code": "LAB-001",
    "name": "Computer Laboratory",
    "type": "LABORATORY",
    "capacity": 30,
    "building": "Building A",
    "floor": "1st Floor"
  }'
```

#### Register Entry
```bash
curl -X POST http://localhost:8081/api/access/entry \
  -H "Authorization: Bearer {token}" \
  -H "Content-Type: application/json" \
  -d '{
    "studentId": 1,
    "spaceId": 1,
    "notes": "Study session"
  }'
```

## 🗄️ Database Schema

### Entity Relationship Diagram

```
┌─────────────┐       ┌──────────────┐       ┌─────────────┐
│   Student   │       │ AccessRecord │       │    Space    │
├─────────────┤       ├──────────────┤       ├─────────────┤
│ id (PK)     │───┐   │ id (PK)      │   ┌───│ id (PK)     │
│ regNumber   │   └──→│ student_id   │   │   │ code        │
│ name        │       │ space_id     │←──┘   │ name        │
│ email       │       │ entryTime    │       │ type        │
│ phoneNumber │       │ exitTime     │       │ capacity    │
│ status      │       │ status       │       │ building    │
│ createdAt   │       │ notes        │       │ floor       │
└─────────────┘       │ createdAt    │       │ status      │
                      └──────────────┘       └─────────────┘
       │
       │
       ↓
┌─────────────┐
│    User     │
├─────────────┤
│ id (PK)     │
│ username    │
│ email       │
│ password    │
│ role        │
│ student_id  │
│ enabled     │
└─────────────┘
```

### Tables

#### students
```sql
CREATE TABLE students (
    id BIGSERIAL PRIMARY KEY,
    registration_number VARCHAR(20) UNIQUE NOT NULL,
    name VARCHAR(100) NOT NULL,
    email VARCHAR(100) UNIQUE NOT NULL,
    password VARCHAR(255) NOT NULL,
    phone_number VARCHAR(20),
    status VARCHAR(20) NOT NULL,
    created_at TIMESTAMP NOT NULL,
    updated_at TIMESTAMP
);
```

#### spaces
```sql
CREATE TABLE spaces (
    id BIGSERIAL PRIMARY KEY,
    code VARCHAR(50) UNIQUE NOT NULL,
    name VARCHAR(100) NOT NULL,
    type VARCHAR(20) NOT NULL,
    capacity INTEGER NOT NULL,
    building VARCHAR(50),
    floor VARCHAR(20),
    description VARCHAR(500),
    status VARCHAR(20) NOT NULL,
    created_at TIMESTAMP NOT NULL,
    updated_at TIMESTAMP
);
```

#### access_records
```sql
CREATE TABLE access_records (
    id BIGSERIAL PRIMARY KEY,
    student_id BIGINT NOT NULL,
    space_id BIGINT NOT NULL,
    entry_time TIMESTAMP NOT NULL,
    exit_time TIMESTAMP,
    status VARCHAR(20) NOT NULL,
    notes VARCHAR(500),
    created_at TIMESTAMP NOT NULL,
    FOREIGN KEY (student_id) REFERENCES students(id),
    FOREIGN KEY (space_id) REFERENCES spaces(id)
);
```

#### users
```sql
CREATE TABLE users (
    id BIGSERIAL PRIMARY KEY,
    username VARCHAR(50) UNIQUE NOT NULL,
    email VARCHAR(100) UNIQUE NOT NULL,
    password VARCHAR(255) NOT NULL,
    role VARCHAR(20) NOT NULL,
    enabled BOOLEAN NOT NULL,
    student_id BIGINT,
    created_at TIMESTAMP NOT NULL,
    updated_at TIMESTAMP,
    FOREIGN KEY (student_id) REFERENCES students(id)
);
```

## 🤝 Contributing

Contributions are welcome! Please follow these steps:

### 1. Fork the Repository

Click the "Fork" button at the top right of the repository page.

### 2. Clone Your Fork

```bash
git clone https://github.com/your-username/space-management.git
cd space-management
```

### 3. Create a Branch

```bash
git checkout -b feature/your-feature-name
```

### 4. Make Your Changes

- Write clean, documented code
- Follow existing code style
- Add tests for new features
- Update documentation

### 5. Run Tests

```bash
mvn clean test
```

### 6. Commit Your Changes

```bash
git add .
git commit -m "Add: description of your changes"
```

### 7. Push to Your Fork

```bash
git push origin feature/your-feature-name
```

### 8. Create a Pull Request

Go to the original repository and create a pull request from your fork.

### Coding Standards

- **Java**: Follow Java Code Conventions
- **Naming**: Use descriptive names for classes, methods, and variables
- **Comments**: Document complex logic and public APIs
- **Tests**: Maintain minimum 50% code coverage
- **Commits**: Use clear, descriptive commit messages

## 📄 License

This project is licensed under the Apache License 2.0 - see the [LICENSE](LICENSE) file for details.

```
Copyright 2024 Victor Rivas

Licensed under the Apache License, Version 2.0 (the "License");
you may not use this file except in compliance with the License.
You may obtain a copy of the License at

    http://www.apache.org/licenses/LICENSE-2.0

Unless required by applicable law or agreed to in writing, software
distributed under the License is distributed on an "AS IS" BASIS,
WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
See the License for the specific language governing permissions and
limitations under the License.
```

## 👥 Authors

- **Victor Rivas** - *Initial work* - [@victor-rivas-dev](https://github.com/victor-rivas-dev)

## 🗺️ Roadmap

### Version 1.1 (Planned)

- [ ] WebSocket support for real-time updates
- [ ] Email notifications
- [ ] PDF report generation
- [ ] Advanced filtering and search
- [ ] Space reservation system

### Version 2.0 (Future)

- [ ] Mobile application
- [ ] QR code access control
- [ ] Integration with university systems
- [ ] Machine learning for occupancy prediction
- [ ] Multi-tenancy support