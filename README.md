# 🏦 Finance Data Processing & Access Control Backend

A production-grade Spring Boot backend for a fintech dashboard with role-based access control (RBAC), financial records management, and analytical dashboard APIs.

## 📋 Table of Contents

- [Overview](#overview)
- [Architecture](#architecture)
- [Tech Stack](#tech-stack)
- [Setup Instructions](#setup-instructions)
- [API Documentation](#api-documentation)
- [Role Permissions](#role-permissions)
- [Seeded Test Data](#seeded-test-data)
- [Assumptions](#assumptions)
- [Edge Cases Handled](#edge-cases-handled)
- [Future Improvements](#future-improvements)

---

## Overview

This backend system serves a fintech dashboard where multiple users with different roles manage financial records and access analytical insights. It enforces strict role-based access control, provides CRUD operations for financial data, and delivers aggregated dashboard analytics.

**Key Features:**
- JWT-based stateless authentication
- Three-tier RBAC (Viewer, Analyst, Admin)
- Financial records CRUD with filtering & pagination
- Dashboard analytics (summary, category breakdown, monthly trends)
- Global exception handling with structured error responses
- Input validation with Hibernate Validator

---

## Architecture

```
Controller → Service → Repository → Database
```

### Layered Architecture

| Layer | Responsibility |
|-------|---------------|
| **Controller** | HTTP request/response handling, input validation, no business logic |
| **Service** | Core business logic, data processing, aggregations |
| **Repository** | Database interaction via Spring Data JPA |
| **Security** | JWT authentication, role-based authorization |
| **Exception** | Global error handling with structured responses |

### Project Structure

```
com.zorvyn.finance
├── config/          # DataSeeder (CommandLineRunner)
├── controller/      # AuthController, UserController, FinancialRecordController, DashboardController
├── dto/             # Request/Response DTOs with validation annotations
├── exception/       # Custom exceptions + GlobalExceptionHandler
├── model/           # JPA entities (User, FinancialRecord) + Enums (Role, TransactionType)
├── repository/      # JPA repositories with custom JPQL queries
├── security/        # SecurityConfig, JwtUtil, JwtAuthenticationFilter, CustomUserDetailsService
└── service/         # AuthService, UserService, FinancialRecordService, DashboardService
```

---

## Tech Stack

| Technology | Purpose |
|-----------|---------|
| Java 17 | Language |
| Spring Boot 3.2.5 | Application framework |
| Spring Security | Authentication & authorization |
| Spring Data JPA | ORM (Hibernate) |
| H2 Database | In-memory database |
| JWT (jjwt 0.12.5) | Stateless authentication |
| Lombok | Boilerplate reduction |
| Hibernate Validator | Input validation |
| Maven | Build tool |

---

## Setup Instructions

### Prerequisites

- **Java 17+** (JDK)
- **Maven 3.8+**

### Run the Application

```bash
# Clone the repository
git clone <repository-url>
cd finance-backend-rbac-springboot

# Build and run
mvn clean install
mvn spring-boot:run
```

The application starts on **http://localhost:8080**.

### H2 Database Console

Access the in-memory database at **http://localhost:8080/h2-console**:
- JDBC URL: `jdbc:h2:mem:financedb`
- Username: `sa`
- Password: *(empty)*

---

## API Documentation

### 🔑 Authentication

| Method | Endpoint | Description | Access |
|--------|----------|-------------|--------|
| POST | `/api/auth/register` | Register new user (default: VIEWER role) | Public |
| POST | `/api/auth/login` | Login and receive JWT | Public |

**Register:**
```json
POST /api/auth/register
{
  "name": "John Doe",
  "email": "john@example.com",
  "password": "password123"
}
```

**Login:**
```json
POST /api/auth/login
{
  "email": "admin@zorvyn.com",
  "password": "admin123"
}
```

**Response:**
```json
{
  "token": "eyJhbGciOiJIUzM4NCJ9...",
  "email": "admin@zorvyn.com",
  "name": "Admin User",
  "role": "ROLE_ADMIN"
}
```

> **Note:** Include the JWT in all subsequent requests as `Authorization: Bearer <token>`

---

### 👤 User Management (ADMIN only)

| Method | Endpoint | Description |
|--------|----------|-------------|
| POST | `/api/users` | Create user with specific role |
| GET | `/api/users` | List all users |
| GET | `/api/users/{id}` | Get user by ID |
| PATCH | `/api/users/{id}/status` | Activate/deactivate user |

**Create User:**
```json
POST /api/users
{
  "name": "Jane Analyst",
  "email": "jane@zorvyn.com",
  "password": "password123",
  "role": "ROLE_ANALYST"
}
```

**Update Status:**
```json
PATCH /api/users/{id}/status
{
  "active": false
}
```

---

### 💰 Financial Records

| Method | Endpoint | Description | Access |
|--------|----------|-------------|--------|
| POST | `/api/records` | Create record | ADMIN |
| GET | `/api/records` | List records (filtered, paginated) | ANALYST, ADMIN |
| GET | `/api/records/{id}` | Get record by ID | ANALYST, ADMIN |
| PUT | `/api/records/{id}` | Update record | ADMIN |
| DELETE | `/api/records/{id}` | Delete record | ADMIN |

**Create/Update Record:**
```json
{
  "amount": 5000.00,
  "type": "INCOME",
  "category": "Salary",
  "date": "2024-01-15",
  "note": "Monthly salary"
}
```

**Query Parameters for GET /api/records:**
| Param | Type | Description |
|-------|------|-------------|
| `type` | INCOME/EXPENSE | Filter by type |
| `category` | String | Filter by category |
| `startDate` | yyyy-MM-dd | Start date filter |
| `endDate` | yyyy-MM-dd | End date filter |
| `page` | int (default: 0) | Page number |
| `size` | int (default: 20) | Page size |

---

### 📊 Dashboard

| Method | Endpoint | Description | Access |
|--------|----------|-------------|--------|
| GET | `/api/dashboard/summary` | Total income, expense, net balance | ANALYST, ADMIN |
| GET | `/api/dashboard/category-breakdown` | Grouped totals by category | ANALYST, ADMIN |
| GET | `/api/dashboard/recent` | Last 10 transactions | All authenticated |
| GET | `/api/dashboard/monthly-trend` | Monthly income/expense trends | ANALYST, ADMIN |

**Summary Response:**
```json
{
  "totalIncome": 23000.00,
  "totalExpense": 4750.00,
  "netBalance": 18250.00
}
```

---

## Role Permissions

| Endpoint | VIEWER | ANALYST | ADMIN |
|----------|--------|---------|-------|
| `GET /api/dashboard/recent` | ✅ | ✅ | ✅ |
| `GET /api/dashboard/summary` | ❌ | ✅ | ✅ |
| `GET /api/dashboard/category-breakdown` | ❌ | ✅ | ✅ |
| `GET /api/dashboard/monthly-trend` | ❌ | ✅ | ✅ |
| `GET /api/records` | ❌ | ✅ | ✅ |
| `POST /api/records` | ❌ | ❌ | ✅ |
| `PUT /api/records/{id}` | ❌ | ❌ | ✅ |
| `DELETE /api/records/{id}` | ❌ | ❌ | ✅ |
| `GET /api/users` | ❌ | ❌ | ✅ |
| `POST /api/users` | ❌ | ❌ | ✅ |
| `PATCH /api/users/{id}/status` | ❌ | ❌ | ✅ |

---

## Seeded Test Data

On startup, the database is automatically seeded with:

### Users

| Role | Email | Password |
|------|-------|----------|
| ADMIN | admin@zorvyn.com | admin123 |
| ANALYST | analyst@zorvyn.com | analyst123 |
| VIEWER | viewer@zorvyn.com | viewer123 |

### Financial Records

12 sample records across categories: Salary, Rent, Freelance, Utilities, Software, Investment, Travel, Office Supplies, Consulting, Marketing.

---

## Assumptions

1. **Self-registration defaults to VIEWER role** — only ADMINs can assign ANALYST/ADMIN roles
2. **H2 in-memory database** — data resets on restart; suitable for evaluation
3. **JWT token expires in 24 hours** — configurable via `app.jwt.expiration-ms`
4. **Financial records are not user-scoped for reading** — ANALYST/ADMIN can see all records
5. **Deactivated users cannot authenticate** — CustomUserDetailsService checks active status
6. **No soft-delete** — records are permanently deleted

---

## Edge Cases Handled

| Scenario | Handling |
|----------|---------|
| Invalid input (negative amount, missing fields) | 400 with field-level error messages |
| Duplicate email registration | 409 Conflict |
| Record not found | 404 with descriptive message |
| Unauthorized access | 403 Forbidden |
| Invalid JWT token | Request proceeds unauthenticated → 401/403 |
| Inactive user login | Authentication fails |
| Empty dataset aggregation | Returns 0.0 via `COALESCE` in queries |
| Invalid enum values | 400 Bad Request |

---

## Future Improvements

- **PostgreSQL** migration for production persistence
- **Soft delete** with `deletedAt` timestamp
- **Swagger/OpenAPI** documentation
- **Rate limiting** for API protection
- **Unit & integration tests** with Spring Boot Test
- **Audit logging** for financial record changes
- **User password reset** functionality
- **Refresh token** mechanism
- **Docker** containerization
