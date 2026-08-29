import React, { useState } from 'react';
import { AlertCircle, CheckCircle2, ShieldAlert, Bell, Clock } from 'lucide-react';

export interface AlertItem {
  id: string;
  severity: 'CRITICAL' | 'HIGH' | 'MEDIUM' | 'LOW';
  title: string;
  message: string;
  timeAgo: string;
  acknowledged: boolean;
  targetVehicleCode?: string;
}

const INITIAL_ALERTS: AlertItem[] = [
  {
    id: 'alt-1',
    severity: 'CRITICAL',
    title: 'Landslide Blockage Reported',
    message: 'NH-27 Haflong Pass blocked by 400m earth slip. Emergency BRO response active.',
    timeAgo: '2 mins ago',
    acknowledged: false,
    targetVehicleCode: 'NER-07',
  },
  {
    id: 'alt-2',
    severity: 'HIGH',
    title: 'Heavy Rainfall Warning Elevated Risk',
    message: 'Dima Hasao & Cachar sectors experiencing 145mm/24h precipitation. Risk elevated to HIGH.',
    timeAgo: '5 mins ago',
    acknowledged: false,
  },
  {
    id: 'alt-3',
    severity: 'MEDIUM',
    title: 'Delivery Schedule Delay',
    message: 'Vehicle NER-07 delayed by estimated 25 minutes due to Haflong crawl.',
    timeAgo: '8 mins ago',
    acknowledged: true,
    targetVehicleCode: 'NER-07',
  },
  {
    id: 'alt-4',
    severity: 'LOW',
    title: 'GPS Signal Degraded',
    message: 'Vehicle NER-21 entered low network corridor near Phedema Gap.',
    timeAgo: '24 mins ago',
    acknowledged: true,
    targetVehicleCode: 'NER-21',
  },
];

export const LiveAlertsPanel: React.FC = () => {
  const [alerts, setAlerts] = useState<AlertItem[]>(INITIAL_ALERTS);

  const toggleAcknowledge = (id: string) => {
    setAlerts((prev) =>
      prev.map((a) => (a.id === id ? { ...a, acknowledged: !a.acknowledged } : a))
    );
  };

  return (
    <div className="bg-white border border-slate-200 rounded-xl p-4 shadow-sm flex flex-col h-full">
      <div className="flex items-center justify-between border-b border-slate-100 pb-3 mb-3">
        <div className="flex items-center space-x-2">
          <Bell className="w-4 h-4 text-rose-600" />
          <h2 className="text-xs font-bold text-slate-900 uppercase tracking-wider">
            LIVE ALERTS & NOTIFICATION STREAM
          </h2>
        </div>
        <span className="text-[10px] font-mono bg-rose-50 text-rose-700 border border-rose-200 px-2 py-0.5 rounded font-bold">
          {alerts.filter((a) => !a.acknowledged).length} UNACKNOWLEDGED
        </span>
      </div>

      <div className="space-y-2 overflow-y-auto max-h-[380px] pr-1 flex-1">
        {alerts.map((alert) => {
          let badgeColor = 'bg-rose-50 text-rose-700 border-rose-200';
          let borderLeft = 'border-l-4 border-l-rose-600';
          if (alert.severity === 'HIGH') {
            badgeColor = 'bg-amber-50 text-amber-700 border-amber-200';
            borderLeft = 'border-l-4 border-l-amber-500';
          } else if (alert.severity === 'MEDIUM') {
            badgeColor = 'bg-yellow-50 text-yellow-800 border-yellow-200';
            borderLeft = 'border-l-4 border-l-yellow-500';
          } else if (alert.severity === 'LOW') {
            badgeColor = 'bg-blue-50 text-blue-700 border-blue-200';
            borderLeft = 'border-l-4 border-l-blue-500';
          }

          return (
            <div
              key={alert.id}
              className={`bg-slate-50 p-3 rounded-lg border border-slate-200 ${borderLeft} space-y-1.5 transition ${
                alert.acknowledged ? 'opacity-60' : ''
              }`}
            >
              <div className="flex items-center justify-between">
                <span className={`text-[10px] font-bold px-1.5 py-0.5 rounded border ${badgeColor}`}>
                  {alert.severity}
                </span>
                <span className="text-[10px] text-slate-500 flex items-center space-x-1">
                  <Clock className="w-3 h-3 text-slate-400" />
                  <span>{alert.timeAgo}</span>
                </span>
              </div>

              <h4 className="text-xs font-bold text-slate-900">{alert.title}</h4>
              <p className="text-[11px] text-slate-600 leading-relaxed">{alert.message}</p>

              {alert.targetVehicleCode && (
                <div className="text-[10px] text-teal-700 font-mono font-semibold">
                  Vehicle Target: {alert.targetVehicleCode}
                </div>
              )}

              <div className="pt-1 flex justify-end">
                <button
                  onClick={() => toggleAcknowledge(alert.id)}
                  className={`text-[10px] font-semibold px-2 py-1 rounded transition flex items-center space-x-1 ${
                    alert.acknowledged
                      ? 'bg-slate-200 text-slate-600 border border-slate-300'
                      : 'bg-emerald-50 hover:bg-emerald-100 text-emerald-700 border border-emerald-200'
                  }`}
                >
                  <CheckCircle2 className="w-3 h-3" />
                  <span>{alert.acknowledged ? 'Acknowledged' : 'Acknowledge Alert'}</span>
                </button>
              </div>
            </div>
          );
        })}
      </div>
    </div>
  );
};
