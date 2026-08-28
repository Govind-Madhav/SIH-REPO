import React from 'react';
import { Link, useNavigate } from 'react-router-dom';
import { useAuth } from '../../hooks/useAuth';
import {
  ShieldAlert,
  User as UserIcon,
  LogOut,
  MapPin,
  Building2,
  Truck,
  Activity,
  UserCheck,
} from 'lucide-react';
import { UserRole } from '../../types/auth';

const getRoleBadge = (role?: UserRole) => {
  switch (role) {
    case 'SUPER_ADMIN':
      return { label: 'SUPER ADMIN', color: 'bg-purple-950/80 text-purple-300 border-purple-800' };
    case 'ADMIN':
      return { label: 'ADMINISTRATOR', color: 'bg-blue-950/80 text-blue-300 border-blue-800' };
    case 'DISTRICT_AUTHORITY':
      return { label: 'DISTRICT AUTHORITY', color: 'bg-emerald-950/80 text-emerald-300 border-emerald-800' };
    case 'LOGISTICS_OPERATOR':
      return { label: 'LOGISTICS OPERATOR', color: 'bg-amber-950/80 text-amber-300 border-amber-800' };
    case 'FIELD_OFFICER':
      return { label: 'FIELD OFFICER', color: 'bg-teal-950/80 text-teal-300 border-teal-800' };
    case 'DRIVER':
      return { label: 'DRIVER / FLEET', color: 'bg-slate-800 text-slate-300 border-slate-700' };
    default:
      return { label: 'USER', color: 'bg-slate-800 text-slate-400 border-slate-700' };
  }
};

const getRoleDashboardPath = (role?: UserRole) => {
  switch (role) {
    case 'SUPER_ADMIN':
    case 'ADMIN':
      return '/admin';
    case 'DISTRICT_AUTHORITY':
      return '/district-dashboard';
    case 'LOGISTICS_OPERATOR':
      return '/logistics';
    case 'FIELD_OFFICER':
      return '/field';
    case 'DRIVER':
      return '/driver';
    default:
      return '/profile';
  }
};

export const Navbar: React.FC = () => {
  const { user, logout } = useAuth();
  const navigate = useNavigate();

  const handleLogout = async () => {
    await logout();
    navigate('/login');
  };

  const badge = getRoleBadge(user?.role);
  const dashboardPath = getRoleDashboardPath(user?.role);

  return (
    <header className="bg-slate-900 border-b border-slate-800 text-slate-100 sticky top-0 z-50 shadow-md">
      <div className="max-w-7xl mx-auto px-4 sm:px-6 lg:px-8 h-16 flex items-center justify-between">
        {/* Brand */}
        <Link to={dashboardPath} className="flex items-center space-x-3 group">
          <div className="w-10 h-10 rounded-lg bg-gradient-to-br from-emerald-600 to-teal-800 flex items-center justify-center text-white font-bold shadow-inner">
            <Truck className="w-5 h-5 text-emerald-200" />
          </div>
          <div>
            <div className="flex items-center space-x-2">
              <span className="font-bold tracking-wider text-base text-slate-100 group-hover:text-emerald-400 transition">
                NER SMART LOGISTICS
              </span>
              <span className="bg-emerald-950 text-emerald-400 text-[10px] font-bold px-1.5 py-0.5 rounded border border-emerald-800">
                SIH 2026
              </span>
            </div>
            <span className="text-[11px] text-slate-400 block tracking-tight">
              Accessibility & Regional Transport Intelligence
            </span>
          </div>
        </Link>

        {/* Right Section */}
        {user ? (
          <div className="flex items-center space-x-4">
            {/* District Badge */}
            {user.district && (
              <div className="hidden md:flex items-center space-x-1.5 bg-slate-950 px-2.5 py-1 rounded-md border border-slate-800 text-slate-300 text-xs font-medium">
                <MapPin className="w-3.5 h-3.5 text-emerald-400" />
                <span>{user.district}</span>
              </div>
            )}

            {/* Role Badge */}
            <span
              className={`text-[11px] font-bold tracking-wide px-2.5 py-1 rounded border ${badge.color}`}
            >
              {badge.label}
            </span>

            {/* Dashboard Link */}
            <Link
              to={dashboardPath}
              className="text-xs font-medium px-3 py-1.5 rounded-md bg-slate-800 hover:bg-slate-700 text-slate-200 transition flex items-center space-x-1.5"
            >
              <Activity className="w-3.5 h-3.5 text-teal-400" />
              <span className="hidden sm:inline">Dashboard</span>
            </Link>

            {/* Profile Link */}
            <Link
              to="/profile"
              className="text-xs font-medium px-3 py-1.5 rounded-md bg-slate-800 hover:bg-slate-700 text-slate-200 transition flex items-center space-x-1.5"
            >
              <UserIcon className="w-3.5 h-3.5 text-emerald-400" />
              <span className="hidden sm:inline">{user.fullName || 'Profile'}</span>
            </Link>

            {/* Logout Button */}
            <button
              onClick={handleLogout}
              className="text-xs font-medium px-3 py-1.5 rounded-md bg-rose-950/60 hover:bg-rose-900/80 border border-rose-800/60 text-rose-200 transition flex items-center space-x-1.5"
              title="Logout session"
            >
              <LogOut className="w-3.5 h-3.5 text-rose-400" />
              <span className="hidden sm:inline">Logout</span>
            </button>
          </div>
        ) : (
          <div className="flex items-center space-x-3">
            <Link
              to="/login"
              className="text-xs font-medium px-3.5 py-1.5 rounded-md bg-emerald-600 hover:bg-emerald-500 text-white transition shadow-sm"
            >
              Log In
            </Link>
          </div>
        )}
      </div>
    </header>
  );
};
