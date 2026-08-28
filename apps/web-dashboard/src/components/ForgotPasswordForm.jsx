import React, { useState } from 'react';
import { Mail, Loader2, ArrowRight } from 'lucide-react';

const getApiUrl = () => import.meta.env?.VITE_API_URL || 'http://localhost:3000';

export default function ForgotPasswordForm({ onViewChange }) {
    const [identifier, setIdentifier] = useState('');
    const [isLoading, setIsLoading] = useState(false);
    const [error, setError] = useState('');
    const [sent, setSent] = useState(false);

    const handleSubmit = async (e) => {
        e.preventDefault();
        setIsLoading(true);
        setError('');
        setSent(false);

        try {
            const response = await fetch(`${getApiUrl()}/auth/forgot-password`, {
                method: 'POST',
                headers: { 'Content-Type': 'application/json' },
                body: JSON.stringify({ identifier })
            });

            const data = await response.json();

            if (!response.ok) {
                throw new Error(data.error || 'Request failed');
            }

            setSent(true);
        } catch (err) {
            setError(err.message);
        } finally {
            setIsLoading(false);
        }
    };

    if (sent) {
        return (
            <div className="space-y-5 animate-in fade-in duration-300">
                <div className="text-center p-4 rounded-xl bg-emerald-500/10 border border-emerald-500/30 text-emerald-300 text-sm">
                    If an account exists for this identifier, a reset link has been sent. Check your email (or server logs in development).
                </div>
                <p className="text-slate-400 text-sm text-center">
                    To reset your password, use the link from the email or open the reset page and enter the token you received.
                </p>
                <div className="flex gap-3">
                    <button
                        type="button"
                        onClick={() => { setSent(false); setIdentifier(''); }}
                        className="flex-1 py-2 px-4 rounded-xl border border-slate-600 text-slate-300 hover:bg-slate-800/50"
                    >
                        Send another
                    </button>
                    <button
                        type="button"
                        onClick={() => onViewChange('reset')}
                        className="flex-1 py-2 px-4 rounded-xl bg-purple-600 text-white hover:bg-purple-500"
                    >
                        I have a reset token
                    </button>
                </div>
                <div className="text-center">
                    <button
                        type="button"
                        onClick={() => onViewChange('login')}
                        className="text-purple-400 hover:text-purple-300 text-sm font-semibold"
                    >
                        ← Back to Sign in
                    </button>
                </div>
            </div>
        );
    }

    return (
        <form onSubmit={handleSubmit} className="space-y-5 animate-in fade-in slide-in-from-bottom-4 duration-500">
            <div className="space-y-1">
                <label className="text-sm font-medium text-slate-300 ml-1">Email, Phone, or Username</label>
                <div className="relative group">
                    <div className="absolute inset-y-0 left-0 pl-3 flex items-center pointer-events-none text-slate-400 group-focus-within:text-purple-400 transition-colors">
                        <Mail size={18} />
                    </div>
                    <input
                        type="text"
                        value={identifier}
                        onChange={(e) => setIdentifier(e.target.value)}
                        required
                        className="w-full pl-10 pr-4 py-3 bg-slate-900/50 border border-slate-700/50 rounded-xl focus:ring-2 focus:ring-purple-500/50 focus:border-purple-500 outline-none text-slate-100 placeholder-slate-500"
                        placeholder="you@example.com"
                    />
                </div>
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
                        Send reset link
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
