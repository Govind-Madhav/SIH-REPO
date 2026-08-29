import React from 'react';

export const MapLegend: React.FC = () => {
  return (
    <div className="bg-white/95 backdrop-blur border border-slate-200 rounded-lg p-3 text-[11px] space-y-2 shadow-md">
      <div className="font-bold text-slate-800 uppercase tracking-wider text-[10px] border-b border-slate-200 pb-1 mb-1.5 flex items-center justify-between">
        <span>Map Key & Status Legend</span>
        <span className="text-emerald-700 font-mono text-[9px]">NER GIS v1.0</span>
      </div>

      {/* Vehicle States */}
      <div>
        <span className="text-slate-500 font-semibold block mb-1">Vehicle Status</span>
        <div className="grid grid-cols-2 gap-x-3 gap-y-1">
          <div className="flex items-center space-x-1.5">
            <span className="w-2.5 h-2.5 rounded-full bg-emerald-500 shadow-sm" />
            <span className="text-slate-800">On Track</span>
          </div>
          <div className="flex items-center space-x-1.5">
            <span className="w-2.5 h-2.5 rounded-full bg-amber-500 shadow-sm" />
            <span className="text-slate-800">Delayed</span>
          </div>
          <div className="flex items-center space-x-1.5">
            <span className="w-2.5 h-2.5 rounded-full bg-rose-500 shadow-sm animate-pulse" />
            <span className="text-slate-800">At Risk</span>
          </div>
          <div className="flex items-center space-x-1.5">
            <span className="w-2.5 h-2.5 rounded-full bg-slate-400" />
            <span className="text-slate-500">Offline</span>
          </div>
        </div>
      </div>

      {/* Incidents */}
      <div>
        <span className="text-slate-500 font-semibold block mb-1">Incident Types</span>
        <div className="grid grid-cols-2 gap-x-3 gap-y-1">
          <div className="flex items-center space-x-1.5">
            <span className="w-2.5 h-2.5 rounded bg-rose-600" />
            <span className="text-slate-800">Landslide</span>
          </div>
          <div className="flex items-center space-x-1.5">
            <span className="w-2.5 h-2.5 rounded bg-blue-600" />
            <span className="text-slate-800">Flood</span>
          </div>
          <div className="flex items-center space-x-1.5">
            <span className="w-2.5 h-2.5 rounded bg-amber-600" />
            <span className="text-slate-800">Road Damage</span>
          </div>
          <div className="flex items-center space-x-1.5">
            <span className="w-2.5 h-2.5 rounded bg-purple-600" />
            <span className="text-slate-800">Bridge Issue</span>
          </div>
        </div>
      </div>

      {/* Route Types */}
      <div>
        <span className="text-slate-500 font-semibold block mb-1">Route Corridors</span>
        <div className="space-y-1">
          <div className="flex items-center space-x-2">
            <span className="w-4 h-1 bg-emerald-600 rounded" />
            <span className="text-slate-800">Primary Highway (Normal)</span>
          </div>
          <div className="flex items-center space-x-2">
            <span className="w-4 h-1 bg-rose-600 rounded border border-dashed border-white" />
            <span className="text-rose-700 font-bold">Blocked Corridor</span>
          </div>
          <div className="flex items-center space-x-2">
            <span className="w-4 h-1 bg-cyan-600 rounded" />
            <span className="text-cyan-800 font-bold">AI Rerouted Corridor</span>
          </div>
        </div>
      </div>
    </div>
  );
};
