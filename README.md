# Finance Dashboard Backend

This project is a **Spring Boot–based backend system** designed to manage financial records, enforce role-based access control, and provide dashboard analytics.

It demonstrates clean backend design, secure API development, and real-world features such as JWT authentication, pagination, search, and rate limiting.

---

## 1️⃣ Architecture

The application follows a **layered architecture**:

```
Controller → Service → Repository → Database

↓
DTO ↔ Entity Mapping
↓
Business Logic
↓
Security (JWT + RBAC)
```


### Architectural Highlights

**Controller Layer**
- Exposes REST APIs
- Handles request/response

**Service Layer**
- Contains business logic
- Enforces RBAC rules
- Handles validations

**Repository Layer**
- Uses Spring Data JPA
- Manages database operations

**DTO Layer**
- Prevents direct entity exposure
- Ensures clean API contracts

**Security Layer**
- JWT-based authentication
- Role-based authorization

**Global Exception Handler**
- Centralized error handling

---

## 🛠 Tech Stack

- Java 17 / 21
- Spring Boot
- Spring Security (JWT)
- Spring Data JPA (Hibernate)
- MySQL
- ModelMapper
- Bucket4j (Rate Limiting)
- Swagger / OpenAPI
- Lombok

---

## 📂 Project Structure

```
finance-dashboard/
│
├── controller/
├── service/
│ └── impl/
├── repository/
├── entity/
├── dto/
├── security/
├── config/
├── exception/
├── enums/
├── util/
└── resources/
```



---

## 🔐 Authentication & Authorization

### JWT Flow

1. User logs in using email
2. JWT token is generated
3. Token contains user role
4. All requests must include:





Authorization: Bearer <TOKEN>
---
## 🔑 Role-Based Access Control (RBAC)

```

| Role    | Permissions                         |
|---------|-------------------------------------|
| ADMIN   | Full access (users + records)       |
| ANALYST | Manage records + dashboard          |
| VIEWER  | Read-only dashboard                 |
```

---

## 📊 Core Features

### ✅ User Management
- Create users
- Assign roles
- Update users
- Soft delete users

---

### ✅ Financial Records
- Create records
- Update records
- Delete records
- Filter records (type, category, date)
- Search functionality

---

### ✅ Dashboard APIs

- Total income
- Total expenses
- Net balance
- Category-wise summary
- Recent transactions
- Monthly trends

---

### ✅ Pagination & Sorting


GET /api/v1/records?page=0&size=5&sort=amount,desc


---

### ✅ Search API


GET /api/v1/records/search?keyword=salary


---

### ✅ Soft Delete

- Users are soft deleted
- Financial records are preserved
- Ensures historical data integrity

---

### ✅ Rate Limiting

- Implemented using Bucket4j
- Limit: **10 requests per minute per IP**
- Prevents API abuse

---

### ✅ Logging

- Console + File logging enabled
- Log file:


logs/finance-dashboard.log


---

## 📡 API Endpoints

### Auth
- POST `/api/v1/auth/login`

---

### Users (ADMIN only)
- POST `/api/v1/users`
- PUT `/api/v1/users/{id}`
- DELETE `/api/v1/users/{id}`
- GET `/api/v1/users`

---

### Financial Records
- POST `/api/v1/records`
- GET `/api/v1/records`
- PUT `/api/v1/records/{id}`
- DELETE `/api/v1/records/{id}`
- GET `/api/v1/records/filter`
- GET `/api/v1/records/search`

---

### Dashboard
- GET `/api/v1/dashboard/summary`
- GET `/api/v1/dashboard/recent`
- GET `/api/v1/dashboard/monthly`

---

## ⚙️ Setup & Run Instructions

### Prerequisites
- Java 17+
- Maven
- MySQL

---

### Database Setup

```sql
CREATE DATABASE finance_dashboard;
```
> Configuration

Update application.properties:

```
spring.datasource.url=jdbc:mysql://localhost:3306/finance_dashboard
spring.datasource.username=YOUR_USERNAME
spring.datasource.password=YOUR_PASSWORD
```

- GET `/api/records`
- PUT `/api/records/{id}`
- DELETE `/api/records/{id}`
- GET `/api/records/filter`
- GET `/api/records/search`

---

### Dashboard
- GET `/api/dashboard/summary`
- GET `/api/dashboard/recent`
- GET `/api/dashboard/monthly`

---

