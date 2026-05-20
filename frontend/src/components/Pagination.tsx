import { ChevronLeft, ChevronRight } from 'lucide-react';

export default function Pagination({
  page, totalPages, onChange,
}: { page: number; totalPages: number; onChange: (p: number) => void }) {
  if (totalPages <= 1) return null;
  return (
    <div className="flex items-center justify-between mt-4">
      <span className="text-sm text-slate-500">
        Page {page + 1} of {totalPages}
      </span>
      <div className="flex gap-1">
        <button
          onClick={() => onChange(page - 1)}
          disabled={page === 0}
          className="btn-secondary p-2 disabled:opacity-40"
        >
          <ChevronLeft size={16} />
        </button>
        <button
          onClick={() => onChange(page + 1)}
          disabled={page >= totalPages - 1}
          className="btn-secondary p-2 disabled:opacity-40"
        >
          <ChevronRight size={16} />
        </button>
      </div>
    </div>
  );
}
