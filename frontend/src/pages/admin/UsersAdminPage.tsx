import { useEffect, useState } from 'react';
import toast from 'react-hot-toast';
import { Search, Trash2 } from 'lucide-react';
import { usersApi } from '../../api/endpoints';
import type { PageResponse, User } from '../../types';
import Pagination from '../../components/Pagination';

export default function UsersAdminPage() {
  const [q, setQ] = useState('');
  const [page, setPage] = useState(0);
  const [data, setData] = useState<PageResponse<User> | null>(null);

  const load = () => usersApi.search({ q, page, size: 10 }).then(setData).catch(() => {});
  useEffect(() => { load(); }, [q, page]);

  const toggleEnabled = async (u: User) => {
    try { await usersApi.update(u.id, { enabled: !u.enabled }); load(); } catch {}
  };

  const del = async (id: number) => {
    if (!confirm('Delete this user?')) return;
    try { await usersApi.delete(id); toast.success('Deleted'); load(); } catch {}
  };

  return (
    <div className="space-y-4">
      <h1 className="text-2xl font-bold">Users</h1>
      <div className="card p-4">
        <div className="relative">
          <Search className="absolute left-3 top-1/2 -translate-y-1/2 text-slate-400" size={18} />
          <input className="input pl-10" placeholder="Search by name, email, matricule…"
                 value={q} onChange={(e) => { setQ(e.target.value); setPage(0); }} />
        </div>
      </div>
      <div className="card overflow-x-auto">
        <table className="w-full text-sm">
          <thead className="text-left text-slate-500 bg-slate-50 dark:bg-slate-900/50">
            <tr>
              <th className="px-4 py-3">Name</th><th>Email</th><th>Matricule</th>
              <th>Department</th><th>Roles</th><th>Status</th><th></th>
            </tr>
          </thead>
          <tbody>
            {data?.content.map((u) => (
              <tr key={u.id} className="border-t border-slate-100 dark:border-slate-800">
                <td className="px-4 py-3 font-medium">{u.firstName} {u.lastName}</td>
                <td className="text-slate-500">{u.email}</td>
                <td>{u.matricule ?? '—'}</td>
                <td>{u.department ?? '—'}</td>
                <td>{(u.roles ?? []).join(', ')}</td>
                <td>
                  <button onClick={() => toggleEnabled(u)}
                          className={u.enabled ? 'badge-success' : 'badge-neutral'}>
                    {u.enabled ? 'Enabled' : 'Disabled'}
                  </button>
                </td>
                <td className="pr-4 text-right">
                  <button onClick={() => del(u.id)} className="text-red-600"><Trash2 size={16}/></button>
                </td>
              </tr>
            ))}
          </tbody>
        </table>
      </div>
      {data && <Pagination page={data.page} totalPages={data.totalPages} onChange={setPage} />}
    </div>
  );
}
