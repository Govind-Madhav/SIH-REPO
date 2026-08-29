import React, { useState } from 'react';
import { Lock, Loader2, ArrowRight } from 'lucide-react';

const getApiUrl = () => import.meta.env?.VITE_API_URL || 'http://localhost:3000';

export default function ResetPasswordForm({ onViewChange, initialToken = '' }) {
    const [token, setToken] = useState(initialToken);
    const [newPassword, setNewPassword] = useState('');
    const [confirmPassword, setConfirmPassword] = useState('');
    const [isLoading, setIsLoading] = useState(false);
    const [error, setError] = useState('');
    const [success, setSuccess] = useState(false);

    const handleSubmit = async (e) => {
        e.preventDefault();
        setError('');

        if (newPassword !== confirmPassword) {
            setError('Passwords do not match');
            return;
        }
        if (newPassword.length < 8) {
            setError('Password must be at least 8 characters');
            return;
        }
        if (!token.trim()) {
            setError('Reset token is required');
            return;
        }

        setIsLoading(true);

        try {
            const response = await fetch(`${getApiUrl()}/auth/reset-password`, {
                method: 'POST',
                headers: { 'Content-Type': 'application/json' },
                body: JSON.stringify({ token: token.trim(), newPassword })
            });

            const data = await response.json();

            if (!response.ok) {
                throw new Error(data.error || 'Reset failed');
            }

            setSuccess(true);
        } catch (err) {
            setError(err.message);
        } finally {
            setIsLoading(false);
        }
    };

    if (success) {
        return (
            <div className="space-y-5 animate-in fade-in duration-300">
                <div className="text-center p-4 rounded-xl bg-emerald-500/10 border border-emerald-500/30 text-emerald-300">
                    Password has been reset successfully. You can sign in now.
                </div>
                <button
                    type="button"
                    onClick={() => onViewChange('login')}
                    className="w-full py-3 px-4 bg-gradient-to-r from-purple-600 to-blue-600 text-white font-semibold rounded-xl"
                >
                    Sign in
                </button>
            </div>
        );
    }

    return (
        <form onSubmit={handleSubmit} className="space-y-5 animate-in fade-in slide-in-from-bottom-4 duration-500">
            <div className="space-y-1">
                <label className="text-sm font-medium text-slate-300 ml-1">Reset token</label>
                <input
                    type="text"
                    value={token}
                    onChange={(e) => setToken(e.target.value)}
                    placeholder="Paste the token from your email"
                    className="w-full px-4 py-3 bg-slate-900/50 border border-slate-700/50 rounded-xl focus:ring-2 focus:ring-purple-500/50 outline-none text-slate-100 placeholder-slate-500"
                />
            </div>
            <div className="space-y-1">
                <label className="text-sm font-medium text-slate-300 ml-1">New password</label>
                <div className="relative group">
                    <div className="absolute inset-y-0 left-0 pl-3 flex items-center pointer-events-none text-slate-400">
                        <Lock size={18} />
                    </div>
                    <input
                        type="password"
                        value={newPassword}
                        onChange={(e) => setNewPassword(e.target.value)}
                        required
                        minLength={8}
                        className="w-full pl-10 pr-4 py-3 bg-slate-900/50 border border-slate-700/50 rounded-xl focus:ring-2 focus:ring-purple-500/50 outline-none text-slate-100 placeholder-slate-500"
                        placeholder="At least 8 characters"
                    />
                </div>
            </div>
            <div className="space-y-1">
                <label className="text-sm font-medium text-slate-300 ml-1">Confirm password</label>
                <input
                    type="password"
                    value={confirmPassword}
                    onChange={(e) => setConfirmPassword(e.target.value)}
                    required
                    minLength={8}
                    className="w-full px-4 py-3 bg-slate-900/50 border border-slate-700/50 rounded-xl focus:ring-2 focus:ring-purple-500/50 outline-none text-slate-100 placeholder-slate-500"
                    placeholder="Repeat new password"
                />
            </div>

            {error && (
                <div className="text-red-400 text-sm text-center font-medium bg-red-900/20 py-2 rounded-lg border border-red-500/20">
                    {error}
                </div>
            )}

            <button
                type="submit"
                disabled={isLoading}
                className="w-full py-3 px-4 bg-gradient-to-r from-purple-600 to-blue-600 hover:from-purple-500 hover:to-blue-500 text-white font-semibold rounded-xl shadow-lg shadow-purple-500/30 transition-all flex items-center justify-center gap-2 disabled:opacity-70 disabled:pointer-events-none"
            >
                {isLoading ? (
                    <Loader2 className="animate-spin" size={20} />
                ) : (
                    <>
                        Reset password
                        <ArrowRight size={18} />
                    </>
                )}
            </button>

            <div className="text-center">
                <button
                    type="button"
                    onClick={() => onViewChange('login')}
                    className="text-slate-400 hover:text-white text-sm transition-colors"
                >
                    ← Back to Sign in
                </button>
            </div>
        </form>
    );
}
