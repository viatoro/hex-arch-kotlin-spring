package com.example.user.domain.model

/**
 * User Status Enum
 *
 * Represents the lifecycle status of a user account.
 */
enum class UserStatus {
    /**
     * User has registered but hasn't completed email verification
     */
    PENDING,

    /**
     * User account is active and can access protected endpoints
     */
    ACTIVE,

    /**
     * User account has been suspended (e.g., for policy violations)
     */
    SUSPENDED
}