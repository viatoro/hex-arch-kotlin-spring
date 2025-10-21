package com.example.user.adapters.inbound.function

import com.example.user.application.adapters.TokenGenerator
import com.example.user.application.usecase.DeleteUserUseCase
import com.example.user.application.usecase.GetUserProfileUseCase
import com.example.user.application.usecase.UpdateUserProfileUseCase
import com.example.user.domain.value.UserProfileResponse
import com.example.user.domain.value.UserResponse
import com.example.user.adapters.inbound.function.communication.DeleteUserRequest
import com.example.user.adapters.inbound.function.communication.GetUserProfileRequest
import com.example.user.adapters.inbound.function.communication.UpdateUserProfileRequestWithAuth
import kotlinx.coroutines.runBlocking
import org.springframework.context.annotation.Bean
import org.springframework.context.annotation.Configuration
import org.springframework.messaging.Message
import org.springframework.messaging.support.MessageBuilder
import java.util.function.Function

/**
 * User Profile Function Configuration
 *
 * Defines Spring Cloud Function beans for protected user profile endpoints.
 */
@Configuration
class UserProfileFunctionConfiguration(
    private val getUserProfileUseCase: GetUserProfileUseCase,
    private val updateUserProfileUseCase: UpdateUserProfileUseCase,
    private val deleteUserUseCase: DeleteUserUseCase,
    private val tokenGenerator: TokenGenerator
) {

    /**
     * Get User Profile Function
     *
     * Protected endpoint - requires JWT authentication
     */
    @Bean
    fun getUserProfile(): Function<Message<GetUserProfileRequest>, Message<UserProfileResponse>> {
        return Function { message ->
            val request = message.payload
            val userId = extractUserIdFromToken(request.authToken)

            val result = runBlocking {
                getUserProfileUseCase.execute(request.userId, userId)
            }

            if (result.isSuccess) {
                MessageBuilder.withPayload(result.getOrThrow()).build()
            } else {
                throw result.exceptionOrNull() ?: Exception("Get profile failed")
            }
        }
    }

    /**
     * Update User Profile Function
     *
     * Protected endpoint - requires JWT authentication
     */
    @Bean
    fun updateUserProfile(): Function<Message<UpdateUserProfileRequestWithAuth>, Message<UserResponse>> {
        return Function { message ->
            val request = message.payload
            val userId = extractUserIdFromToken(request.authToken)

            val result = runBlocking {
                updateUserProfileUseCase.execute(request.updateRequest, userId)
            }

            if (result.isSuccess) {
                MessageBuilder.withPayload(result.getOrThrow()).build()
            } else {
                throw result.exceptionOrNull() ?: Exception("Update failed")
            }
        }
    }

    /**
     * Delete User Function
     *
     * Protected endpoint - requires JWT authentication
     */
    @Bean
    fun deleteUser(): Function<Message<DeleteUserRequest>, Message<Unit>> {
        return Function { message ->
            val request = message.payload
            val requestingUserId = extractUserIdFromToken(request.authToken)

            val result = runBlocking {
                deleteUserUseCase.execute(request.userId, requestingUserId)
            }

            if (result.isSuccess) {
                MessageBuilder.withPayload(Unit).build()
            } else {
                throw result.exceptionOrNull() ?: Exception("Delete failed")
            }
        }
    }

    /**
     * Extract user ID from JWT token
     */
    private fun extractUserIdFromToken(authToken: String): String {
        val token = authToken.removePrefix("Bearer ")
        return runBlocking {
            tokenGenerator.validateToken(token)
                ?: throw Exception("Invalid or expired token")
        }
    }
}
