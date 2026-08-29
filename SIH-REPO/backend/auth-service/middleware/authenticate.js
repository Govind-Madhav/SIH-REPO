const tokenEngine = require('../core/token');
const sessionEngine = require('../core/session');
const claimsEngine = require('../core/claims');

// Helper to extract token from standard Bearer header
const extractToken = (req) => {
    if (req.headers.authorization && req.headers.authorization.split(' ')[0] === 'Bearer') {
        return req.headers.authorization.split(' ')[1];
    } else if (req.query && req.query.token) {
        return req.query.token;
    }
    return null;
};

// Build context for policies/claims based on the request
// You can make this even more extensible later
const buildContext = (req) => {
    return {
        requestIp: req.headers['x-forwarded-for'] || req.socket?.remoteAddress,
        route: req.originalUrl,
        method: req.method,
        tenant: req.headers['x-tenant-id'] || null,
    };
};

const authenticate = (options = { trustJwtClaims: false }) => {
    return async (req, res, next) => {
        try {
            const token = extractToken(req);

            if (!token) {
                return res.status(401).json({ error: 'UNAUTHORIZED_MISSING_TOKEN' });
            }

            // 1. Verify Access Token Crypto & Expiry
            let payload;
            try {
                payload = tokenEngine.verifyAccessToken(token);
            } catch (err) {
                return res.status(401).json({ error: err.message }); // e.g., 'ACCESS_TOKEN_EXPIRED'
            }

            const { sub: userId, sid: sessionId } = payload;

            // 2. Build Request Context
            const context = buildContext(req);

            // 3. Resolve Claims (Fresh resolution on every request, or trust token payload. 
            let claims;
            if (options.trustJwtClaims && payload.claims) {
                // Completely stateless route for high performance
                claims = payload.claims;
            } else {
                // Stateful route for high-security immediate revocation
                claims = await claimsEngine.resolveClaims({ userId, sessionId, context });
            }

            // 4. Attach to Request (Extensible Context)
            req.identity = { id: userId };
            req.session = { id: sessionId };
            req.claims = claims;
            req.context = context;

            next();

        } catch (err) {
            console.error('[Auth Middleware Error]', err);
            return res.status(500).json({ error: 'INTERNAL_SERVER_ERROR' });
        }
    };
};

module.exports = authenticate;
