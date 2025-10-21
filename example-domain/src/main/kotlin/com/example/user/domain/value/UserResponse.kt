package com.example.user.domain.value

import com.example.user.domain.model.User

/**
 * User Response DTO
 *
 * Data transfer object for user information.
 */
data class UserResponse(
    val id: String,
    val email: String,
    val name: String,
    val status: String,
    val createdAt: String
) {
    companion object {
        fun from(user: User) = UserResponse(
            id = user.id,
            email = user.email.value,
            name = user.name,
            status = user.status.name,
            createdAt = user.createdAt.toString()
        )
    }
}
