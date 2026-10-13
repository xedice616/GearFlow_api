# GearFlow — Equipment Rental & Booking Platform

GearFlow is a production-style REST API for managing equipment rentals and bookings.

The platform allows users to browse available equipment, check availability for specific date ranges, create and manage bookings, while administrators can manage equipment, users, booking workflows, and platform statistics.

The project focuses on clean backend architecture, secure authentication, role-based authorization, booking conflict prevention, pagination, filtering, validation, testing, and containerized deployment.

---

## Features

### Authentication & Security

- User registration and login
- JWT-based authentication
- BCrypt password hashing
- Role-Based Access Control (RBAC)
- `USER` and `ADMIN` roles
- Protected API endpoints
- Disabled user protection
- Stateless Spring Security configuration

### Equipment Management

Users can:

- Browse active equipment
- View equipment details
- Search equipment by name
- Filter by category
- Filter by minimum and maximum daily price
- Use pagination and sorting
- Check equipment availability for a selected date range

Administrators can:

- Create equipment
- Update equipment
- Disable/remove equipment
- Manage equipment inventory and quantity

### Booking System

Authenticated users can:

- Create bookings
- View their own bookings
- View individual booking details
- Cancel their bookings

GearFlow automatically:

- Calculates booking prices
- Validates booking dates
- Checks equipment quantity
- Prevents overlapping reservations
- Prevents overbooking
- Handles concurrent booking requests using database locking

Bookings follow the workflow:

```text
PENDING → APPROVED → COMPLETED
    │
    └────→ REJECTED

PENDING / APPROVED → CANCELLED
```

### Admin Panel API

Administrators can:

- View all bookings
- Filter bookings by status
- Approve pending bookings
- Reject pending bookings
- Complete approved bookings
- View all users
- Enable users
- Disable users
- View dashboard statistics

The dashboard includes:

- Total users
- Active users
- Active equipment
- Total bookings
- Pending bookings
- Approved bookings
- Completed bookings
- Cancelled bookings
- Rejected bookings
- Total completed booking revenue

---

## Tech Stack

| Technology | Purpose |
|---|---|
| Java 17 | Programming language |
| Spring Boot 4 | Application framework |
| Spring Web MVC | REST API |
| Spring Security | Authentication & authorization |
| JWT | Token-based authentication |
| BCrypt | Password hashing |
| Spring Data JPA | Persistence layer |
| Hibernate | ORM |
| PostgreSQL | Relational database |
| Jakarta Validation | Request validation |
| OpenAPI / Swagger | API documentation |
| JUnit 5 | Testing |
| Mockito | Unit testing |
| Maven | Build & dependency management |
| Docker | Application containerization |
| Docker Compose | Backend + PostgreSQL orchestration |

---

## Architecture

GearFlow follows a layered backend architecture:

```text
Controller
    ↓
Service
    ↓
Repository
    ↓
PostgreSQL
```

Main package structure:

```text
src/main/java/com/xadice/gearflow
│
├── config
├── controller
├── dto
│   ├── request
│   └── response
├── exception
├── model
├── repository
├── security
├── service
└── specification
```

The application uses DTOs instead of exposing JPA entities directly.

---

## Booking Availability Logic

GearFlow uses date-based inventory calculation.

Bookings with the following statuses reserve equipment:

```text
PENDING
APPROVED
```

Rejected, cancelled, and completed bookings do not reserve current inventory.

Two bookings overlap when:

```text
existing.startDate < requested.endDate
AND
existing.endDate > requested.startDate
```

The booking date range uses end-exclusive semantics.

For example:

```text
Booking A: 10 Oct → 12 Oct
Booking B: 12 Oct → 14 Oct
```

These bookings do not overlap.

Available quantity is calculated as:

```text
availableQuantity = totalQuantity - reservedQuantity
```

If the requested quantity exceeds the available quantity, the booking is rejected.

Equipment is locked during booking creation using pessimistic database locking to reduce the risk of concurrent overbooking.

---

## Price Calculation

The total booking price is calculated by the backend.

```text
totalPrice = dailyPrice × numberOfDays × quantity
```

The client does not provide the final price.

`BigDecimal` is used for monetary values.

---

## API Endpoints

Base API path:

```text
/api/v1
```

### Authentication

| Method | Endpoint | Access |
|---|---|---|
| POST | `/api/v1/auth/register` | Public |
| POST | `/api/v1/auth/login` | Public |
| GET | `/api/v1/auth/me` | Authenticated |

### Equipment

| Method | Endpoint | Access |
|---|---|---|
| GET | `/api/v1/equipment` | Authenticated |
| GET | `/api/v1/equipment/{id}` | Authenticated |
| GET | `/api/v1/equipment/{id}/availability` | Authenticated |
| POST | `/api/v1/equipment` | ADMIN |
| PUT | `/api/v1/equipment/{id}` | ADMIN |
| DELETE | `/api/v1/equipment/{id}` | ADMIN |

### Bookings

