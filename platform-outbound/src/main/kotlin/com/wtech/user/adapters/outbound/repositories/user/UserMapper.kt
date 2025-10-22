package com.wtech.user.adapters.outbound.repositories.user

import com.wtech.user.domain.model.Email
import com.wtech.user.domain.model.User
import com.wtech.user.domain.model.UserStatus
import software.amazon.awssdk.services.dynamodb.model.AttributeValue
import java.time.LocalDateTime

/**
 * User Mapper
 *
 * Object for converting between domain User and DynamoDB UserEntity.
 */
object UserMapper {
    private const val PK = "pk"
    private const val SK = "sk"
    private const val ENTITY_TYPE = "entityType"
    private const val USER_ID = "userId"
    private const val EMAIL = "email"
    private const val PASSWORD_HASH = "passwordHash"
    private const val NAME = "name"
    private const val STATUS = "status"
    private const val CREATED_AT = "createdAt"
    private const val UPDATED_AT = "updatedAt"
    private const val GSI1_PK = "gsi1pk"
    private const val GSI1_SK = "gsi1sk"

    /**
     * Convert domain User to DynamoDB UserEntity
     */
    fun userToEntity(user: User): UserEntity = UserEntity(
        pk = "USER#${user.id}",
        sk = "METADATA",
        entityType = "USER",
        userId = user.id,
        email = user.email.value,
        passwordHash = user.passwordHash,
        name = user.name,
        status = user.status.name,
        createdAt = user.createdAt.toString(),
        updatedAt = user.updatedAt.toString(),
        gsi1pk = "USER_EMAIL",
        gsi1sk = user.email.value
    )

    /**
     * Convert DynamoDB AttributeValue map to domain User
     */
    fun userFromDynamoDB(items: MutableMap<String, AttributeValue>): User? {
        return try {
            User(
                id = items[USER_ID]?.s() ?: return null,
                email = Email(items[EMAIL]?.s() ?: return null),
                passwordHash = items[PASSWORD_HASH]?.s() ?: return null,
                name = items[NAME]?.s() ?: return null,
                status = UserStatus.valueOf(items[STATUS]?.s() ?: return null),
                createdAt = LocalDateTime.parse(items[CREATED_AT]?.s() ?: return null),
                updatedAt = LocalDateTime.parse(items[UPDATED_AT]?.s() ?: return null)
            )
        } catch (_: Exception) {
            null
        }
    }

    /**
     * Convert UserEntity to AttributeValue map for DynamoDB
     */
    fun userEntityToDynamoDB(entity: UserEntity): MutableMap<String, AttributeValue> {
        return mutableMapOf(
            PK to AttributeValue.builder().s(entity.pk).build(),
            SK to AttributeValue.builder().s(entity.sk).build(),
            ENTITY_TYPE to AttributeValue.builder().s(entity.entityType).build(),
            USER_ID to AttributeValue.builder().s(entity.userId).build(),
            EMAIL to AttributeValue.builder().s(entity.email).build(),
            PASSWORD_HASH to AttributeValue.builder().s(entity.passwordHash).build(),
            NAME to AttributeValue.builder().s(entity.name).build(),
            STATUS to AttributeValue.builder().s(entity.status).build(),
            CREATED_AT to AttributeValue.builder().s(entity.createdAt).build(),
            UPDATED_AT to AttributeValue.builder().s(entity.updatedAt).build(),
            GSI1_PK to AttributeValue.builder().s(entity.gsi1pk).build(),
            GSI1_SK to AttributeValue.builder().s(entity.gsi1sk).build()
        )
    }
}
