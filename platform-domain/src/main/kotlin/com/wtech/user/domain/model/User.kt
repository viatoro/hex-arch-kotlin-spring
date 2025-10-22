package com.wtech.user.domain.model

import java.time.LocalDateTime

/**
 * User Domain Entity
 *
 * Core business entity representing a user in the system.
 * Contains business logic for user operations.
 */
data class User(
    val id: String,
    val email: Email,
    val passwordHash: String,
    val name: String,
    val status: UserStatus,
    val createdAt: LocalDateTime,
    val updatedAt: LocalDateTime
) {
    /**
     * Business rule: Only ACTIVE users can access protected endpoints
     */
    fun canAccessProtectedEndpoints(): Boolean = status == UserStatus.ACTIVE

    /**
     * Update user profile information
     *
     * @param name New name for the user
     * @return Updated user with new name and updated timestamp
     */
    fun updateProfile(name: String): User = copy(
        name = name,
        updatedAt = LocalDateTime.now()
    )

    /**
     * Activate user account (after email verification)
     */
    fun activate(): User = copy(
        status = UserStatus.ACTIVE,
        updatedAt = LocalDateTime.now()
    )

    /**
     * Suspend user account
     */
    fun suspend(): User = copy(
        status = UserStatus.SUSPENDED,
        updatedAt = LocalDateTime.now()
    )
}