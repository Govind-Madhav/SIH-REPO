import React, { useState } from 'react';
import { LogOut, User, Loader2 } from 'lucide-react';
import { logoutApi } from '../lib/authApi';

export default function Dashboard({ onViewChange }) {
    const [isLoggingOut, setIsLoggingOut] = useState(false);

    const handleLogout = async () => {
        setIsLoggingOut(true);
        try {
            await logoutApi();
            onViewChange('landing');
        } catch (error) {
            console.error('Logout failed', error);
        } finally {
            setIsLoggingOut(false);
        }
    };

    return (
        <div className="relative z-10 w-full max-w-2xl p-8 mx-auto glass-panel rounded-3xl overflow-hidden transition-all duration-500 pointer-events-auto">
            <div className="absolute top-[-50px] right-[-50px] w-40 h-40 bg-indigo-500 rounded-full mix-blend-multiply filter blur-3xl opacity-30 animate-pulse"></div>

            <div className="relative z-20 text-center space-y-8">
                <div className="mx-auto w-20 h-20 bg-indigo-500/20 rounded-full flex items-center justify-center border border-indigo-400/30 mb-6">
                    <User size={40} className="text-indigo-300" />
                </div>

                <h2 className="text-4xl font-bold text-transparent bg-clip-text bg-gradient-to-r from-indigo-300 to-purple-300 tracking-tight">
                    Welcome to your Dashboard
                </h2>

                <p className="text-slate-300 text-lg max-w-md mx-auto">
                    You have successfully authenticated using the Universal Auth Engine. Your secure JWT tokens are stored locally.
                </p>

                <div className="pt-8 flex justify-center">
                    <button
                        onClick={handleLogout}
                        disabled={isLoggingOut}
                        className="px-8 py-3 bg-slate-800/80 hover:bg-slate-700/80 border border-slate-600/50 hover:border-red-500/50 text-slate-200 hover:text-red-400 font-semibold rounded-xl transition-all flex items-center gap-3 group disabled:opacity-50"
                    >
                        {isLoggingOut ? (
                            <Loader2 size={18} className="animate-spin" />
                        ) : (
                            <LogOut size={18} className="group-hover:-translate-x-1 transition-transform" />
                        )}
                        Sign Out Securely
                    </button>
                </div>
            </div>
        </div>
    );
}
