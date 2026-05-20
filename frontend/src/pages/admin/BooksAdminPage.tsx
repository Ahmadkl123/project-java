import { useEffect, useState } from 'react';
import { Plus, Pencil, Trash2, Search } from 'lucide-react';
import toast from 'react-hot-toast';
import { authorsApi, booksApi, categoriesApi } from '../../api/endpoints';
import type { Author, Book, Category, PageResponse } from '../../types';
import Pagination from '../../components/Pagination';

interface FormState {
  id?: number; title: string; isbn: string; description: string; publisher: string;
  publicationYear?: number; language: string; pages?: number; coverUrl: string;
  totalCopies: number; categoryId?: number; authorIds: number[];
}

const empty: FormState = { title: '', isbn: '', description: '', publisher: '', language: 'English', coverUrl: '', totalCopies: 1, authorIds: [] };

export default function BooksAdminPage() {
  const [q, setQ] = useState('');
  const [page, setPage] = useState(0);
  const [data, setData] = useState<PageResponse<Book> | null>(null);
  const [categories, setCategories] = useState<Category[]>([]);
  const [authors, setAuthors] = useState<Author[]>([]);
  const [open, setOpen] = useState(false);
  const [form, setForm] = useState<FormState>(empty);

  const load = () => booksApi.search({ q, page, size: 10 }).then(setData).catch(() => {});
  useEffect(() => { load(); }, [q, page]);
  useEffect(() => {
    categoriesApi.list().then(setCategories);
    authorsApi.search({ size: 200 }).then((r) => setAuthors(r.content));
  }, []);

  const openCreate = () => { setForm(empty); setOpen(true); };
  const openEdit = (b: Book) => {
    setForm({
      id: b.id, title: b.title, isbn: b.isbn ?? '', description: b.description ?? '',
      publisher: b.publisher ?? '', publicationYear: b.publicationYear,
      language: b.language ?? 'English', pages: b.pages, coverUrl: b.coverUrl ?? '',
      totalCopies: b.totalCopies, categoryId: b.categoryId,
      authorIds: b.authors.map((a) => a.id),
    });
    setOpen(true);
  };

  const save = async (e: React.FormEvent) => {
    e.preventDefault();
    try {
      const payload = { ...form };
      if (payload.id) await booksApi.update(payload.id, payload as any);
      else await booksApi.create(payload as any);
      toast.success('Saved');
      setOpen(false); load();
    } catch {}
  };

  const del = async (id: number) => {
    if (!confirm('Delete this book?')) return;
    try { await booksApi.delete(id); toast.success('Deleted'); load(); } catch {}
  };

  return (
    <div className="space-y-4">
      <div className="flex items-center justify-between">
        <h1 className="text-2xl font-bold">Books</h1>
        <button onClick={openCreate} className="btn-primary"><Plus size={16} /> New book</button>
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
            <tr>
              <th className="px-4 py-3">Title</th><th>ISBN</th><th>Category</th>
              <th>Authors</th><th>Available</th><th></th>
            </tr>
          </thead>
          <tbody>
            {data?.content.map((b) => (
              <tr key={b.id} className="border-t border-slate-100 dark:border-slate-800">
                <td className="px-4 py-3 font-medium">{b.title}</td>
                <td className="text-slate-500">{b.isbn ?? '—'}</td>
                <td>{b.categoryName ?? '—'}</td>
                <td>{b.authors.map((a) => a.fullName).join(', ') || '—'}</td>
                <td>{b.availableCopies} / {b.totalCopies}</td>
                <td className="pr-4 text-right whitespace-nowrap">
                  <button onClick={() => openEdit(b)} className="text-brand-600 mr-3"><Pencil size={16} /></button>
                  <button onClick={() => del(b.id)} className="text-red-600"><Trash2 size={16} /></button>
                </td>
              </tr>
            ))}
          </tbody>
        </table>
      </div>

      {data && <Pagination page={data.page} totalPages={data.totalPages} onChange={setPage} />}

      {open && (
        <div className="fixed inset-0 bg-black/50 grid place-items-center p-4 z-40">
          <form onSubmit={save} className="card p-6 w-full max-w-2xl space-y-4 max-h-[90vh] overflow-y-auto">
            <h2 className="text-lg font-semibold">{form.id ? 'Edit book' : 'New book'}</h2>
            <div className="grid grid-cols-2 gap-3">
              <div className="col-span-2"><label className="label">Title *</label>
                <input className="input" required value={form.title}
                       onChange={(e) => setForm({ ...form, title: e.target.value })} /></div>
              <div><label className="label">ISBN</label>
                <input className="input" value={form.isbn} onChange={(e) => setForm({ ...form, isbn: e.target.value })} /></div>
              <div><label className="label">Publisher</label>
                <input className="input" value={form.publisher} onChange={(e) => setForm({ ...form, publisher: e.target.value })} /></div>
              <div><label className="label">Publication year</label>
                <input className="input" type="number" value={form.publicationYear ?? ''}
                       onChange={(e) => setForm({ ...form, publicationYear: Number(e.target.value) || undefined })} /></div>
              <div><label className="label">Language</label>
                <input className="input" value={form.language} onChange={(e) => setForm({ ...form, language: e.target.value })} /></div>
              <div><label className="label">Pages</label>
                <input className="input" type="number" value={form.pages ?? ''}
                       onChange={(e) => setForm({ ...form, pages: Number(e.target.value) || undefined })} /></div>
              <div><label className="label">Total copies *</label>
                <input className="input" type="number" required min={0} value={form.totalCopies}
                       onChange={(e) => setForm({ ...form, totalCopies: Number(e.target.value) })} /></div>
              <div className="col-span-2"><label className="label">Cover URL</label>
                <input className="input" value={form.coverUrl} onChange={(e) => setForm({ ...form, coverUrl: e.target.value })} /></div>
              <div><label className="label">Category</label>
                <select className="input" value={form.categoryId ?? ''}
                        onChange={(e) => setForm({ ...form, categoryId: e.target.value ? Number(e.target.value) : undefined })}>
                  <option value="">—</option>
                  {categories.map((c) => <option key={c.id} value={c.id}>{c.name}</option>)}
                </select></div>
              <div><label className="label">Authors</label>
                <select multiple className="input h-32" value={form.authorIds.map(String)}
                        onChange={(e) => setForm({
                          ...form,
                          authorIds: Array.from(e.target.selectedOptions, (o) => Number(o.value)),
                        })}>
                  {authors.map((a) => <option key={a.id} value={a.id}>{a.firstName} {a.lastName}</option>)}
                </select></div>
              <div className="col-span-2"><label className="label">Description</label>
                <textarea className="input min-h-[100px]" value={form.description}
                          onChange={(e) => setForm({ ...form, description: e.target.value })} /></div>
            </div>
            <div className="flex justify-end gap-2">
              <button type="button" onClick={() => setOpen(false)} className="btn-secondary">Cancel</button>
              <button type="submit" className="btn-primary">Save</button>
            </div>
          </form>
        </div>
      )}
    </div>
  );
}
