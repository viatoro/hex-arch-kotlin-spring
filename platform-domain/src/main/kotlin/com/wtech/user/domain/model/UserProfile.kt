package com.wtech.user.domain.model

/**
 * User Profile Domain Entity
 *
 * Extended profile information for a user.
 * This is separate from User to allow for future expansion.
 */
data class UserProfile(
    val userId: String,
    val bio: String = "",
    val avatarUrl: String = "",
    val preferences: Map<String, String> = emptyMap()
) {
    /**
     * Update bio
     */
    fun withBio(bio: String): UserProfile = copy(bio = bio)

    /**
     * Update avatar URL
     */
    fun withAvatarUrl(url: String): UserProfile = copy(avatarUrl = url)

    /**
     * Update or add a preference
     */
    fun withPreference(key: String, value: String): UserProfile = copy(
        preferences = preferences + (key to value)
    )
}