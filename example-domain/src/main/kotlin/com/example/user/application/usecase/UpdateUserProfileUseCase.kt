package com.example.user.application.usecase

import com.example.user.application.adapters.UserRepository
import com.example.user.domain.value.UpdateUserProfileRequest
import com.example.user.domain.value.UserResponse
import com.example.user.domain.model.UserDomainException

/**
 * Update User Profile Use Case
 *
 * Updates user profile information with authorization check.
 */
class UpdateUserProfileUseCase(
    private val userRepository: UserRepository
) {

    suspend fun execute(
        request: UpdateUserProfileRequest,
        requestingUserId: String
    ): Result<UserResponse> {
        return try {
            // Authorization: users can only update their own profile
            if (request.userId != requestingUserId) {
                throw UserDomainException.UnauthorizedException(
                    "Cannot update another user's profile"
                )
            }

            // Find user
            val user = userRepository.findById(request.userId)
                ?: throw UserDomainException.UserNotFoundException(request.userId)

            // Update profile using domain logic
            val updated = user.updateProfile(request.name)
            val saved = userRepository.save(updated)

            Result.success(UserResponse.from(saved))
        } catch (e: UserDomainException) {
            Result.failure(e)
        } catch (e: Exception) {
            Result.failure(Exception("Failed to update user profile: ${e.message}", e))
        }
    }
}
