const jwt = require('jsonwebtoken');
const crypto = require('crypto');

let JWT_SECRET = null;
let ACCESS_EXPIRY = '15m';

const __init__ = (secret, expiry) => {
    if (!secret) throw new Error("JWT_SECRET is required to initialize the Token Engine.");
    JWT_SECRET = secret;
    if (expiry) ACCESS_EXPIRY = expiry;
};

const generateAccessToken = (payload) => {
    if (!JWT_SECRET) throw new Error("Token Engine not initialized with secret");

    // payload should contain: sub (userId), sid (sessionId), claims, tenant
    return jwt.sign(payload, JWT_SECRET, { expiresIn: ACCESS_EXPIRY });
};

const verifyAccessToken = (token) => {
    if (!JWT_SECRET) throw new Error("Token Engine not initialized with secret");

    try {
        return jwt.verify(token, JWT_SECRET);
    } catch (err) {
        if (err.name === 'TokenExpiredError') {
            throw new Error('ACCESS_TOKEN_EXPIRED');
        }
        throw new Error('INVALID_TOKEN');
    }
};

const generateRefreshToken = () => {
    // Generate a secure, random opaque token
    return crypto.randomBytes(40).toString('hex');
};

const hashRefreshToken = (token) => {
    // We hash the refresh token before storing it in the DB.
    // SHA-256 is sufficient here because the token is randomly generated with high entropy.
    return crypto.createHash('sha256').update(token).digest('hex');
};

// For password reset: single-use opaque token
const generateResetToken = () => crypto.randomBytes(32).toString('hex');
const hashResetToken = (token) => crypto.createHash('sha256').update(token).digest('hex');

module.exports = {
    __init__,
    generateAccessToken,
    verifyAccessToken,
    generateRefreshToken,
    hashRefreshToken,
    generateResetToken,
    hashResetToken,
    get ACCESS_EXPIRY() { return ACCESS_EXPIRY; }
};
