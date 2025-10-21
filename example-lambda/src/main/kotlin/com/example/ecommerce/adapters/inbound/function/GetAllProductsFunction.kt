package com.example.ecommerce.adapters.inbound.function

import com.amazonaws.services.lambda.runtime.events.APIGatewayProxyRequestEvent
import com.amazonaws.services.lambda.runtime.events.APIGatewayProxyResponseEvent
import com.fasterxml.jackson.core.JsonProcessingException
import com.fasterxml.jackson.databind.ObjectMapper
import com.example.ecommerce.application.repositories.ProductRepository
import org.springframework.http.HttpMethod
import org.springframework.stereotype.Component
import software.amazon.awssdk.http.HttpStatusCode
import java.util.function.Function


@Component
class GetAllProductsFunction(
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
            return APIGatewayProxyResponseEvent()
                .withStatusCode(HttpStatusCode.OK)
                .withBody(objectMapper.writeValueAsString(productRepository.getAll()))
        } catch (je: JsonProcessingException) {
            je.printStackTrace()
            return APIGatewayProxyResponseEvent()
                .withStatusCode(HttpStatusCode.INTERNAL_SERVER_ERROR)
                .withBody("Internal Server Error")
        }
    }
}