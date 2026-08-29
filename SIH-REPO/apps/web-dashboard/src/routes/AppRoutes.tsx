import React from 'react';
import { Routes, Route, Navigate } from 'react-router-dom';
import { ProtectedRoute } from './ProtectedRoute';
import { LoginPage } from '../pages/LoginPage';
import { RegisterPage } from '../pages/RegisterPage';
import { ForgotPasswordPage } from '../pages/ForgotPasswordPage';
import { ResetPasswordPage } from '../pages/ResetPasswordPage';
import { ProfilePage } from '../pages/ProfilePage';
import { DashboardPage } from '../pages/DashboardPage';
import { UnauthorizedPage } from '../pages/UnauthorizedPage';

export const AppRoutes: React.FC = () => {
  return (
    <Routes>
      {/* Public Auth Routes */}
      <Route path="/login" element={<LoginPage />} />
      <Route path="/register" element={<RegisterPage />} />
      <Route path="/forgot-password" element={<ForgotPasswordPage />} />
      <Route path="/reset-password" element={<ResetPasswordPage />} />
      <Route path="/unauthorized" element={<UnauthorizedPage />} />

      {/* Protected Profile Route */}
      <Route
        path="/profile"
        element={
          <ProtectedRoute>
            <ProfilePage />
          </ProtectedRoute>
        }
      />

      {/* Role-Based Dashboard Routes */}
      <Route
        path="/admin"
        element={
          <ProtectedRoute allowedRoles={['SUPER_ADMIN', 'ADMIN']}>
            <DashboardPage />
          </ProtectedRoute>
        }
      />
      <Route
        path="/district-dashboard"
        element={
          <ProtectedRoute allowedRoles={['SUPER_ADMIN', 'ADMIN', 'DISTRICT_AUTHORITY']}>
            <DashboardPage />
          </ProtectedRoute>
        }
      />
      <Route
        path="/logistics"
        element={
          <ProtectedRoute allowedRoles={['SUPER_ADMIN', 'ADMIN', 'LOGISTICS_OPERATOR']}>
            <DashboardPage />
          </ProtectedRoute>
        }
      />
      <Route
        path="/field"
        element={
          <ProtectedRoute allowedRoles={['SUPER_ADMIN', 'ADMIN', 'FIELD_OFFICER']}>
            <DashboardPage />
          </ProtectedRoute>
        }
      />
      <Route
        path="/driver"
        element={
          <ProtectedRoute allowedRoles={['SUPER_ADMIN', 'ADMIN', 'DRIVER']}>
            <DashboardPage />
          </ProtectedRoute>
        }
      />

      {/* Default Fallback */}
      <Route
        path="/"
        element={
          <ProtectedRoute>
            <DashboardPage />
          </ProtectedRoute>
        }
      />

      {/* Wildcard Catch-All */}
      <Route path="*" element={<Navigate to="/" replace />} />
    </Routes>
  );
};
