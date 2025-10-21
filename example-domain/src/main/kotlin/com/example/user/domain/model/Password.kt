package com.example.user.domain.model

/**
 * Password Value Object
 *
 * Provides type safety and validation for passwords.
 * Enforces password strength requirements.
 */
@JvmInline
value class Password(val value: String) {
    init {
        require(value.length >= 8) {
            "Password must be at least 8 characters"
        }
        require(value.any { it.isUpperCase() }) {
            "Password must contain at least one uppercase letter"
        }
        require(value.any { it.isLowerCase() }) {
            "Password must contain at least one lowercase letter"
        }
        require(value.any { it.isDigit() }) {
            "Password must contain at least one number"
        }
    }

    // Never expose raw password in toString
    override fun toString(): String = "Password(***)"
}