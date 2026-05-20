import { useEffect, useState } from 'react';
import toast from 'react-hot-toast';
import { reservationsApi } from '../api/endpoints';
import type { PageResponse, Reservation } from '../types';
import Pagination from '../components/Pagination';
import { fmtDate, statusBadgeClass } from '../utils/format';

export default function MyReservationsPage() {
  const [page, setPage] = useState(0);
  const [data, setData] = useState<PageResponse<Reservation> | null>(null);

  const load = () => reservationsApi.mine(page, 10).then(setData).catch(() => {});
  useEffect(() => { load(); }, [page]);

  const cancel = async (id: number) => {
    try {
      await reservationsApi.cancel(id);
      toast.success('Reservation cancelled');
      load();
    } catch {}
  };

  return (
    <div className="space-y-4">
      <h1 className="text-2xl font-bold">My Reservations</h1>

      <div className="card overflow-hidden">
        <table className="w-full text-sm">
          <thead className="text-left text-slate-500 bg-slate-50 dark:bg-slate-900/50">
            <tr>
              <th className="px-4 py-3">Book</th>
              <th>Reserved</th>
              <th>Expires</th>
              <th>Status</th>
              <th></th>
            </tr>
          </thead>
          <tbody>
            {data?.content.length === 0 && (
              <tr><td colSpan={5} className="text-center py-12 text-slate-500">No reservations.</td></tr>
            )}
            {data?.content.map((r) => (
              <tr key={r.id} className="border-t border-slate-100 dark:border-slate-800">
                <td className="px-4 py-3">{r.bookTitle}</td>
                <td>{fmtDate(r.reservationDate)}</td>
                <td>{fmtDate(r.expiryDate)}</td>
                <td><span className={statusBadgeClass(r.status)}>{r.status}</span></td>
                <td className="pr-4">
                  {r.status === 'PENDING' && (
                    <button onClick={() => cancel(r.id)} className="text-red-600 hover:underline text-sm">
                      Cancel
                    </button>
                  )}
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
