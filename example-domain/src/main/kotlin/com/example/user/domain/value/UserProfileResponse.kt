package com.example.user.domain.value

import com.example.user.domain.model.User


/**
 * User Profile Response DTO
 *
 * Data transfer object for user profile information.
 */
data class UserProfileResponse(
    val id: String,
    val email: String,
    val name: String,
    val status: String
) {
    companion object {
        fun from(user: User) = UserProfileResponse(
            id = user.id,
            email = user.email.value,
            name = user.name,
            status = user.status.name
        )
    }
}
