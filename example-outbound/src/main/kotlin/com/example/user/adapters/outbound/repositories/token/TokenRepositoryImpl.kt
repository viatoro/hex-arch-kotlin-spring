package com.example.user.adapters.outbound.repositories.token

import com.example.user.application.adapters.TokenRepository
import com.example.user.domain.model.AuthToken
import org.springframework.stereotype.Repository
import software.amazon.awssdk.services.dynamodb.DynamoDbClient
import software.amazon.awssdk.services.dynamodb.model.AttributeValue

@Repository
class TokenRepositoryImpl(
    val dynamoDbClient: DynamoDbClient
) : TokenRepository {
    companion object {
        val TOKEN_TABLE_NAME: String? = System.getenv("TOKEN_TABLE_NAME")
    }

    override suspend fun save(token: AuthToken): AuthToken {
        val entity = TokenMapper.tokenToEntity(token)
        dynamoDbClient.putItem {
            it.tableName(TOKEN_TABLE_NAME)
                .item(TokenMapper.tokenEntityToDynamoDB(entity))
        }
        return token
    }

    override suspend fun findByToken(token: String): AuthToken? {
        val scanResponse = dynamoDbClient.scan {
            it.tableName(TOKEN_TABLE_NAME)
                .filterExpression("#token = :tokenValue")
                .expressionAttributeNames(mapOf("#token" to "token"))
                .expressionAttributeValues(
                    mapOf(":tokenValue" to AttributeValue.builder().s(token).build())
                )
        }

        return scanResponse.items().firstOrNull()?.let { TokenMapper.tokenFromDynamoDB(it) }
    }

    override suspend fun findByUserId(userId: String): List<AuthToken> {
        val queryResponse = dynamoDbClient.query {
            it.tableName(TOKEN_TABLE_NAME)
                .keyConditionExpression("pk = :pk")
                .expressionAttributeValues(
                    mapOf(":pk" to AttributeValue.builder().s("USER#$userId").build())
                )
        }

        return queryResponse.items().mapNotNull { TokenMapper.tokenFromDynamoDB(it) }
    }

    override suspend fun delete(userId: String, tokenId: String) {
        dynamoDbClient.deleteItem {
            it.tableName(TOKEN_TABLE_NAME)
                .key(
                    mapOf(
                        "pk" to AttributeValue.builder().s("USER#$userId").build(),
                        "sk" to AttributeValue.builder().s("TOKEN#${tokenId.take(8)}").build()
                    )
                )
        }
    }
}
