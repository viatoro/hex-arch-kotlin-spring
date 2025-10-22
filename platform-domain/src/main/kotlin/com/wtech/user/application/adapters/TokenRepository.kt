package com.wtech.user.application.adapters

import com.wtech.user.domain.model.AuthToken

/**
 * Token Repository Port (Outbound)
 *
 * Interface for authentication token persistence.
 * Implemented by infrastructure layer (DynamoDB adapter).
 */
interface TokenRepository {
    /**
     * Save authentication token
     */
    suspend fun save(token: AuthToken): AuthToken

    /**
     * Find token by token string
     */
    suspend fun findByToken(token: String): AuthToken?

    /**
     * Find all tokens for a user
     */
    suspend fun findByUserId(userId: String): List<AuthToken>

    /**
     * Delete a specific token
     */
    suspend fun delete(userId: String, tokenId: String)
}
