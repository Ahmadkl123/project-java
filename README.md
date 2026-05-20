# Bibliotheque Universitaire en Ligne

A modern, full-stack online university library management platform.

Students can search the catalog, reserve books, track borrows, and receive email reminders.
Librarians and admins manage the catalog, students, reservations, loans, and review analytics.

---

## Tech stack

**Backend**
- Spring Boot 3.3 (Java 17)
- Spring Security + JWT (HS256)
- Spring Data JPA (MySQL in prod, H2 in dev)
- Spring Mail + Thymeleaf email templates
- Spring Validation (`@NotBlank`, `@Email`, DTOs)
- springdoc OpenAPI / Swagger UI
- Scheduled jobs for due-date reminders & overdue flagging
- Audit logging
- Layered architecture: `controller / service / repository / dto / entity / security / config`

**Frontend**
- React 18 + TypeScript + Vite
- React Router 6, Axios, react-hot-toast
- Tailwind CSS with dark / light mode
- Recharts (dashboard analytics)
- Lucide icons

**Infrastructure**
- Docker + docker-compose (MySQL 8.4, backend, nginx-served frontend)
- MySQL reference schema in `db/schema.sql`

---

## Repository layout

```
.
├── backend/                Spring Boot service
│   ├── src/main/java/com/library/biblio/
│   │   ├── config/         Security, OpenAPI, Async, CORS
│   │   ├── controller/     REST endpoints
│   │   ├── dto/            Request / response DTOs
│   │   ├── entity/         JPA entities
│   │   ├── exception/      Global error handling
│   │   ├── mapper/         Entity ↔ DTO mappers
│   │   ├── repository/     Spring Data JPA repositories
│   │   ├── security/       JWT, principal, filter, entry point
│   │   ├── service/        Business logic
│   │   └── seed/           Initial data (roles, demo users, catalog)
│   └── src/main/resources/
│       ├── application.yml
│       └── templates/      Thymeleaf email templates
│
├── frontend/               React + Vite + Tailwind
│   └── src/
│       ├── api/            Axios client and endpoint wrappers
│       ├── components/     Layout, sidebar, header, shared UI
│       ├── context/        Auth + Theme contexts
│       ├── pages/          Routes for student + admin
│       │   └── admin/      Admin CRUD + analytics pages
│       ├── types/          TypeScript domain types
│       └── utils/          Formatters
│
├── db/schema.sql           MySQL reference schema
├── docker-compose.yml      Three-service stack
└── README.md
```

---

## Quick start (local dev — no Docker required)

The dev profile uses an in-memory H2 database and seeds data automatically, so you can boot the backend without installing MySQL.

### Backend
```bash
cd backend
./mvnw spring-boot:run        # or: mvn spring-boot:run
```
- API: http://localhost:8080/api
- Swagger UI: http://localhost:8080/api/swagger-ui.html
- H2 console: http://localhost:8080/api/h2-console (JDBC: `jdbc:h2:mem:biblio`, user `sa`, no password)

### Frontend
```bash
cd frontend
npm install
npm run dev
```
- App: http://localhost:5173 (Vite proxies `/api` to `:8080`)

### Demo accounts (seeded automatically)
| Role            | Email                     | Password       |
|-----------------|---------------------------|----------------|
| Admin           | admin@biblio.local        | Admin@123      |
| Librarian       | librarian@biblio.local    | Librarian@123  |
| Student         | etudiant@biblio.local     | Etudiant@123   |

---

## Production stack (Docker)

```bash
docker compose up --build
```
- Frontend: http://localhost
- Backend: http://localhost:8080/api
- MySQL: localhost:3306 (user `biblio` / pwd `bibliopassword`)

The MySQL volume persists across restarts. The first boot seeds default roles, users, and a small catalog.

---

## Authentication & roles

- JWT bearer tokens (HS256) — header `Authorization: Bearer <token>`
- Three roles: `ADMIN`, `BIBLIOTHECAIRE`, `ETUDIANT`
- Method security with `@PreAuthorize`
- Self-access guard: students can update / read their own profile via the `userSecurity` SpEL helper

