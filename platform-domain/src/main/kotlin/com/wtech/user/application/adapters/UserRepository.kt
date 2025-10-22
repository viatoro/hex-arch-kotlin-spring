package com.wtech.user.application.adapters

import com.wtech.user.domain.model.Email
import com.wtech.user.domain.model.User

/**
 * User Repository Port (Outbound)
 *
 * Interface for user persistence operations.
 * Implemented by infrastructure layer (DynamoDB adapter).
 */
interface UserRepository {
    /**
     * Save or update a user
     */
    suspend fun save(user: User): User

    /**
     * Find user by ID
     */
    suspend fun findById(id: String): User?

    /**
     * Find user by email (used for login)
     */
    suspend fun findByEmail(email: Email): User?

    /**
     * Delete user
     */
    suspend fun delete(id: String)
}
