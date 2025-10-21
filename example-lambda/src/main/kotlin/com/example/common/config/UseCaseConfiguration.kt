package com.example.common.config

import com.example.user.application.adapters.PasswordEncoder
import com.example.user.application.adapters.TokenGenerator
import com.example.user.application.adapters.UserRepository
import com.example.user.application.usecase.AuthenticateUserUseCase
import com.example.user.application.usecase.DeleteUserUseCase
import com.example.user.application.usecase.GetUserProfileUseCase
import com.example.user.application.usecase.RegisterUserUseCase
import com.example.user.application.usecase.UpdateUserProfileUseCase
import org.springframework.context.annotation.Bean
import org.springframework.context.annotation.Configuration

/**
 * Use Case Configuration
 *
 * Creates beans for all use cases (application layer).
 */
@Configuration
class UseCaseConfiguration {

    @Bean
    fun registerUserUseCase(
        userRepository: UserRepository,
        passwordEncoder: PasswordEncoder
    ) = RegisterUserUseCase(userRepository, passwordEncoder)

    @Bean
    fun authenticateUserUseCase(
        userRepository: UserRepository,
        passwordEncoder: PasswordEncoder,
        tokenGenerator: TokenGenerator
    ) = AuthenticateUserUseCase(userRepository, passwordEncoder, tokenGenerator)

    @Bean
    fun getUserProfileUseCase(
        userRepository: UserRepository
    ) = GetUserProfileUseCase(userRepository)

    @Bean
    fun updateUserProfileUseCase(
        userRepository: UserRepository
    ) = UpdateUserProfileUseCase(userRepository)

    @Bean
    fun deleteUserUseCase(
        userRepository: UserRepository
    ) = DeleteUserUseCase(userRepository)
}