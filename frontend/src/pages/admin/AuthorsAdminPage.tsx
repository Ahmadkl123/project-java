import { useEffect, useState } from 'react';
import toast from 'react-hot-toast';
import { Plus, Pencil, Trash2, Search } from 'lucide-react';
import { authorsApi } from '../../api/endpoints';
import type { Author, PageResponse } from '../../types';
import Pagination from '../../components/Pagination';

export default function AuthorsAdminPage() {
  const [q, setQ] = useState('');
  const [page, setPage] = useState(0);
  const [data, setData] = useState<PageResponse<Author> | null>(null);
  const [edit, setEdit] = useState<Author | null>(null);
  const [form, setForm] = useState({ firstName: '', lastName: '', biography: '', nationality: '' });

  const load = () => authorsApi.search({ q, page, size: 10 }).then(setData).catch(() => {});
  useEffect(() => { load(); }, [q, page]);

  const open = (a: Author | null) => {
    setEdit(a);
    setForm({
      firstName: a?.firstName ?? '', lastName: a?.lastName ?? '',
      biography: a?.biography ?? '', nationality: a?.nationality ?? '',
    });
  };

  const save = async (e: React.FormEvent) => {
    e.preventDefault();
    try {
      if (edit?.id) await authorsApi.update(edit.id, form);
      else await authorsApi.create(form);
      toast.success('Saved'); setEdit(null); load();
    } catch {}
  };

  const del = async (id: number) => {
    if (!confirm('Delete this author?')) return;
    try { await authorsApi.delete(id); toast.success('Deleted'); load(); } catch {}
  };

  return (
    <div className="space-y-4">
      <div className="flex items-center justify-between">
        <h1 className="text-2xl font-bold">Authors</h1>
        <button onClick={() => open({ id: 0, firstName: '', lastName: '' })} className="btn-primary"><Plus size={16}/> New</button>
      </div>
      <div className="card p-4">
        <div className="relative">
          <Search className="absolute left-3 top-1/2 -translate-y-1/2 text-slate-400" size={18} />
          <input className="input pl-10" placeholder="Search…" value={q}
                 onChange={(e) => { setQ(e.target.value); setPage(0); }} />
        </div>
      </div>
      <div className="card overflow-x-auto">
        <table className="w-full text-sm">
          <thead className="text-left text-slate-500 bg-slate-50 dark:bg-slate-900/50">
            <tr><th className="px-4 py-3">Name</th><th>Nationality</th><th>Biography</th><th></th></tr>
          </thead>
          <tbody>
            {data?.content.map((a) => (
              <tr key={a.id} className="border-t border-slate-100 dark:border-slate-800">
                <td className="px-4 py-3 font-medium">{a.firstName} {a.lastName}</td>
                <td>{a.nationality}</td>
                <td className="text-slate-500 line-clamp-1 max-w-md">{a.biography}</td>
                <td className="pr-4 text-right">
                  <button onClick={() => open(a)} className="text-brand-600 mr-3"><Pencil size={16}/></button>
                  <button onClick={() => del(a.id)} className="text-red-600"><Trash2 size={16}/></button>
                </td>
              </tr>
            ))}
          </tbody>
        </table>
      </div>
      {data && <Pagination page={data.page} totalPages={data.totalPages} onChange={setPage} />}

      {edit !== null && (
        <div className="fixed inset-0 bg-black/50 grid place-items-center p-4 z-40">
          <form onSubmit={save} className="card p-6 w-full max-w-lg space-y-4">
            <h2 className="text-lg font-semibold">{edit.id ? 'Edit' : 'New'} author</h2>
            <div className="grid grid-cols-2 gap-3">
              <div><label className="label">First name *</label>
                <input className="input" required value={form.firstName}
                       onChange={(e) => setForm({ ...form, firstName: e.target.value })}/></div>
              <div><label className="label">Last name *</label>
                <input className="input" required value={form.lastName}
                       onChange={(e) => setForm({ ...form, lastName: e.target.value })}/></div>
              <div className="col-span-2"><label className="label">Nationality</label>
                <input className="input" value={form.nationality}
                       onChange={(e) => setForm({ ...form, nationality: e.target.value })}/></div>
              <div className="col-span-2"><label className="label">Biography</label>
                <textarea className="input min-h-[80px]" value={form.biography}
                          onChange={(e) => setForm({ ...form, biography: e.target.value })}/></div>
            </div>
            <div className="flex justify-end gap-2">
              <button type="button" onClick={() => setEdit(null)} className="btn-secondary">Cancel</button>
              <button type="submit" className="btn-primary">Save</button>
            </div>
          </form>
        </div>
      )}
    </div>
  );
}
