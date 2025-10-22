package com.wtech.user.adapters.outbound.repositories.token

import com.wtech.user.domain.model.AuthToken
import software.amazon.awssdk.services.dynamodb.model.AttributeValue
import java.time.LocalDateTime

/**
 * Token Mapper
 *
 * Object for converting between domain AuthToken and DynamoDB TokenEntity.
 */
object TokenMapper {
    private const val PK = "pk"
    private const val SK = "sk"
    private const val ENTITY_TYPE = "entityType"
    private const val USER_ID = "userId"
    private const val TOKEN = "token"
    private const val EXPIRES_AT = "expiresAt"

    /**
     * Convert domain AuthToken to DynamoDB TokenEntity
     */
    fun tokenToEntity(authToken: AuthToken): TokenEntity = TokenEntity(
        pk = "USER#${authToken.userId}",
        sk = "TOKEN#${authToken.token.take(8)}", // Use token prefix as sort key
        entityType = "TOKEN",
        userId = authToken.userId,
        token = authToken.token,
        expiresAt = authToken.expiresAt.toString()
    )

    /**
     * Convert DynamoDB AttributeValue map to domain AuthToken
     */
    fun tokenFromDynamoDB(items: MutableMap<String, AttributeValue>): AuthToken? {
        return try {
            AuthToken(
                userId = items[USER_ID]?.s() ?: return null,
                token = items[TOKEN]?.s() ?: return null,
                expiresAt = LocalDateTime.parse(items[EXPIRES_AT]?.s() ?: return null)
            )
        } catch (_: Exception) {
            null
        }
    }

    /**
     * Convert TokenEntity to AttributeValue map for DynamoDB
     */
    fun tokenEntityToDynamoDB(entity: TokenEntity): MutableMap<String, AttributeValue> {
        return mutableMapOf(
            PK to AttributeValue.builder().s(entity.pk).build(),
            SK to AttributeValue.builder().s(entity.sk).build(),
            ENTITY_TYPE to AttributeValue.builder().s(entity.entityType).build(),
            USER_ID to AttributeValue.builder().s(entity.userId).build(),
            TOKEN to AttributeValue.builder().s(entity.token).build(),
            EXPIRES_AT to AttributeValue.builder().s(entity.expiresAt).build()
        )
    }
}
