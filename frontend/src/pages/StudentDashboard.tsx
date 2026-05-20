import { useEffect, useState } from 'react';
import { BookOpen, BookMarked, Library, Bell, Sparkles } from 'lucide-react';
import StatCard from '../components/StatCard';
import BookCard from '../components/BookCard';
import { booksApi, borrowsApi, notificationsApi, reservationsApi } from '../api/endpoints';
import type { Book } from '../types';
import { fmtDate, statusBadgeClass } from '../utils/format';
import { Link } from 'react-router-dom';

export default function StudentDashboard() {
  const [stats, setStats] = useState({ borrows: 0, reservations: 0, unread: 0, overdue: 0 });
  const [recentBorrows, setRecentBorrows] = useState<any[]>([]);
  const [recommended, setRecommended] = useState<Book[]>([]);
  const [loadingRecs, setLoadingRecs] = useState(true);

  const loadRecommended = () => {
    setLoadingRecs(true);
    booksApi.recommended(8)
      .then(setRecommended)
      .catch(() => setRecommended([]))
      .finally(() => setLoadingRecs(false));
  };

  useEffect(() => {
    Promise.all([
      borrowsApi.mine(0, 5),
      reservationsApi.mine(0, 5),
      notificationsApi.unreadCount(),
    ]).then(([b, r, n]) => {
      setStats({
        borrows: b.totalElements,
        reservations: r.totalElements,
        unread: n.count,
        overdue: b.content.filter((x) => x.status === 'OVERDUE').length,
      });
      setRecentBorrows(b.content);
    }).catch(() => {});
    loadRecommended();
  }, []);

  return (
    <div className="space-y-6">
      <div className="grid grid-cols-1 sm:grid-cols-2 lg:grid-cols-4 gap-4">
        <StatCard title="Active Borrows" value={stats.borrows} icon={Library} color="blue" />
        <StatCard title="Reservations" value={stats.reservations} icon={BookMarked} color="purple" />
        <StatCard title="Overdue" value={stats.overdue} icon={BookOpen} color="red" />
        <StatCard title="Unread Notifications" value={stats.unread} icon={Bell} color="amber" />
      </div>

      <div className="card p-6">
        <div className="flex items-center justify-between mb-4">
          <div className="flex items-center gap-2">
            <Sparkles size={18} className="text-brand-500" />
            <h2 className="text-lg font-semibold">Recommended for you</h2>
          </div>
          <Link to="/books" className="text-sm text-brand-600">Browse catalog</Link>
        </div>

        <p className="text-sm text-slate-500 mb-4">
          {recommended.length > 0
            ? 'Picks based on the categories of books you have reserved or borrowed.'
            : loadingRecs
              ? 'Looking for matches…'
              : 'Reserve or borrow a book first — we will suggest similar titles here.'}
        </p>

        {loadingRecs ? (
          <div className="grid grid-cols-2 sm:grid-cols-3 lg:grid-cols-4 gap-4">
            {Array.from({ length: 4 }).map((_, i) => (
              <div key={i} className="card p-4 animate-pulse">
                <div className="aspect-[4/5] bg-slate-200 dark:bg-slate-800 rounded-lg mb-3" />
                <div className="h-3 bg-slate-200 dark:bg-slate-800 rounded mb-2" />
                <div className="h-3 bg-slate-200 dark:bg-slate-800 rounded w-2/3" />
              </div>
            ))}
          </div>
        ) : recommended.length === 0 ? (
          <div className="text-sm text-slate-500 py-6 text-center">
            <Link to="/books" className="text-brand-600 font-medium">Browse the catalog</Link>
            {' '}to find your first book.
          </div>
        ) : (
          <div className="grid grid-cols-2 sm:grid-cols-3 lg:grid-cols-4 gap-4">
            {recommended.map((b) => (
              <BookCard key={b.id} book={b} onReserved={loadRecommended} />
            ))}
          </div>
        )}
      </div>

      <div className="card p-6">
        <div className="flex items-center justify-between mb-4">
          <h2 className="text-lg font-semibold">Recent activity</h2>
          <Link to="/my-borrows" className="text-sm text-brand-600">View all</Link>
        </div>
        {recentBorrows.length === 0 ? (
          <div className="text-sm text-slate-500 py-8 text-center">
            No borrows yet. <Link to="/books" className="text-brand-600">Browse the catalog</Link>
          </div>
        ) : (
          <div className="overflow-x-auto">
            <table className="w-full text-sm">
              <thead className="text-left text-slate-500 border-b border-slate-200 dark:border-slate-800">
                <tr><th className="py-2">Book</th><th>Borrowed</th><th>Due</th><th>Status</th></tr>
              </thead>
              <tbody>
                {recentBorrows.map((b) => (
                  <tr key={b.id} className="border-b border-slate-100 dark:border-slate-800/60">
                    <td className="py-2">{b.bookTitle}</td>
                    <td>{fmtDate(b.borrowDate)}</td>
                    <td>{fmtDate(b.dueDate)}</td>
                    <td><span className={statusBadgeClass(b.status)}>{b.status}</span></td>
                  </tr>
                ))}
              </tbody>
            </table>
          </div>
        )}
      </div>
    </div>
  );
}
