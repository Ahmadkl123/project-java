import { api, unwrap } from './client';
import type {
  AuthResponse, Book, Borrow, Category, Author, Notification, PageResponse,
  Reservation, ReservationStatus, BorrowStatus, User, DashboardStats, ChatReply,
} from '../types';

export const authApi = {
  login: (email: string, password: string) =>
    unwrap<AuthResponse>(api.post('/auth/login', { email, password })),
  register: (data: { firstName: string; lastName: string; email: string; password: string;
                     matricule?: string; phone?: string; department?: string; }) =>
    unwrap<AuthResponse>(api.post('/auth/register', data)),
  me: () => unwrap<User>(api.get('/auth/me')),
  changePassword: (data: { currentPassword: string; newPassword: string }) =>
    unwrap<void>(api.post('/auth/change-password', data)),
};

export const booksApi = {
  search: (params: { q?: string; categoryId?: number; availableOnly?: boolean;
                     page?: number; size?: number; sort?: string }) =>
    unwrap<PageResponse<Book>>(api.get('/books', { params })),
  get: (id: number) => unwrap<Book>(api.get(`/books/${id}`)),
  recommended: (limit = 8) =>
    unwrap<Book[]>(api.get('/books/recommended', { params: { limit } })),
  create: (data: Partial<Book> & { authorIds?: number[]; categoryId?: number }) =>
    unwrap<Book>(api.post('/books', data)),
  update: (id: number, data: Partial<Book> & { authorIds?: number[]; categoryId?: number }) =>
    unwrap<Book>(api.put(`/books/${id}`, data)),
  delete: (id: number) => unwrap<void>(api.delete(`/books/${id}`)),
};

export const categoriesApi = {
  list: () => unwrap<Category[]>(api.get('/categories')),
  create: (data: Partial<Category>) => unwrap<Category>(api.post('/categories', data)),
  update: (id: number, data: Partial<Category>) => unwrap<Category>(api.put(`/categories/${id}`, data)),
  delete: (id: number) => unwrap<void>(api.delete(`/categories/${id}`)),
};

export const authorsApi = {
  search: (params: { q?: string; page?: number; size?: number }) =>
    unwrap<PageResponse<Author>>(api.get('/authors', { params })),
  create: (data: Partial<Author>) => unwrap<Author>(api.post('/authors', data)),
  update: (id: number, data: Partial<Author>) => unwrap<Author>(api.put(`/authors/${id}`, data)),
  delete: (id: number) => unwrap<void>(api.delete(`/authors/${id}`)),
};

export const reservationsApi = {
  create: (bookId: number, notes?: string) =>
    unwrap<Reservation>(api.post('/reservations', { bookId, notes })),
  mine: (page = 0, size = 10) =>
    unwrap<PageResponse<Reservation>>(api.get('/reservations/me', { params: { page, size } })),
  listAll: (params: { status?: ReservationStatus; page?: number; size?: number }) =>
    unwrap<PageResponse<Reservation>>(api.get('/reservations', { params })),
  updateStatus: (id: number, status: ReservationStatus) =>
    unwrap<Reservation>(api.patch(`/reservations/${id}/status`, null, { params: { status } })),
  cancel: (id: number) => unwrap<void>(api.delete(`/reservations/${id}`)),
};

export const borrowsApi = {
  create: (data: { userId: number; bookId: number; durationDays?: number }) =>
    unwrap<Borrow>(api.post('/borrows', data)),
  returnBook: (id: number) => unwrap<Borrow>(api.patch(`/borrows/${id}/return`)),
  mine: (page = 0, size = 10) =>
    unwrap<PageResponse<Borrow>>(api.get('/borrows/me', { params: { page, size } })),
  listAll: (params: { status?: BorrowStatus; page?: number; size?: number }) =>
    unwrap<PageResponse<Borrow>>(api.get('/borrows', { params })),
  overdue: () => unwrap<Borrow[]>(api.get('/borrows/overdue')),
};

export const usersApi = {
  search: (params: { q?: string; page?: number; size?: number }) =>
    unwrap<PageResponse<User>>(api.get('/users', { params })),
  get: (id: number) => unwrap<User>(api.get(`/users/${id}`)),
  update: (id: number, data: Partial<User>) => unwrap<User>(api.put(`/users/${id}`, data)),
  delete: (id: number) => unwrap<void>(api.delete(`/users/${id}`)),
};

export const notificationsApi = {
  list: (page = 0, size = 20) =>
    unwrap<PageResponse<Notification>>(api.get('/notifications', { params: { page, size } })),
  unreadCount: () => unwrap<{ count: number }>(api.get('/notifications/unread-count')),
  markRead: (id: number) => unwrap<void>(api.patch(`/notifications/${id}/read`)),
  markAllRead: () => unwrap<void>(api.patch('/notifications/read-all')),
};

export const dashboardApi = {
  stats: () => unwrap<DashboardStats>(api.get('/admin/dashboard')),
};

export const chatApi = {
  send: (message: string) => unwrap<ChatReply>(api.post('/chat', { message })),
};
