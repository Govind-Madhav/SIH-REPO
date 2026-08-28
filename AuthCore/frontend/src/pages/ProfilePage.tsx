import React, { useState } from 'react';
import { useAuth } from '../hooks/useAuth';
import { authApi } from '../api/authApi';
import { NER_DISTRICTS } from '../types/auth';
import {
  User as UserIcon,
  Mail,
  Phone,
  Shield,
  Building2,
  MapPin,
  Lock,
  Edit,
  Save,
  CheckCircle,
  AlertCircle,
  Key,
  LogOut,
} from 'lucide-react';
import { Navbar } from '../components/common/Navbar';

export const ProfilePage: React.FC = () => {
  const { user, updateUserProfile, logout } = useAuth();

  const [activeTab, setActiveTab] = useState<'info' | 'edit' | 'password'>('info');

  // Edit Profile Form State
  const [fullName, setFullName] = useState(user?.fullName || '');
  const [phone, setPhone] = useState(user?.phone || '');
  const [organization, setOrganization] = useState(user?.organization || '');
  const [district, setDistrict] = useState(user?.district || NER_DISTRICTS[0]);
  const [isUpdating, setIsUpdating] = useState(false);
  const [updateMsg, setUpdateMsg] = useState<string | null>(null);
  const [updateErr, setUpdateErr] = useState<string | null>(null);

  // Change Password Form State
  const [currentPassword, setCurrentPassword] = useState('');
  const [newPassword, setNewPassword] = useState('');
  const [confirmPassword, setConfirmPassword] = useState('');
  const [isChangingPass, setIsChangingPass] = useState(false);
  const [passMsg, setPassMsg] = useState<string | null>(null);
  const [passErr, setPassErr] = useState<string | null>(null);

  const handleUpdateProfile = async (e: React.FormEvent) => {
    e.preventDefault();
    setUpdateMsg(null);
    setUpdateErr(null);

    setIsUpdating(true);

    try {
      await updateUserProfile({
        fullName: fullName.trim(),
        phone: phone.trim(),
        organization: organization.trim(),
        district,
      });
      setUpdateMsg('Profile updated successfully!');
      setActiveTab('info');
    } catch (err: any) {
      setUpdateErr(err.response?.data?.error || err.message || 'Failed to update profile.');
    } finally {
      setIsUpdating(false);
    }
  };

  const handleChangePassword = async (e: React.FormEvent) => {
    e.preventDefault();
    setPassMsg(null);
    setPassErr(null);

    if (!currentPassword) {
      setPassErr('Please enter your current password');
      return;
    }
    if (!newPassword || newPassword.length < 8) {
      setPassErr('New password must be at least 8 characters');
      return;
    }
    if (newPassword !== confirmPassword) {
      setPassErr('Passwords do not match');
      return;
    }

    setIsChangingPass(true);

    try {
      await authApi.changePassword({ currentPassword, newPassword });
      setPassMsg('Password changed successfully!');
      setCurrentPassword('');
      setNewPassword('');
      setConfirmPassword('');
    } catch (err: any) {
      setPassErr(err.response?.data?.error || err.message || 'Failed to change password.');
    } finally {
      setIsChangingPass(false);
    }
  };

  if (!user) return null;

  return (
    <div className="min-h-screen bg-slate-950 text-slate-100 flex flex-col">
      <Navbar />

      <main className="max-w-4xl mx-auto px-4 py-8 w-full flex-1">
        {/* Header Banner */}
        <div className="bg-slate-900 border border-slate-800 rounded-xl p-6 mb-6 shadow-xl flex flex-col sm:flex-row items-start sm:items-center justify-between gap-4">
          <div className="flex items-center space-x-4">
            <div className="w-16 h-16 rounded-full bg-gradient-to-br from-emerald-600 to-teal-800 flex items-center justify-center text-white text-2xl font-bold border-2 border-emerald-500/30 shadow-inner">
              {user.fullName ? user.fullName.charAt(0).toUpperCase() : 'U'}
            </div>
            <div>
              <div className="flex items-center space-x-2">
                <h1 className="text-xl font-bold text-slate-100">{user.fullName || 'Operational User'}</h1>
                <span className="bg-emerald-950 text-emerald-400 border border-emerald-800 text-[10px] font-bold px-2 py-0.5 rounded">
                  ACTIVE
                </span>
              </div>
              <p className="text-xs text-slate-400 mt-0.5">{user.email}</p>
              <div className="flex items-center space-x-2 mt-2">
                <span className="text-[11px] font-bold px-2 py-0.5 rounded bg-slate-800 text-slate-300 border border-slate-700">
                  {user.role}
                </span>
                {user.district && (
                  <span className="text-[11px] font-medium px-2 py-0.5 rounded bg-slate-950 text-slate-400 border border-slate-800 flex items-center space-x-1">
                    <MapPin className="w-3 h-3 text-emerald-400" />
                    <span>{user.district}</span>
                  </span>
                )}
              </div>
            </div>
          </div>

          <button
            onClick={() => logout()}
            className="px-3.5 py-2 bg-rose-950/70 hover:bg-rose-900/90 border border-rose-800 text-rose-200 rounded-lg text-xs font-semibold flex items-center space-x-1.5 transition"
          >
            <LogOut className="w-4 h-4 text-rose-400" />
            <span>Terminate Session</span>
          </button>
        </div>

        {/* Messages */}
        {updateMsg && (
          <div className="mb-4 bg-emerald-950/70 border border-emerald-800 p-3 rounded-lg flex items-center space-x-2 text-emerald-200 text-xs">
            <CheckCircle className="w-4 h-4 text-emerald-400 shrink-0" />
            <span>{updateMsg}</span>
          </div>
        )}

        {passMsg && (
          <div className="mb-4 bg-emerald-950/70 border border-emerald-800 p-3 rounded-lg flex items-center space-x-2 text-emerald-200 text-xs">
            <CheckCircle className="w-4 h-4 text-emerald-400 shrink-0" />
            <span>{passMsg}</span>
          </div>
        )}

        {/* Navigation Tabs */}
        <div className="flex space-x-2 bg-slate-900 p-1.5 rounded-lg border border-slate-800 mb-6">
          <button
            onClick={() => setActiveTab('info')}
            className={`flex-1 py-2 text-xs font-semibold rounded-md transition flex items-center justify-center space-x-1.5 ${
              activeTab === 'info'
                ? 'bg-emerald-600 text-white shadow'
                : 'text-slate-400 hover:text-slate-200'
            }`}
          >
            <UserIcon className="w-4 h-4" />
            <span>Profile Overview</span>
          </button>

          <button
            onClick={() => setActiveTab('edit')}
            className={`flex-1 py-2 text-xs font-semibold rounded-md transition flex items-center justify-center space-x-1.5 ${
              activeTab === 'edit'
                ? 'bg-emerald-600 text-white shadow'
                : 'text-slate-400 hover:text-slate-200'
            }`}
          >
            <Edit className="w-4 h-4" />
            <span>Edit Profile</span>
          </button>

          <button
            onClick={() => setActiveTab('password')}
            className={`flex-1 py-2 text-xs font-semibold rounded-md transition flex items-center justify-center space-x-1.5 ${
              activeTab === 'password'
                ? 'bg-emerald-600 text-white shadow'
                : 'text-slate-400 hover:text-slate-200'
            }`}
          >
            <Key className="w-4 h-4" />
            <span>Security & Password</span>
          </button>
        </div>

        {/* Tab Content */}
        {activeTab === 'info' && (
          <div className="bg-slate-900 border border-slate-800 rounded-xl p-6 shadow-xl space-y-6">
            <h2 className="text-sm font-bold text-slate-200 uppercase tracking-wider border-b border-slate-800 pb-3 flex items-center space-x-2">
              <Shield className="w-4 h-4 text-emerald-400" />
              <span>ACCOUNT & OPERATIONAL IDENTITY</span>
            </h2>

            <div className="grid grid-cols-1 md:grid-cols-2 gap-6 text-xs">
              <div className="bg-slate-950 p-4 rounded-lg border border-slate-800/80">
                <span className="text-slate-400 block mb-1">Full Name</span>
                <span className="text-slate-100 font-semibold text-sm">{user.fullName || '—'}</span>
              </div>

              <div className="bg-slate-950 p-4 rounded-lg border border-slate-800/80">
                <span className="text-slate-400 block mb-1">Email Address</span>
                <span className="text-slate-100 font-semibold text-sm">{user.email}</span>
              </div>

              <div className="bg-slate-950 p-4 rounded-lg border border-slate-800/80">
                <span className="text-slate-400 block mb-1">Phone Number</span>
                <span className="text-slate-100 font-semibold text-sm">{user.phone || 'Not specified'}</span>
              </div>

              <div className="bg-slate-950 p-4 rounded-lg border border-slate-800/80">
                <span className="text-slate-400 block mb-1">Platform Role</span>
                <span className="text-emerald-400 font-bold text-sm tracking-wide">{user.role}</span>
              </div>

              <div className="bg-slate-950 p-4 rounded-lg border border-slate-800/80">
                <span className="text-slate-400 block mb-1">Organization / Department</span>
                <span className="text-slate-100 font-semibold text-sm">{user.organization || 'General NER Logistics'}</span>
              </div>

              <div className="bg-slate-950 p-4 rounded-lg border border-slate-800/80">
                <span className="text-slate-400 block mb-1">Assigned NER District</span>
                <span className="text-slate-100 font-semibold text-sm">{user.district || 'All North-Eastern Region'}</span>
              </div>
            </div>
          </div>
        )}

        {activeTab === 'edit' && (
          <div className="bg-slate-900 border border-slate-800 rounded-xl p-6 shadow-xl">
            <h2 className="text-sm font-bold text-slate-200 uppercase tracking-wider border-b border-slate-800 pb-3 mb-5 flex items-center space-x-2">
              <Edit className="w-4 h-4 text-emerald-400" />
              <span>UPDATE PROFILE INFORMATION</span>
            </h2>

            {updateErr && (
              <div className="mb-4 bg-rose-950/70 border border-rose-800 p-3 rounded-lg flex items-center space-x-2 text-rose-200 text-xs">
                <AlertCircle className="w-4 h-4 text-rose-400 shrink-0" />
                <span>{updateErr}</span>
              </div>
            )}

            <form onSubmit={handleUpdateProfile} className="space-y-4 text-xs">
              <div>
                <label className="block text-slate-300 font-medium mb-1">Full Name</label>
                <input
                  type="text"
                  required
                  value={fullName}
                  onChange={(e) => setFullName(e.target.value)}
                  className="w-full px-3 py-2 bg-slate-950 border border-slate-800 rounded-lg text-slate-100 focus:outline-none focus:ring-2 focus:ring-emerald-500"
                />
              </div>

              <div>
                <label className="block text-slate-300 font-medium mb-1">Phone Number</label>
                <input
                  type="tel"
                  value={phone}
                  onChange={(e) => setPhone(e.target.value)}
                  className="w-full px-3 py-2 bg-slate-950 border border-slate-800 rounded-lg text-slate-100 focus:outline-none focus:ring-2 focus:ring-emerald-500"
                />
              </div>

              <div>
                <label className="block text-slate-300 font-medium mb-1">Organization / Department</label>
                <input
                  type="text"
                  value={organization}
                  onChange={(e) => setOrganization(e.target.value)}
                  className="w-full px-3 py-2 bg-slate-950 border border-slate-800 rounded-lg text-slate-100 focus:outline-none focus:ring-2 focus:ring-emerald-500"
                />
              </div>

              <div>
                <label className="block text-slate-300 font-medium mb-1">Assigned NER District</label>
                <select
                  value={district}
                  onChange={(e) => setDistrict(e.target.value)}
                  className="w-full px-3 py-2 bg-slate-950 border border-slate-800 rounded-lg text-slate-100 focus:outline-none focus:ring-2 focus:ring-emerald-500"
                >
                  {NER_DISTRICTS.map((d) => (
                    <option key={d} value={d}>
                      {d}
                    </option>
                  ))}
                </select>
              </div>

              <div className="pt-2 flex justify-end">
                <button
                  type="submit"
                  disabled={isUpdating}
                  className="px-4 py-2 bg-emerald-600 hover:bg-emerald-500 text-white rounded-lg text-xs font-semibold flex items-center space-x-1.5 transition"
                >
                  <Save className="w-4 h-4" />
                  <span>{isUpdating ? 'Saving...' : 'Save Profile Changes'}</span>
                </button>
              </div>
            </form>
          </div>
        )}

        {activeTab === 'password' && (
          <div className="bg-slate-900 border border-slate-800 rounded-xl p-6 shadow-xl">
            <h2 className="text-sm font-bold text-slate-200 uppercase tracking-wider border-b border-slate-800 pb-3 mb-5 flex items-center space-x-2">
              <Lock className="w-4 h-4 text-emerald-400" />
              <span>CHANGE PASSWORD</span>
            </h2>

            {passErr && (
              <div className="mb-4 bg-rose-950/70 border border-rose-800 p-3 rounded-lg flex items-center space-x-2 text-rose-200 text-xs">
                <AlertCircle className="w-4 h-4 text-rose-400 shrink-0" />
                <span>{passErr}</span>
              </div>
            )}

            <form onSubmit={handleChangePassword} className="space-y-4 text-xs">
              <div>
                <label className="block text-slate-300 font-medium mb-1">Current Password *</label>
                <input
                  type="password"
                  required
                  placeholder="Enter current password"
                  value={currentPassword}
                  onChange={(e) => setCurrentPassword(e.target.value)}
                  className="w-full px-3 py-2 bg-slate-950 border border-slate-800 rounded-lg text-slate-100 focus:outline-none focus:ring-2 focus:ring-emerald-500"
                />
              </div>

              <div>
                <label className="block text-slate-300 font-medium mb-1">New Password *</label>
                <input
                  type="password"
                  required
                  placeholder="At least 8 characters"
                  value={newPassword}
                  onChange={(e) => setNewPassword(e.target.value)}
                  className="w-full px-3 py-2 bg-slate-950 border border-slate-800 rounded-lg text-slate-100 focus:outline-none focus:ring-2 focus:ring-emerald-500"
                />
              </div>

              <div>
                <label className="block text-slate-300 font-medium mb-1">Confirm New Password *</label>
                <input
                  type="password"
                  required
                  placeholder="Re-enter new password"
                  value={confirmPassword}
                  onChange={(e) => setConfirmPassword(e.target.value)}
                  className="w-full px-3 py-2 bg-slate-950 border border-slate-800 rounded-lg text-slate-100 focus:outline-none focus:ring-2 focus:ring-emerald-500"
                />
              </div>

              <div className="pt-2 flex justify-end">
                <button
                  type="submit"
                  disabled={isChangingPass}
                  className="px-4 py-2 bg-emerald-600 hover:bg-emerald-500 text-white rounded-lg text-xs font-semibold transition"
                >
                  {isChangingPass ? 'Updating Password...' : 'Update Password'}
                </button>
              </div>
            </form>
          </div>
        )}
      </main>
    </div>
  );
};
