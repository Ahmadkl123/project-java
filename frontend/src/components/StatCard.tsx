import { LucideIcon } from 'lucide-react';

interface Props {
  title: string;
  value: number | string;
  icon: LucideIcon;
  trend?: string;
  color?: 'blue' | 'green' | 'amber' | 'red' | 'purple';
}

const palette: Record<NonNullable<Props['color']>, string> = {
  blue: 'from-blue-500 to-blue-700',
  green: 'from-emerald-500 to-emerald-700',
  amber: 'from-amber-500 to-amber-700',
  red: 'from-red-500 to-red-700',
  purple: 'from-purple-500 to-purple-700',
};

export default function StatCard({ title, value, icon: Icon, trend, color = 'blue' }: Props) {
  return (
    <div className="card p-5">
      <div className="flex items-center justify-between">
        <div>
          <p className="text-sm text-slate-500">{title}</p>
          <p className="text-3xl font-bold mt-1">{value}</p>
          {trend && <p className="text-xs text-slate-500 mt-2">{trend}</p>}
        </div>
        <div className={`w-12 h-12 rounded-xl bg-gradient-to-br ${palette[color]} grid place-items-center text-white`}>
          <Icon size={22} />
        </div>
      </div>
    </div>
  );
}
