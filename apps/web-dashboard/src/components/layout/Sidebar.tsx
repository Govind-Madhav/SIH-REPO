import React from 'react';
import {
  LayoutDashboard,
  MapPin,
  Truck,
  Package,
  AlertTriangle,
  ShieldAlert,
  Camera,
  Bell,
  Settings,
} from 'lucide-react';

interface SidebarProps {
  activeTab: string;
  onTabChange: (tab: string) => void;
}

export const Sidebar: React.FC<SidebarProps> = ({ activeTab, onTabChange }) => {
  const navItems = [
    { id: 'dashboard', label: 'Dashboard', icon: LayoutDashboard },
    { id: 'accessibility', label: 'Accessibility', icon: MapPin },
    { id: 'vehicles', label: 'Vehicles', icon: Truck },
    { id: 'shipments', label: 'Shipments', icon: Package },
    { id: 'incidents', label: 'Incidents', icon: AlertTriangle },
    { id: 'risk', label: 'Risk Intelligence', icon: ShieldAlert },
    { id: 'field', label: 'Field Reports', icon: Camera },
    { id: 'alerts', label: 'Alerts Stream', icon: Bell },
    { id: 'settings', label: 'Settings', icon: Settings },
  ];

  return (
    <aside className="w-56 bg-white border-r border-slate-200 flex flex-col justify-between py-4 shrink-0 shadow-sm">
      <div className="space-y-1 px-3">
        <div className="px-3 pb-2 text-[10px] font-bold text-slate-400 uppercase tracking-wider">
          Command Modules
        </div>
        {navItems.map((item) => {
          const Icon = item.icon;
          const isActive = activeTab === item.id;
          return (
            <button
              key={item.id}
              onClick={() => onTabChange(item.id)}
              className={`w-full flex items-center space-x-3 px-3 py-2.5 rounded-lg text-xs font-semibold transition ${
                isActive
                  ? 'bg-emerald-600 text-white shadow-sm shadow-emerald-600/30'
                  : 'text-slate-600 hover:text-slate-900 hover:bg-slate-100'
              }`}
            >
              <Icon className={`w-4 h-4 ${isActive ? 'text-white' : 'text-slate-500'}`} />
              <span>{item.label}</span>
            </button>
          );
        })}
      </div>

      {/* Footer Info */}
      <div className="px-4 pt-4 border-t border-slate-200 text-[11px] text-slate-500 space-y-1">
        <div className="flex items-center space-x-1.5 text-slate-600 font-mono">
          <span className="w-2 h-2 rounded-full bg-emerald-500" />
          <span>NER GIS Engine v1.0</span>
        </div>
        <p className="text-[10px] text-slate-400">Logistics & Regional Intelligence</p>
      </div>
    </aside>
  );
};
