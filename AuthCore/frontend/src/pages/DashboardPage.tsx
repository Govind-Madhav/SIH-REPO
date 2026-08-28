import React, { useEffect, useState } from 'react';
import { useAuth } from '../hooks/useAuth';
import { Navbar } from '../components/common/Navbar';
import { authApi } from '../api/authApi';
import { User, UserRole } from '../types/auth';
import {
  ShieldCheck,
  Building,
  Truck,
  MapPin,
  Camera,
  Navigation,
  AlertTriangle,
  Users,
  Activity,
  Radio,
  FileText,
  Clock,
  ExternalLink,
} from 'lucide-react';

export const DashboardPage: React.FC = () => {
  const { user } = useAuth();
  const [usersList, setUsersList] = useState<User[]>([]);
  const [loadingUsers, setLoadingUsers] = useState(false);

  useEffect(() => {
    if (user?.role === 'SUPER_ADMIN' || user?.role === 'ADMIN') {
      setLoadingUsers(true);
      authApi
        .getAllUsers()
        .then((data) => setUsersList(data.users))
        .catch((err) => console.error('Failed to load user directory:', err))
        .finally(() => setLoadingUsers(false));
    }
  }, [user?.role]);

  if (!user) return null;

  return (
    <div className="min-h-screen bg-slate-950 text-slate-100 flex flex-col">
      <Navbar />

      <main className="max-w-7xl mx-auto px-4 sm:px-6 lg:px-8 py-8 w-full flex-1">
        {/* Banner */}
        <div className="bg-gradient-to-r from-slate-900 via-slate-900 to-slate-950 border border-slate-800 rounded-xl p-6 mb-8 shadow-xl relative overflow-hidden">
          <div className="absolute right-0 top-0 w-64 h-full bg-emerald-500/5 blur-3xl rounded-full pointer-events-none" />
          <div className="flex flex-col md:flex-row md:items-center justify-between gap-4">
            <div>
              <div className="flex items-center space-x-2">
                <span className="text-xs font-bold px-2 py-0.5 rounded bg-emerald-950 text-emerald-400 border border-emerald-800 uppercase">
                  {user.role} CONSOLE
                </span>
                <span className="text-xs text-slate-400">| Session Status: Authenticated</span>
              </div>
              <h1 className="text-2xl font-extrabold text-slate-100 mt-2">
                Welcome back, {user.fullName || 'Operational Officer'}
              </h1>
              <p className="text-xs text-slate-400 mt-1 max-w-2xl">
                North Eastern Region Logistics & Accessibility Intelligence Monitoring Surface. Authorizations and RBAC privileges active for {user.district || 'all regional sectors'}.
              </p>
            </div>

            <div className="flex items-center space-x-3 bg-slate-950/80 p-3 rounded-lg border border-slate-800 text-xs">
              <Activity className="w-5 h-5 text-emerald-400 shrink-0" />
              <div>
                <span className="text-slate-400 block">District Jurisdiction</span>
                <span className="font-semibold text-slate-200">{user.district || 'Regional HQ (NER)'}</span>
              </div>
            </div>
          </div>
        </div>

        {/* Role Specific Views */}
        {user.role === 'SUPER_ADMIN' || user.role === 'ADMIN' ? (
          <AdminView user={user} usersList={usersList} loadingUsers={loadingUsers} />
        ) : user.role === 'DISTRICT_AUTHORITY' ? (
          <DistrictAuthorityView user={user} />
        ) : user.role === 'LOGISTICS_OPERATOR' ? (
          <LogisticsOperatorView user={user} />
        ) : user.role === 'FIELD_OFFICER' ? (
          <FieldOfficerView user={user} />
        ) : (
          <DriverView user={user} />
        )}
      </main>
    </div>
  );
};

