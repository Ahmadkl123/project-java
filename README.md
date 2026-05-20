# 📚 Bibliotheque Universitaire en Ligne

A modern, full-stack online university library management platform.

Students can search the catalog, reserve books, track borrows, and receive email reminders.  
Librarians and admins manage the catalog, students, reservations, loans, and analytics.

---

# 👥 Collaborators

| GitHub | Profile |
|--------|---------|
| Ahmad El Kadi | https://github.com/Ahmadkl123 |
| Abdellah Bouabdli | https://github.com/AbdellahBouabdli |

---

# ✨ Features

## 👨‍🎓 Student Features
- Register & login securely with JWT authentication
- Search books by:
  - Title
  - ISBN
  - Author
  - Category
  - Availability
- Reserve books online
- Track:
  - Borrow history
  - Due dates
  - Return history
  - Fines
- Receive notifications & email reminders
- Manage profile and password
- Dark / Light mode support

---

## 👨‍💼 Admin & Librarian Features
- Dashboard analytics
- Monthly borrow statistics
- Top borrowed books
- Full CRUD operations for:
  - Books
  - Authors
  - Categories
- User management
- Reservation approval/rejection
- Borrow & return management
- Automatic fine calculation
- Overdue tracking
- Audit logs
- Email reminder system

---

# 🛠️ Tech Stack

## Backend
- Java 17
- Spring Boot 3.3
- Spring Security + JWT
- Spring Data JPA
- MySQL / H2 Database
- Spring Mail
- Thymeleaf
- Swagger OpenAPI
- Maven

---

## Frontend
- React 18
- TypeScript
- Vite
- Tailwind CSS
- React Router
- Axios
- Recharts
- Lucide Icons

---

## Infrastructure
- Docker
- Docker Compose
- Nginx

---

# 📁 Project Structure

```bash
.
├── backend/
│   ├── controller/
│   ├── service/
│   ├── repository/
│   ├── entity/
│   ├── dto/
│   ├── security/
│   ├── config/
│   └── templates/
│
├── frontend/
│   ├── api/
│   ├── components/
│   ├── context/
│   ├── pages/
│   ├── types/
│   └── utils/
│
├── db/
├── docker-compose.yml
└── README.md
```

---

# 🚀 Quick Start

# Backend

```bash
cd backend
mvn spring-boot:run
```

Backend runs on:

```bash
http://localhost:8080/api
```

Swagger:

```bash
http://localhost:8080/api/swagger-ui.html
```

---

# Frontend

```bash
cd frontend
npm install
npm run dev
```

Frontend runs on:

```bash
http://localhost:5173
```

---

# 🐳 Run with Docker

```bash
docker compose up --build
```

Services:
- Frontend → http://localhost
- Backend → http://localhost:8080/api
- MySQL → localhost:3306

---

# 🔐 Authentication & Roles

## Roles
- ADMIN
- BIBLIOTHECAIRE
- ETUDIANT

## Authentication
- JWT Bearer Token
- Spring Security
- Role-based authorization
- Protected routes

---

# 📡 API Overview

| Module | Endpoints |
|--------|-----------|
| Auth | `/auth/login`, `/auth/register` |
| Books | `/books` |
| Authors | `/authors` |
| Categories | `/categories` |
| Users | `/users` |
| Reservations | `/reservations` |
| Borrows | `/borrows` |
| Notifications | `/notifications` |
| Admin Dashboard | `/admin/dashboard` |

---

# 📧 Email Notifications

Supported templates:
- Reservation confirmation
- Borrow approved
- Due reminder
- Overdue alert

Scheduler automatically:
- Sends reminders
- Flags overdue books
- Sends alert emails

---

# ⚙️ Configuration

Main configuration file:

```bash
backend/src/main/resources/application.yml
```

Important configs:
- JWT Secret
- Mail Configuration
- Database Credentials
- CORS Origins
- Borrow Rules

---

# 🌙 UI Features

- Responsive Design
- Modern Dashboard
- Dark / Light Theme
- Charts & Analytics
- Mobile Friendly

---

# 📊 Dashboard Analytics

- Total books
- Total users
- Borrow statistics
- Monthly activity
- Top borrowed books
- Active reservations

---

# 🔒 Security

- JWT Authentication
- Password Encryption
- Role-based Access
- Protected APIs
- Validation & DTOs
- Audit Logging

---

# 🧪 Demo Accounts

| Role | Email | Password |
|------|-------|----------|
| Admin | admin@biblio.local | Admin@123 |
| Librarian | librarian@biblio.local | Librarian@123 |
| Student | etudiant@biblio.local | Etudiant@123 |

---

# 📌 Future Improvements

- Mobile application
- QR Code borrowing
- AI book recommendations
- Multi-language support
- Real-time notifications
- Advanced analytics

---

# 📜 License

MIT License

---

# ❤️ Contributors

- [Ahmadkl123](https://github.com/Ahmadkl123)
- [AbdellahBouabdli](https://github.com/AbdellahBouabdli)
