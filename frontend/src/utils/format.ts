export const fmtDate = (iso?: string | null) => {
  if (!iso) return '—';
  try {
    return new Date(iso).toLocaleDateString(undefined, {
      year: 'numeric', month: 'short', day: 'numeric',
    });
  } catch {
    return iso;
  }
};

export const fmtDateTime = (iso?: string | null) => {
  if (!iso) return '—';
  try {
    return new Date(iso).toLocaleString();
  } catch {
    return iso;
  }
};

export const statusBadgeClass = (status: string) => {
  switch (status) {
    case 'APPROVED':
    case 'ACTIVE':
    case 'FULFILLED':
      return 'badge-info';
    case 'RETURNED':
      return 'badge-success';
    case 'PENDING':
      return 'badge-warning';
    case 'OVERDUE':
    case 'REJECTED':
    case 'LOST':
      return 'badge-danger';
    default:
      return 'badge-neutral';
  }
};
