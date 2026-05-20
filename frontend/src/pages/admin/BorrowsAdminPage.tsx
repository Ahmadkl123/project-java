import { useEffect, useState } from 'react';
import toast from 'react-hot-toast';
import { Plus } from 'lucide-react';
import { booksApi, borrowsApi, usersApi } from '../../api/endpoints';
import type { Book, Borrow, BorrowStatus, PageResponse, User } from '../../types';
import Pagination from '../../components/Pagination';
import { fmtDate, statusBadgeClass } from '../../utils/format';

export default function BorrowsAdminPage() {
  const [status, setStatus] = useState<BorrowStatus | ''>('');
  const [page, setPage] = useState(0);
  const [data, setData] = useState<PageResponse<Borrow> | null>(null);
  const [open, setOpen] = useState(false);
  const [form, setForm] = useState({ userId: 0, bookId: 0, durationDays: 14 });
  const [users, setUsers] = useState<User[]>([]);
  const [books, setBooks] = useState<Book[]>([]);

  const load = () => borrowsApi.listAll({ status: status || undefined, page, size: 15 })
    .then(setData).catch(() => {});
  useEffect(() => { load(); }, [status, page]);

  const openCreate = async () => {
    setOpen(true);
    if (!users.length) usersApi.search({ size: 200 }).then((r) => setUsers(r.content));
    if (!books.length) booksApi.search({ availableOnly: true, size: 200 }).then((r) => setBooks(r.content));
  };

  const save = async (e: React.FormEvent) => {
    e.preventDefault();
    try { await borrowsApi.create(form); toast.success('Borrow created'); setOpen(false); load(); } catch {}
  };

  const ret = async (id: number) => {
    try { await borrowsApi.returnBook(id); toast.success('Returned'); load(); } catch {}
  };

  return (
    <div className="space-y-4">
      <div className="flex items-center justify-between">
        <h1 className="text-2xl font-bold">Borrows</h1>
        <div className="flex gap-2">
          <select className="input w-44" value={status}
                  onChange={(e) => { setStatus(e.target.value as any); setPage(0); }}>
            <option value="">All</option>
            {['ACTIVE','RETURNED','OVERDUE','LOST'].map((s) => <option key={s} value={s}>{s}</option>)}
          </select>
          <button className="btn-primary" onClick={openCreate}><Plus size={16}/> New borrow</button>
        </div>
      </div>

      <div className="card overflow-x-auto">
        <table className="w-full text-sm">
          <thead className="text-left text-slate-500 bg-slate-50 dark:bg-slate-900/50">
            <tr>
              <th className="px-4 py-3">Student</th><th>Book</th>
              <th>Borrowed</th><th>Due</th><th>Returned</th><th>Status</th><th>Fine</th><th></th>
            </tr>
          </thead>
          <tbody>
            {data?.content.map((b) => (
              <tr key={b.id} className="border-t border-slate-100 dark:border-slate-800">
                <td className="px-4 py-3">{b.userFullName}</td>
                <td>{b.bookTitle}</td>
                <td>{fmtDate(b.borrowDate)}</td>
                <td>{fmtDate(b.dueDate)}</td>
                <td>{fmtDate(b.returnDate)}</td>
                <td><span className={statusBadgeClass(b.status)}>{b.status}</span></td>
                <td>{b.fineAmount ? `${b.fineAmount.toFixed(2)} €` : '—'}</td>
                <td className="pr-4 text-right">
                  {b.status !== 'RETURNED' && (
                    <button onClick={() => ret(b.id)} className="text-brand-600 hover:underline">
                      Mark returned
                    </button>
                  )}
                </td>
              </tr>
            ))}
          </tbody>
        </table>
      </div>

      {data && <Pagination page={data.page} totalPages={data.totalPages} onChange={setPage} />}

      {open && (
        <div className="fixed inset-0 bg-black/50 grid place-items-center p-4 z-40">
          <form onSubmit={save} className="card p-6 w-full max-w-lg space-y-4">
            <h2 className="text-lg font-semibold">New borrow</h2>
            <div><label className="label">Student *</label>
              <select required className="input" value={form.userId}
                      onChange={(e) => setForm({ ...form, userId: Number(e.target.value) })}>
                <option value={0}>Select…</option>
                {users.map((u) => <option key={u.id} value={u.id}>{u.firstName} {u.lastName} — {u.email}</option>)}
              </select></div>
            <div><label className="label">Book *</label>
              <select required className="input" value={form.bookId}
                      onChange={(e) => setForm({ ...form, bookId: Number(e.target.value) })}>
                <option value={0}>Select…</option>
                {books.map((b) => <option key={b.id} value={b.id}>{b.title}</option>)}
              </select></div>
            <div><label className="label">Duration (days)</label>
              <input type="number" min={1} className="input" value={form.durationDays}
                     onChange={(e) => setForm({ ...form, durationDays: Number(e.target.value) })} /></div>
            <div className="flex justify-end gap-2">
              <button type="button" className="btn-secondary" onClick={() => setOpen(false)}>Cancel</button>
              <button type="submit" className="btn-primary">Create</button>
            </div>
          </form>
        </div>
      )}
    </div>
  );
}
