import React, { useState } from 'react';
import { Radio, Cpu, X } from 'lucide-react';

export const LiveSensorsPanel: React.FC = () => {
  const [showModal, setShowModal] = useState(false);

  const mockSensors = [
    { id: 'RAIN-102', type: 'Precipitation Radar', reading: '145 mm / 24h', location: 'Dima Hasao Pass', status: 'ALERT' },
    { id: 'SLOPE-405', type: 'Tilt Inclinometer', reading: '4.2° Soil Displacement', location: 'Haflong Hill Cut', status: 'WARNING' },
    { id: 'WATER-88', type: 'River Depth Gauge', reading: '0.8m Submerged Bed', location: 'Bararak Bridge', status: 'CRITICAL' },
    { id: 'GPS-NER07', type: 'Vehicle Telemetry', reading: '54 km/h • Lat 25.40', location: 'NH-27 Corridor', status: 'NORMAL' },
  ];

  return (
    <>
      <div className="bg-white border border-slate-200 rounded-xl p-4 shadow-sm space-y-3">
        <div className="flex items-center justify-between border-b border-slate-100 pb-2.5">
          <div className="flex items-center space-x-2">
            <Radio className="w-4 h-4 text-emerald-600" />
            <h2 className="text-xs font-bold text-slate-900 uppercase tracking-wider">
              LIVE SENSOR & TELEMETRY INGESTION
            </h2>
          </div>
          <span className="text-[10px] text-emerald-700 font-mono font-bold flex items-center space-x-1">
            <span className="w-1.5 h-1.5 rounded-full bg-emerald-500 animate-pulse" />
            <span>ACTIVE</span>
          </span>
        </div>

        <div className="grid grid-cols-2 sm:grid-cols-4 gap-2 text-xs">
          <div className="bg-slate-50 p-2.5 rounded-lg border border-slate-200 text-center">
            <span className="text-slate-500 text-[10px] block">🛰 GPS Telemetry</span>
            <strong className="text-emerald-700 font-bold">ONLINE</strong>
          </div>
          <div className="bg-slate-50 p-2.5 rounded-lg border border-slate-200 text-center">
            <span className="text-slate-500 text-[10px] block">🌦 Weather API</span>
            <strong className="text-emerald-700 font-bold">CONNECTED</strong>
          </div>
          <button
            onClick={() => setShowModal(true)}
            className="bg-slate-50 hover:bg-slate-100 p-2.5 rounded-lg border border-slate-200 text-center cursor-pointer transition"
          >
            <span className="text-slate-500 text-[10px] block">📡 IoT Sensors</span>
            <strong className="text-teal-700 font-bold underline">CONNECTED (4)</strong>
          </button>
          <div className="bg-slate-50 p-2.5 rounded-lg border border-slate-200 text-center">
            <span className="text-slate-500 text-[10px] block">👷 Field Reports</span>
            <strong className="text-amber-700 font-bold">3 NEW STREAM</strong>
          </div>
        </div>
      </div>

      {/* IoT Sensors Modal */}
      {showModal && (
        <div className="fixed inset-0 bg-slate-900/40 backdrop-blur-sm z-50 flex items-center justify-center p-4">
          <div className="bg-white border border-slate-200 rounded-xl max-w-lg w-full p-5 shadow-2xl space-y-4">
            <div className="flex items-center justify-between border-b border-slate-100 pb-3">
              <div className="flex items-center space-x-2">
                <Cpu className="w-5 h-5 text-teal-600" />
                <h3 className="text-sm font-bold text-slate-900 uppercase">
                  IoT & Environmental Sensor Stream
                </h3>
              </div>
              <button
                onClick={() => setShowModal(false)}
                className="text-slate-400 hover:text-slate-600"
              >
                <X className="w-5 h-5" />
              </button>
            </div>

            <div className="space-y-2 text-xs">
              {mockSensors.map((s) => (
                <div key={s.id} className="bg-slate-50 p-3 rounded-lg border border-slate-200 flex justify-between items-center">
                  <div>
                    <div className="font-bold text-slate-900">{s.id} — {s.type}</div>
                    <div className="text-[11px] text-slate-500 mt-0.5">{s.location}</div>
                  </div>
                  <div className="text-right">
                    <div className="font-mono text-teal-700 font-bold">{s.reading}</div>
                    <span className={`text-[10px] font-bold px-1.5 py-0.5 rounded ${
                      s.status === 'CRITICAL' || s.status === 'ALERT' ? 'bg-rose-50 text-rose-700 border border-rose-200' : 'bg-slate-200 text-slate-700'
                    }`}>
                      {s.status}
                    </span>
                  </div>
                </div>
              ))}
            </div>

            <div className="pt-2 border-t border-slate-100 flex justify-end">
              <button
                onClick={() => setShowModal(false)}
                className="px-4 py-2 bg-slate-100 hover:bg-slate-200 text-slate-700 text-xs font-semibold rounded-lg"
              >
                Close Sensor View
              </button>
            </div>
          </div>
        </div>
      )}
    </>
  );
};
