# TaskFlow — Full-Stack Task Manager

> **Portfolio project** demonstrating production-ready Java/Spring Boot + React/TypeScript skills.

[![CI](https://github.com/AaronCx/task-manager/actions/workflows/ci.yml/badge.svg)](https://github.com/AaronCx/task-manager/actions/workflows/ci.yml)

---

## Screenshots

> *(Add screenshots here after first run — see the [Screenshots](#-screenshots) section)*

| Login | Dashboard | Task Editor |
|-------|-----------|-------------|
| ![Login](docs/login.png) | ![Dashboard](docs/dashboard.png) | ![Editor](docs/editor.png) |

---

## Tech Stack

| Layer | Technology |
|---|---|
| **Backend** | Java 17, Spring Boot 3.2, Spring Security, Hibernate/JPA |
| **Auth** | JWT (jjwt 0.11.5) — stateless Bearer tokens |
| **Database** | PostgreSQL 15 |
| **API Docs** | Springdoc OpenAPI 3 / Swagger UI |
| **Frontend** | React 18, TypeScript, Vite, Tailwind CSS |
| **HTTP Client** | Axios (with in-memory JWT, not localStorage) |
| **Routing** | React Router v6 with protected routes |
| **DevOps** | Docker, Docker Compose, GitHub Actions CI |

---

## Architecture Overview

```
┌─────────────────────────────────────────────────────────┐
│                    Client (Browser)                     │
│  React 18 + TypeScript + Tailwind CSS + Vite            │
│  JWT stored in memory (AuthContext) — never localStorage │
└──────────────────────┬──────────────────────────────────┘
                       │ HTTP  (Axios + Bearer token)
                       ▼
┌─────────────────────────────────────────────────────────┐
│             Spring Boot 3 API  :8080                    │
│                                                         │
│  ┌──────────────┐  ┌──────────────┐  ┌──────────────┐  │
│  │ AuthController│  │TaskController│  │GlobalException│  │
│  └──────┬───────┘  └──────┬───────┘  │   Handler    │  │
│         │                 │          └──────────────┘  │
│  ┌──────▼───────────────────────────┐                  │
│  │   Spring Security Filter Chain   │                  │
│  │   JwtAuthenticationFilter        │                  │
│  └──────────────────────────────────┘                  │
│                                                         │
│  ┌──────────────┐  ┌──────────────┐                    │
│  │  AuthService │  │  TaskService │                    │
│  └──────┬───────┘  └──────┬───────┘                    │
│         │                 │                             │
│  ┌──────▼─────────────────▼───────┐                    │
│  │   UserRepository  TaskRepository│  (Spring Data JPA)│
│  └──────────────────────────────────┘                  │
└──────────────────────┬──────────────────────────────────┘
                       │ JDBC
                       ▼
┌─────────────────────────────────────────────────────────┐
│                    PostgreSQL 15                         │
│           tables: users, tasks                          │
└─────────────────────────────────────────────────────────┘
```

### Request Flow (authenticated)

1. React sends `Authorization: Bearer <jwt>` header via Axios.
2. `JwtAuthenticationFilter` validates the token and populates `SecurityContext`.
3. Spring Security permits the request; the controller receives `@AuthenticationPrincipal User`.
4. Service layer executes business logic, scoped to the authenticated user.
5. JPA/Hibernate queries PostgreSQL; entity is mapped to a DTO and returned as JSON.

---

## API Endpoints

| Method | Path | Auth | Description |
|--------|------|------|-------------|
| `POST` | `/api/auth/register` | Public | Create a new account |
| `POST` | `/api/auth/login`    | Public | Authenticate → receive JWT |
| `GET`  | `/api/tasks`         | Bearer | List all user's tasks (optional `?status=` filter) |
| `GET`  | `/api/tasks/{id}`    | Bearer | Get a single task |
| `POST` | `/api/tasks`         | Bearer | Create a task |
| `PUT`  | `/api/tasks/{id}`    | Bearer | Update a task |
| `DELETE` | `/api/tasks/{id}` | Bearer | Delete a task |

Full interactive docs: **http://localhost:8080/swagger-ui.html**

---

## Local Development

### Prerequisites

- Java 17+
- Maven 3.9+ (or use the included `./mvnw` wrapper)
- Node.js 20+
- Docker & Docker Compose

### Option A — Docker Compose (recommended)

```bash
# Clone the repo
git clone https://github.com/AaronCx/task-manager.git
cd task-manager

# Start everything (API + PostgreSQL)
docker compose up --build

# The API is now at:  http://localhost:8080
# Swagger UI:         http://localhost:8080/swagger-ui.html
```

Then start the frontend separately:

```bash
cd frontend
npm install
npm run dev
# → http://localhost:5173
```

### Option B — Manual setup

**Backend:**

```bash
# Start a local PostgreSQL instance (or update application.properties)
# Then:
cd backend
./mvnw spring-boot:run
```

**Frontend:**

```bash
cd frontend
npm install
npm run dev
```

---

## Running Tests

```bash
# Backend unit tests (uses H2 in-memory — no PostgreSQL needed)
cd backend
./mvnw test

# Frontend type-check
cd frontend
npx tsc --noEmit
```

---

## Demo Credentials

The database is seeded with sample data on first run:

| Email | Password | Tasks |
|-------|----------|-------|
| `alice@demo.com` | `password123` | 8 tasks across all statuses |
| `bob@demo.com`   | `password123` | 2 tasks |

---

## Project Structure

```
task-manager/
├── backend/                          Spring Boot 3 API (Maven)
│   ├── src/main/java/com/portfolio/taskmanager/
│   │   ├── config/                   Security, OpenAPI config
│   │   ├── controller/               REST endpoints (Auth, Task)
│   │   ├── dto/                      Request/Response records
│   │   ├── entity/                   JPA entities (User, Task)
│   │   ├── enums/                    TaskStatus, TaskPriority
│   │   ├── exception/                GlobalExceptionHandler + custom exceptions
│   │   ├── repository/               Spring Data JPA repositories
│   │   ├── security/                 JWT provider, filter, UserDetailsService
│   │   ├── seeder/                   DataSeeder (runs on first boot)
│   │   └── service/                  Business logic (AuthService, TaskService)
│   ├── Dockerfile                    Multi-stage Docker build
│   └── pom.xml
│
├── frontend/                         React 18 + TypeScript + Vite
│   └── src/
│       ├── api/                      Axios client + endpoint wrappers
│       ├── components/               Layout, ProtectedRoute, Badges
│       ├── context/                  AuthContext (in-memory JWT)
│       ├── pages/                    Login, Register, Dashboard, TaskDetail
│       └── types/                    Shared TypeScript interfaces
│
├── .github/workflows/ci.yml          GitHub Actions CI (build + test)
├── docker-compose.yml                API + PostgreSQL together
└── README.md
```

---

## Key Design Decisions

- **Stateless JWT** — no server-side sessions; tokens are validated on every request.
- **In-memory token storage** — JWT lives in React state (never `localStorage`) to reduce XSS surface.
- **Ownership scoping** — `TaskRepository.findByIdAndOwner` ensures users can only access their own tasks.
- **Global exception handler** — all errors return a consistent `ErrorResponse` JSON shape.
- **Multi-stage Docker build** — final image uses JRE-only alpine, keeping the image ~250 MB.
- **DataSeeder guard** — checks `userRepository.count()` before seeding to prevent duplicate data on restart.

---

## License

MIT
