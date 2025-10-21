package com.example.user.adapters.outbound.repositories.user

/**
 * DynamoDB User Entity
 *
 * Separate from domain User to handle DynamoDB-specific concerns.
 * All properties have default values to support no-arg constructor (required by DynamoDB).
 */
data class UserEntity(
    var pk: String = "",            // USER#<userId>
    var sk: String = "",            // METADATA
    var entityType: String = "USER",
    var userId: String = "",
    var email: String = "",
    var passwordHash: String = "",
    var name: String = "",
    var status: String = "",
    var createdAt: String = "",
    var updatedAt: String = "",
    // GSI attributes for email lookup
    var gsi1pk: String = "",        // USER_EMAIL
    var gsi1sk: String = ""         // <email>
)
