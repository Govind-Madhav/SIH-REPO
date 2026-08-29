import React, { useState } from 'react';
import { MapContainer, TileLayer, Marker, Popup, Polyline, Circle, Tooltip } from 'react-leaflet';
import L from 'leaflet';
import {
  NER_CENTER,
  DEMO_PRIMARY_ROUTE_COORDS,
  DEMO_ALTERNATIVE_ROUTE_COORDS,
} from '../../data/mockData';
import { Vehicle } from '../../types/vehicle';
import { Incident } from '../../types/incident';
import { DistrictStatus } from '../../types/district';
import { SimulationState } from '../../types/simulation';
import { LayerControl, MapLayersState } from './LayerControl';
import { MapLegend } from './MapLegend';

interface NERMapProps {
  vehicles: Vehicle[];
  incidents: Incident[];
  districts: DistrictStatus[];
  simulationState: SimulationState;
  onSelectVehicle?: (vehicle: Vehicle) => void;
  onSelectIncident?: (incident: Incident) => void;
}

// Custom DivIcons for crisp styling without asset loading issues
const createVehicleIcon = (status: string, riskLevel: string, isTargeted: boolean = false) => {
  let bgColor = '#10b981'; // Green (ON_TRACK)
  if (status === 'DELAYED') bgColor = '#f59e0b'; // Amber
  if (status === 'AT_RISK' || riskLevel === 'CRITICAL') bgColor = '#ef4444'; // Red
  if (status === 'OFFLINE') bgColor = '#64748b'; // Gray

  const ringClass = isTargeted ? 'animate-ping opacity-75' : '';

  return L.divIcon({
    className: 'custom-vehicle-marker',
    html: `
      <div style="position: relative; display: flex; align-items: center; justify-content: center; width: 34px; height: 34px;">
        ${isTargeted ? `<div style="position: absolute; inset: 0; border-radius: 50%; background-color: ${bgColor}; opacity: 0.5;" class="${ringClass}"></div>` : ''}
        <div style="background-color: ${bgColor}; border: 2px solid #ffffff; border-radius: 50%; width: 28px; height: 28px; display: flex; align-items: center; justify-content: center; color: white; box-shadow: 0 4px 12px rgba(0,0,0,0.25); font-weight: bold; font-size: 12px;">
          🚚
        </div>
      </div>
    `,
    iconSize: [34, 34],
    iconAnchor: [17, 17],
  });
};

const createIncidentIcon = (type: string, severity: string) => {
  let bgColor = '#ef4444'; // Landslide / Critical
  let emoji = '🚨';
  if (type === 'FLOOD') {
    bgColor = '#2563eb';
    emoji = '🌊';
  }
  if (type === 'ROAD_DAMAGE') {
    bgColor = '#d97706';
    emoji = '🚧';
  }
  if (type === 'BRIDGE_ISSUE') {
    bgColor = '#7c3aed';
    emoji = '🌉';
  }

  return L.divIcon({
    className: 'custom-incident-marker',
    html: `
      <div style="background-color: ${bgColor}; border: 2px solid #ffffff; border-radius: 8px; width: 30px; height: 30px; display: flex; align-items: center; justify-content: center; color: white; box-shadow: 0 4px 12px rgba(0,0,0,0.25); transform: rotate(-5deg);">
        <span style="font-size: 14px;">${emoji}</span>
      </div>
    `,
    iconSize: [30, 30],
    iconAnchor: [15, 15],
  });
};

