import { Routes, Route, Navigate } from 'react-router-dom';
import Layout from './components/Layout';
import ProtectedRoute from './components/ProtectedRoute';

import LoginPage from './pages/LoginPage';
import RegisterPage from './pages/RegisterPage';
import DashboardRouter from './pages/DashboardRouter';
import StudentDashboard from './pages/StudentDashboard';
import AdminDashboard from './pages/AdminDashboard';
import BooksPage from './pages/BooksPage';
import MyReservationsPage from './pages/MyReservationsPage';
import MyBorrowsPage from './pages/MyBorrowsPage';
import NotificationsPage from './pages/NotificationsPage';
import ProfilePage from './pages/ProfilePage';

import BooksAdminPage from './pages/admin/BooksAdminPage';
import CategoriesAdminPage from './pages/admin/CategoriesAdminPage';
import AuthorsAdminPage from './pages/admin/AuthorsAdminPage';
import UsersAdminPage from './pages/admin/UsersAdminPage';
import ReservationsAdminPage from './pages/admin/ReservationsAdminPage';
import BorrowsAdminPage from './pages/admin/BorrowsAdminPage';
import AuditLogsPage from './pages/admin/AuditLogsPage';

export default function App() {
  return (
    <Routes>
      <Route path="/login" element={<LoginPage />} />
      <Route path="/register" element={<RegisterPage />} />

      <Route element={<ProtectedRoute />}>
        <Route element={<Layout />}>
          <Route path="/dashboard" element={<DashboardRouter />} />
          <Route path="/student" element={<StudentDashboard />} />
          <Route path="/books" element={<BooksPage />} />
          <Route path="/my-reservations" element={<MyReservationsPage />} />
          <Route path="/my-borrows" element={<MyBorrowsPage />} />
          <Route path="/notifications" element={<NotificationsPage />} />
          <Route path="/profile" element={<ProfilePage />} />
        </Route>
      </Route>

      <Route element={<ProtectedRoute roles={['ADMIN', 'BIBLIOTHECAIRE']} />}>
        <Route element={<Layout />}>
          <Route path="/admin" element={<AdminDashboard />} />
          <Route path="/admin/books" element={<BooksAdminPage />} />
          <Route path="/admin/categories" element={<CategoriesAdminPage />} />
          <Route path="/admin/authors" element={<AuthorsAdminPage />} />
          <Route path="/admin/users" element={<UsersAdminPage />} />
          <Route path="/admin/reservations" element={<ReservationsAdminPage />} />
          <Route path="/admin/borrows" element={<BorrowsAdminPage />} />
        </Route>
      </Route>

      <Route element={<ProtectedRoute roles={['ADMIN']} />}>
        <Route element={<Layout />}>
          <Route path="/admin/audit" element={<AuditLogsPage />} />
        </Route>
      </Route>

      <Route path="/" element={<Navigate to="/dashboard" replace />} />
      <Route path="*" element={<Navigate to="/dashboard" replace />} />
    </Routes>
  );
}
