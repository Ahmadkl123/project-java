import { Navigate } from 'react-router-dom';
import { useAuth } from '../context/AuthContext';

export default function DashboardRouter() {
  const { hasRole } = useAuth();
  return hasRole('ADMIN', 'BIBLIOTHECAIRE')
    ? <Navigate to="/admin" replace />
    : <Navigate to="/student" replace />;
}
