import React from 'react';
import { useAuth } from '../../hooks/useAuth';
import { Truck, Bell, Radio, MapPin, LogOut } from 'lucide-react';
import { useNavigate } from 'react-router-dom';

interface CommandHeaderProps {
  activeStep: number;
  onResetSimulation: () => void;
}

export const CommandHeader: React.FC<CommandHeaderProps> = ({ activeStep, onResetSimulation }) => {
  const { user, logout } = useAuth();
  const navigate = useNavigate();

  const handleLogout = async () => {
    await logout();
    navigate('/login');
  };

  return (
    <header className="bg-white border-b border-slate-200 text-slate-800 sticky top-0 z-50 shadow-sm">
      <div className="max-w-[1920px] mx-auto px-4 h-16 flex items-center justify-between">
        {/* Brand & Platform Identity */}
        <div className="flex items-center space-x-3">
          <div className="w-10 h-10 rounded-xl bg-emerald-600 flex items-center justify-center text-white font-bold shadow-md shadow-emerald-600/20 border border-emerald-500">
            <Truck className="w-5 h-5 text-white" />
          </div>
          <div>
            <div className="flex items-center space-x-2">
              <span className="font-extrabold tracking-wider text-sm sm:text-base text-slate-900">
                NER SMART LOGISTICS
              </span>
              <span className="bg-emerald-50 text-emerald-700 text-[10px] font-bold px-2 py-0.5 rounded border border-emerald-200 tracking-wide">
                ACCESSIBILITY INTELLIGENCE
              </span>
            </div>
            <span className="text-[11px] text-slate-500 block tracking-tight">
              Command Center Surface • North Eastern Region (8 States)
            </span>
          </div>
        </div>

        {/* System & Telemetry Live Telemetry Bar */}
        <div className="hidden lg:flex items-center space-x-6 text-xs bg-slate-50 px-4 py-1.5 rounded-lg border border-slate-200">
          <div className="flex items-center space-x-1.5">
            <span className="w-2 h-2 rounded-full bg-emerald-500 animate-pulse" />
            <span className="text-slate-500">SYSTEM:</span>
            <span className="font-bold text-emerald-700 uppercase tracking-wide">OPERATIONAL</span>
          </div>

          <div className="h-3 w-px bg-slate-200" />

          <div className="flex items-center space-x-1.5">
            <Radio className="w-3.5 h-3.5 text-teal-600" />
            <span className="text-slate-500">TELEMETRY:</span>
            <span className="font-semibold text-slate-700">GPS • Weather • Sensors</span>
          </div>

          <div className="h-3 w-px bg-slate-200" />

          <div className="flex items-center space-x-1.5">
            <MapPin className="w-3.5 h-3.5 text-amber-600" />
            <span className="text-slate-500">HIGH-RISK CORRIDOR:</span>
            <span className="font-semibold text-amber-700">Dima Hasao Pass</span>
          </div>
        </div>

        {/* Right Section - Notifications & User Info */}
        <div className="flex items-center space-x-3">
          {/* Notifications Trigger */}
          <button className="relative p-2 rounded-lg bg-slate-100 hover:bg-slate-200 text-slate-600 transition">
            <Bell className="w-4 h-4" />
            <span className="absolute top-1 right-1 w-2 h-2 bg-rose-500 rounded-full animate-ping" />
            <span className="absolute top-1 right-1 w-2 h-2 bg-rose-500 rounded-full" />
          </button>

          {/* User Profile Info */}
          {user && (
            <div className="flex items-center space-x-3 pl-2 border-l border-slate-200">
              <div className="text-right hidden sm:block">
                <span className="block text-xs font-bold text-slate-900">{user.fullName || 'Govind (Officer)'}</span>
                <span className="block text-[10px] text-emerald-700 font-mono font-semibold">{user.role || 'ADMIN'}</span>
              </div>
              <button
                onClick={() => navigate('/profile')}
                className="w-8 h-8 rounded-lg bg-emerald-600 flex items-center justify-center text-white font-bold text-xs border border-emerald-500 shadow-sm hover:scale-105 transition"
                title="View Profile"
              >
                {user.fullName ? user.fullName.charAt(0).toUpperCase() : 'A'}
              </button>
              <button
                onClick={handleLogout}
                className="p-2 rounded-lg bg-rose-50 hover:bg-rose-100 border border-rose-200 text-rose-700 transition"
                title="Logout"
              >
                <LogOut className="w-4 h-4" />
              </button>
            </div>
          )}
        </div>
      </div>
    </header>
  );
};
