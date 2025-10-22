package com.wtech.ecommerce.adapters.outbound.repositories.product

import com.wtech.ecommerce.application.repositories.ProductRepository
import com.wtech.ecommerce.domain.product.model.Product
import org.springframework.stereotype.Repository
import software.amazon.awssdk.services.dynamodb.DynamoDbClient
import software.amazon.awssdk.services.dynamodb.model.AttributeValue

@Repository
class ProductRepositoryImpl(
    val dynamoDbClient: DynamoDbClient
) : ProductRepository {
    companion object {
        val PRODUCT_TABLE_NAME: String? = System.getenv("PRODUCT_TABLE_NAME")
        const val LIMIT_ROW = 20
    }

    override fun findByID(id: String): Product? {
        val itemResponse = dynamoDbClient.getItem { builder ->
            builder
                .key(
                    mapOf(
                        "PK" to AttributeValue.builder().s(id).build(),
                    )
                )
                .tableName(PRODUCT_TABLE_NAME)
        }

        return if (itemResponse.hasItem()) {
            ProductMapper.productFromDynamoDB(itemResponse.item())
        } else {
            null
        }
    }

    override fun put(product: Product) {
        dynamoDbClient.putItem {
            it.tableName(PRODUCT_TABLE_NAME)
                .item(ProductMapper.productToDynamoDb(product))
        }
    }

    override fun delete(id: String) {
        dynamoDbClient.deleteItem {
            it.tableName(PRODUCT_TABLE_NAME)
                .key(
                    mapOf(
                        "PK" to AttributeValue.builder().s(id).build(),
                    )
                )
        }
    }

    override fun getAll(): List<Product> {
        val scanResponse = dynamoDbClient.scan {
            it.tableName(PRODUCT_TABLE_NAME)
                .limit(LIMIT_ROW)
        }
        return scanResponse.items().map {
            ProductMapper.productFromDynamoDB(it)
        }
    }
}
