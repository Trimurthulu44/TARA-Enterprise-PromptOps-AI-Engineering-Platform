import React from 'react';
import { LucideIcon } from 'lucide-react';

interface StatCardProps {
  title: string;
  value: string | number;
  change?: string;
  isPositive?: boolean;
  icon: LucideIcon;
  subtitle?: string;
  accentColor?: 'cyan' | 'violet' | 'emerald' | 'amber' | 'rose';
}

export const StatCard: React.FC<StatCardProps> = ({
  title,
  value,
  change,
  isPositive = true,
  icon: Icon,
  subtitle,
  accentColor = 'cyan',
}) => {
  const colorMap = {
    cyan: 'text-cyan-400 bg-cyan-500/10 border-cyan-500/30',
    violet: 'text-violet-400 bg-violet-500/10 border-violet-500/30',
    emerald: 'text-emerald-400 bg-emerald-500/10 border-emerald-500/30',
    amber: 'text-amber-400 bg-amber-500/10 border-amber-500/30',
    rose: 'text-rose-400 bg-rose-500/10 border-rose-500/30',
  };

  return (
    <div className="glass-card rounded-xl p-5 relative overflow-hidden transition-all duration-200">
      <div className="flex items-start justify-between">
        <div>
          <p className="text-xs font-medium text-slate-400 tracking-wide uppercase">{title}</p>
          <h3 className="text-2xl font-bold text-white mt-1 font-mono tracking-tight">{value}</h3>
        </div>
        <div className={`p-2.5 rounded-xl border ${colorMap[accentColor]}`}>
          <Icon className="w-5 h-5" />
        </div>
      </div>

      <div className="mt-3 flex items-center gap-2">
        {change && (
          <span className={`text-xs font-semibold px-1.5 py-0.5 rounded ${isPositive ? 'text-emerald-400 bg-emerald-500/10' : 'text-rose-400 bg-rose-500/10'}`}>
            {change}
          </span>
        )}
        {subtitle && <span className="text-xs text-slate-400">{subtitle}</span>}
      </div>
    </div>
  );
};
