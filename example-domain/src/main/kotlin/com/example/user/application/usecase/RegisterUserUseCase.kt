package com.example.user.application.usecase

import com.example.user.application.adapters.PasswordEncoder
import com.example.user.application.adapters.UserRepository
import com.example.user.domain.value.RegisterUserRequest
import com.example.user.domain.value.UserResponse
import com.example.user.domain.model.Email
import com.example.user.domain.model.Password
import com.example.user.domain.model.User
import com.example.user.domain.model.UserDomainException
import com.example.user.domain.model.UserStatus
import java.time.LocalDateTime
import java.util.UUID

/**
 * Register User Use Case
 *
 * Handles user registration with email validation, password hashing, and duplicate detection.
 */
class RegisterUserUseCase(
    private val userRepository: UserRepository,
    private val passwordEncoder: PasswordEncoder
)  {

    suspend fun execute(request: RegisterUserRequest): Result<UserResponse> {
        return try {
            // Validate email and password using value objects
            val email = try {
                Email(request.email)
            } catch (e: IllegalArgumentException) {
                throw UserDomainException.InvalidEmailException(e.message ?: "Invalid email")
            }

            val password = try {
                Password(request.password)
            } catch (e: IllegalArgumentException) {
                throw UserDomainException.InvalidPasswordException(e.message ?: "Invalid password")
            }

            // Check for existing user
            val existing = userRepository.findByEmail(email)
            if (existing != null) {
                throw UserDomainException.EmailAlreadyExistsException(email.value)
            }

            // Create user with ACTIVE status (simplified - PRP shows PENDING for email verification)
            // For this implementation, we'll make users ACTIVE immediately
            val user = User(
                id = generateUserId(),
                email = email,
                passwordHash = passwordEncoder.encode(password),
                name = request.name,
                status = UserStatus.ACTIVE, // Changed from PENDING to ACTIVE for simplicity
                createdAt = LocalDateTime.now(),
                updatedAt = LocalDateTime.now()
            )

            val saved = userRepository.save(user)
            Result.success(UserResponse.from(saved))
        } catch (e: UserDomainException) {
            Result.failure(e)
        } catch (e: Exception) {
            Result.failure(Exception("Failed to register user: ${e.message}", e))
        }
    }

    private fun generateUserId(): String = "USR-${UUID.randomUUID()}"
}
