import { useEffect, useState } from 'react';
import { BookOpen, Users, Library, AlertTriangle, BookMarked } from 'lucide-react';
import StatCard from '../components/StatCard';
import { dashboardApi } from '../api/endpoints';
import type { DashboardStats } from '../types';
import {
  BarChart, Bar, XAxis, YAxis, Tooltip, ResponsiveContainer, CartesianGrid,
  PieChart, Pie, Cell, Legend,
} from 'recharts';
import { useTheme } from '../context/ThemeContext';

const COLORS = ['#3b82f6', '#8b5cf6', '#10b981', '#f59e0b', '#ef4444'];

export default function AdminDashboard() {
  const [stats, setStats] = useState<DashboardStats | null>(null);
  const { theme } = useTheme();

  useEffect(() => { dashboardApi.stats().then(setStats).catch(() => {}); }, []);

  if (!stats) return <div className="text-slate-400">Loading…</div>;

  const monthData = Object.entries(stats.borrowsByMonth)
    .sort()
    .map(([month, count]) => ({ month, count }));

  const topBooksData = stats.topBooks.map((b) => ({
    name: b.title.length > 22 ? b.title.slice(0, 22) + '…' : b.title,
    value: b.borrowCount,
  }));

  const axisColor = theme === 'dark' ? '#94a3b8' : '#475569';

  return (
    <div className="space-y-6">
      <div className="grid grid-cols-1 sm:grid-cols-2 lg:grid-cols-4 gap-4">
        <StatCard title="Total Books" value={stats.totalBooks} icon={BookOpen} color="blue" trend={`${stats.availableBooks} available`} />
        <StatCard title="Total Users" value={stats.totalUsers} icon={Users} color="purple" trend={`${stats.activeUsers} active`} />
        <StatCard title="Active Borrows" value={stats.activeBorrows} icon={Library} color="green" />
        <StatCard title="Overdue" value={stats.overdueBorrows} icon={AlertTriangle} color="red" />
      </div>
      <div className="grid grid-cols-1 sm:grid-cols-2 lg:grid-cols-4 gap-4">
        <StatCard title="Pending Reservations" value={stats.pendingReservations} icon={BookMarked} color="amber" />
      </div>

      <div className="grid grid-cols-1 lg:grid-cols-2 gap-6">
        <div className="card p-6">
          <h3 className="font-semibold mb-4">Borrows — last 6 months</h3>
          <ResponsiveContainer width="100%" height={280}>
            <BarChart data={monthData}>
              <CartesianGrid strokeDasharray="3 3" stroke="rgba(148, 163, 184, 0.2)" />
              <XAxis dataKey="month" stroke={axisColor} />
              <YAxis stroke={axisColor} allowDecimals={false} />
              <Tooltip contentStyle={{ borderRadius: 8 }} />
              <Bar dataKey="count" fill="#3b82f6" radius={[6, 6, 0, 0]} />
            </BarChart>
          </ResponsiveContainer>
        </div>

        <div className="card p-6">
          <h3 className="font-semibold mb-4">Top 5 borrowed books</h3>
          {topBooksData.length === 0 ? (
            <div className="h-[280px] grid place-items-center text-sm text-slate-500">
              No borrow data yet
            </div>
          ) : (
            <ResponsiveContainer width="100%" height={280}>
              <PieChart>
                <Pie data={topBooksData} dataKey="value" nameKey="name"
                     cx="50%" cy="50%" outerRadius={90} innerRadius={50} paddingAngle={2}>
                  {topBooksData.map((_, i) => <Cell key={i} fill={COLORS[i % COLORS.length]} />)}
                </Pie>
                <Legend />
                <Tooltip />
              </PieChart>
            </ResponsiveContainer>
          )}
        </div>
      </div>
    </div>
  );
}
