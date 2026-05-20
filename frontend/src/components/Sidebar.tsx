import { NavLink } from 'react-router-dom';
import {
  LayoutDashboard, BookOpen, Library, Users, BookMarked, ClipboardList,
  Bell, Settings, FolderTree, UserCircle, ShieldCheck,
} from 'lucide-react';
import { useAuth } from '../context/AuthContext';

const studentLinks = [
  { to: '/dashboard', label: 'Dashboard', icon: LayoutDashboard },
  { to: '/books', label: 'Books', icon: BookOpen },
  { to: '/my-reservations', label: 'My Reservations', icon: BookMarked },
  { to: '/my-borrows', label: 'My Borrows', icon: Library },
  { to: '/notifications', label: 'Notifications', icon: Bell },
  { to: '/profile', label: 'Profile', icon: UserCircle },
];

const adminLinks = [
  { to: '/admin', label: 'Dashboard', icon: LayoutDashboard },
  { to: '/admin/books', label: 'Books', icon: BookOpen },
  { to: '/admin/categories', label: 'Categories', icon: FolderTree },
  { to: '/admin/authors', label: 'Authors', icon: ClipboardList },
  { to: '/admin/users', label: 'Users', icon: Users },
  { to: '/admin/reservations', label: 'Reservations', icon: BookMarked },
  { to: '/admin/borrows', label: 'Borrows', icon: Library },
  { to: '/admin/audit', label: 'Audit Logs', icon: ShieldCheck, role: 'ADMIN' as const },
  { to: '/profile', label: 'Profile', icon: Settings },
];

export default function Sidebar() {
  const { hasRole } = useAuth();
  const isAdmin = hasRole('ADMIN', 'BIBLIOTHECAIRE');
  const links = isAdmin ? adminLinks : studentLinks;

  return (
    <aside className="w-64 shrink-0 hidden md:flex flex-col border-r border-slate-200 dark:border-slate-800 bg-white dark:bg-slate-900">
      <div className="p-6 border-b border-slate-200 dark:border-slate-800">
        <div className="flex items-center gap-3">
          <div className="w-10 h-10 rounded-xl bg-gradient-to-br from-brand-500 to-brand-700 grid place-items-center text-white font-bold">
            BU
          </div>
          <div>
            <div className="font-semibold leading-tight">Bibliotheque</div>
            <div className="text-xs text-slate-500">Universitaire</div>
          </div>
        </div>
      </div>

      <nav className="flex-1 p-3 space-y-1 overflow-y-auto scroll-pretty">
        {links
          .filter((l) => !('role' in l) || !l.role || hasRole(l.role))
          .map(({ to, label, icon: Icon }) => (
            <NavLink
              key={to}
              to={to}
              end={to === '/dashboard' || to === '/admin'}
              className={({ isActive }) =>
                `flex items-center gap-3 px-3 py-2.5 rounded-lg text-sm font-medium transition ${
                  isActive
                    ? 'bg-brand-50 dark:bg-brand-900/30 text-brand-700 dark:text-brand-300'
                    : 'text-slate-600 dark:text-slate-300 hover:bg-slate-100 dark:hover:bg-slate-800'
                }`
              }
            >
              <Icon size={18} />
              {label}
            </NavLink>
          ))}
      </nav>

      <div className="p-4 text-xs text-slate-400 border-t border-slate-200 dark:border-slate-800">
        v1.0.0 · {new Date().getFullYear()}
      </div>
    </aside>
  );
}
