package com.wtech.ecommerce.adapters.inbound.function

import com.amazonaws.services.lambda.runtime.events.APIGatewayProxyRequestEvent
import com.amazonaws.services.lambda.runtime.events.APIGatewayProxyResponseEvent
import com.fasterxml.jackson.databind.ObjectMapper
import com.wtech.ecommerce.application.usecase.ProductUseCase
import com.wtech.ecommerce.domain.product.model.Product
import org.springframework.http.HttpMethod
import org.springframework.stereotype.Component
import software.amazon.awssdk.http.HttpStatusCode
import java.util.function.Function

@Component
class CreateProductFunction(
    val productUseCase: ProductUseCase,
    val objectMapper: ObjectMapper,
): Function<APIGatewayProxyRequestEvent, APIGatewayProxyResponseEvent> {
    override fun apply(requestEvent: APIGatewayProxyRequestEvent): APIGatewayProxyResponseEvent {
        if (!requestEvent.httpMethod.equals(HttpMethod.PUT.name())) {
            return APIGatewayProxyResponseEvent()
                .withStatusCode(HttpStatusCode.METHOD_NOT_ALLOWED)
                .withBody("Only PUT method is supported")
        }
        try {
            val id: String? = requestEvent.pathParameters["id"]
            val jsonPayload: String? = requestEvent.body
            val product = objectMapper.readValue(jsonPayload, Product::class.java)
            if (product != null) {
                if (product.id != id) {
                    return APIGatewayProxyResponseEvent()
                        .withStatusCode(HttpStatusCode.BAD_REQUEST)
                        .withBody("Product ID in the body does not match path parameter")
                }
                productUseCase.create(product)
            }
            return APIGatewayProxyResponseEvent()
                .withStatusCode(HttpStatusCode.CREATED)
                .withBody("Product with id = $id created")
        } catch (e: Exception) {
            e.printStackTrace()
            return APIGatewayProxyResponseEvent()
                .withStatusCode(HttpStatusCode.INTERNAL_SERVER_ERROR)
                .withBody("Internal Server Error :: " + e.message)
        }
    }
}