# 🏦 Finance Data Processing & Access Control Backend

A production-grade Spring Boot backend for a fintech dashboard with role-based access control (RBAC), financial records management, and analytical dashboard APIs.

## 📋 Table of Contents

- [Overview](#overview)
- [Why This Design](#why-this-design)
- [Architecture & Request Flow](#architecture--request-flow)
- [Data Modeling Decisions](#data-modeling-decisions)
- [Tech Stack](#tech-stack)
- [Setup Instructions](#setup-instructions)
- [API Documentation](#api-documentation)
- [Access Control Strategy](#access-control-strategy)
- [Seeded Test Data](#seeded-test-data)
- [Security Considerations](#security-considerations)
- [Observability](#observability)
- [Failure Handling Strategy](#failure-handling-strategy)
- [Trade-offs](#trade-offs)
- [Scalability Considerations](#scalability-considerations)

---

## Overview

This backend system serves a fintech dashboard where multiple users with different roles manage financial records and access analytical insights. It enforces strict role-based access control, provides CRUD operations for financial data, and delivers aggregated dashboard analytics.

**Key Features:**
- JWT-based stateless authentication
- Three-tier RBAC (Viewer, Analyst, Admin)
- Financial records CRUD with filtering & pagination
- Dashboard analytics (summary, category breakdown, monthly trends)
- Swagger/OpenAPI documentation with JWT integration
- Global exception handling with structured error responses
- Input validation with Hibernate Validator
- PostgreSQL support via Spring profiles

---

## Why This Design

### Layered Architecture
The project follows a strict **Controller → Service → Repository** pattern. This separation ensures that:
- **Controllers** handle only HTTP concerns (request parsing, response formatting, input validation) — zero business logic
- **Services** encapsulate all business rules, data processing, and aggregation logic
- **Repositories** are the single point of database interaction via Spring Data JPA

This keeps each layer independently testable and replaceable. A controller change (e.g., adding GraphQL) doesn't touch business logic. A database migration (e.g., H2 → PostgreSQL) doesn't touch controllers.

### Why JWT
Stateless authentication via JWT was chosen over session-based auth for several reasons:
- **No server-side session storage** — the token carries all necessary claims (email, role), making it horizontally scalable
- **Decoupled auth** — any service or API gateway can validate the token independently
- **Mobile/SPA friendly** — JWT tokens work naturally with frontend frameworks and mobile apps

### Why RBAC
A role-based model (`VIEWER`, `ANALYST`, `ADMIN`) was chosen over attribute-based (ABAC) or permission-based access control because:
- The access patterns are clearly role-driven (read-only vs. analytics vs. full CRUD)
- It's simple to reason about, audit, and extend
- Spring Security's `@PreAuthorize` integrates cleanly with role-based checks

---

## Architecture & Request Flow

### Layered Architecture

| Layer | Responsibility |
|-------|---------------|
| **Controller** | HTTP request/response handling, input validation, no business logic |
| **Service** | Core business logic, data processing, aggregations |
| **Repository** | Database interaction via Spring Data JPA |
| **Security** | JWT authentication, role-based authorization |
| **Exception** | Global error handling with structured responses |

### Request Flow

```
Client (HTTP Request)
  │
  ├── Authorization: Bearer <JWT>
  │
  ▼
┌──────────────────────────────────┐
│  JwtAuthenticationFilter         │  ① Extract & validate JWT token
│  (OncePerRequestFilter)          │  ② Load UserDetails from DB
│                                  │  ③ Set SecurityContext
└──────────────────┬───────────────┘
                   ▼
┌──────────────────────────────────┐
│  SecurityConfig                  │  ④ Check URL-level authorization
│  (SecurityFilterChain)           │     (permitAll / hasAuthority)
└──────────────────┬───────────────┘
                   ▼
┌──────────────────────────────────┐
│  Controller                      │  ⑤ @PreAuthorize method-level check
│  (@Valid input validation)       │  ⑥ Delegate to service
└──────────────────┬───────────────┘
                   ▼
┌──────────────────────────────────┐
│  Service                         │  ⑦ Execute business logic
│  (@Transactional where needed)   │  ⑧ Call repository
└──────────────────┬───────────────┘
                   ▼
┌──────────────────────────────────┐
│  Repository                      │  ⑨ JPQL / derived queries
│  (Spring Data JPA)               │  ⑩ Return entities
└──────────────────┬───────────────┘
                   ▼
              H2 / PostgreSQL
```

### Project Structure

```
com.zorvyn.finance
├── config/          # OpenApiConfig, DataSeeder
├── controller/      # AuthController, UserController, FinancialRecordController, DashboardController
├── dto/             # Request/Response DTOs with validation & Swagger annotations
├── exception/       # Custom exceptions + GlobalExceptionHandler
├── model/           # JPA entities (User, FinancialRecord) + Enums (Role, TransactionType)
├── repository/      # JPA repositories with custom JPQL queries
├── security/        # SecurityConfig, JwtUtil, JwtAuthenticationFilter, CustomUserDetailsService
└── service/         # AuthService, UserService, FinancialRecordService, DashboardService
```

---

## Data Modeling Decisions

A relational database was chosen to ensure structured financial data storage and transactional consistency.

- A **Many-to-One** relationship is used between `FinancialRecord` and `User` to represent ownership of records — every financial record is traceable to the user who created it
- **Enums** (`TransactionType`, `Role`) are persisted as strings via `@Enumerated(EnumType.STRING)` to enforce domain constraints at both the application and database level
- **UUIDs** are used as primary keys instead of auto-incrementing integers to ensure global uniqueness and support distributed system scalability
- **`@CreationTimestamp`** is used on both entities for automatic audit trails without requiring manual timestamp management

---

## Tech Stack

| Technology | Purpose |
|-----------|---------|
| Java 17 | Language |
| Spring Boot 3.2.5 | Application framework |
| Spring Security | Authentication & authorization |
| Spring Data JPA | ORM (Hibernate) |
| H2 Database | In-memory database (default) |
| PostgreSQL | Production database (optional profile) |
| JWT (jjwt 0.12.5) | Stateless authentication |
| SpringDoc OpenAPI 2.5 | Swagger UI & API documentation |
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
git clone https://github.com/Vatsal-y/finance-backend-rbac-springboot.git
cd finance-backend-rbac-springboot

# Build and run
mvn clean install
mvn spring-boot:run
```

The application starts on **http://localhost:8080**.

### Swagger UI

Interactive API documentation is available at:

```
http://localhost:8080/swagger-ui.html
```

To test protected endpoints:
1. Call `POST /api/auth/login` with seeded credentials
2. Copy the JWT token from the response
3. Click the **Authorize** button in Swagger UI
4. Enter: `Bearer <your-token>`
5. All subsequent requests will include the token

### H2 Database Console

Access the in-memory database at **http://localhost:8080/h2-console**:
- JDBC URL: `jdbc:h2:mem:financedb`
- Username: `sa`
- Password: *(empty)*

### PostgreSQL (Optional)

To run with PostgreSQL locally instead of H2:

1. Ensure PostgreSQL is running with a database named `finance_db`
2. Update `src/main/resources/application-postgres.properties` with your credentials
3. Run with the postgres profile:

```bash
mvn spring-boot:run -Dspring-boot.run.profiles=postgres
```

---

## API Documentation

All API responses follow a consistent structure using a standardized `ApiResponse<T>` wrapper, ensuring uniform handling across all clients.

### API Response Format

Successful responses:

```json
{
  "success": true,
  "data": { },
  "message": "Operation successful"
}
```

Error responses:

```json
{
  "success": false,
  "message": "Error description",
  "status": 400,
  "timestamp": "2024-01-15T10:30:00",
  "errors": { "field": "error message" }
}
```

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
  "success": true,
  "data": {
    "token": "eyJhbGciOiJIUzM4NCJ9...",
    "email": "admin@zorvyn.com",
    "name": "Admin User",
    "role": "ROLE_ADMIN"
  },
  "message": "Login successful"
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

---

### 💰 Financial Records

| Method | Endpoint | Description | Access |
|--------|----------|-------------|--------|
| POST | `/api/records` | Create record | ADMIN |
| GET | `/api/records` | List records (filtered, paginated) | ANALYST, ADMIN |
| GET | `/api/records/{id}` | Get record by ID | ANALYST, ADMIN |
| PUT | `/api/records/{id}` | Update record | ADMIN |
| DELETE | `/api/records/{id}` | Delete record | ADMIN |

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

---

## Access Control Strategy

### Role Hierarchy

| Role | Description | Scope |
|------|-------------|-------|
| **VIEWER** | Basic authenticated user | Can view recent transactions only |
| **ANALYST** | Data analyst | Can read all financial data and analytics |
| **ADMIN** | Full access | CRUD on records + user management |

### Permission Matrix

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

### Enforcement Layers

Authorization is enforced at **two levels** for defense in depth:

1. **URL-level** (`SecurityConfig`): Broad rules via `authorizeHttpRequests()` — catches requests before they reach controllers
2. **Method-level** (`@PreAuthorize`): Fine-grained rules on individual controller methods — serves as a second gate

Both layers must pass for a request to succeed, making accidental exposure of sensitive endpoints extremely unlikely.

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

## Security Considerations

### Authentication
- **JWT tokens** are signed with HMAC-SHA384 using a Base64-encoded secret key
- Tokens carry the user's email (as subject) and role (as claim) — no sensitive data in the payload
- Token expiry is set to 24 hours (configurable via `app.jwt.expiration-ms`)
- Invalid or expired tokens are silently rejected — the request continues unauthenticated

### Authorization
- All endpoints (except `/api/auth/**`) require authentication
- Role checks are enforced at both URL and method level (defense in depth)
- Self-registration defaults to `ROLE_VIEWER` — only ADMINs can assign elevated roles

### Account Security
- Passwords are hashed with **BCrypt** (adaptive hashing with salt)
- Deactivated users cannot authenticate — `CustomUserDetailsService` checks `active` status
- Duplicate email registration returns `409 Conflict` without leaking user information

---

## Observability

Structured logging is implemented across all critical system flows to support debugging, monitoring, and operational awareness:

- **Authentication events** — login success, registration, failed login attempts (`WARN`)
- **Financial record operations** — creation, update, and deletion with user and record context
- **Dashboard requests** — analytics queries with result set sizes
- **Exception handling** — `WARN` for client errors (400/404/409), `ERROR` for unexpected server failures with full stack traces

All logging uses SLF4J via Lombok's `@Slf4j` with parameterized messages to avoid string concatenation overhead. This provides a foundation for integration with centralized logging tools (ELK, CloudWatch, etc.) in production.

---

## Failure Handling Strategy

### Input Validation
- All DTOs use Jakarta Validation annotations (`@NotNull`, `@NotBlank`, `@Positive`, `@Email`, `@Size`)
- Validation errors return `400 Bad Request` with field-level error messages:

```json
{
  "success": false,
  "message": "Validation failed",
  "status": 400,
  "errors": {
    "amount": "Amount must be positive",
    "email": "Invalid email format"
  }
}
```

### Exception Handling

| Scenario | HTTP Status | Handling |
|----------|-------------|---------|
| Invalid input (negative amount, missing fields) | 400 | Field-level error messages |
| Invalid credentials | 401 | Generic "Invalid email or password" |
| Insufficient role/permissions | 403 | "You do not have permission" |
| Record/User not found | 404 | Descriptive "not found with id: X" |
| Duplicate email registration | 409 | "Email already registered" |
| Invalid enum values | 400 | Bad Request |
| Unhandled exceptions | 500 | Generic message (details logged server-side) |

### Edge Cases

| Scenario | Handling |
|----------|---------|
| Empty dataset aggregation | Returns `0.0` via `COALESCE` in JPQL queries |
| Invalid JWT token | Request proceeds unauthenticated → 401/403 |
| Inactive user login | Authentication fails at `UserDetailsService` |
| Concurrent updates | Handled by `@Transactional` with default isolation |

---

## Trade-offs

| Decision | Rationale | Alternative |
|----------|-----------|-------------|
| **H2 (default)** | Zero-config setup for development and evaluation | PostgreSQL available via profile switch |
| **No caching** | Data volume is small; premature optimization avoided | Add Spring Cache with Redis for high-traffic scenarios |
| **No soft delete** | Simplicity; hard delete is explicit and predictable | Add `deletedAt` timestamp with `@Where` clause |
| **Records not user-scoped** | All ANALYST/ADMIN users see all records (org-wide dashboard) | Add user-scoping with `@Query` filters |
| **No refresh tokens** | Single-token flow is simpler for evaluation | Add refresh token rotation for production |
| **BCrypt (10 rounds)** | Good balance of security and performance | Increase rounds or switch to Argon2 for higher security |

---

## Scalability Considerations

### Current Design Supports
- **Stateless authentication** — no server-side sessions, enabling horizontal scaling behind a load balancer
- **Spring profiles** — easy switch from H2 to PostgreSQL for production workloads
- **Paginated queries** — financial records are paginated by default, preventing unbounded result sets
- **UUID primary keys** — globally unique IDs support distributed database setups

### Recommended Next Steps for Production
- **Database indexing** — add indexes on `financial_records.date`, `financial_records.type`, and `users.email`
- **Connection pooling** — configure HikariCP pool size based on expected concurrency
- **Database migrations** — replace `ddl-auto` with Flyway or Liquibase for version-controlled schema changes
- **Rate limiting** — protect auth endpoints against brute-force attacks
- **Monitoring** — add Spring Actuator with Prometheus/Grafana for observability
- **Containerization** — Dockerize the application for consistent deployment

---

## Assumptions

1. **Self-registration defaults to VIEWER role** — only ADMINs can assign ANALYST/ADMIN roles
2. **H2 in-memory database** — data resets on restart; suitable for evaluation
3. **JWT token expires in 24 hours** — configurable via `app.jwt.expiration-ms`
4. **Financial records are not user-scoped for reading** — ANALYST/ADMIN can see all records
5. **Deactivated users cannot authenticate** — CustomUserDetailsService checks active status
6. **No soft-delete** — records are permanently deleted
