import React, { useState } from 'react';
import { Mail, Lock, User, Loader2, ArrowRight } from 'lucide-react';

const getApiUrl = () => import.meta.env?.VITE_API_URL || 'http://localhost:3000';

export default function RegisterForm({ onViewChange }) {
    const [name, setName] = useState('');
    const [username, setUsername] = useState('');
    const [email, setEmail] = useState('');
    const [phone, setPhone] = useState('');
    const [password, setPassword] = useState('');
    const [isLoading, setIsLoading] = useState(false);
    const [error, setError] = useState('');

    const handleSubmit = async (e) => {
        e.preventDefault();
        setIsLoading(true);
        setError('');

        try {
            // We default the primary backend identity to the username
            // But we store email and phone in metadata so the storage adapter can check them
            const primaryIdentifier = username || email || phone;

            const response = await fetch(`${getApiUrl()}/auth/register`, {
                method: 'POST',
                headers: { 'Content-Type': 'application/json' },
                body: JSON.stringify({
                    identifier: primaryIdentifier,
                    password,
                    metadata: { name, username, email, phone }
                })
            });

            const data = await response.json();

            if (!response.ok) {
                throw new Error(data.error || 'Failed to register');
            }

            // Success
            alert('Registration Successful! Please login.');
            onViewChange('login');

        } catch (err) {
            setError(err.message);
        } finally {
            setIsLoading(false);
        }
    };

    return (
        <form onSubmit={handleSubmit} className="space-y-5 animate-in fade-in slide-in-from-right-4 duration-500">

            <div className="space-y-1">
                <label className="text-sm font-medium text-slate-300 ml-1">Full Name</label>
                <div className="relative group">
                    <div className="absolute inset-y-0 left-0 pl-3 flex items-center pointer-events-none text-slate-400 group-focus-within:text-blue-400 transition-colors">
                        <User size={18} />
                    </div>
                    <input
                        type="text"
                        value={name}
                        onChange={(e) => setName(e.target.value)}
                        required
                        className="w-full pl-10 pr-4 py-3 bg-slate-900/50 border border-slate-700/50 rounded-xl focus:ring-2 focus:ring-blue-500/50 focus:border-blue-500 outline-none text-slate-100 placeholder-slate-500 transition-all shadow-inner"
                        placeholder="John Doe"
                    />
                </div>
            </div>

            <div className="space-y-1">
                <label className="text-sm font-medium text-slate-300 ml-1">Username</label>
                <div className="relative group">
                    <div className="absolute inset-y-0 left-0 pl-3 flex items-center pointer-events-none text-slate-400 group-focus-within:text-blue-400 transition-colors">
                        <User size={18} />
                    </div>
                    <input
                        type="text"
                        value={username}
                        onChange={(e) => setUsername(e.target.value)}
                        required
                        className="w-full pl-10 pr-4 py-3 bg-slate-900/50 border border-slate-700/50 rounded-xl focus:ring-2 focus:ring-blue-500/50 focus:border-blue-500 outline-none text-slate-100 placeholder-slate-500 transition-all shadow-inner"
                        placeholder="johndoe88"
                    />
                </div>
            </div>

            <div className="space-y-1">
                <label className="text-sm font-medium text-slate-300 ml-1">Email</label>
                <div className="relative group">
                    <div className="absolute inset-y-0 left-0 pl-3 flex items-center pointer-events-none text-slate-400 group-focus-within:text-blue-400 transition-colors">
                        <Mail size={18} />
                    </div>
                    <input
                        type="email"
                        value={email}
                        onChange={(e) => setEmail(e.target.value)}
                        className="w-full pl-10 pr-4 py-3 bg-slate-900/50 border border-slate-700/50 rounded-xl focus:ring-2 focus:ring-blue-500/50 focus:border-blue-500 outline-none text-slate-100 placeholder-slate-500 transition-all shadow-inner"
                        placeholder="you@example.com"
                    />
                </div>
            </div>

            <div className="space-y-1">
                <label className="text-sm font-medium text-slate-300 ml-1">Phone Number</label>
                <div className="relative group">
                    <div className="absolute inset-y-0 left-0 pl-3 flex items-center pointer-events-none text-slate-400 group-focus-within:text-blue-400 transition-colors">
                        <User size={18} /> {/* Using User icon for phone temporarily as Phone icon is not imported from lucide */}
                    </div>
                    <input
                        type="tel"
                        value={phone}
                        onChange={(e) => setPhone(e.target.value)}
                        className="w-full pl-10 pr-4 py-3 bg-slate-900/50 border border-slate-700/50 rounded-xl focus:ring-2 focus:ring-blue-500/50 focus:border-blue-500 outline-none text-slate-100 placeholder-slate-500 transition-all shadow-inner"
                        placeholder="+1234567890"
                    />
                </div>
            </div>

            <div className="space-y-1">
                <label className="text-sm font-medium text-slate-300 ml-1">Password</label>
                <div className="relative group">
                    <div className="absolute inset-y-0 left-0 pl-3 flex items-center pointer-events-none text-slate-400 group-focus-within:text-blue-400 transition-colors">
                        <Lock size={18} />
                    </div>
                    <input
                        type="password"
                        value={password}
                        onChange={(e) => setPassword(e.target.value)}
                        required
                        className="w-full pl-10 pr-4 py-3 bg-slate-900/50 border border-slate-700/50 rounded-xl focus:ring-2 focus:ring-blue-500/50 focus:border-blue-500 outline-none text-slate-100 placeholder-slate-500 transition-all shadow-inner"
                        placeholder="••••••••"
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
                className="w-full py-3 px-4 bg-gradient-to-r from-blue-600 to-indigo-600 hover:from-blue-500 hover:to-indigo-500 text-white font-semibold rounded-xl shadow-lg shadow-blue-500/30 transition-all transform hover:-translate-y-0.5 active:translate-y-0 flex items-center justify-center gap-2 group disabled:opacity-70 disabled:pointer-events-none"
            >
                {isLoading ? (
                    <Loader2 className="animate-spin" size={20} />
                ) : (
                    <>
                        Create Account
                        <ArrowRight size={18} className="group-hover:translate-x-1 transition-transform" />
                    </>
                )}
            </button>

            <div className="text-center mt-6">
                <span className="text-slate-400 text-sm">Already have an account? </span>
                <button
                    type="button"
                    onClick={() => onViewChange('login')}
                    className="text-blue-400 hover:text-blue-300 text-sm font-semibold transition-colors underline-offset-4 hover:underline"
                >
                    Sign in
                </button>
            </div>
        </form>
    );
}
