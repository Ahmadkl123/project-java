import { useEffect, useState } from 'react';
import toast from 'react-hot-toast';
import { reservationsApi } from '../../api/endpoints';
import type { PageResponse, Reservation, ReservationStatus } from '../../types';
import Pagination from '../../components/Pagination';
import { fmtDate, statusBadgeClass } from '../../utils/format';

export default function ReservationsAdminPage() {
  const [status, setStatus] = useState<ReservationStatus | ''>('');
  const [page, setPage] = useState(0);
  const [data, setData] = useState<PageResponse<Reservation> | null>(null);

  const load = () => reservationsApi.listAll({ status: status || undefined, page, size: 15 })
    .then(setData).catch(() => {});
  useEffect(() => { load(); }, [status, page]);

  const updateStatus = async (id: number, newStatus: ReservationStatus) => {
    try { await reservationsApi.updateStatus(id, newStatus); toast.success('Updated'); load(); } catch {}
  };

  return (
    <div className="space-y-4">
      <div className="flex items-center justify-between">
        <h1 className="text-2xl font-bold">Reservations</h1>
        <select className="input w-48" value={status}
                onChange={(e) => { setStatus(e.target.value as any); setPage(0); }}>
          <option value="">All statuses</option>
          {['PENDING','APPROVED','REJECTED','CANCELLED','FULFILLED','EXPIRED'].map((s) =>
            <option key={s} value={s}>{s}</option>)}
        </select>
      </div>

      <div className="card overflow-x-auto">
        <table className="w-full text-sm">
          <thead className="text-left text-slate-500 bg-slate-50 dark:bg-slate-900/50">
            <tr>
              <th className="px-4 py-3">Student</th><th>Book</th>
              <th>Date</th><th>Expires</th><th>Status</th><th></th>
            </tr>
          </thead>
          <tbody>
            {data?.content.map((r) => (
              <tr key={r.id} className="border-t border-slate-100 dark:border-slate-800">
                <td className="px-4 py-3">{r.userFullName}</td>
                <td>{r.bookTitle}</td>
                <td>{fmtDate(r.reservationDate)}</td>
                <td>{fmtDate(r.expiryDate)}</td>
                <td><span className={statusBadgeClass(r.status)}>{r.status}</span></td>
                <td className="pr-4 text-right space-x-2 whitespace-nowrap">
                  {r.status === 'PENDING' && (
                    <>
                      <button onClick={() => updateStatus(r.id, 'APPROVED')}
                              className="text-green-600 hover:underline">Approve</button>
                      <button onClick={() => updateStatus(r.id, 'REJECTED')}
                              className="text-red-600 hover:underline">Reject</button>
                    </>
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
