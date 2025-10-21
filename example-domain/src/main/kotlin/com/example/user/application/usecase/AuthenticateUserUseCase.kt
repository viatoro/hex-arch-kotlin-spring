package com.example.user.application.usecase

import com.example.user.application.adapters.PasswordEncoder
import com.example.user.application.adapters.TokenGenerator
import com.example.user.application.adapters.UserRepository
import com.example.user.domain.value.AuthResponse
import com.example.user.domain.value.LoginRequest
import com.example.user.domain.model.Email
import com.example.user.domain.model.UserDomainException

/**
 * Authenticate User Use Case
 *
 * Handles user authentication with password verification and JWT token generation.
 */
class AuthenticateUserUseCase(
    private val userRepository: UserRepository,
    private val passwordEncoder: PasswordEncoder,
    private val tokenGenerator: TokenGenerator
)  {

    suspend fun execute(request: LoginRequest): Result<AuthResponse> {
        return try {
            // Validate email format
            val email = try {
                Email(request.email)
            } catch (e: IllegalArgumentException) {
                throw UserDomainException.InvalidEmailException(e.message ?: "Invalid email")
            }

            // Find user by email
            val user = userRepository.findByEmail(email)
                ?: throw Exception("Invalid credentials") // Don't reveal if email exists

            // Verify password
            if (!passwordEncoder.matches(request.password, user.passwordHash)) {
                throw Exception("Invalid credentials") // Generic error message for security
            }

            // Check user status
            if (!user.canAccessProtectedEndpoints()) {
                throw UserDomainException.UserNotActiveException(user.id)
            }

            // Generate JWT token (24 hours expiration)
            val authToken = tokenGenerator.generateToken(user.id, expiresInHours = 24)

            Result.success(
                AuthResponse(
                    token = authToken.token,
                    expiresAt = authToken.expiresAt.toString(),
                    userId = user.id
                )
            )
        } catch (e: UserDomainException) {
            Result.failure(e)
        } catch (e: Exception) {
            Result.failure(Exception("Authentication failed: ${e.message}", e))
        }
    }
}
