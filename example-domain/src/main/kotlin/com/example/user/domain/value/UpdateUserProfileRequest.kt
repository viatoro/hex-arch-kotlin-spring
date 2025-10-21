package com.example.user.domain.value

/**
 * Update User Profile Request DTO
 *
 * Data transfer object for updating user profile.
 */
data class UpdateUserProfileRequest(
    val userId: String,
    val name: String,
    val bio: String = ""
)