/* Component for Super Admin & Admin */
const AdminView: React.FC<{ user: User; usersList: User[]; loadingUsers: boolean }> = ({
  user,
  usersList,
  loadingUsers,
}) => (
  <div className="space-y-6">
    <div className="grid grid-cols-1 md:grid-cols-4 gap-4">
      <StatCard title="Total Platform Users" value={usersList.length || '1'} icon={<Users className="w-5 h-5 text-purple-400" />} />
      <StatCard title="Operational Districts" value="8 States (NER)" icon={<MapPin className="w-5 h-5 text-emerald-400" />} />
      <StatCard title="Active RBAC Security" value="Strict Enforce" icon={<ShieldCheck className="w-5 h-5 text-blue-400" />} />
      <StatCard title="System Health" value="100% Operational" icon={<Activity className="w-5 h-5 text-teal-400" />} />
    </div>

    {/* User Directory Table */}
    <div className="bg-slate-900 border border-slate-800 rounded-xl p-6 shadow-xl">
      <div className="flex items-center justify-between mb-4 border-b border-slate-800 pb-3">
        <div className="flex items-center space-x-2">
          <Users className="w-5 h-5 text-purple-400" />
          <h2 className="text-sm font-bold text-slate-200 uppercase tracking-wider">
            User Directory & Authorization Registry
          </h2>
        </div>
        <span className="text-xs text-slate-400">Role-Based Access Enforcement</span>
      </div>

      {loadingUsers ? (
        <div className="py-8 text-center text-xs text-slate-400">Loading user registry...</div>
      ) : (
        <div className="overflow-x-auto">
          <table className="w-full text-left text-xs border-collapse">
            <thead>
              <tr className="border-b border-slate-800 text-slate-400 bg-slate-950/60">
                <th className="py-2.5 px-3">User ID</th>
                <th className="py-2.5 px-3">Full Name</th>
                <th className="py-2.5 px-3">Email / Identifier</th>
                <th className="py-2.5 px-3">Role</th>
                <th className="py-2.5 px-3">Organization</th>
                <th className="py-2.5 px-3">Assigned District</th>
              </tr>
            </thead>
            <tbody className="divide-y divide-slate-800/60 text-slate-200">
              {usersList.length > 0 ? (
                usersList.map((u) => (
                  <tr key={u.id} className="hover:bg-slate-800/40">
                    <td className="py-2.5 px-3 font-mono text-[11px] text-slate-400">{u.id}</td>
                    <td className="py-2.5 px-3 font-semibold">{u.fullName || '—'}</td>
                    <td className="py-2.5 px-3">{u.email || u.identifier}</td>
                    <td className="py-2.5 px-3">
                      <span className="px-2 py-0.5 text-[10px] font-bold rounded bg-slate-800 text-emerald-400 border border-slate-700">
                        {u.role}
                      </span>
                    </td>
                    <td className="py-2.5 px-3">{u.organization || 'NER Logistics'}</td>
                    <td className="py-2.5 px-3">{u.district || 'Regional'}</td>
                  </tr>
                ))
              ) : (
                <tr>
                  <td colSpan={6} className="py-4 text-center text-slate-500">
                    No users registered yet
                  </td>
                </tr>
              )}
            </tbody>
          </table>
        </div>
      )}
    </div>
  </div>
);

/* Component for District Authority */
const DistrictAuthorityView: React.FC<{ user: User }> = ({ user }) => (
  <div className="space-y-6">
    <div className="grid grid-cols-1 md:grid-cols-3 gap-4">
      <StatCard title="Jurisdiction Monitoring" value={user.district || 'Assam Sector'} icon={<MapPin className="w-5 h-5 text-emerald-400" />} />
      <StatCard title="Active Alerts" value="2 Route Hazards" icon={<AlertTriangle className="w-5 h-5 text-amber-400" />} />
      <StatCard title="Monitored Fleet" value="14 Vehicles" icon={<Truck className="w-5 h-5 text-teal-400" />} />
    </div>

    <div className="bg-slate-900 border border-slate-800 rounded-xl p-6 shadow-xl">
      <h2 className="text-sm font-bold text-slate-200 uppercase tracking-wider mb-4 pb-2 border-b border-slate-800 flex items-center space-x-2">
        <Building className="w-4 h-4 text-emerald-400" />
        <span>District Authority Jurisdiction Scope</span>
      </h2>
      <div className="grid grid-cols-1 md:grid-cols-2 gap-4 text-xs">
        <FeatureCard title="Monitor Connectivity" desc="View real-time road accessibility, weather blockages, and terrain alerts across assigned district." />
        <FeatureCard title="Incident Alerts & Acknowledgements" desc="Review field-reported landslips, flood advisories, and vehicle breakdown reports in jurisdiction." />
        <FeatureCard title="Shipment Jurisdiction View" desc="Track essential cargo vehicles traversing through district checkpoints." />
        <FeatureCard title="District Emergency Hotline" desc="Trigger district emergency protocols and inter-department coordination." />
      </div>
    </div>
  </div>
);

/* Component for Logistics Operator */
const LogisticsOperatorView: React.FC<{ user: User }> = ({ user }) => (
  <div className="space-y-6">
    <div className="grid grid-cols-1 md:grid-cols-3 gap-4">
      <StatCard title="Active Shipments" value="8 In-Transit" icon={<Truck className="w-5 h-5 text-amber-400" />} />
      <StatCard title="Assigned Vehicles" value="12 Freight Trucks" icon={<Navigation className="w-5 h-5 text-emerald-400" />} />
      <StatCard title="Route Disruption Alerts" value="1 Weather Block" icon={<AlertTriangle className="w-5 h-5 text-rose-400" />} />
    </div>

    <div className="bg-slate-900 border border-slate-800 rounded-xl p-6 shadow-xl">
      <h2 className="text-sm font-bold text-slate-200 uppercase tracking-wider mb-4 pb-2 border-b border-slate-800 flex items-center space-x-2">
        <Truck className="w-4 h-4 text-amber-400" />
        <span>Logistics Operator Dispatch & Delivery Capabilities</span>
      </h2>
      <div className="grid grid-cols-1 md:grid-cols-2 gap-4 text-xs">
        <FeatureCard title="Shipment Management" desc="Create, assign, and manage essential supply shipments across NER hill corridors." />
        <FeatureCard title="Vehicle Assignment" desc="Map drivers and GPS tracking units to designated freight routes." />
        <FeatureCard title="Delivery Monitoring" desc="Monitor ETA and real-time checkpoint updates for sensitive supplies." />
        <FeatureCard title="Route Disruption Rerouting" desc="Re-route shipments away from blocked highway stretches." />
      </div>
    </div>
  </div>
);

