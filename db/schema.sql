-- =====================================================================
-- Bibliotheque Universitaire en Ligne — MySQL reference schema
-- =====================================================================
-- This file is for reference / manual provisioning only.
-- In dev, Hibernate will manage the schema automatically (ddl-auto=update).
-- =====================================================================

CREATE DATABASE IF NOT EXISTS biblio_universitaire
  CHARACTER SET utf8mb4 COLLATE utf8mb4_unicode_ci;

USE biblio_universitaire;

CREATE TABLE IF NOT EXISTS roles (
  id BIGINT AUTO_INCREMENT PRIMARY KEY,
  name VARCHAR(30) NOT NULL UNIQUE
);

CREATE TABLE IF NOT EXISTS users (
  id BIGINT AUTO_INCREMENT PRIMARY KEY,
  first_name   VARCHAR(80)  NOT NULL,
  last_name    VARCHAR(80)  NOT NULL,
  email        VARCHAR(120) NOT NULL UNIQUE,
  password     VARCHAR(200) NOT NULL,
  matricule    VARCHAR(40)  UNIQUE,
  phone        VARCHAR(30),
  department   VARCHAR(80),
  date_of_birth DATE,
  enabled      TINYINT(1)   NOT NULL DEFAULT 1,
  created_at   DATETIME(6)  NOT NULL,
  updated_at   DATETIME(6)
);

CREATE TABLE IF NOT EXISTS user_roles (
  user_id BIGINT NOT NULL,
  role_id BIGINT NOT NULL,
  PRIMARY KEY (user_id, role_id),
  FOREIGN KEY (user_id) REFERENCES users(id) ON DELETE CASCADE,
  FOREIGN KEY (role_id) REFERENCES roles(id) ON DELETE CASCADE
);

CREATE TABLE IF NOT EXISTS categories (
  id BIGINT AUTO_INCREMENT PRIMARY KEY,
  name        VARCHAR(100) NOT NULL UNIQUE,
  description VARCHAR(500),
  created_at  DATETIME(6) NOT NULL,
  updated_at  DATETIME(6)
);

CREATE TABLE IF NOT EXISTS authors (
  id BIGINT AUTO_INCREMENT PRIMARY KEY,
  first_name  VARCHAR(80) NOT NULL,
  last_name   VARCHAR(80) NOT NULL,
  biography   VARCHAR(2000),
  nationality VARCHAR(60),
  created_at  DATETIME(6) NOT NULL,
  updated_at  DATETIME(6)
);

CREATE TABLE IF NOT EXISTS books (
  id BIGINT AUTO_INCREMENT PRIMARY KEY,
  title             VARCHAR(250) NOT NULL,
  isbn              VARCHAR(20)  UNIQUE,
  description       VARCHAR(3000),
  publisher         VARCHAR(120),
  publication_year  INT,
  language          VARCHAR(30),
  pages             INT,
  cover_url         VARCHAR(500),
  total_copies      INT NOT NULL DEFAULT 1,
  available_copies  INT NOT NULL DEFAULT 1,
  category_id       BIGINT,
  created_at        DATETIME(6) NOT NULL,
  updated_at        DATETIME(6),
  INDEX idx_books_title (title),
  FOREIGN KEY (category_id) REFERENCES categories(id) ON DELETE SET NULL
);

CREATE TABLE IF NOT EXISTS book_authors (
  book_id   BIGINT NOT NULL,
  author_id BIGINT NOT NULL,
  PRIMARY KEY (book_id, author_id),
  FOREIGN KEY (book_id)   REFERENCES books(id)   ON DELETE CASCADE,
  FOREIGN KEY (author_id) REFERENCES authors(id) ON DELETE CASCADE
);

CREATE TABLE IF NOT EXISTS reservations (
  id BIGINT AUTO_INCREMENT PRIMARY KEY,
  user_id          BIGINT NOT NULL,
  book_id          BIGINT NOT NULL,
  reservation_date DATE NOT NULL,
  expiry_date      DATE,
  status           VARCHAR(30) NOT NULL,
  notes            VARCHAR(500),
  created_at       DATETIME(6) NOT NULL,
  updated_at       DATETIME(6),
  FOREIGN KEY (user_id) REFERENCES users(id),
  FOREIGN KEY (book_id) REFERENCES books(id)
);

CREATE TABLE IF NOT EXISTS borrows (
  id BIGINT AUTO_INCREMENT PRIMARY KEY,
  user_id      BIGINT NOT NULL,
  book_id      BIGINT NOT NULL,
  borrow_date  DATE NOT NULL,
  due_date     DATE NOT NULL,
  return_date  DATE,
  status       VARCHAR(30) NOT NULL,
  fine_amount  DOUBLE DEFAULT 0,
  notes        VARCHAR(500),
  approved_by  BIGINT,
  created_at   DATETIME(6) NOT NULL,
  updated_at   DATETIME(6),
  INDEX idx_borrows_user (user_id),
  INDEX idx_borrows_status (status),
  FOREIGN KEY (user_id)     REFERENCES users(id),
  FOREIGN KEY (book_id)     REFERENCES books(id),
  FOREIGN KEY (approved_by) REFERENCES users(id)
);

CREATE TABLE IF NOT EXISTS notifications (
  id BIGINT AUTO_INCREMENT PRIMARY KEY,
  user_id     BIGINT NOT NULL,
  type        VARCHAR(40) NOT NULL,
  title       VARCHAR(200) NOT NULL,
  message     VARCHAR(1000),
  is_read     TINYINT(1) NOT NULL DEFAULT 0,
  email_sent  TINYINT(1) NOT NULL DEFAULT 0,
  created_at  DATETIME(6) NOT NULL,
  updated_at  DATETIME(6),
  INDEX idx_notifications_user (user_id),
  INDEX idx_notifications_read (is_read),
  FOREIGN KEY (user_id) REFERENCES users(id) ON DELETE CASCADE
);

CREATE TABLE IF NOT EXISTS audit_logs (
  id BIGINT AUTO_INCREMENT PRIMARY KEY,
  actor       VARCHAR(120),
  action      VARCHAR(80) NOT NULL,
  entity_type VARCHAR(80),
  entity_id   BIGINT,
  details     VARCHAR(2000),
  ip_address  VARCHAR(45),
  timestamp   DATETIME(6) NOT NULL,
  INDEX idx_audit_actor (actor),
  INDEX idx_audit_action (action)
);

-- Default roles
INSERT IGNORE INTO roles(name) VALUES ('ADMIN'), ('BIBLIOTHECAIRE'), ('ETUDIANT');
