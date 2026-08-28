import React from 'react';
import { Sparkles, Shield, Rocket, ArrowRight } from 'lucide-react';

export default function LandingPage({ onViewChange }) {
    return (
        <div className="relative z-10 w-full max-w-6xl mx-auto px-6 py-20 flex flex-col items-center justify-center min-h-screen animate-in fade-in slide-in-from-bottom-8 duration-700 pointer-events-none">

            {/* Decorative Orbs */}
            <div className="absolute top-[10%] left-[20%] w-64 h-64 bg-purple-600 rounded-full mix-blend-multiply filter blur-[80px] opacity-30 animate-pulse"></div>
            <div className="absolute top-[40%] right-[20%] w-80 h-80 bg-blue-600 rounded-full mix-blend-multiply filter blur-[100px] opacity-20 animate-pulse" style={{ animationDelay: '2s' }}></div>

            {/* Hero Content */}
            <div className="text-center space-y-8 max-w-4xl relative z-10">
                <div className="inline-flex items-center gap-2 px-4 py-2 rounded-full glass-panel border-purple-500/30 text-purple-300 text-sm font-medium mb-4">
                    <Sparkles size={16} />
                    <span>The Next Generation Auth Module</span>
                </div>

                <h1 className="text-5xl md:text-7xl font-extrabold tracking-tight text-transparent bg-clip-text bg-gradient-to-r from-white via-purple-200 to-indigo-400 pb-2">
                    The Universal <br className="hidden md:block" /> Authentication Engine
                </h1>

                <p className="text-lg md:text-xl text-slate-300 max-w-3xl mx-auto leading-relaxed">
                    Built to be the ONLY auth system you'll ever need. Whether you are building a simple <b>single-role blog</b> or a complex <b>multi-role enterprise platform</b>, the Auth Module adapts instantly. It provides deeply decoupled components for Claims, Policies, and Sessions that fit right into any website or backend infrastructure.
                </p>

                {/* CTA Buttons */}
                <div className="flex flex-col sm:flex-row items-center justify-center gap-4 pt-8 pointer-events-auto">
                    <button
                        onClick={() => onViewChange('auth')}
                        className="w-full sm:w-auto px-8 py-4 bg-white text-slate-900 font-bold rounded-2xl shadow-[0_0_40px_rgba(255,255,255,0.3)] hover:shadow-[0_0_60px_rgba(255,255,255,0.5)] transition-all transform hover:-translate-y-1 flex items-center justify-center gap-2 group"
                    >
                        Get Started
                        <ArrowRight size={20} className="group-hover:translate-x-1 transition-transform" />
                    </button>

                    <button
                        onClick={() => onViewChange('auth')}
                        className="w-full sm:w-auto px-8 py-4 glass-panel text-white font-bold rounded-2xl hover:bg-slate-800/50 transition-all border border-slate-600/50 hover:border-slate-500/50"
                    >
                        Sign In
                    </button>
                </div>
            </div>

            {/* Features Grid */}
            <div className="grid grid-cols-1 md:grid-cols-3 gap-8 mt-32 w-full relative z-10 pointer-events-auto">

                {/* Feature 1 */}
                <div className="glass-panel p-8 rounded-3xl group hover:-translate-y-2 transition-transform duration-300">
                    <div className="w-14 h-14 bg-purple-500/20 rounded-2xl flex items-center justify-center mb-6 group-hover:bg-purple-500/30 transition-colors border border-purple-500/30">
                        <Shield className="text-purple-400" size={28} />
                    </div>
                    <h3 className="text-2xl font-bold text-white mb-3">Single or Multi-Role</h3>
                    <p className="text-slate-400 leading-relaxed text-sm">
                        Powered by a dynamic Claims and Policy resolver engine, you can restrict access entirely (admin-only) or manage granular, complex multi-tenant permissions out-of-the-box.
                    </p>
                </div>

                {/* Feature 2 */}
                <div className="glass-panel p-8 rounded-3xl group hover:-translate-y-2 transition-transform duration-300">
                    <div className="w-14 h-14 bg-blue-500/20 rounded-2xl flex items-center justify-center mb-6 group-hover:bg-blue-500/30 transition-colors border border-blue-500/30">
                        <Rocket className="text-blue-400" size={28} />
                    </div>
                    <h3 className="text-2xl font-bold text-white mb-3">Storage Adapters</h3>
                    <p className="text-slate-400 leading-relaxed text-sm">
                        Because `auth-engine` relies on injected `StorageAdapters` (like our current MockDB), it can be seamlessly attached to MongoDB, Postgres, or Redis without altering the session logic.
                    </p>
                </div>

                {/* Feature 3 */}
                <div className="glass-panel p-8 rounded-3xl group hover:-translate-y-2 transition-transform duration-300">
                    <div className="w-14 h-14 bg-indigo-500/20 rounded-2xl flex items-center justify-center mb-6 group-hover:bg-indigo-500/30 transition-colors border border-indigo-500/30">
                        <Sparkles className="text-indigo-400" size={28} />
                    </div>
                    <h3 className="text-2xl font-bold text-white mb-3">Event-Driven Architecture</h3>
                    <p className="text-slate-400 leading-relaxed text-sm">
                        Featuring built-in `EventEmitter` hooks under the hood, host backend apps can listen to `LOGIN_SUCCESS` or `POLICY_DENIED` events instantly triggering analytics or audit logs.
                    </p>
                </div>
            </div>
        </div>
    );
}
