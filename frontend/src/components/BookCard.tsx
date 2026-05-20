import { BookOpen, BookMarked } from 'lucide-react';
import { useState } from 'react';
import toast from 'react-hot-toast';
import type { Book } from '../types';
import { reservationsApi } from '../api/endpoints';

interface Props {
  book: Book;
  onReserved?: () => void;
}

export default function BookCard({ book, onReserved }: Props) {
  const [busy, setBusy] = useState(false);
  const [coverFailed, setCoverFailed] = useState(false);

  const reserve = async () => {
    setBusy(true);
    try {
      await reservationsApi.create(book.id);
      toast.success(`Reserved: ${book.title}`);
      onReserved?.();
    } catch {
      // global toast
    } finally {
      setBusy(false);
    }
  };

  return (
    <article className="card p-4 flex flex-col h-full">
      <div className="aspect-[4/5] w-full rounded-lg mb-3 bg-gradient-to-br from-brand-100 to-brand-300 dark:from-slate-800 dark:to-slate-700 grid place-items-center overflow-hidden">
        {book.coverUrl && !coverFailed ? (
          <img
            src={book.coverUrl}
            alt={book.title}
            className="w-full h-full object-cover"
            loading="lazy"
            onError={() => setCoverFailed(true)}
          />
        ) : (
          <BookOpen size={36} className="text-brand-600/60" />
        )}
      </div>
      <h3 className="font-semibold leading-tight line-clamp-2 text-sm">{book.title}</h3>
      <p className="text-xs text-slate-500 mt-1 line-clamp-1">
        {book.authors.map((a) => a.fullName).join(', ') || 'Unknown author'}
      </p>
      <div className="flex items-center justify-between mt-2 text-xs">
        <span className="text-slate-500">{book.categoryName ?? '—'}</span>
        <span className={book.availableCopies > 0 ? 'badge-success' : 'badge-danger'}>
          {book.availableCopies > 0 ? 'Available' : 'Unavailable'}
        </span>
      </div>
      <button
        onClick={reserve}
        disabled={book.availableCopies <= 0 || busy}
        className="btn-primary mt-3 text-xs py-1.5"
      >
        <BookMarked size={14} /> Reserve
      </button>
    </article>
  );
}
