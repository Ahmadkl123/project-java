import { useEffect, useState } from 'react';
import { Bell, LogOut, Moon, Sun, User as UserIcon } from 'lucide-react';
import { useAuth } from '../context/AuthContext';
import { useTheme } from '../context/ThemeContext';
import { notificationsApi } from '../api/endpoints';
import { Link } from 'react-router-dom';

export default function Header() {
  const { user, logout } = useAuth();
  const { theme, toggle } = useTheme();
  const [unread, setUnread] = useState(0);

  useEffect(() => {
    if (!user) return;
    notificationsApi.unreadCount().then((r) => setUnread(r.count)).catch(() => {});
    const id = setInterval(() => {
      notificationsApi.unreadCount().then((r) => setUnread(r.count)).catch(() => {});
    }, 60000);
    return () => clearInterval(id);
  }, [user]);

  return (
    <header className="h-16 border-b border-slate-200 dark:border-slate-800 bg-white dark:bg-slate-900 px-6 flex items-center justify-between">
      <h1 className="text-lg font-semibold">Welcome back, {user?.firstName}</h1>
      <div className="flex items-center gap-2">
        <button
          onClick={toggle}
          aria-label="Toggle theme"
          className="p-2 rounded-lg hover:bg-slate-100 dark:hover:bg-slate-800 text-slate-600 dark:text-slate-300"
        >
          {theme === 'dark' ? <Sun size={18} /> : <Moon size={18} />}
        </button>

        <Link
          to="/notifications"
          className="relative p-2 rounded-lg hover:bg-slate-100 dark:hover:bg-slate-800 text-slate-600 dark:text-slate-300"
        >
          <Bell size={18} />
          {unread > 0 && (
            <span className="absolute -top-1 -right-1 bg-red-500 text-white text-xs rounded-full min-w-[18px] h-[18px] grid place-items-center px-1">
              {unread > 9 ? '9+' : unread}
            </span>
          )}
        </Link>

        <Link
          to="/profile"
          className="flex items-center gap-2 px-3 py-1.5 rounded-lg hover:bg-slate-100 dark:hover:bg-slate-800"
        >
          <div className="w-8 h-8 rounded-full bg-brand-100 dark:bg-brand-900 text-brand-700 dark:text-brand-300 grid place-items-center">
            <UserIcon size={16} />
          </div>
          <div className="hidden sm:block">
            <div className="text-sm font-medium leading-none">{user?.firstName} {user?.lastName}</div>
            <div className="text-xs text-slate-500">{user?.roles?.[0]}</div>
          </div>
        </Link>

        <button
          onClick={logout}
          aria-label="Logout"
          className="p-2 rounded-lg hover:bg-slate-100 dark:hover:bg-slate-800 text-slate-600 dark:text-slate-300"
        >
          <LogOut size={18} />
        </button>
      </div>
    </header>
  );
}
