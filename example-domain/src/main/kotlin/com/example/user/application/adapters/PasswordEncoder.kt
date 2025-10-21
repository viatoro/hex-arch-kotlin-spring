package com.example.user.application.adapters

import com.example.user.domain.model.Password

/**
 * Password Encoder Port (Outbound)
 *
 * Interface for password hashing and verification.
 * Implemented by infrastructure layer (BCrypt adapter).
 */
interface PasswordEncoder {
    /**
     * Encode (hash) a password using BCrypt
     */
    suspend fun encode(password: Password): String

    /**
     * Verify if raw password matches encoded password
     */
    suspend fun matches(rawPassword: String, encodedPassword: String): Boolean
}
