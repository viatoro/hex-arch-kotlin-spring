package com.wtech.user.application.adapters

import com.wtech.user.domain.model.AuthToken

/**
 * Token Generator Port (Outbound)
 *
 * Interface for JWT token generation and validation.
 * Implemented by infrastructure layer (JWT adapter).
 */
interface TokenGenerator {
    /**
     * Generate JWT authentication token
     *
     * @param userId User ID to embed in token
     * @param expiresInHours Token expiration in hours
     * @return AuthToken with JWT string and expiration
     */
    suspend fun generateToken(userId: String, expiresInHours: Int = 24): AuthToken

    /**
     * Validate JWT token and extract user ID
     *
     * @param token JWT token string
     * @return User ID if valid, null if invalid or expired
     */
    suspend fun validateToken(token: String): String?
}
