import React, { useState } from 'react';
import { useNavigate, Link, useSearchParams } from 'react-router-dom';
import { authApi } from '../api/authApi';
import { Lock, Key, AlertCircle, CheckCircle, ArrowLeft, Eye, EyeOff } from 'lucide-react';

export const ResetPasswordPage: React.FC = () => {
  const navigate = useNavigate();
  const [searchParams] = useSearchParams();

  const [token, setToken] = useState(searchParams.get('token') || '');
  const [newPassword, setNewPassword] = useState('');
  const [confirmPassword, setConfirmPassword] = useState('');
  const [showPassword, setShowPassword] = useState(false);

  const [isLoading, setIsLoading] = useState(false);
  const [error, setError] = useState<string | null>(null);
  const [success, setSuccess] = useState<string | null>(null);

  const handleSubmit = async (e: React.FormEvent) => {
    e.preventDefault();
    setError(null);
    setSuccess(null);

    if (!token.trim()) {
      setError('Please enter the reset token');
      return;
    }
    if (!newPassword || newPassword.length < 8) {
      setError('New password must be at least 8 characters long');
      return;
    }
    if (newPassword !== confirmPassword) {
      setError('Passwords do not match');
      return;
    }

    setIsLoading(true);

    try {
      await authApi.resetPassword({ token: token.trim(), newPassword });
      setSuccess('Password reset successfully! You can now log in with your new password.');

      setTimeout(() => {
        navigate('/login');
      }, 2000);
    } catch (err: any) {
      setError(
        err.response?.data?.error === 'INVALID_OR_EXPIRED_RESET_TOKEN'
          ? 'Invalid or expired reset token. Please request a new one.'
          : err.response?.data?.error || err.message || 'Failed to reset password.'
      );
    } finally {
      setIsLoading(false);
    }
  };

  return (
    <div className="min-h-screen bg-slate-950 flex flex-col justify-center py-12 sm:px-6 lg:px-8 text-slate-100">
      <div className="sm:mx-auto sm:w-full sm:max-w-md">
        <div className="flex justify-center mb-3">
          <div className="w-12 h-12 rounded-xl bg-emerald-950/80 border border-emerald-800 flex items-center justify-center text-emerald-400 shadow-lg">
            <Lock className="w-6 h-6" />
          </div>
        </div>
        <h2 className="text-center text-2xl font-bold tracking-tight text-slate-100">
          RESET OPERATIONAL PASSWORD
        </h2>
        <p className="mt-1 text-center text-xs text-slate-400">
          Enter the reset token issued by AuthCore and specify a new password
        </p>
      </div>

      <div className="mt-8 sm:mx-auto sm:w-full sm:max-w-md">
        <div className="bg-slate-900 py-8 px-4 shadow-2xl border border-slate-800 sm:rounded-xl sm:px-10">
          {error && (
            <div className="mb-5 bg-rose-950/70 border border-rose-800 p-3 rounded-lg flex items-start space-x-2.5 text-rose-200 text-xs">
              <AlertCircle className="w-4 h-4 text-rose-400 shrink-0 mt-0.5" />
              <span>{error}</span>
            </div>
          )}

          {success && (
            <div className="mb-5 bg-emerald-950/70 border border-emerald-800 p-3 rounded-lg flex items-start space-x-2.5 text-emerald-200 text-xs">
              <CheckCircle className="w-4 h-4 text-emerald-400 shrink-0 mt-0.5" />
              <span>{success}</span>
            </div>
          )}

          <form className="space-y-4" onSubmit={handleSubmit}>
            <div>
              <label className="block text-xs font-medium text-slate-300 mb-1">Reset Token *</label>
              <div className="relative rounded-md shadow-sm">
                <div className="absolute inset-y-0 left-0 pl-3 flex items-center pointer-events-none text-slate-400">
                  <Key className="w-4 h-4" />
                </div>
                <input
                  type="text"
                  required
                  placeholder="Paste raw reset token"
                  value={token}
                  onChange={(e) => setToken(e.target.value)}
                  className="block w-full pl-9 pr-3 py-2 bg-slate-950 border border-slate-800 rounded-lg text-slate-100 text-xs font-mono focus:outline-none focus:ring-2 focus:ring-emerald-500"
                />
              </div>
              <p className="mt-1 text-[11px] text-slate-500">
                In local dev mode, check the auth-engine server console output for the token.
              </p>
            </div>

            <div>
              <label className="block text-xs font-medium text-slate-300 mb-1">
                New Password *
              </label>
              <div className="relative rounded-md shadow-sm">
                <div className="absolute inset-y-0 left-0 pl-3 flex items-center pointer-events-none text-slate-400">
                  <Lock className="w-4 h-4" />
                </div>
                <input
                  type={showPassword ? 'text' : 'password'}
                  required
                  placeholder="Min 8 characters"
                  value={newPassword}
                  onChange={(e) => setNewPassword(e.target.value)}
                  className="block w-full pl-9 pr-9 py-2 bg-slate-950 border border-slate-800 rounded-lg text-slate-100 text-xs focus:outline-none focus:ring-2 focus:ring-emerald-500"
                />
                <button
                  type="button"
                  onClick={() => setShowPassword(!showPassword)}
                  className="absolute inset-y-0 right-0 pr-3 flex items-center text-slate-400 hover:text-slate-200"
                >
                  {showPassword ? <EyeOff className="w-3.5 h-3.5" /> : <Eye className="w-3.5 h-3.5" />}
                </button>
              </div>
            </div>

            <div>
              <label className="block text-xs font-medium text-slate-300 mb-1">
                Confirm New Password *
              </label>
              <div className="relative rounded-md shadow-sm">
                <div className="absolute inset-y-0 left-0 pl-3 flex items-center pointer-events-none text-slate-400">
                  <Lock className="w-4 h-4" />
                </div>
                <input
                  type={showPassword ? 'text' : 'password'}
                  required
                  placeholder="Re-enter new password"
                  value={confirmPassword}
                  onChange={(e) => setConfirmPassword(e.target.value)}
                  className="block w-full pl-9 pr-3 py-2 bg-slate-950 border border-slate-800 rounded-lg text-slate-100 text-xs focus:outline-none focus:ring-2 focus:ring-emerald-500"
                />
              </div>
            </div>

            <div>
              <button
                type="submit"
                disabled={isLoading}
                className="w-full flex justify-center py-2.5 px-4 border border-transparent rounded-lg shadow-sm text-xs font-semibold text-white bg-emerald-600 hover:bg-emerald-500 focus:outline-none focus:ring-2 focus:ring-emerald-500 disabled:opacity-50 transition"
              >
                {isLoading ? 'Resetting Password...' : 'Confirm Reset Password'}
              </button>
            </div>
          </form>

          <div className="mt-6 pt-4 border-t border-slate-800 text-center">
            <Link
              to="/login"
              className="inline-flex items-center space-x-1.5 text-xs text-slate-400 hover:text-slate-200"
            >
              <ArrowLeft className="w-3.5 h-3.5" />
              <span>Return to Login</span>
            </Link>
          </div>
        </div>
      </div>
    </div>
  );
};