## ⚙️ Setup & Run Instructions

### Prerequisites
- Java 17+
- Maven
- MySQL

---

### Database Setup

```sql
CREATE DATABASE finance_dashboard;
```

## 🗄 Database Schema

The application uses a relational database (MySQL) with the following core tables:

---

### 1️⃣ users

Stores user details and role information.

```
| Column       | Type         | Description                          |
|-------------|-------------|--------------------------------------|
| id          | BIGINT (PK) | Unique user ID                       |
| name        | VARCHAR     | User name                            |
| email       | VARCHAR     | Unique email                         |
| password    | VARCHAR     | User password (simplified)           |
| role_id     | BIGINT      | Foreign key to roles table           |
| status      | VARCHAR     | ACTIVE / INACTIVE                    |
| is_deleted  | BOOLEAN     | Soft delete flag                     |
| created_at  | TIMESTAMP   | Creation timestamp                   |
| updated_at  | TIMESTAMP   | Last update timestamp                |
```
	
---

### 2️⃣ roles

Defines user roles in the system.

```
| Column       | Type         | Description                          |
|-------------|-------------|--------------------------------------|
| id          | BIGINT (PK) | Role ID                              |
| name        | VARCHAR     | Role name (ADMIN, ANALYST, VIEWER)   |
| description | VARCHAR     | Role description                     |
```

---

### 3️⃣ financial_records

Stores financial transactions.

```
| Column        | Type         | Description                          |
|--------------|-------------|--------------------------------------|
| id           | BIGINT (PK) | Record ID                            |
| amount       | DECIMAL     | Transaction amount                   |
| type         | VARCHAR     | INCOME / EXPENSE                     |
| category     | VARCHAR     | Transaction category                 |
| record_date  | DATE        | Date of transaction                  |
| notes        | VARCHAR     | Additional notes                     |
| user_id      | BIGINT      | Foreign key to users table           |
| created_by   | BIGINT      | Created by user                      |
| updated_by   | BIGINT      | Updated by user                      |
| created_at   | TIMESTAMP   | Creation timestamp                   |
| updated_at   | TIMESTAMP   | Last update timestamp                |
```

---

### 🔗 Relationships

- `users.role_id → roles.id`
- `financial_records.user_id → users.id`

---

### 🧠 Design Notes

- Foreign keys ensure data integrity
- Soft delete is applied only on users
- Financial records are preserved for historical and analytical purposes


⚙️ Application Configuration (Profiles)

### Spring Profiles

The application uses **profile-based configuration**.

Default profile:

```
spring.profiles.active=local
```

---

### application-local.properties

```properties
# Server
server.port=8088

# Database
spring.datasource.url=jdbc:mysql://localhost:3306/finance_dashboard
spring.datasource.username=YOUR_USERNAME
spring.datasource.password=YOUR_PASSWORD

spring.datasource.driver-class-name=com.mysql.cj.jdbc.Driver

# JPA
spring.jpa.hibernate.ddl-auto=update
spring.jpa.show-sql=true
spring.jpa.properties.hibernate.format_sql=true
```

> Run Application

```
mvn spring-boot:run
```

> Access

```
Application: http://localhost:8088
Swagger UI: http://localhost:8088/swagger-ui/index.html
```

---

## 🧠 Design Decisions
- JWT used for authentication instead of session-based auth
- RBAC enforced using Spring Security
- Soft delete used for users to preserve financial records
- Service layer handles all business logic
- DTOs used for clean API design

---

## ⚠️ Assumptions
- Authentication simplified (email+password based login)
- Single currency system
- Moderate data size (no distributed caching)

---

## ⚖️ Trade-offs
- In-memory rate limiting instead of Redis (simpler setup)
- No refresh tokens implemented

---

## 🧪 Testing
- APIs tested using Swagger and Postman
- JWT authentication validated across endpoints
- Unit testing implemented using JUnit and Mockito
  - Service layer methods tested
  - Repository dependencies mocked
  - Success and failure scenarios covered

---

## 🧹 Code Quality & Debugging

- SonarQube used for static code analysis and code quality checks
- Followed clean coding practices and naming conventions
- Used IDE debugging for troubleshooting and issue resolution
- Ensured proper logging for monitoring and debugging

## 🚀 Future Enhancements
- Add refresh tokens
- Implement Redis-based rate limiting
- Add integration tests
- Add caching for dashboard APIs

---
 