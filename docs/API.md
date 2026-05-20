# API quick reference

All endpoints are prefixed with `/api`. JWT bearer token required unless noted.

## Authentication
```http
POST /api/auth/register
{ "firstName": "...", "lastName": "...", "email": "...", "password": "...",
  "matricule": "...", "department": "...", "phone": "..." }

POST /api/auth/login
{ "email": "...", "password": "..." }
=> { "accessToken": "...", "tokenType": "Bearer", "expiresIn": 86400, "user": {...} }
```

## Catalog (public reads, librarian writes)
```http
GET    /api/books?q=algo&categoryId=1&availableOnly=true&page=0&size=20&sort=title,asc
GET    /api/books/{id}
POST   /api/books           # ADMIN / BIBLIOTHECAIRE
PUT    /api/books/{id}      # ADMIN / BIBLIOTHECAIRE
DELETE /api/books/{id}      # ADMIN

GET    /api/categories
POST   /api/categories
PUT    /api/categories/{id}
DELETE /api/categories/{id}

GET    /api/authors?q=knuth&page=0&size=20
POST   /api/authors
PUT    /api/authors/{id}
DELETE /api/authors/{id}
```

## Reservations
```http
POST   /api/reservations          # student
       { "bookId": 12, "notes": "Reading group" }
GET    /api/reservations/me
DELETE /api/reservations/{id}     # cancel own

GET    /api/reservations?status=PENDING                # librarian
PATCH  /api/reservations/{id}/status?status=APPROVED
```

## Borrows
```http
POST   /api/borrows                # librarian
       { "userId": 3, "bookId": 12, "durationDays": 14 }
PATCH  /api/borrows/{id}/return    # librarian
GET    /api/borrows/me
GET    /api/borrows?status=ACTIVE
GET    /api/borrows/overdue
```

## Notifications
```http
GET    /api/notifications
GET    /api/notifications/unread-count
PATCH  /api/notifications/{id}/read
PATCH  /api/notifications/read-all
```

## Users (librarian / admin)
```http
GET    /api/users?q=akram&page=0&size=20
GET    /api/users/{id}             # admin / librarian / self
PUT    /api/users/{id}             # admin or self (self cannot change roles)
DELETE /api/users/{id}             # admin
```

## Admin
```http
GET /api/admin/dashboard           # stats payload
GET /api/admin/audit               # admin only, audit log feed
```

## Interactive docs
- Swagger UI: `/api/swagger-ui.html`
- OpenAPI JSON: `/api/v3/api-docs`
