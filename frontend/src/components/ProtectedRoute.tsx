import { Navigate, Outlet } from 'react-router-dom';
import { useAuth } from '../context/AuthContext';
import type { Role } from '../types';

export default function ProtectedRoute({ roles }: { roles?: Role[] }) {
  const { user, loading, hasRole } = useAuth();

  if (loading) {
    return (
      <div className="h-screen grid place-items-center text-slate-400">
        Loading…
      </div>
    );
  }
  if (!user) return <Navigate to="/login" replace />;
  if (roles && !hasRole(...roles)) return <Navigate to="/dashboard" replace />;
  return <Outlet />;
}
