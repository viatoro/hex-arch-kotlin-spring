package com.wtech.user.adapters.inbound.function

import com.wtech.user.domain.value.AuthResponse
import com.wtech.user.domain.value.LoginRequest
import com.wtech.user.domain.value.RegisterUserRequest
import com.wtech.user.domain.value.UserResponse
import com.wtech.user.application.usecase.AuthenticateUserUseCase
import com.wtech.user.application.usecase.RegisterUserUseCase
import kotlinx.coroutines.runBlocking
import org.springframework.context.annotation.Bean
import org.springframework.context.annotation.Configuration
import org.springframework.messaging.Message
import org.springframework.messaging.support.MessageBuilder
import java.util.function.Function

/**
 * Authentication Function Configuration
 *
 * Defines Spring Cloud Function beans for public authentication endpoints.
 */
@Configuration
class AuthFunctionConfiguration(
    private val registerUserUseCase: RegisterUserUseCase,
    private val authenticateUserUseCase: AuthenticateUserUseCase
) {

    /**
     * Register User Function
     *
     * Public endpoint for user registration
     */
    @Bean
    fun registerUser(): Function<Message<RegisterUserRequest>, Message<UserResponse>> {
        return Function { message ->
            val result = runBlocking {
                registerUserUseCase.execute(message.payload)
            }

            if (result.isSuccess) {
                MessageBuilder.withPayload(result.getOrThrow()).build()
            } else {
                throw result.exceptionOrNull() ?: Exception("Registration failed")
            }
        }
    }

    /**
     * Login User Function
     *
     * Public endpoint for user authentication
     */
    @Bean
    fun loginUser(): Function<Message<LoginRequest>, Message<AuthResponse>> {
        return Function { message ->
            val result = runBlocking {
                authenticateUserUseCase.execute(message.payload)
            }

            if (result.isSuccess) {
                MessageBuilder.withPayload(result.getOrThrow()).build()
            } else {
                throw result.exceptionOrNull() ?: Exception("Authentication failed")
            }
        }
    }
}
