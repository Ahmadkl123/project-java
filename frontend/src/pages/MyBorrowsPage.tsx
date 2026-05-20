import { useEffect, useState } from 'react';
import { borrowsApi } from '../api/endpoints';
import type { Borrow, PageResponse } from '../types';
import Pagination from '../components/Pagination';
import { fmtDate, statusBadgeClass } from '../utils/format';

export default function MyBorrowsPage() {
  const [page, setPage] = useState(0);
  const [data, setData] = useState<PageResponse<Borrow> | null>(null);

  useEffect(() => { borrowsApi.mine(page, 10).then(setData).catch(() => {}); }, [page]);

  return (
    <div className="space-y-4">
      <h1 className="text-2xl font-bold">My Borrows</h1>

      <div className="card overflow-hidden">
        <table className="w-full text-sm">
          <thead className="text-left text-slate-500 bg-slate-50 dark:bg-slate-900/50">
            <tr>
              <th className="px-4 py-3">Book</th>
              <th>Borrowed</th>
              <th>Due</th>
              <th>Returned</th>
              <th>Status</th>
              <th>Fine</th>
            </tr>
          </thead>
          <tbody>
            {data?.content.length === 0 && (
              <tr><td colSpan={6} className="text-center py-12 text-slate-500">No borrows yet.</td></tr>
            )}
            {data?.content.map((b) => (
              <tr key={b.id} className="border-t border-slate-100 dark:border-slate-800">
                <td className="px-4 py-3">{b.bookTitle}</td>
                <td>{fmtDate(b.borrowDate)}</td>
                <td>{fmtDate(b.dueDate)}</td>
                <td>{fmtDate(b.returnDate)}</td>
                <td><span className={statusBadgeClass(b.status)}>{b.status}</span></td>
                <td>{b.fineAmount ? `${b.fineAmount.toFixed(2)} €` : '—'}</td>
              </tr>
            ))}
          </tbody>
        </table>
      </div>

      {data && <Pagination page={data.page} totalPages={data.totalPages} onChange={setPage} />}
    </div>
  );
}
