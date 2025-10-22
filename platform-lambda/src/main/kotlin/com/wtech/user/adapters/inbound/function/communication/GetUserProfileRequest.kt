package com.wtech.user.adapters.inbound.function.communication

/**
 * Get User Profile Request with Authentication
 */
data class GetUserProfileRequest(
    val userId: String,
    val authToken: String
)
