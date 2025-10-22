package com.wtech.user.adapters.inbound.function.communication

import com.wtech.user.domain.value.UpdateUserProfileRequest

/**
 * Update User Profile Request with Authentication
 */
data class UpdateUserProfileRequestWithAuth(
    val updateRequest: UpdateUserProfileRequest,
    val authToken: String
)
