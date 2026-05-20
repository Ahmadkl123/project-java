import { useEffect, useState } from 'react';
import { api, unwrap } from '../../api/client';
import type { PageResponse } from '../../types';
import Pagination from '../../components/Pagination';
import { fmtDateTime } from '../../utils/format';

interface AuditLog {
  id: number;
  actor: string;
  action: string;
  entityType?: string;
  entityId?: number;
  details?: string;
  ipAddress?: string;
  timestamp: string;
}

export default function AuditLogsPage() {
  const [page, setPage] = useState(0);
  const [data, setData] = useState<PageResponse<AuditLog> | null>(null);

  useEffect(() => {
    unwrap<PageResponse<AuditLog>>(api.get('/admin/audit', { params: { page, size: 30 } }))
      .then(setData).catch(() => {});
  }, [page]);

  return (
    <div className="space-y-4">
      <h1 className="text-2xl font-bold">Audit logs</h1>
      <div className="card overflow-x-auto">
        <table className="w-full text-sm">
          <thead className="text-left text-slate-500 bg-slate-50 dark:bg-slate-900/50">
            <tr>
              <th className="px-4 py-3">When</th><th>Actor</th><th>Action</th>
              <th>Entity</th><th>Details</th>
            </tr>
          </thead>
          <tbody>
            {data?.content.map((a) => (
              <tr key={a.id} className="border-t border-slate-100 dark:border-slate-800">
                <td className="px-4 py-3 text-slate-500">{fmtDateTime(a.timestamp)}</td>
                <td>{a.actor}</td>
                <td><span className="badge-info">{a.action}</span></td>
                <td>{a.entityType ? `${a.entityType}#${a.entityId}` : '—'}</td>
                <td className="text-slate-500 max-w-md truncate">{a.details}</td>
              </tr>
            ))}
          </tbody>
        </table>
      </div>
      {data && <Pagination page={data.page} totalPages={data.totalPages} onChange={setPage} />}
    </div>
  );
}
