import React from 'react';
import { EssentialSupplySummary } from '../../types/shipment';
import { Package } from 'lucide-react';

interface EssentialSupplyPanelProps {
  supplies: EssentialSupplySummary[];
}

export const EssentialSupplyPanel: React.FC<EssentialSupplyPanelProps> = ({ supplies }) => {
  const getCategoryIcon = (cat: string) => {
    switch (cat) {
      case 'MEDICINE':
        return '💊';
      case 'FOOD':
        return '🍚';
      case 'AGRICULTURE':
        return '🌾';
      case 'CONSTRUCTION':
        return '🏗';
      default:
        return '📦';
    }
  };

  return (
    <div className="bg-white border border-slate-200 rounded-xl p-4 shadow-sm flex flex-col h-full">
      <div className="flex items-center justify-between border-b border-slate-100 pb-3 mb-3">
        <div className="flex items-center space-x-2">
          <Package className="w-4 h-4 text-teal-600" />
          <h2 className="text-xs font-bold text-slate-900 uppercase tracking-wider">
            ESSENTIAL SUPPLY TRACKER (CRITICAL CARGO)
          </h2>
        </div>
        <span className="text-[10px] text-slate-500 font-medium">Zero Supply Failure</span>
      </div>

      <div className="grid grid-cols-1 sm:grid-cols-2 gap-2.5 flex-1">
        {supplies.map((item) => (
          <div key={item.category} className="bg-slate-50 p-3 rounded-lg border border-slate-200 space-y-1.5">
            <div className="flex items-center justify-between">
              <div className="flex items-center space-x-1.5">
                <span className="text-base">{getCategoryIcon(item.category)}</span>
                <span className="text-xs font-bold text-slate-900">{item.name}</span>
              </div>
              <span className="text-[10px] font-mono bg-white text-teal-700 font-bold border border-slate-200 px-1.5 py-0.5 rounded">
                {item.inTransit} In Transit
              </span>
            </div>

            <div className="grid grid-cols-3 gap-1 text-[10px] pt-1 border-t border-slate-200">
              <div className="bg-white p-1 rounded border border-slate-100 text-center">
                <span className="text-slate-500 block">Delayed</span>
                <strong className={item.delayed > 0 ? 'text-amber-600 font-bold' : 'text-slate-700'}>
                  {item.delayed}
                </strong>
              </div>
              <div className="bg-white p-1 rounded border border-slate-100 text-center">
                <span className="text-slate-500 block">At Risk</span>
                <strong className={item.atRisk > 0 ? 'text-rose-600 font-bold' : 'text-slate-700'}>
                  {item.atRisk}
                </strong>
              </div>
              <div className="bg-white p-1 rounded border border-slate-100 text-center">
                <span className="text-slate-500 block">Delivered</span>
                <strong className="text-emerald-600 font-bold">{item.delivered}</strong>
              </div>
            </div>

            <div className="text-[10px] text-slate-500 truncate pt-0.5">
              Priority Target: <span className="text-slate-800 font-semibold">{item.criticalItem}</span>
            </div>
          </div>
        ))}
      </div>
    </div>
  );
};
