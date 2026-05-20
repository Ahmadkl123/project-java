import { useEffect, useMemo, useState } from 'react';
import { Search, BookOpen, BookMarked } from 'lucide-react';
import toast from 'react-hot-toast';
import { booksApi, categoriesApi, reservationsApi } from '../api/endpoints';
import type { Book, Category, PageResponse } from '../types';
import Pagination from '../components/Pagination';

export default function BooksPage() {
  const [q, setQ] = useState('');
  const [categoryId, setCategoryId] = useState<number | undefined>();
  const [availableOnly, setAvailableOnly] = useState(false);
  const [categories, setCategories] = useState<Category[]>([]);
  const [page, setPage] = useState(0);
  const [data, setData] = useState<PageResponse<Book> | null>(null);
  const [reserving, setReserving] = useState<number | null>(null);

  useEffect(() => { categoriesApi.list().then(setCategories).catch(() => {}); }, []);

  const load = useMemo(() => () => {
    booksApi.search({ q, categoryId, availableOnly, page, size: 12 }).then(setData).catch(() => {});
  }, [q, categoryId, availableOnly, page]);

  useEffect(() => { load(); }, [load]);

  const reserve = async (book: Book) => {
    setReserving(book.id);
    try {
      await reservationsApi.create(book.id);
      toast.success(`Reserved: ${book.title}`);
    } catch {
      // toast handled
    } finally {
      setReserving(null);
    }
  };

  return (
    <div className="space-y-5">
      <div className="card p-4 flex flex-col md:flex-row gap-3 items-stretch md:items-center">
        <div className="flex-1 relative">
          <Search className="absolute left-3 top-1/2 -translate-y-1/2 text-slate-400" size={18} />
          <input
            className="input pl-10"
            placeholder="Search by title, ISBN or author…"
            value={q}
            onChange={(e) => { setQ(e.target.value); setPage(0); }}
          />
        </div>
        <select
          className="input md:w-56"
          value={categoryId ?? ''}
          onChange={(e) => { setCategoryId(e.target.value ? Number(e.target.value) : undefined); setPage(0); }}
        >
          <option value="">All categories</option>
          {categories.map((c) => <option key={c.id} value={c.id}>{c.name}</option>)}
        </select>
        <label className="flex items-center gap-2 text-sm whitespace-nowrap">
          <input type="checkbox" checked={availableOnly}
                 onChange={(e) => { setAvailableOnly(e.target.checked); setPage(0); }} />
          Available only
        </label>
      </div>

      {data && data.content.length === 0 && (
        <div className="card p-12 text-center text-slate-500">
          <BookOpen size={36} className="mx-auto mb-3 text-slate-300" />
          No books match your search.
        </div>
      )}

      <div className="grid grid-cols-1 sm:grid-cols-2 lg:grid-cols-3 xl:grid-cols-4 gap-4">
        {data?.content.map((b) => (
          <article key={b.id} className="card p-5 flex flex-col">
            <div className="aspect-[4/5] w-full rounded-lg mb-4 bg-gradient-to-br from-brand-100 to-brand-300 dark:from-slate-800 dark:to-slate-700 grid place-items-center overflow-hidden">
              {b.coverUrl ? (
                <img
                  src={b.coverUrl}
                  alt={b.title}
                  className="w-full h-full object-cover"
                  loading="lazy"
                  onError={(e) => {
                    (e.currentTarget as HTMLImageElement).style.display = 'none';
                  }}
                />
              ) : (
                <BookOpen size={48} className="text-brand-600/60" />
              )}
            </div>
            <h3 className="font-semibold leading-tight line-clamp-2">{b.title}</h3>
            <p className="text-sm text-slate-500 mt-1 line-clamp-1">
              {b.authors.map((a) => a.fullName).join(', ') || 'Unknown author'}
            </p>
            <div className="flex items-center justify-between mt-3 text-xs text-slate-500">
              <span>{b.categoryName ?? '—'}</span>
              <span className={b.availableCopies > 0 ? 'badge-success' : 'badge-danger'}>
                {b.availableCopies > 0 ? `${b.availableCopies} available` : 'Unavailable'}
              </span>
            </div>
            <button
              onClick={() => reserve(b)}
              disabled={b.availableCopies <= 0 || reserving === b.id}
              className="btn-primary mt-4"
            >
              <BookMarked size={16} /> Reserve
            </button>
          </article>
        ))}
      </div>

      {data && <Pagination page={data.page} totalPages={data.totalPages} onChange={setPage} />}
    </div>
  );
}
