import React from 'react';
import { Cpu, MapPin, ShieldAlert, Route, Bell, Database } from 'lucide-react';

export const SystemFlowDiagram: React.FC = () => {
  const steps = [
    { title: 'Data Ingestion', desc: 'GPS, Weather, IoT Sensors, Field Reports', icon: Database },
    { title: 'GIS & Accessibility', desc: 'PostGIS Spatial Query Engine', icon: MapPin },
    { title: 'AI Risk Engine', desc: 'Rule & ML Model Evaluation', icon: ShieldAlert },
    { title: 'Disruption Detect', desc: 'Hazard & Blockage Verification', icon: Cpu },
    { title: 'Route Optimization', desc: 'GraphHopper Alternative Corridor', icon: Route },
    { title: 'Alert Generation', desc: 'WebSocket, WhatsApp & SMS Dispatch', icon: Bell },
  ];

  return (
    <div className="bg-white border border-slate-200 rounded-xl p-4 shadow-sm space-y-3">
      <div className="flex items-center justify-between border-b border-slate-100 pb-2.5">
        <h2 className="text-xs font-bold text-slate-900 uppercase tracking-wider flex items-center space-x-2">
          <Cpu className="w-4 h-4 text-emerald-600" />
          <span>PLATFORM INTELLIGENCE DATA FLOW PIPELINE</span>
        </h2>
        <span className="text-[10px] text-slate-500 font-mono">Real-time Stream & Fan-out</span>
      </div>

      <div className="grid grid-cols-2 sm:grid-cols-3 lg:grid-cols-6 gap-2 pt-1">
        {steps.map((s, idx) => {
          const Icon = s.icon;
          return (
            <div
              key={s.title}
              className="bg-slate-50 p-2.5 rounded-lg border border-slate-200 flex flex-col justify-between relative text-xs"
            >
              <div>
                <div className="flex items-center justify-between mb-1.5">
                  <span className="text-[9px] font-mono bg-white text-emerald-700 font-bold px-1 rounded border border-slate-200">
                    0{idx + 1}
                  </span>
                  <Icon className="w-3.5 h-3.5 text-teal-600" />
                </div>
                <h4 className="font-bold text-slate-900 text-[11px]">{s.title}</h4>
                <p className="text-[10px] text-slate-500 mt-0.5 leading-tight">{s.desc}</p>
              </div>
              {idx < steps.length - 1 && (
                <div className="hidden lg:block absolute -right-2 top-1/2 -translate-y-1/2 z-10 text-slate-400">
                  ➔
                </div>
              )}
            </div>
          );
        })}
      </div>
    </div>
  );
};
