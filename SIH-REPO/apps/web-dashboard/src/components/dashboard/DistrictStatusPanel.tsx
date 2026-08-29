import React, { useState } from 'react';
import { DistrictStatus } from '../../types/district';
import { MapPin, Search } from 'lucide-react';

interface DistrictStatusPanelProps {
  districts: DistrictStatus[];
}

export const DistrictStatusPanel: React.FC<DistrictStatusPanelProps> = ({ districts }) => {
  const [searchTerm, setSearchTerm] = useState('');

  const filtered = districts.filter(
    (d) =>
      d.name.toLowerCase().includes(searchTerm.toLowerCase()) ||
      d.state.toLowerCase().includes(searchTerm.toLowerCase())
  );

  return (
    <div className="bg-white border border-slate-200 rounded-xl p-4 shadow-sm flex flex-col h-full">
      <div className="flex items-center justify-between border-b border-slate-100 pb-3 mb-3">
        <div className="flex items-center space-x-2">
          <MapPin className="w-4 h-4 text-emerald-600" />
          <h2 className="text-xs font-bold text-slate-900 uppercase tracking-wider">
            DISTRICT CONNECTIVITY MATRIX (NER)
          </h2>
        </div>

        {/* Search */}
        <div className="relative">
          <input
            type="text"
            placeholder="Search district..."
            value={searchTerm}
            onChange={(e) => setSearchTerm(e.target.value)}
            className="pl-7 pr-3 py-1 bg-slate-50 border border-slate-200 rounded text-slate-900 placeholder-slate-400 text-[11px] focus:outline-none focus:ring-1 focus:ring-emerald-500 w-40"
          />
          <Search className="w-3.5 h-3.5 text-slate-400 absolute left-2 top-1.5" />
        </div>
      </div>

      <div className="space-y-2 overflow-y-auto max-h-[300px] pr-1 flex-1">
        {filtered.map((district) => {
          let badgeColor = 'bg-emerald-50 text-emerald-700 border-emerald-200';
          let statusDot = '🟢';
          if (district.status === 'PARTIAL_ACCESS') {
            badgeColor = 'bg-amber-50 text-amber-700 border-amber-200';
            statusDot = '🟡';
          } else if (district.status === 'INACCESSIBLE') {
            badgeColor = 'bg-rose-50 text-rose-700 border-rose-200 animate-pulse';
            statusDot = '🔴';
          }

          return (
            <div
              key={district.id}
              className="bg-slate-50 p-3 rounded-lg border border-slate-200 hover:border-slate-300 transition flex items-center justify-between"
            >
              <div>
                <div className="flex items-center space-x-2">
                  <span className="text-xs font-bold text-slate-900">{district.name}</span>
                  <span className="text-[10px] text-slate-500 font-medium">({district.state})</span>
                </div>
                <div className="flex items-center space-x-3 mt-1 text-[11px] text-slate-600">
                  <span>
                    Incidents: <strong className="text-slate-900">{district.activeIncidentsCount}</strong>
                  </span>
                  <span>•</span>
                  <span>
                    Vehicles: <strong className="text-slate-900">{district.vehiclesOperatingCount}</strong>
                  </span>
                </div>
                {district.weatherAlert && (
                  <div className="text-[10px] text-rose-600 font-semibold mt-1">
                    ⚠ {district.weatherAlert}
                  </div>
                )}
              </div>

              <div className="text-right">
                <div className="text-sm font-black text-slate-900 font-mono">
                  {district.accessibilityPercentage}% Access
                </div>
                <span className={`inline-block mt-1 text-[10px] font-bold px-2 py-0.5 rounded border ${badgeColor}`}>
                  {statusDot} {district.status.replace('_', ' ')}
                </span>
              </div>
            </div>
          );
        })}
      </div>
    </div>
  );
};
