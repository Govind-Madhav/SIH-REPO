const express = require('express');
const cors = require('cors');
require('dotenv').config();
const Auth = require('./index');
const SqliteStorageAdapter = require('./adapters/storage/sqlite');

const app = express();

const CORS_ORIGIN = process.env.CORS_ORIGIN || 'http://localhost:5173';
const PORT = process.env.PORT || 3000;

app.use(cors({
    origin: CORS_ORIGIN,
    credentials: true
}));

app.use(express.json());

const dbAdapter = new SqliteStorageAdapter();

const claimsResolver = async ({ userId, sessionId, context }) => {
    const user = await dbAdapter.findUserById(userId);
    const meta = user?.metadata || {};
    const role = meta.role || 'FIELD_OFFICER';
    return {
        roles: [role],
        role: role,
        fullName: meta.fullName || user?.identifier || 'User',
        email: meta.email || user?.identifier || '',
        phone: meta.phone || '',
        organization: meta.organization || '',
        district: meta.district || '',
        tenant: context?.tenant || 'NER_LOGISTICS'
    };
};

const policyResolver = async ({ policy, claims, context }) => {
    if (!claims || !claims.role) return false;
    const role = claims.role;
    if (policy === 'SUPER_ADMIN') return role === 'SUPER_ADMIN';
    if (policy === 'ADMIN') return role === 'SUPER_ADMIN' || role === 'ADMIN';
    if (policy === 'DISTRICT_AUTHORITY') return ['SUPER_ADMIN', 'ADMIN', 'DISTRICT_AUTHORITY'].includes(role);
    if (policy === 'LOGISTICS_OPERATOR') return ['SUPER_ADMIN', 'ADMIN', 'LOGISTICS_OPERATOR'].includes(role);
    if (policy === 'FIELD_OFFICER') return ['SUPER_ADMIN', 'ADMIN', 'FIELD_OFFICER'].includes(role);
    if (policy === 'DRIVER') return ['SUPER_ADMIN', 'ADMIN', 'DRIVER'].includes(role);
    return true;
};

const authSystem = Auth.init({
    storageAdapter: dbAdapter,
    claimsResolver: claimsResolver,
    policyResolver: policyResolver,
    jwtSecret: process.env.JWT_SECRET || 'super-secret-demo-key-123',
    accessExpiry: process.env.ACCESS_EXPIRY || '1m',
    refreshExpiryMs: parseInt(process.env.REFRESH_EXPIRY_MS, 10) || 1000 * 60 * 60 * 24,
    trustJwtClaims: true
});

// In development, log password reset token so you can test without email
authSystem.onPasswordResetRequested(({ identifier, rawToken, expiresAt }) => {
    if (process.env.NODE_ENV !== 'production') {
        console.log('[Dev] Password reset requested for:', identifier);
        console.log('[Dev] Reset token (use in Reset Password form):', rawToken);
        console.log('[Dev] Expires at:', expiresAt);
    }
});

app.use('/auth', authSystem.router);

app.listen(PORT, () => {
    console.log(`Auth Engine server running on http://localhost:${PORT}`);
    console.log('SQLite Database Storage Adapter is active.');
});
