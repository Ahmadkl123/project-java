import { useEffect, useState } from 'react';
import toast from 'react-hot-toast';
import { Plus, Pencil, Trash2 } from 'lucide-react';
import { categoriesApi } from '../../api/endpoints';
import type { Category } from '../../types';

export default function CategoriesAdminPage() {
  const [rows, setRows] = useState<Category[]>([]);
  const [edit, setEdit] = useState<Category | null>(null);
  const [form, setForm] = useState({ name: '', description: '' });

  const load = () => categoriesApi.list().then(setRows).catch(() => {});
  useEffect(() => { load(); }, []);

  const open = (c: Category | null) => {
    setEdit(c);
    setForm({ name: c?.name ?? '', description: c?.description ?? '' });
  };

  const save = async (e: React.FormEvent) => {
    e.preventDefault();
    try {
      if (edit?.id) await categoriesApi.update(edit.id, form);
      else await categoriesApi.create(form);
      toast.success('Saved'); setEdit(null); load();
    } catch {}
  };

  const del = async (id: number) => {
    if (!confirm('Delete this category?')) return;
    try { await categoriesApi.delete(id); toast.success('Deleted'); load(); } catch {}
  };

  return (
    <div className="space-y-4">
      <div className="flex items-center justify-between">
        <h1 className="text-2xl font-bold">Categories</h1>
        <button onClick={() => open({ id: 0, name: '' })} className="btn-primary"><Plus size={16}/> New</button>
      </div>

      <div className="card overflow-x-auto">
        <table className="w-full text-sm">
          <thead className="text-left text-slate-500 bg-slate-50 dark:bg-slate-900/50">
            <tr><th className="px-4 py-3">Name</th><th>Description</th><th>Books</th><th></th></tr>
          </thead>
          <tbody>
            {rows.map((c) => (
              <tr key={c.id} className="border-t border-slate-100 dark:border-slate-800">
                <td className="px-4 py-3 font-medium">{c.name}</td>
                <td className="text-slate-500">{c.description}</td>
                <td>{c.bookCount ?? 0}</td>
                <td className="pr-4 text-right">
                  <button onClick={() => open(c)} className="text-brand-600 mr-3"><Pencil size={16}/></button>
                  <button onClick={() => del(c.id)} className="text-red-600"><Trash2 size={16}/></button>
                </td>
              </tr>
            ))}
          </tbody>
        </table>
      </div>

      {edit !== null && (
        <div className="fixed inset-0 bg-black/50 grid place-items-center p-4 z-40">
          <form onSubmit={save} className="card p-6 w-full max-w-md space-y-4">
            <h2 className="text-lg font-semibold">{edit.id ? 'Edit' : 'New'} category</h2>
            <div><label className="label">Name *</label>
              <input className="input" required value={form.name}
                     onChange={(e) => setForm({ ...form, name: e.target.value })}/></div>
            <div><label className="label">Description</label>
              <textarea className="input" value={form.description}
                        onChange={(e) => setForm({ ...form, description: e.target.value })}/></div>
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
