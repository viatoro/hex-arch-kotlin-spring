package com.example.ecommerce.adapters.inbound.function

import com.amazonaws.services.lambda.runtime.events.APIGatewayProxyRequestEvent
import com.amazonaws.services.lambda.runtime.events.APIGatewayProxyResponseEvent
import com.fasterxml.jackson.databind.ObjectMapper
import com.example.ecommerce.application.repositories.ProductRepository
import org.springframework.http.HttpMethod
import org.springframework.stereotype.Component
import software.amazon.awssdk.http.HttpStatusCode
import java.util.function.Function

@Component
class GetProductByIdFunction(
    val productRepository: ProductRepository,
    val objectMapper: ObjectMapper,
): Function<APIGatewayProxyRequestEvent, APIGatewayProxyResponseEvent> {
    override fun apply(requestEvent: APIGatewayProxyRequestEvent): APIGatewayProxyResponseEvent {
        if (!requestEvent.httpMethod.equals(HttpMethod.GET.name())) {
            return APIGatewayProxyResponseEvent()
                .withStatusCode(HttpStatusCode.METHOD_NOT_ALLOWED)
                .withBody("Only GET method is supported")
        }
        try {
            val id = requestEvent.pathParameters["id"]!!
            val product = productRepository.findByID(id) ?: return APIGatewayProxyResponseEvent()
                .withStatusCode(HttpStatusCode.NOT_FOUND)
                .withBody("Product with id = $id not found")
            return APIGatewayProxyResponseEvent()
                .withStatusCode(HttpStatusCode.OK)
                .withBody(objectMapper.writeValueAsString(product))
        } catch (je: Exception) {
            je.printStackTrace()
            return APIGatewayProxyResponseEvent()
                .withStatusCode(HttpStatusCode.INTERNAL_SERVER_ERROR)
                .withBody("Internal Server Error :: " + je.message)
        }
    }
}