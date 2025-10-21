package com.example.user.domain.model

import java.time.LocalDateTime

/**
 * Authentication Token Domain Entity
 *
 * Represents a JWT authentication token with expiration.
 */
data class AuthToken(
    val userId: String,
    val token: String,
    val expiresAt: LocalDateTime
) {
    /**
     * Check if token has expired
     *
     * @return true if current time is after expiration time
     */
    fun isExpired(): Boolean = LocalDateTime.now().isAfter(expiresAt)

    /**
     * Check if token is valid (not expired)
     */
    fun isValid(): Boolean = !isExpired()
}