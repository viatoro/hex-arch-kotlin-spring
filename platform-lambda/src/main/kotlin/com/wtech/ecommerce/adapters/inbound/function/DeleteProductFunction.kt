package com.wtech.ecommerce.adapters.inbound.function

import com.amazonaws.services.lambda.runtime.events.APIGatewayProxyRequestEvent
import com.amazonaws.services.lambda.runtime.events.APIGatewayProxyResponseEvent
import com.wtech.ecommerce.application.repositories.ProductRepository
import org.springframework.http.HttpMethod
import org.springframework.stereotype.Component
import software.amazon.awssdk.http.HttpStatusCode
import java.util.function.Function


@Component
class DeleteProductFunction(
    val productRepository: ProductRepository,
): Function<APIGatewayProxyRequestEvent, APIGatewayProxyResponseEvent> {
    override fun apply(requestEvent: APIGatewayProxyRequestEvent): APIGatewayProxyResponseEvent {
        if (!requestEvent.httpMethod.equals(HttpMethod.DELETE.name())) {
            return APIGatewayProxyResponseEvent()
                .withStatusCode(HttpStatusCode.METHOD_NOT_ALLOWED)
                .withBody("Only DELETE method is supported")
        }
        try {
            val id: String = requestEvent.pathParameters["id"]!!
            productRepository.findByID(id) ?: return APIGatewayProxyResponseEvent()
                .withStatusCode(HttpStatusCode.NOT_FOUND)
                .withBody("Product with id = $id not found")
            productRepository.delete(id)
            return APIGatewayProxyResponseEvent()
                .withStatusCode(HttpStatusCode.OK)
                .withBody("Product with id = $id deleted")
        } catch (je: Exception) {
            je.printStackTrace()
            return APIGatewayProxyResponseEvent()
                .withStatusCode(HttpStatusCode.INTERNAL_SERVER_ERROR)
                .withBody("Internal Server Error :: " + je.message)
        }
    }
}