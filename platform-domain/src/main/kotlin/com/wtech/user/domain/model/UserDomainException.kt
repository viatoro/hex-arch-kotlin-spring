package com.wtech.user.domain.model

/**
 * Domain Exceptions
 *
 * Sealed class hierarchy for domain-specific exceptions.
 * These represent business rule violations.
 */
sealed class UserDomainException(message: String) : Exception(message) {

    /**
     * Thrown when password validation fails
     */
    class InvalidPasswordException(message: String) : UserDomainException(message)

    /**
     * Thrown when email validation fails
     */
    class InvalidEmailException(message: String) : UserDomainException(message)

    /**
     * Thrown when user account is not in ACTIVE status
     */
    class UserNotActiveException(userId: String) : UserDomainException(
        "User $userId is not active and cannot access protected endpoints"
    )

    /**
     * Thrown when user is not found
     */
    class UserNotFoundException(userId: String) : UserDomainException("User $userId not found")

    /**
     * Thrown when email is already registered
     */
    class EmailAlreadyExistsException(email: String) : UserDomainException(
        "Email $email is already registered"
    )

    /**
     * Thrown when authorization fails
     */
    class UnauthorizedException(message: String) : UserDomainException(message)
}