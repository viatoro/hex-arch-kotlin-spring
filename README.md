# Hex-Arch-Service

A reference implementation of **Hexagonal Architecture** (Ports & Adapters) using Kotlin, Spring Boot, and AWS Lambda with native compilation support via GraalVM.

## Table of Contents

- [Overview](#overview)
- [Architecture](#architecture)
- [Project Structure](#project-structure)
- [Tech Stack](#tech-stack)
- [Getting Started](#getting-started)
- [Module Details](#module-details)
- [Design Patterns](#design-patterns)
- [Development](#development)
- [Testing](#testing)
- [Deployment](#deployment)

## Overview

This project demonstrates a clean implementation of hexagonal architecture for building scalable, maintainable serverless applications. It features two example domains:

- **User Management**: Registration, authentication, profile management
- **E-commerce**: Product catalog management

The architecture enforces strict separation of concerns, making the business logic independent of external frameworks and infrastructure.

## Architecture

### Hexagonal Architecture Principles

```
┌─────────────────────────────────────────────────────────────┐
│                     Inbound Adapters                        │
│              (AWS Lambda, REST APIs, Events)                │
└────────────────────────┬────────────────────────────────────┘
                         │
                         ▼
┌─────────────────────────────────────────────────────────────┐
│                    Application Layer                        │
│                  (Use Cases / Ports)                        │
│  - RegisterUserUseCase    - AuthenticateUserUseCase        │
│  - ProductUseCase         - GetUserProfileUseCase          │
└────────────────────────┬────────────────────────────────────┘
                         │
                         ▼
┌─────────────────────────────────────────────────────────────┐
│                      Domain Layer                           │
│                   (Business Logic)                          │
│  - User, Email, Password    - Product                      │
│  - AuthToken, UserStatus    - Domain Rules                 │
└─────────────────────────────────────────────────────────────┘
                         │
                         ▼
┌─────────────────────────────────────────────────────────────┐
│                    Outbound Adapters                        │
│           (DynamoDB, External Services)                     │
└─────────────────────────────────────────────────────────────┘
```

### Key Concepts

- **Domain Layer**: Contains pure business logic with no external dependencies
- **Application Layer**: Orchestrates use cases using ports (interfaces)
- **Adapters**: Implement ports to connect with external systems
  - **Inbound**: AWS Lambda functions, REST controllers
  - **Outbound**: DynamoDB repositories, external API clients

## Project Structure

```
hex-arch-kotlin-spring-boot/
├── example-domain/              # Core business logic (no dependencies)
│   └── src/main/kotlin/com/example/
│       ├── ecommerce/
│       │   ├── application/
│       │   │   ├── repositories/    # Repository ports
│       │   │   └── usecase/         # Use cases
│       │   └── domain/
│       │       └── product/
│       │           └── model/       # Domain entities
│       └── user/
│           ├── application/
│           │   ├── adapters/        # Adapter ports
│           │   └── usecase/         # Use cases
│           └── domain/
│               ├── model/           # Domain entities
│               └── value/           # Value objects & DTOs
│
├── example-lambda/              # Inbound adapters (AWS Lambda)
│   └── src/main/kotlin/com/example/
│       ├── common/config/
│       ├── ecommerce/adapters/inbound/function/
│       └── user/adapters/inbound/function/
│
├── example-outbound/            # Outbound adapters (DynamoDB)
│   └── src/main/kotlin/com/example/
│       ├── ecommerce/adapters/outbound/repositories/
│       └── user/adapters/outbound/repositories/
│
├── buildSrc/                    # Build conventions
├── infrastructure/              # IaC (Terraform, CloudFormation, etc.)
└── gradle/                      # Gradle configuration
```

## Tech Stack

### Core

- **Language**: Kotlin 2.2.20
- **JDK**: 21
- **Framework**: Spring Boot 3.5.5
- **Build Tool**: Gradle with Kotlin DSL

### AWS

- **Compute**: AWS Lambda
- **Database**: DynamoDB
- **SDK**: AWS SDK for Java 2.33.0
- **Tracing**: AWS X-Ray 2.19.0

### Libraries

- **Logging**: Kotlin Logging 7.0.12
- **Spring Cloud**: 2025.0.0
- **Spring Cloud AWS**: 3.4.0

### Build & Quality

- **Native Image**: GraalVM 0.10.3
- **Code Quality**: Detekt 1.23.8, SpotBugs 6.0.4
- **Code Coverage**: JaCoCo 0.8.13
- **Testing**: Cucumber, JUnit
- **Code Formatting**: Spotless 8.0.0
- **Documentation**: Dokka 2.0.0

## Getting Started

### Prerequisites

- JDK 21
- Gradle 8.x (or use included wrapper)
- AWS CLI (for deployment)
- Docker (optional, for local testing)

### Build

```bash
# Build all modules
./gradlew build

# Build specific module
./gradlew :example-domain:build

# Run tests
./gradlew test

# Generate code coverage report
./gradlew jacocoTestReport

# Run code quality checks
./gradlew detekt spotbugsMain
```

### Native Image Build

```bash
# Build GraalVM native image
./gradlew :example-lambda:nativeCompile

# Build native Lambda deployment package
./gradlew :example-lambda:buildNativeLambda
```

## Module Details

### example-domain

**Pure business logic with zero external dependencies.**

#### User Domain

- **Entities**: `User`, `Email`, `Password`, `AuthToken`
- **Value Objects**: `UserStatus`, `UserProfile`
- **Use Cases**:
  - `RegisterUserUseCase`: User registration with validation
  - `AuthenticateUserUseCase`: User login and token generation
  - `GetUserProfileUseCase`: Retrieve user profile
  - `UpdateUserProfileUseCase`: Update user information
  - `DeleteUserUseCase`: Soft/hard delete user

#### E-commerce Domain

- **Entities**: `Product`
- **Use Cases**:
  - `ProductUseCase`: CRUD operations for products

### example-lambda

**Inbound adapters for AWS Lambda.**

- Lambda function handlers for user and product operations
- Request/response transformation
- Integration with Spring Cloud Function
- Native compilation optimizations

**Functions**:
- `CreateProductFunction`, `GetProductByIdFunction`, `GetAllProductsFunction`, `DeleteProductFunction`
- `AuthFunctionConfiguration`: Login, registration
- `UserProfileFunctionConfiguration`: Profile CRUD

### example-outbound

**Outbound adapters for external systems.**

#### DynamoDB Repositories

- **UserRepositoryImpl**: User persistence with GSI for email lookup
- **TokenRepositoryImpl**: Authentication token management
- **ProductRepositoryImpl**: Product catalog persistence

#### Mappers

- Object-based mappers for converting between domain and DynamoDB entities
- Type-safe attribute mapping
- Consistent naming conventions

**Features**:
- Single-table design patterns
- Composite keys (PK, SK)
- Global Secondary Indexes (GSI)
- Entity type discrimination

## Design Patterns

### Dependency Inversion

The domain layer defines ports (interfaces), and adapters implement them:

```kotlin
// Domain defines the port
interface UserRepository {
    suspend fun save(user: User): User
    suspend fun findById(id: String): User?
    suspend fun findByEmail(email: Email): User?
}

// Outbound adapter implements the port
@Repository
class UserRepositoryImpl(
    val dynamoDbClient: DynamoDbClient
) : UserRepository {
    // Implementation details
}
```

### Use Case Pattern

Each business operation is encapsulated in a use case:

```kotlin
class RegisterUserUseCase(
    private val userRepository: UserRepository,
    private val passwordEncoder: PasswordEncoder
) {
    suspend fun execute(request: RegisterUserRequest): UserResponse {
        // Business logic here
    }
}
```

### Mapper Objects

Centralized mapping logic using singleton objects:

```kotlin
object UserMapper {
    fun userToEntity(user: User): UserEntity
    fun userFromDynamoDB(items: MutableMap<String, AttributeValue>): User?
    fun userEntityToDynamoDB(entity: UserEntity): MutableMap<String, AttributeValue>
}
```

## Development

### Code Conventions

The project uses standardized Gradle conventions defined in `buildSrc/`:

- `kotlin-conventions.gradle.kts`: Kotlin compiler settings
- `spring-conventions.gradle.kts`: Spring Boot configuration
- `testing-conventions.gradle.kts`: Test framework setup
- `java-conventions.gradle.kts`: Java toolchain configuration
- `dokka-conventions.gradle.kts`: API documentation

### Code Quality

```bash
# Run all quality checks
./gradlew check

# Format code
./gradlew spotlessApply

# Generate documentation
./gradlew dokkaHtml
```

### Environment Variables

**User Service**:
- `USER_TABLE_NAME`: DynamoDB table for users
- `TOKEN_TABLE_NAME`: DynamoDB table for auth tokens

**Product Service**:
- `PRODUCT_TABLE_NAME`: DynamoDB table for products

## Testing

```bash
# Run unit tests
./gradlew test

# Run integration tests
./gradlew integrationTest

# Run Cucumber tests
./gradlew cucumber

# Generate test report
./gradlew testLogger
```

## Deployment

### AWS Lambda

```bash
# Build deployment package
./gradlew :example-lambda:buildZip

# Deploy using AWS SAM/CloudFormation
# (Add your deployment commands here)

# Deploy native image
./gradlew :example-lambda:buildNativeLambda
aws lambda update-function-code \
  --function-name my-function \
  --zip-file fileb://build/distributions/lambda-native.zip
```

### DynamoDB Tables

**User Table**:
```
PK: USER#<userId>
SK: METADATA
GSI1: gsi1pk=USER_EMAIL, gsi1sk=<email>
```

**Token Table**:
```
PK: USER#<userId>
SK: TOKEN#<tokenPrefix>
```

**Product Table**:
```
PK: PRODUCT#<productId>
SK: METADATA
```

## Contributing

1. Follow hexagonal architecture principles
2. Keep domain logic pure (no external dependencies)
3. Write tests for use cases and domain logic
4. Use type-safe Kotlin features
5. Document public APIs with KDoc

## License

See [LICENSE](LICENSE) file for details.

## Resources

- [Hexagonal Architecture](https://alistair.cockburn.us/hexagonal-architecture/)
- [Domain-Driven Design](https://martinfowler.com/tags/domain%20driven%20design.html)
- [AWS Lambda with Kotlin](https://docs.aws.amazon.com/lambda/latest/dg/lambda-kotlin.html)
- [GraalVM Native Image](https://www.graalvm.org/latest/reference-manual/native-image/)
