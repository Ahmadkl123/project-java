export type Role = 'ADMIN' | 'BIBLIOTHECAIRE' | 'ETUDIANT';

export interface User {
  id: number;
  firstName: string;
  lastName: string;
  email: string;
  matricule?: string;
  phone?: string;
  department?: string;
  enabled: boolean;
  roles: Role[];
}

export interface AuthResponse {
  accessToken: string;
  tokenType: string;
  expiresIn: number;
  user: User;
}

export interface ApiResponse<T> {
  success: boolean;
  message: string;
  data: T;
  timestamp: string;
}

export interface PageResponse<T> {
  content: T[];
  page: number;
  size: number;
  totalElements: number;
  totalPages: number;
  first: boolean;
  last: boolean;
}

export interface Author {
  id: number;
  firstName: string;
  lastName: string;
  biography?: string;
  nationality?: string;
}

export interface Category {
  id: number;
  name: string;
  description?: string;
  bookCount?: number;
}

export interface Book {
  id: number;
  title: string;
  isbn?: string;
  description?: string;
  publisher?: string;
  publicationYear?: number;
  language?: string;
  pages?: number;
  coverUrl?: string;
  totalCopies: number;
  availableCopies: number;
  categoryId?: number;
  categoryName?: string;
  authors: { id: number; fullName: string }[];
}

export type ReservationStatus =
  | 'PENDING' | 'APPROVED' | 'REJECTED' | 'CANCELLED' | 'FULFILLED' | 'EXPIRED';

export interface Reservation {
  id: number;
  userId: number;
  userFullName: string;
  bookId: number;
  bookTitle: string;
  reservationDate: string;
  expiryDate?: string;
  status: ReservationStatus;
  notes?: string;
}

export type BorrowStatus = 'ACTIVE' | 'RETURNED' | 'OVERDUE' | 'LOST';

export interface Borrow {
  id: number;
  userId: number;
  userFullName: string;
  bookId: number;
  bookTitle: string;
  bookIsbn?: string;
  borrowDate: string;
  dueDate: string;
  returnDate?: string;
  status: BorrowStatus;
  fineAmount?: number;
  notes?: string;
}

export interface Notification {
  id: number;
  type: string;
  title: string;
  message: string;
  read: boolean;
  createdAt: string;
}

export interface ChatSuggestion {
  label: string;
  value: string;
}

export interface ChatReply {
  reply: string;
  intent: string;
  suggestions?: ChatSuggestion[] | null;
  timestamp: string;
}

export interface DashboardStats {
  totalBooks: number;
  availableBooks: number;
  totalUsers: number;
  activeUsers: number;
  activeBorrows: number;
  overdueBorrows: number;
  pendingReservations: number;
  borrowsByMonth: Record<string, number>;
  topBooks: { bookId: number; title: string; borrowCount: number }[];
}
