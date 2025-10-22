package com.wtech.user.adapters.outbound.repositories.user

import com.wtech.user.application.adapters.UserRepository
import com.wtech.user.domain.model.Email
import com.wtech.user.domain.model.User
import org.springframework.stereotype.Repository
import software.amazon.awssdk.services.dynamodb.DynamoDbClient
import software.amazon.awssdk.services.dynamodb.model.AttributeValue

@Repository
class UserRepositoryImpl(
    val dynamoDbClient: DynamoDbClient
) : UserRepository {
    override suspend fun save(user: User): User {
        val entity = UserMapper.userToEntity(user)
        dynamoDbClient.putItem {
            it.tableName(USER_TABLE_NAME)
                .item(UserMapper.userEntityToDynamoDB(entity))
        }
        return user
    }

    override suspend fun findById(id: String): User? {
        val itemResponse = dynamoDbClient.getItem {
            it.tableName(USER_TABLE_NAME)
                .key(
                    mapOf(
                        "pk" to AttributeValue.builder().s("USER#$id").build(),
                        "sk" to AttributeValue.builder().s("METADATA").build()
                    )
                )
        }

        return if (itemResponse.hasItem()) {
            UserMapper.userFromDynamoDB(itemResponse.item())
        } else {
            null
        }
    }

    override suspend fun findByEmail(email: Email): User? {
        val queryResponse = dynamoDbClient.query {
            it.tableName(USER_TABLE_NAME)
                .indexName(GSI1_INDEX_NAME)
                .keyConditionExpression("gsi1pk = :gsi1pk AND gsi1sk = :gsi1sk")
                .expressionAttributeValues(
                    mapOf(
                        ":gsi1pk" to AttributeValue.builder().s("USER_EMAIL").build(),
                        ":gsi1sk" to AttributeValue.builder().s(email.value).build()
                    )
                )
        }

        return queryResponse.items().firstOrNull()?.let { UserMapper.userFromDynamoDB(it) }
    }

    override suspend fun delete(id: String) {
        dynamoDbClient.deleteItem {
            it.tableName(USER_TABLE_NAME)
                .key(
                    mapOf(
                        "pk" to AttributeValue.builder().s("USER#$id").build(),
                        "sk" to AttributeValue.builder().s("METADATA").build()
                    )
                )
        }
    }

    companion object {
        val USER_TABLE_NAME: String? = System.getenv("USER_TABLE_NAME")
        const val GSI1_INDEX_NAME = "GSI1"
    }


}