| Method | Endpoint | Access |
|---|---|---|
| POST | `/api/v1/bookings` | Authenticated |
| GET | `/api/v1/bookings/my` | Authenticated |
| GET | `/api/v1/bookings/{id}` | Authenticated |
| PATCH | `/api/v1/bookings/{id}/cancel` | Authenticated |

### Admin Bookings

| Method | Endpoint | Access |
|---|---|---|
| GET | `/api/v1/admin/bookings` | ADMIN |
| GET | `/api/v1/admin/bookings/{id}` | ADMIN |
| PATCH | `/api/v1/admin/bookings/{id}/approve` | ADMIN |
| PATCH | `/api/v1/admin/bookings/{id}/reject` | ADMIN |
| PATCH | `/api/v1/admin/bookings/{id}/complete` | ADMIN |

### Admin Users

| Method | Endpoint | Access |
|---|---|---|
| GET | `/api/v1/admin/users` | ADMIN |
| GET | `/api/v1/admin/users/{id}` | ADMIN |
| PATCH | `/api/v1/admin/users/{id}/enable` | ADMIN |
| PATCH | `/api/v1/admin/users/{id}/disable` | ADMIN |

### Admin Dashboard

| Method | Endpoint | Access |
|---|---|---|
| GET | `/api/v1/admin/dashboard` | ADMIN |

---

## Pagination, Filtering & Sorting

Equipment supports:

```text
search
category
minPrice
maxPrice
page
size
sort
```

Example:

```http
GET /api/v1/equipment?category=CAMERA&minPrice=10&maxPrice=100&page=0&size=10
```

Admin booking listing also supports pagination and filtering by booking status.

---

## API Documentation

Swagger UI is available locally at:

```text
http://localhost:8080/swagger-ui/index.html
```

OpenAPI specification:

```text
http://localhost:8080/v3/api-docs
```

JWT authentication can be tested directly through Swagger UI using the **Authorize** button.

---

## Running with Docker

### Requirements

- Docker
- Docker Compose

Create a local `.env` file based on `.env.example`.

Example:

```env
DB_PASSWORD=your_database_password
JWT_SECRET=your_base64_jwt_secret

ADMIN_NAME=GearFlow Admin
ADMIN_EMAIL=admin@example.com
ADMIN_PASSWORD=your_secure_admin_password
```

Never commit the real `.env` file.

Start the complete application:

```bash
docker compose up -d --build
```

Check containers:

```bash
docker compose ps
```

View backend logs:

```bash
docker compose logs backend
```

Stop the application:

```bash
docker compose down
```

The backend will be available at:

```text
http://localhost:8080
```

PostgreSQL is exposed locally on:

```text
localhost:7878
```

Inside the Docker network, the backend communicates with PostgreSQL through:

```text
postgres:5432
```

---

## Running Without Docker

Configure the required environment variables:

```text
DB_URL
DB_USERNAME
DB_PASSWORD
JWT_SECRET
ADMIN_NAME
ADMIN_EMAIL
ADMIN_PASSWORD
```

Then run:

```bash
./mvnw spring-boot:run
```

On Windows:

```powershell
.\mvnw.cmd spring-boot:run
```

---

## Testing

Run all tests with:

```bash
./mvnw test
```

On Windows:

```powershell
.\mvnw.cmd test
```

The project includes tests for core booking business logic, admin booking state transitions, authentication, authorization, and protected endpoints.

---

## Security

GearFlow implements:

- Stateless JWT authentication
- BCrypt password hashing
- Role-Based Access Control
- Authentication entry point handling
- Access denied handling
- CORS configuration
- Input validation
- Global exception handling
- Disabled account protection
- Protected admin endpoints

Sensitive configuration is supplied through environment variables and is not stored directly in source code.

---

## Error Handling

The project uses centralized exception handling with `@ControllerAdvice`.

API errors return a consistent response structure containing information such as:

```json
{
  "timestamp": "...",
  "status": 400,
  "error": "Bad Request",
  "message": "...",
  "path": "/api/v1/..."
}
```

Custom exceptions are used for cases such as:

- Resource not found
- Duplicate email
- Invalid operations
- Booking conflicts
- Validation failures

---

## Docker Architecture

```text
                    ┌──────────────────────┐
                    │        Client        │
                    │ Swagger / Frontend   │
                    └──────────┬───────────┘
                               │
                               │ HTTP :8080
                               ▼
                    ┌──────────────────────┐
                    │   GearFlow Backend   │
                    │    Spring Boot       │
                    └──────────┬───────────┘
                               │
                               │ JDBC
                               ▼
                    ┌──────────────────────┐
                    │     PostgreSQL       │
                    │    postgres:5432     │
                    └──────────────────────┘
```

---

## Future Improvements

- Web frontend
- Email notifications
- Equipment reviews and ratings
- Payment integration
- Booking history analytics
- Cloud storage for equipment images
- CI/CD pipeline
- Production monitoring

---

## Author

**Khadija Ahmadova**

Java Backend Developer

GitHub: `xedice616`

---

## Project Status

Backend REST API implemented and Dockerized.

Core authentication, authorization, equipment management, booking management, availability checking, admin operations, validation, exception handling, API documentation, and automated testing are implemented.

Public production deployment will be added separately.