### Auth endpoints
| Method | Path                          | Auth   | Description                       |
|--------|-------------------------------|--------|-----------------------------------|
| POST   | `/auth/register`              | public | Register a new student            |
| POST   | `/auth/login`                 | public | Get a JWT token                   |
| GET    | `/auth/me`                    | user   | Current user                      |
| POST   | `/auth/change-password`       | user   | Change own password               |

---

## REST API overview

All paths are prefixed with `/api`.

| Domain        | Endpoints                                                                                                  |
|---------------|------------------------------------------------------------------------------------------------------------|
| Books         | `GET /books` (search, paging) · `GET /books/{id}` · `POST/PUT/DELETE /books/...` (admin/librarian)         |
| Categories    | `GET/POST/PUT/DELETE /categories`                                                                          |
| Authors       | `GET/POST/PUT/DELETE /authors`                                                                             |
| Users         | `GET /users` · `GET /users/{id}` · `PUT /users/{id}` · `DELETE /users/{id}`                                |
| Reservations  | `POST /reservations` · `GET /reservations/me` · `GET /reservations` · `PATCH /reservations/{id}/status`    |
| Borrows       | `POST /borrows` · `PATCH /borrows/{id}/return` · `GET /borrows/me` · `GET /borrows` · `GET /borrows/overdue` |
| Notifications | `GET /notifications` · `GET /notifications/unread-count` · `PATCH /notifications/{id}/read`                |
| Admin         | `GET /admin/dashboard` (stats) · `GET /admin/audit` (audit logs, admin only)                               |

Full interactive docs: `/api/swagger-ui.html`.

---

## Features

### Student
- Register / login with JWT
- Search & filter books (title, ISBN, author, category, availability)
- Reserve a book online
- Track borrows, due dates, return history, fines
- In-app notification center + email reminders before return
- Profile management + password change
- Dark / light theme

### Admin & Librarian
- Dashboard with stats, monthly borrow chart, top 5 most-borrowed books
- Full CRUD for books, categories, authors
- User management (search, enable/disable, role assignment)
- Reservation queue with approve / reject
- Loan creation + return registration with automatic fine calculation
- Overdue tracking
- Email reminders & overdue alerts (Thymeleaf templates)
- Audit log of mutating actions (admin only)

---

## Email notifications

The backend includes Thymeleaf HTML templates in `backend/src/main/resources/templates/`:
- `reservation-created.html`
- `borrow-approved.html`
- `due-reminder.html`
- `overdue-alert.html`

Scheduled jobs (`ReminderScheduler`):
- **08:00 daily** — sends reminders for books due within `app.borrow.reminder-days-before` days.
- **08:30 daily** — flags overdue borrows and emails the borrower.

Email is **disabled by default** (`MAIL_ENABLED=false`); it logs what would be sent. To enable, set `MAIL_ENABLED=true` and fill in the SMTP variables.

---

## Configuration reference

Key application properties (`backend/src/main/resources/application.yml`):

| Property                            | Default                     | Description                          |
|-------------------------------------|-----------------------------|--------------------------------------|
| `app.jwt.secret`                    | (base64-encoded)            | HMAC-SHA256 secret, ≥ 32 bytes       |
| `app.jwt.expiration-ms`             | 86_400_000                  | Token lifetime                       |
| `app.cors.allowed-origins`          | localhost:5173,localhost:3000 | CORS origins                       |
| `app.mail.enabled`                  | `false`                     | Toggle real email sending            |
| `app.borrow.default-duration-days`  | `14`                        | Default loan duration                |
| `app.borrow.max-active-borrows`     | `5`                         | Max concurrent borrows per student   |
| `app.borrow.reminder-days-before`   | `2`                         | Days before due date to send reminder|

---

## Notes & extension points

- `mvnw` wrapper not included — use a locally installed Maven, or generate one with `mvn -N io.takari:maven:wrapper`.
- `prod` profile expects MySQL; `dev` profile uses H2 and is the default if no profile is set.
- `springdoc-openapi-starter-webmvc-ui` exposes Swagger at `/api/swagger-ui.html` with JWT support pre-wired.

---

## License

MIT
