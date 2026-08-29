const bcrypt = require('bcryptjs');

let storageAdapter = null;

const __init__ = (injectedAdapter) => {
    if (!injectedAdapter) throw new Error("Storage adapter is required");
    storageAdapter = injectedAdapter;
};

// Hashes a plain text password (Async)
const hashPassword = async (password) => {
    // We are using bcrypt as the secure fallback as requested, argon2 requires node-gyp builds
    // which may complicate the "adapt to any project" requirement out of the box.
    const saltRounds = 12; // Sufficient secure default
    return await bcrypt.hash(password, saltRounds);
};

const createUser = async ({ identifier, password, metadata = {} }) => {
    if (!storageAdapter) throw new Error("Identity layer not initialized with storage adapter");
    if (!identifier || !password) throw new Error("Identifier and password are required");

    const existingUser = await storageAdapter.findUserByIdentifier(identifier);
    if (existingUser) {
        throw new Error('IDENTIFIER_IN_USE');
    }

    const passwordHash = await hashPassword(password);

    // Call the injected storage adapter
    return await storageAdapter.createUser(identifier, passwordHash, metadata);
};

const findUserByIdentifier = async (identifier) => {
    if (!storageAdapter) throw new Error("Identity layer not initialized");
    return await storageAdapter.findUserByIdentifier(identifier);
};

const findUserById = async (id) => {
    if (!storageAdapter) throw new Error("Identity layer not initialized");
    return await storageAdapter.findUserById(id);
};

const verifyPassword = async (user, plainTextPassword) => {
    if (!user || !user.password_hash) return false;
    return await bcrypt.compare(plainTextPassword, user.password_hash);
};

const changePassword = async (userId, currentPassword, newPassword) => {
    if (!storageAdapter) throw new Error("Identity layer not initialized");
    const user = await storageAdapter.findUserById(userId);
    if (!user) throw new Error("USER_NOT_FOUND");
    const isValid = await verifyPassword(user, currentPassword);
    if (!isValid) throw new Error("INVALID_CURRENT_PASSWORD");
    const newHash = await hashPassword(newPassword);
    return await storageAdapter.updatePassword(userId, newHash);
};

const updateUserMetadata = async (userId, metadata) => {
    if (!storageAdapter) throw new Error("Identity layer not initialized");
    return await storageAdapter.updateUserMetadata(userId, metadata);
};

const getAllUsers = async () => {
    if (!storageAdapter) throw new Error("Identity layer not initialized");
    return await storageAdapter.getAllUsers();
};

// Export identity functions
module.exports = {
    __init__,
    createUser,
    findUserByIdentifier,
    findUserById,
    verifyPassword,
    hashPassword,
    changePassword,
    updateUserMetadata,
    getAllUsers
};
