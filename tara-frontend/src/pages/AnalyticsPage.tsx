import React, { useState } from 'react';
import {
  AreaChart, Area, LineChart, Line, BarChart, Bar,
  XAxis, YAxis, Tooltip, ResponsiveContainer, CartesianGrid, Legend
} from 'recharts';
import { BarChart3, TrendingUp, DollarSign, Clock, ShieldCheck, CheckCircle2 } from 'lucide-react';
import { StatCard } from '../components/ui/StatCard';

const TIMESERIES_METRICS = [
  { day: 'Day 1', requests: 1240, latency: 410, cost: 14.2, successRate: 98.8, evalScore: 92 },
  { day: 'Day 2', requests: 1480, latency: 395, cost: 16.8, successRate: 99.1, evalScore: 93 },
  { day: 'Day 3', requests: 1620, latency: 380, cost: 18.5, successRate: 99.4, evalScore: 94 },
  { day: 'Day 4', requests: 1390, latency: 390, cost: 15.1, successRate: 99.0, evalScore: 93 },
  { day: 'Day 5', requests: 1950, latency: 360, cost: 22.4, successRate: 99.5, evalScore: 95 },
  { day: 'Day 6', requests: 2100, latency: 375, cost: 24.1, successRate: 99.3, evalScore: 94 },
  { day: 'Day 7', requests: 2450, latency: 350, cost: 28.0, successRate: 99.7, evalScore: 96 },
];

const VERSION_TRENDS = [
  { version: 'v1.0.0', accuracy: 91.2, p95Latency: 385, tokensPerReq: 410 },
  { version: 'v1.1.0', accuracy: 96.5, p95Latency: 340, tokensPerReq: 380 },
  { version: 'v1.2.0-rc', accuracy: 97.8, p95Latency: 320, tokensPerReq: 355 },
];

