package com.example.user.adapters.inbound.function.communication

import com.example.user.domain.value.UpdateUserProfileRequest

/**
 * Update User Profile Request with Authentication
 */
data class UpdateUserProfileRequestWithAuth(
    val updateRequest: UpdateUserProfileRequest,
    val authToken: String
)
