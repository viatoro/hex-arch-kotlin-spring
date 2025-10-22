package com.wtech.user.application.usecase

import com.wtech.user.application.adapters.UserRepository
import com.wtech.user.domain.model.UserDomainException

/**
 * Delete User Use Case
 *
 * Deletes a user account with authorization check.
 */
class DeleteUserUseCase(
    private val userRepository: UserRepository
) {

    suspend fun execute(userId: String, requestingUserId: String): Result<Unit> {
        return try {
            // Authorization: users can only delete their own account
            if (userId != requestingUserId) {
                throw UserDomainException.UnauthorizedException(
                    "Cannot delete another user's account"
                )
            }

            // Verify user exists
            val user = userRepository.findById(userId)
                ?: throw UserDomainException.UserNotFoundException(userId)

            // Delete user
            userRepository.delete(user.id)

            Result.success(Unit)
        } catch (e: UserDomainException) {
            Result.failure(e)
        } catch (e: Exception) {
            Result.failure(Exception("Failed to delete user: ${e.message}", e))
        }
    }
}
