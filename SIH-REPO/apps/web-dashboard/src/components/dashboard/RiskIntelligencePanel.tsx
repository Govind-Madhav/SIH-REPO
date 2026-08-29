import React from 'react';
import { Cpu } from 'lucide-react';

interface RiskIntelligencePanelProps {
  overallRisk: 'LOW' | 'MEDIUM' | 'HIGH' | 'CRITICAL';
  weatherImpactPct: number;
  roadConditionPct: number;
  historicalRiskPct: number;
  fieldReportsPct: number;
}

export const RiskIntelligencePanel: React.FC<RiskIntelligencePanelProps> = ({
  overallRisk,
  weatherImpactPct,
  roadConditionPct,
  historicalRiskPct,
  fieldReportsPct,
}) => {
  let riskColor = 'text-emerald-700 bg-emerald-50 border-emerald-200';
  if (overallRisk === 'MEDIUM') riskColor = 'text-yellow-800 bg-yellow-50 border-yellow-200';
  if (overallRisk === 'HIGH') riskColor = 'text-amber-800 bg-amber-50 border-amber-200';
  if (overallRisk === 'CRITICAL') riskColor = 'text-rose-700 bg-rose-50 border-rose-200 animate-pulse';

  return (
    <div className="bg-white border border-slate-200 rounded-xl p-4 shadow-sm space-y-3">
      <div className="flex items-center justify-between border-b border-slate-100 pb-2.5">
        <div className="flex items-center space-x-2">
          <Cpu className="w-4 h-4 text-teal-600" />
          <h2 className="text-xs font-bold text-slate-900 uppercase tracking-wider">
            AI / RISK INTELLIGENCE EVALUATOR
          </h2>
        </div>
        <span className={`text-[10px] font-extrabold px-2 py-0.5 rounded border ${riskColor}`}>
          OVERALL RISK: {overallRisk}
        </span>
      </div>

      <p className="text-[11px] text-slate-500">
        Multi-source accessibility synthesis combining Satellite Weather, PostGIS Spatial Layers, Historical Slope Failure Data, and Field Officer Telemetry.
      </p>

      {/* Metric Progress Bars */}
      <div className="space-y-2.5 text-xs">
        <div>
          <div className="flex justify-between mb-1 text-[11px]">
            <span className="text-slate-700 font-medium">Weather & Precipitation Impact</span>
            <span className="font-mono text-amber-700 font-bold">{weatherImpactPct}%</span>
          </div>
          <div className="w-full bg-slate-100 h-2 rounded-full overflow-hidden border border-slate-200">
            <div
              style={{ width: `${weatherImpactPct}%` }}
              className="bg-amber-500 h-full rounded-full transition-all duration-500"
            />
          </div>
        </div>

        <div>
          <div className="flex justify-between mb-1 text-[11px]">
            <span className="text-slate-700 font-medium">Roadbed & Slope Vulnerability</span>
            <span className="font-mono text-rose-700 font-bold">{roadConditionPct}%</span>
          </div>
          <div className="w-full bg-slate-100 h-2 rounded-full overflow-hidden border border-slate-200">
            <div
              style={{ width: `${roadConditionPct}%` }}
              className="bg-rose-500 h-full rounded-full transition-all duration-500"
            />
          </div>
        </div>

        <div>
          <div className="flex justify-between mb-1 text-[11px]">
            <span className="text-slate-700 font-medium">Historical Disruption Weight</span>
            <span className="font-mono text-teal-700 font-bold">{historicalRiskPct}%</span>
          </div>
          <div className="w-full bg-slate-100 h-2 rounded-full overflow-hidden border border-slate-200">
            <div
              style={{ width: `${historicalRiskPct}%` }}
              className="bg-teal-500 h-full rounded-full transition-all duration-500"
            />
          </div>
        </div>

        <div>
          <div className="flex justify-between mb-1 text-[11px]">
            <span className="text-slate-700 font-medium">Field Officer Ground Reports</span>
            <span className="font-mono text-purple-700 font-bold">{fieldReportsPct}%</span>
          </div>
          <div className="w-full bg-slate-100 h-2 rounded-full overflow-hidden border border-slate-200">
            <div
              style={{ width: `${fieldReportsPct}%` }}
              className="bg-purple-500 h-full rounded-full transition-all duration-500"
            />
          </div>
        </div>
      </div>
    </div>
  );
};
