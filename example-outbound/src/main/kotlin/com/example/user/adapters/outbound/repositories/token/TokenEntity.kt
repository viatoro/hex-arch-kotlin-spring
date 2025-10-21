package com.example.user.adapters.outbound.repositories.token

/**
 * DynamoDB Token Entity
 *
 * Represents authentication tokens stored in DynamoDB.
 * All properties have default values for no-arg constructor.
 */
data class TokenEntity(
    var pk: String = "",            // USER#<userId>
    var sk: String = "",            // TOKEN#<tokenId>
    var entityType: String = "TOKEN",
    var userId: String = "",
    var token: String = "",
    var expiresAt: String = ""
)
