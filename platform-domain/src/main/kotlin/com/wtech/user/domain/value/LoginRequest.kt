package com.wtech.user.domain.value

/**
 * Login Request DTO
 *
 * Data transfer object for user authentication.
 */
data class LoginRequest(
    val email: String,
    val password: String
)