export const AnalyticsPage: React.FC = () => {
  const [timeRange, setTimeRange] = useState('7d');

  return (
    <div className="space-y-6">
      <div className="flex flex-col sm:flex-row sm:items-center justify-between gap-4">
        <div>
          <h1 className="text-2xl font-bold text-white tracking-tight">Enterprise Analytics & Observability</h1>
          <p className="text-xs text-slate-400 mt-1">Multi-dimensional operational telemetry across prompts, latency percentiles, and costs</p>
        </div>

        <div className="flex items-center gap-2">
          {['24h', '7d', '30d', '90d'].map((range) => (
            <button
              key={range}
              onClick={() => setTimeRange(range)}
              className={`px-3 py-1.5 rounded-lg text-xs font-mono font-medium transition-all ${
                timeRange === range
                  ? 'bg-brand-600 text-white shadow-sm'
                  : 'bg-slate-900 text-slate-400 hover:text-white border border-tara-border'
              }`}
            >
              {range}
            </button>
          ))}
        </div>
      </div>

      {/* 4 Analytics Stat Cards */}
      <div className="grid grid-cols-1 sm:grid-cols-2 lg:grid-cols-4 gap-4">
        <StatCard
          title="Total Invocations"
          value="148,920"
          change="+18.4%"
          icon={BarChart3}
          accentColor="cyan"
          subtitle="over past 7 days"
        />
        <StatCard
          title="Avg Success Rate"
          value="99.42%"
          change="+0.8%"
          icon={CheckCircle2}
          accentColor="emerald"
          subtitle="99.9% target SLA"
        />
        <StatCard
          title="P95 Latency"
          value="350 ms"
          change="-60ms"
          icon={Clock}
          accentColor="violet"
          subtitle="1500ms max ceiling"
        />
        <StatCard
          title="Accumulated Cost"
          value="$312.45"
          change="On budget"
          icon={DollarSign}
          accentColor="amber"
          subtitle="62.5M tokens billed"
        />
      </div>

      {/* Recharts Grid */}
      <div className="grid grid-cols-1 lg:grid-cols-2 gap-6">
        {/* Latency & Invocations */}
        <div className="glass-card rounded-xl p-5 border border-tara-border">
          <div className="flex items-center justify-between mb-4">
            <div>
              <h3 className="text-sm font-semibold text-white">Execution Volume vs P95 Latency</h3>
              <p className="text-xs text-slate-400">Correlation between API concurrency and response times</p>
            </div>
          </div>
          <div className="h-64 w-full">
            <ResponsiveContainer width="100%" height="100%">
              <AreaChart data={TIMESERIES_METRICS}>
                <CartesianGrid strokeDasharray="3 3" stroke="#1e293b" />
                <XAxis dataKey="day" stroke="#475569" fontSize={11} />
                <YAxis stroke="#475569" fontSize={11} />
                <Tooltip contentStyle={{ backgroundColor: '#111726', borderColor: '#24304b', borderRadius: '8px', fontSize: '12px' }} />
                <Area type="monotone" dataKey="requests" stroke="#06b6d4" fill="#06b6d4" fillOpacity={0.2} />
                <Area type="monotone" dataKey="latency" stroke="#818cf8" fill="#818cf8" fillOpacity={0.2} />
              </AreaChart>
            </ResponsiveContainer>
          </div>
        </div>

        {/* Cost & Token Economics */}
        <div className="glass-card rounded-xl p-5 border border-tara-border">
          <div className="flex items-center justify-between mb-4">
            <div>
              <h3 className="text-sm font-semibold text-white">Daily Cost Consumption ($ USD)</h3>
              <p className="text-xs text-slate-400">Computed via model provider token pricing matrices</p>
            </div>
          </div>
          <div className="h-64 w-full">
            <ResponsiveContainer width="100%" height="100%">
              <BarChart data={TIMESERIES_METRICS}>
                <CartesianGrid strokeDasharray="3 3" stroke="#1e293b" />
                <XAxis dataKey="day" stroke="#475569" fontSize={11} />
                <YAxis stroke="#475569" fontSize={11} />
                <Tooltip contentStyle={{ backgroundColor: '#111726', borderColor: '#24304b', borderRadius: '8px', fontSize: '12px' }} />
                <Bar dataKey="cost" fill="#f59e0b" radius={[4, 4, 0, 0]} />
              </BarChart>
            </ResponsiveContainer>
          </div>
        </div>

        {/* Version Evolution & Accuracy Trends */}
        <div className="lg:col-span-2 glass-card rounded-xl p-5 border border-tara-border">
          <div className="flex items-center justify-between mb-4">
            <div>
              <h3 className="text-sm font-semibold text-white">Semantic Version Evolution Trends</h3>
              <p className="text-xs text-slate-400">Accuracy score improvement and token efficiency over released versions</p>
            </div>
          </div>
          <div className="h-64 w-full">
            <ResponsiveContainer width="100%" height="100%">
              <LineChart data={VERSION_TRENDS}>
                <CartesianGrid strokeDasharray="3 3" stroke="#1e293b" />
                <XAxis dataKey="version" stroke="#475569" fontSize={11} />
                <YAxis stroke="#475569" fontSize={11} />
                <Tooltip contentStyle={{ backgroundColor: '#111726', borderColor: '#24304b', borderRadius: '8px', fontSize: '12px' }} />
                <Legend />
                <Line type="monotone" dataKey="accuracy" stroke="#10b981" strokeWidth={2} name="Judge Accuracy (%)" />
                <Line type="monotone" dataKey="p95Latency" stroke="#8b5cf6" strokeWidth={2} name="P95 Latency (ms)" />
                <Line type="monotone" dataKey="tokensPerReq" stroke="#06b6d4" strokeWidth={2} name="Tokens per Request" />
              </LineChart>
            </ResponsiveContainer>
          </div>
        </div>
      </div>
    </div>
  );
};
