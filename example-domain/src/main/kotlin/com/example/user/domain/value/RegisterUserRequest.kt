package com.example.user.domain.value

/**
 * Register User Request DTO
 *
 * Data transfer object for user registration.
 */
data class RegisterUserRequest(
    val email: String,
    val password: String,
    val name: String
)
