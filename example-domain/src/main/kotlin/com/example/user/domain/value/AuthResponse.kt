package com.example.user.domain.value

/**
 * Authentication Response DTO
 *
 * Data transfer object for authentication result.
 */
data class AuthResponse(
    val token: String,
    val expiresAt: String,
    val userId: String
)
