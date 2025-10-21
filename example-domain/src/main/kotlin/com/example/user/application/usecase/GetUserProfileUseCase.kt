package com.example.user.application.usecase

import com.example.user.application.adapters.UserRepository
import com.example.user.domain.value.UserProfileResponse
import com.example.user.domain.model.UserDomainException

/**
 * Get User Profile Use Case
 *
 * Retrieves user profile information.
 */
class GetUserProfileUseCase(
    private val userRepository: UserRepository
)  {

    suspend fun execute(userId: String, requestingUserId: String): Result<UserProfileResponse> {
        return try {
            // Find user by ID
            val user = userRepository.findById(userId)
                ?: throw UserDomainException.UserNotFoundException(userId)

            Result.success(UserProfileResponse.from(user))
        } catch (e: UserDomainException) {
            Result.failure(e)
        } catch (e: Exception) {
            Result.failure(Exception("Failed to get user profile: ${e.message}", e))
        }
    }
}
