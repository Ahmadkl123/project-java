import { useEffect, useState } from 'react';
import { Bell, BellOff } from 'lucide-react';
import toast from 'react-hot-toast';
import { notificationsApi } from '../api/endpoints';
import type { Notification, PageResponse } from '../types';
import Pagination from '../components/Pagination';
import { fmtDateTime } from '../utils/format';

export default function NotificationsPage() {
  const [page, setPage] = useState(0);
  const [data, setData] = useState<PageResponse<Notification> | null>(null);

  const load = () => notificationsApi.list(page, 20).then(setData).catch(() => {});
  useEffect(() => { load(); }, [page]);

  const markRead = async (id: number) => {
    try { await notificationsApi.markRead(id); load(); } catch {}
  };

  const markAll = async () => {
    try { await notificationsApi.markAllRead(); toast.success('Marked all as read'); load(); } catch {}
  };

  return (
    <div className="space-y-4">
      <div className="flex items-center justify-between">
        <h1 className="text-2xl font-bold">Notifications</h1>
        <button onClick={markAll} className="btn-secondary">
          <BellOff size={16} /> Mark all read
        </button>
      </div>

      <div className="space-y-2">
        {data?.content.length === 0 && (
          <div className="card p-12 text-center text-slate-500">
            <Bell size={32} className="mx-auto mb-2 text-slate-300" />
            No notifications.
          </div>
        )}
        {data?.content.map((n) => (
          <div key={n.id}
               onClick={() => !n.read && markRead(n.id)}
               className={`card p-4 flex items-start gap-3 cursor-pointer hover:bg-slate-50 dark:hover:bg-slate-900/60 ${
                 !n.read ? 'border-l-4 border-l-brand-500' : ''
               }`}>
            <Bell size={18} className={n.read ? 'text-slate-400' : 'text-brand-500'} />
            <div className="flex-1">
              <div className="flex items-center justify-between gap-3">
                <h3 className="font-medium">{n.title}</h3>
                <span className="text-xs text-slate-500">{fmtDateTime(n.createdAt)}</span>
              </div>
              <p className="text-sm text-slate-600 dark:text-slate-300 mt-1">{n.message}</p>
            </div>
          </div>
        ))}
      </div>

      {data && <Pagination page={data.page} totalPages={data.totalPages} onChange={setPage} />}
    </div>
  );
}
