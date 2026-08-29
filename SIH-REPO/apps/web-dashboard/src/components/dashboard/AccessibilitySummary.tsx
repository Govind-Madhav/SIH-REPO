import React from 'react';
import { MapPin, Truck, AlertTriangle, ShieldAlert, Clock } from 'lucide-react';

interface AccessibilitySummaryProps {
  districtAccessiblePct: number;
  activeVehiclesCount: number;
  activeIncidentsCount: number;
  highRiskCorridorsCount: number;
  delayedShipmentsCount: number;
}

export const AccessibilitySummary: React.FC<AccessibilitySummaryProps> = ({
  districtAccessiblePct,
  activeVehiclesCount,
  activeIncidentsCount,
  highRiskCorridorsCount,
  delayedShipmentsCount,
}) => {
  return (
    <div className="grid grid-cols-1 sm:grid-cols-2 lg:grid-cols-5 gap-3">
      {/* Card 1: District Accessibility Breakdown */}
      <div className="bg-white border border-slate-200 rounded-xl p-4 shadow-sm flex flex-col justify-between">
        <div className="flex items-center justify-between text-xs text-slate-500">
          <span className="font-semibold uppercase tracking-wider text-[11px]">District Access</span>
          <MapPin className="w-4 h-4 text-emerald-600" />
        </div>
        <div className="mt-2">
          <div className="text-2xl font-black text-slate-900">{districtAccessiblePct}%</div>
          <div className="flex items-center space-x-2 text-[10px] mt-1 font-semibold">
            <span className="text-emerald-700">🟢 68% Access</span>
            <span className="text-amber-700">🟡 22% Partial</span>
            <span className="text-rose-700">🔴 10% Blocked</span>
          </div>
        </div>
        <div className="w-full bg-slate-100 h-1.5 rounded-full overflow-hidden flex mt-2">
          <div style={{ width: '68%' }} className="bg-emerald-500 h-full" />
          <div style={{ width: '22%' }} className="bg-amber-500 h-full" />
          <div style={{ width: '10%' }} className="bg-rose-500 h-full" />
        </div>
      </div>

      {/* Card 2: Active Vehicles */}
      <div className="bg-white border border-slate-200 rounded-xl p-4 shadow-sm flex items-center justify-between">
        <div>
          <span className="text-slate-500 text-xs font-semibold uppercase tracking-wider block">
            Active Vehicles
          </span>
          <span className="text-2xl font-black text-slate-900 mt-1 block">{activeVehiclesCount}</span>
          <span className="text-[11px] text-emerald-700 font-semibold">18 On Schedule</span>
        </div>
        <div className="w-10 h-10 rounded-lg bg-emerald-50 border border-emerald-200 flex items-center justify-center text-emerald-600">
          <Truck className="w-5 h-5" />
        </div>
      </div>

      {/* Card 3: Active Incidents */}
      <div className="bg-white border border-slate-200 rounded-xl p-4 shadow-sm flex items-center justify-between">
        <div>
          <span className="text-slate-500 text-xs font-semibold uppercase tracking-wider block">
            Active Incidents
          </span>
          <span className="text-2xl font-black text-rose-600 mt-1 block">{activeIncidentsCount}</span>
          <span className="text-[11px] text-rose-700 font-semibold">1 Critical Landslide</span>
        </div>
        <div className="w-10 h-10 rounded-lg bg-rose-50 border border-rose-200 flex items-center justify-center text-rose-600">
          <AlertTriangle className="w-5 h-5" />
        </div>
      </div>

      {/* Card 4: High-Risk Corridors */}
      <div className="bg-white border border-slate-200 rounded-xl p-4 shadow-sm flex items-center justify-between">
        <div>
          <span className="text-slate-500 text-xs font-semibold uppercase tracking-wider block">
            Risk Corridors
          </span>
          <span className="text-2xl font-black text-amber-600 mt-1 block">{highRiskCorridorsCount}</span>
          <span className="text-[11px] text-amber-700 font-semibold">Haflong & Bararak</span>
        </div>
        <div className="w-10 h-10 rounded-lg bg-amber-50 border border-amber-200 flex items-center justify-center text-amber-600">
          <ShieldAlert className="w-5 h-5" />
        </div>
      </div>

      {/* Card 5: Delayed Shipments */}
      <div className="bg-white border border-slate-200 rounded-xl p-4 shadow-sm flex items-center justify-between">
        <div>
          <span className="text-slate-500 text-xs font-semibold uppercase tracking-wider block">
            Delayed Goods
          </span>
          <span className="text-2xl font-black text-slate-900 mt-1 block">{delayedShipmentsCount}</span>
          <span className="text-[11px] text-teal-700 font-semibold">Rerouting in Progress</span>
        </div>
        <div className="w-10 h-10 rounded-lg bg-teal-50 border border-teal-200 flex items-center justify-center text-teal-600">
          <Clock className="w-5 h-5" />
        </div>
      </div>
    </div>
  );
};