export const NERMap: React.FC<NERMapProps> = ({
  vehicles,
  incidents,
  districts,
  simulationState,
  onSelectVehicle,
  onSelectIncident,
}) => {
  const [layers, setLayers] = useState<MapLayersState>({
    showVehicles: true,
    showIncidents: true,
    showRiskZones: true,
    showRoutes: true,
    showDistrictBoundaries: true,
  });

  const toggleLayer = (key: keyof MapLayersState) => {
    setLayers((prev) => ({ ...prev, [key]: !prev[key] }));
  };

  return (
    <div className="relative w-full h-full rounded-xl overflow-hidden border border-slate-200 shadow-md bg-slate-100">
      {/* Map Container */}
      <MapContainer
        center={NER_CENTER}
        zoom={7}
        scrollWheelZoom={true}
        className="w-full h-full z-0"
        style={{ background: '#f8fafc' }}
      >
        {/* CARTO Voyager Light Map Tiles */}
        <TileLayer
          attribution='&copy; <a href="https://www.openstreetmap.org/copyright">OpenStreetMap</a> &copy; <a href="https://carto.com/attributions">CARTO</a>'
          url="https://{s}.basemaps.cartocdn.com/rastertiles/voyager/{z}/{x}/{y}{r}.png"
        />

        {/* Primary Highway Route Polyline */}
        {layers.showRoutes && (
          <Polyline
            positions={DEMO_PRIMARY_ROUTE_COORDS}
            pathOptions={{
              color: simulationState.primaryRouteBlocked ? '#dc2626' : '#059669',
              weight: simulationState.primaryRouteBlocked ? 5 : 4,
              dashArray: simulationState.primaryRouteBlocked ? '8, 8' : undefined,
              opacity: 0.85,
            }}
          >
            <Tooltip permanent={false} direction="top">
              <span>
                {simulationState.primaryRouteBlocked
                  ? 'NH-27 Primary Corridor [BLOCKED - LANDSLIDE]'
                  : 'NH-27 Primary Freight Corridor [ACTIVE]'}
              </span>
            </Tooltip>
          </Polyline>
        )}

        {/* Alternative Rerouted Corridor Polyline */}
        {layers.showRoutes && (simulationState.alternativeRouteActive || simulationState.step >= 4) && (
          <Polyline
            positions={DEMO_ALTERNATIVE_ROUTE_COORDS}
            pathOptions={{
              color: '#0891b2', // Cyan Reroute
              weight: 5,
              dashArray: '10, 6',
              opacity: 0.9,
            }}
          >
            <Tooltip permanent={true} direction="center">
              <span className="font-bold text-cyan-700 text-xs">
                ✨ AI RE-ROUTED ALTERNATIVE CORRIDOR (132 km - LOW RISK)
              </span>
            </Tooltip>
          </Polyline>
        )}

        {/* Risk Zones Overlay */}
        {layers.showRiskZones && (
          <>
            {/* Dima Hasao Risk Circle */}
            <Circle
              center={[25.2200, 92.9500]}
              radius={simulationState.step >= 2 ? 35000 : 20000}
              pathOptions={{
                color: simulationState.step >= 3 ? '#dc2626' : simulationState.step === 2 ? '#d97706' : '#2563eb',
                fillColor: simulationState.step >= 3 ? '#dc2626' : simulationState.step === 2 ? '#d97706' : '#2563eb',
                fillOpacity: simulationState.step >= 3 ? 0.2 : 0.12,
                weight: 2,
              }}
            />
            {/* Cachar Flood Risk Zone */}
            <Circle
              center={[24.8333, 92.7789]}
              radius={22000}
              pathOptions={{
                color: '#2563eb',
                fillColor: '#2563eb',
                fillOpacity: 0.12,
                weight: 1,
              }}
            />
          </>
        )}

        {/* District Accessibility Markers & Circles */}
        {layers.showDistrictBoundaries &&
          districts.map((d) => (
            <Circle
              key={d.id}
              center={d.center}
              radius={18000}
              pathOptions={{
                color:
                  d.status === 'ACCESSIBLE'
                    ? '#059669'
                    : d.status === 'PARTIAL_ACCESS'
                    ? '#d97706'
                    : '#dc2626',
                fillColor:
                  d.status === 'ACCESSIBLE'
                    ? '#059669'
                    : d.status === 'PARTIAL_ACCESS'
                    ? '#d97706'
                    : '#dc2626',
                fillOpacity: 0.08,
                weight: 1,
              }}
            />
          ))}

        {/* Incident Markers */}
        {layers.showIncidents &&
          incidents.map((inc) => (
            <Marker
              key={inc.id}
              position={[inc.location.lat, inc.location.lng]}
              icon={createIncidentIcon(inc.type, inc.severity)}
              eventHandlers={{
                click: () => onSelectIncident && onSelectIncident(inc),
              }}
            >
              <Popup className="custom-leaflet-popup">
                <div className="p-2 space-y-1.5 text-xs text-slate-900 font-sans max-w-xs">
                  <div className="flex items-center justify-between border-b pb-1 border-slate-200">
                    <span className="font-bold text-rose-700 uppercase flex items-center space-x-1">
                      <span>🚨</span>
                      <span>{inc.type}</span>
                    </span>
                    <span className="bg-rose-100 text-rose-800 text-[10px] font-bold px-1.5 py-0.5 rounded">
                      {inc.severity}
                    </span>
                  </div>

                  <div>
                    <h4 className="font-bold text-sm text-slate-900">{inc.title}</h4>
                    <p className="text-[11px] text-slate-600 mt-0.5">{inc.description}</p>
                  </div>

                  <div className="bg-slate-50 p-2 rounded border border-slate-200 text-[11px] space-y-1">
                    <div><strong>Affected Route:</strong> {inc.affectedRoute}</div>
                    <div><strong>District:</strong> {inc.location.district}</div>
                    <div><strong>Reported:</strong> {inc.reportedTime}</div>
                    <div><strong>Source:</strong> {inc.source}</div>
                  </div>
                </div>
              </Popup>
            </Marker>
          ))}

        {/* Vehicle Markers */}
        {layers.showVehicles &&
          vehicles.map((v) => {
            const isTarget = v.code === 'NER-07';
            return (
              <Marker
                key={v.id}
                position={[v.location.lat, v.location.lng]}
                icon={createVehicleIcon(v.status, v.riskLevel, isTarget)}
                eventHandlers={{
                  click: () => onSelectVehicle && onSelectVehicle(v),
                }}
              >
                <Popup className="custom-leaflet-popup">
                  <div className="p-2 space-y-2 text-xs text-slate-900 font-sans max-w-xs">
                    <div className="flex items-center justify-between border-b pb-1 border-slate-200">
                      <div className="flex items-center space-x-1">
                        <span className="font-extrabold text-slate-900 text-sm">{v.code}</span>
                        <span className="text-[10px] bg-slate-100 text-slate-700 px-1.5 py-0.5 rounded border border-slate-200">
                          {v.connectivity}
                        </span>
                      </div>
                      <span
                        className={`text-[10px] font-bold px-2 py-0.5 rounded ${
                          v.status === 'ON_TRACK'
                            ? 'bg-emerald-100 text-emerald-800'
                            : v.status === 'DELAYED'
                            ? 'bg-amber-100 text-amber-800'
                            : 'bg-rose-100 text-rose-800'
                        }`}
                      >
                        {v.status}
                      </span>
                    </div>

                    <div>
                      <div className="font-semibold text-slate-800">{v.cargo}</div>
                      <div className="text-[11px] text-slate-500">
                        {v.origin} → {v.destination}
                      </div>
                    </div>

                    <div className="bg-slate-50 p-2 rounded border border-slate-200 text-[11px] space-y-1">
                      <div className="flex justify-between">
                        <span>Speed:</span>
                        <strong className="font-mono">{v.location.speedKmH} km/h</strong>
                      </div>
                      <div className="flex justify-between">
                        <span>ETA:</span>
                        <strong className="text-emerald-700">{v.eta}</strong>
                      </div>
                      <div className="flex justify-between">
                        <span>Risk Level:</span>
                        <strong
                          className={
                            v.riskLevel === 'CRITICAL' || v.riskLevel === 'HIGH'
                              ? 'text-rose-600 font-bold'
                              : 'text-slate-800'
                          }
                        >
                          {v.riskLevel}
                        </strong>
                      </div>
                      <div className="text-[10px] text-slate-500 pt-1 border-t border-slate-200">
                        Location: {v.location.address}
                      </div>
                    </div>

                    {isTarget && simulationState.alternativeRouteActive && (
                      <div className="bg-cyan-50 border border-cyan-200 p-1.5 rounded text-[11px] text-cyan-900 font-semibold text-center">
                        ✅ Rerouted to Low-Risk Alternative Corridor
                      </div>
                    )}
                  </div>
                </Popup>
              </Marker>
            );
          })}
      </MapContainer>

      {/* Layer Control Overlay */}
      <div className="absolute top-3 right-3 z-10 w-48">
        <LayerControl layers={layers} onToggleLayer={toggleLayer} />
      </div>

      {/* Map Legend Overlay */}
      <div className="absolute bottom-3 left-3 z-10 w-56">
        <MapLegend />
      </div>
    </div>
  );
};
