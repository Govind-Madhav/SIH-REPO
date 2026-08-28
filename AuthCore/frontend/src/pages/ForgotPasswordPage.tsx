import React, { useState } from 'react';
import { Link, useNavigate } from 'react-router-dom';
import { authApi } from '../api/authApi';
import { Mail, KeyRound, AlertCircle, CheckCircle, ArrowLeft } from 'lucide-react';

export const ForgotPasswordPage: React.FC = () => {
  const navigate = useNavigate();
  const [identifier, setIdentifier] = useState('');
  const [isLoading, setIsLoading] = useState(false);
  const [error, setError] = useState<string | null>(null);
  const [success, setSuccess] = useState<string | null>(null);

  const handleSubmit = async (e: React.FormEvent) => {
    e.preventDefault();
    setError(null);
    setSuccess(null);

    if (!identifier.trim()) {
      setError('Please enter your email address or phone number');
      return;
    }

    setIsLoading(true);

    try {
      const res = await authApi.forgotPassword({ identifier: identifier.trim() });
      setSuccess(
        res.message ||
          'If an account exists for this identifier, a password reset token has been issued.'
      );
    } catch (err: any) {
      setError(err.response?.data?.error || err.message || 'Failed to request password reset.');
    } finally {
      setIsLoading(false);
    }
  };

  return (
    <div className="min-h-screen bg-slate-950 flex flex-col justify-center py-12 sm:px-6 lg:px-8 text-slate-100">
      <div className="sm:mx-auto sm:w-full sm:max-w-md">
        <div className="flex justify-center mb-3">
          <div className="w-12 h-12 rounded-xl bg-amber-950/80 border border-amber-800 flex items-center justify-center text-amber-400 shadow-lg">
            <KeyRound className="w-6 h-6" />
          </div>
        </div>
        <h2 className="text-center text-2xl font-bold tracking-tight text-slate-100">
          RECOVER OPERATIONAL ACCESS
        </h2>
        <p className="mt-1 text-center text-xs text-slate-400">
          Enter your registered Email or Phone number to issue a reset token
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
            <div className="mb-5 bg-emerald-950/70 border border-emerald-800 p-3.5 rounded-lg text-xs space-y-3">
              <div className="flex items-start space-x-2 text-emerald-200">
                <CheckCircle className="w-4 h-4 text-emerald-400 shrink-0 mt-0.5" />
                <span>{success}</span>
              </div>
              <div className="pt-2 border-t border-emerald-900 flex justify-end">
                <button
                  onClick={() => navigate('/reset-password')}
                  className="px-3 py-1.5 bg-emerald-600 hover:bg-emerald-500 text-white rounded font-semibold text-xs"
                >
                  Proceed to Reset Password →
                </button>
              </div>
            </div>
          )}

          {!success && (
            <form className="space-y-4" onSubmit={handleSubmit}>
              <div>
                <label className="block text-xs font-medium text-slate-300 mb-1.5">
                  Email Address or Registered Phone
                </label>
                <div className="relative rounded-md shadow-sm">
                  <div className="absolute inset-y-0 left-0 pl-3 flex items-center pointer-events-none text-slate-400">
                    <Mail className="w-4 h-4" />
                  </div>
                  <input
                    type="text"
                    required
                    placeholder="user@ner.logistics.gov.in"
                    value={identifier}
                    onChange={(e) => setIdentifier(e.target.value)}
                    className="block w-full pl-9 pr-3 py-2.5 bg-slate-950 border border-slate-800 rounded-lg text-slate-100 placeholder-slate-500 text-sm focus:outline-none focus:ring-2 focus:ring-emerald-500"
                  />
                </div>
              </div>

              <div>
                <button
                  type="submit"
                  disabled={isLoading}
                  className="w-full flex justify-center py-2.5 px-4 border border-transparent rounded-lg shadow-sm text-xs font-semibold text-white bg-amber-600 hover:bg-amber-500 focus:outline-none focus:ring-2 focus:ring-amber-500 disabled:opacity-50 transition"
                >
                  {isLoading ? 'Issuing Reset Token...' : 'Send Reset Token'}
                </button>
              </div>
            </form>
          )}

          <div className="mt-6 pt-4 border-t border-slate-800 text-center">
            <Link
              to="/login"
              className="inline-flex items-center space-x-1.5 text-xs text-slate-400 hover:text-slate-200 transition"
            >
              <ArrowLeft className="w-3.5 h-3.5" />
              <span>Back to Login</span>
            </Link>
          </div>
        </div>
      </div>
    </div>
  );
};
