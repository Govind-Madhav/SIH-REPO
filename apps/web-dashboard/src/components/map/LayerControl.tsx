import React from 'react';
import { Layers, Eye, EyeOff, Truck, AlertTriangle, ShieldAlert, Route, MapPin } from 'lucide-react';

export interface MapLayersState {
  showVehicles: boolean;
  showIncidents: boolean;
  showRiskZones: boolean;
  showRoutes: boolean;
  showDistrictBoundaries: boolean;
}

interface LayerControlProps {
  layers: MapLayersState;
  onToggleLayer: (layerKey: keyof MapLayersState) => void;
}

export const LayerControl: React.FC<LayerControlProps> = ({ layers, onToggleLayer }) => {
  return (
    <div className="bg-white/95 backdrop-blur border border-slate-200 rounded-lg p-3 text-xs shadow-md space-y-2">
      <div className="flex items-center justify-between border-b border-slate-200 pb-1.5 mb-1 text-[11px] font-bold uppercase tracking-wider text-slate-800">
        <div className="flex items-center space-x-1.5">
          <Layers className="w-3.5 h-3.5 text-emerald-600" />
          <span>GIS Map Layer Controls</span>
        </div>
      </div>

      <div className="space-y-1.5">
        {/* Toggle Vehicles */}
        <button
          onClick={() => onToggleLayer('showVehicles')}
          className={`w-full flex items-center justify-between px-2.5 py-1.5 rounded transition ${
            layers.showVehicles
              ? 'bg-slate-100 text-slate-900 font-semibold border border-slate-300'
              : 'bg-slate-50 text-slate-500 border border-transparent'
          }`}
        >
          <div className="flex items-center space-x-2">
            <Truck className={`w-3.5 h-3.5 ${layers.showVehicles ? 'text-emerald-600' : 'text-slate-400'}`} />
            <span>Vehicles</span>
          </div>
          {layers.showVehicles ? <Eye className="w-3 h-3 text-emerald-600" /> : <EyeOff className="w-3 h-3 text-slate-400" />}
        </button>

        {/* Toggle Incidents */}
        <button
          onClick={() => onToggleLayer('showIncidents')}
          className={`w-full flex items-center justify-between px-2.5 py-1.5 rounded transition ${
            layers.showIncidents
              ? 'bg-slate-100 text-slate-900 font-semibold border border-slate-300'
              : 'bg-slate-50 text-slate-500 border border-transparent'
          }`}
        >
          <div className="flex items-center space-x-2">
            <AlertTriangle className={`w-3.5 h-3.5 ${layers.showIncidents ? 'text-rose-600' : 'text-slate-400'}`} />
            <span>Incidents</span>
          </div>
          {layers.showIncidents ? <Eye className="w-3 h-3 text-emerald-600" /> : <EyeOff className="w-3 h-3 text-slate-400" />}
        </button>

        {/* Toggle Risk Zones */}
        <button
          onClick={() => onToggleLayer('showRiskZones')}
          className={`w-full flex items-center justify-between px-2.5 py-1.5 rounded transition ${
            layers.showRiskZones
              ? 'bg-slate-100 text-slate-900 font-semibold border border-slate-300'
              : 'bg-slate-50 text-slate-500 border border-transparent'
          }`}
        >
          <div className="flex items-center space-x-2">
            <ShieldAlert className={`w-3.5 h-3.5 ${layers.showRiskZones ? 'text-amber-600' : 'text-slate-400'}`} />
            <span>Risk Zones</span>
          </div>
          {layers.showRiskZones ? <Eye className="w-3 h-3 text-emerald-600" /> : <EyeOff className="w-3 h-3 text-slate-400" />}
        </button>

        {/* Toggle Routes */}
        <button
          onClick={() => onToggleLayer('showRoutes')}
          className={`w-full flex items-center justify-between px-2.5 py-1.5 rounded transition ${
            layers.showRoutes
              ? 'bg-slate-100 text-slate-900 font-semibold border border-slate-300'
              : 'bg-slate-50 text-slate-500 border border-transparent'
          }`}
        >
          <div className="flex items-center space-x-2">
            <Route className={`w-3.5 h-3.5 ${layers.showRoutes ? 'text-teal-600' : 'text-slate-400'}`} />
            <span>Corridors & Reroutes</span>
          </div>
          {layers.showRoutes ? <Eye className="w-3 h-3 text-emerald-600" /> : <EyeOff className="w-3 h-3 text-slate-400" />}
        </button>

        {/* Toggle District Boundaries */}
        <button
          onClick={() => onToggleLayer('showDistrictBoundaries')}
          className={`w-full flex items-center justify-between px-2.5 py-1.5 rounded transition ${
            layers.showDistrictBoundaries
              ? 'bg-slate-100 text-slate-900 font-semibold border border-slate-300'
              : 'bg-slate-50 text-slate-500 border border-transparent'
          }`}
        >
          <div className="flex items-center space-x-2">
            <MapPin className={`w-3.5 h-3.5 ${layers.showDistrictBoundaries ? 'text-purple-600' : 'text-slate-400'}`} />
            <span>District Accessibility</span>
          </div>
          {layers.showDistrictBoundaries ? <Eye className="w-3 h-3 text-emerald-600" /> : <EyeOff className="w-3 h-3 text-slate-400" />}
        </button>
      </div>
    </div>
  );
};