/* Component for Field Officer */
const FieldOfficerView: React.FC<{ user: User }> = ({ user }) => (
  <div className="space-y-6">
    <div className="grid grid-cols-1 md:grid-cols-3 gap-4">
      <StatCard title="Assigned Operational Area" value={user.district || 'Assam Sector'} icon={<MapPin className="w-5 h-5 text-teal-400" />} />
      <StatCard title="Submitted Reports" value="19 Geo-Reports" icon={<FileText className="w-5 h-5 text-emerald-400" />} />
      <StatCard title="Field Incident Stream" value="Active" icon={<Radio className="w-5 h-5 text-amber-400" />} />
    </div>

    <div className="bg-slate-900 border border-slate-800 rounded-xl p-6 shadow-xl">
      <h2 className="text-sm font-bold text-slate-200 uppercase tracking-wider mb-4 pb-2 border-b border-slate-800 flex items-center space-x-2">
        <Camera className="w-4 h-4 text-teal-400" />
        <span>Field Officer Reporting Scope</span>
      </h2>
      <div className="grid grid-cols-1 md:grid-cols-2 gap-4 text-xs">
        <FeatureCard title="Geo-Tagged Incident Reporting" desc="Capture ground coordinates and report road blockages, landslips, or bridge damage." />
        <FeatureCard title="Photograph Uploads" desc="Upload high-res photographic evidence for verification by District Authorities." />
        <FeatureCard title="Assigned Sector Monitoring" desc="Inspect accessibility conditions across remote hill roads." />
        <FeatureCard title="Emergency Alert Broadcast" desc="Notify nearby drivers and operators of immediate hazards." />
      </div>
    </div>
  </div>
);

/* Component for Driver */
const DriverView: React.FC<{ user: User }> = ({ user }) => (
  <div className="space-y-6">
    <div className="grid grid-cols-1 md:grid-cols-3 gap-4">
      <StatCard title="Assigned Shipment" value="SHP-9842 (Essential Goods)" icon={<Truck className="w-5 h-5 text-slate-300" />} />
      <StatCard title="GPS Check-in" value="Active Signal" icon={<Radio className="w-5 h-5 text-emerald-400" />} />
      <StatCard title="Emergency SOS" value="Ready" icon={<AlertTriangle className="w-5 h-5 text-rose-500" />} />
    </div>

    <div className="bg-slate-900 border border-slate-800 rounded-xl p-6 shadow-xl">
      <h2 className="text-sm font-bold text-slate-200 uppercase tracking-wider mb-4 pb-2 border-b border-slate-800 flex items-center space-x-2">
        <Navigation className="w-4 h-4 text-emerald-400" />
        <span>Driver Fleet & Check-in Console</span>
      </h2>
      <div className="grid grid-cols-1 md:grid-cols-2 gap-4 text-xs">
        <FeatureCard title="Shipment Details" desc="Access cargo manifest, destination address, and recipient contact." />
        <FeatureCard title="GPS & Check-in Information" desc="Send automated position updates and manual checkpoint check-ins." />
        <FeatureCard title="Route Information & Terrain Advisories" desc="View real-time hill terrain advisories and weather warnings." />
        <FeatureCard title="Trigger SOS Emergency" desc="Send high-priority SOS emergency signal to District Authority and Logistics Command." />
      </div>
    </div>
  </div>
);

const StatCard: React.FC<{ title: string; value: string | number; icon: React.ReactNode }> = ({
  title,
  value,
  icon,
}) => (
  <div className="bg-slate-900 p-4 rounded-xl border border-slate-800 flex items-center justify-between shadow-lg">
    <div>
      <span className="text-slate-400 text-xs block">{title}</span>
      <span className="text-slate-100 font-bold text-lg mt-0.5 block">{value}</span>
    </div>
    <div className="p-2.5 rounded-lg bg-slate-950 border border-slate-800">{icon}</div>
  </div>
);

const FeatureCard: React.FC<{ title: string; desc: string }> = ({ title, desc }) => (
  <div className="bg-slate-950 p-4 rounded-lg border border-slate-800/80">
    <h3 className="font-semibold text-slate-200 text-sm mb-1">{title}</h3>
    <p className="text-slate-400 text-xs leading-relaxed">{desc}</p>
  </div>
);
