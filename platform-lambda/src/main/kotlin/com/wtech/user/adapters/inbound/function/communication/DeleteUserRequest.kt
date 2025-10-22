package com.wtech.user.adapters.inbound.function.communication

/**
 * Delete User Request with Authentication
 */
data class DeleteUserRequest(
    val userId: String,
    val authToken: String
)
