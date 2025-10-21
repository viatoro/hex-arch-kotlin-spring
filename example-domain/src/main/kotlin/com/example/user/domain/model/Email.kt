package com.example.user.domain.model

/**
 * Email Value Object
 *
 * Provides type safety and validation for email addresses.
 * Immutable and validates format on construction.
 */
@JvmInline
value class Email(val value: String) {
    init {
        require(value.matches(EMAIL_REGEX)) {
            "Invalid email format: $value"
        }
    }

    companion object {
        private val EMAIL_REGEX = Regex("^[A-Za-z0-9+_.-]+@[A-Za-z0-9.-]+\\.[A-Za-z]{2,}$")
    }

    override fun toString(): String = value
}