package com.wtech.ecommerce.adapters.outbound.repositories.product

import com.wtech.ecommerce.domain.product.model.Product
import software.amazon.awssdk.services.dynamodb.model.AttributeValue
import java.math.BigDecimal
import java.util.Map

object ProductMapper {
    private const val PK = "PK"
    private const val NAME = "name"
    private const val PRICE = "price"

    fun productFromDynamoDB(items: MutableMap<String, AttributeValue>): Product {
        return Product(
            items[PK]?.s() ?: throw IllegalArgumentException("Missing PK"),
            items[NAME]?.s() ?: throw IllegalArgumentException("Missing name"),
            BigDecimal(items[PRICE]?.n() ?: throw IllegalArgumentException("Missing price"))
        )
    }

    fun productToDynamoDb(product: Product): MutableMap<String, AttributeValue> {
        return Map.of<String, AttributeValue>(
            PK,
            AttributeValue.builder().s(product.id).build(),
            NAME,
            AttributeValue.builder().s(product.name).build(),
            PRICE,
            AttributeValue.builder().n(product.price.toString()).build()
        )
    }
}
