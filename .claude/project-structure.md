# BeBe Place API - Project Structure for Claude Code

*Optimized project context for AI-assisted development*

## Quick Project Context

**Language**: Kotlin with Spring Boot  
**Architecture**: Hexagonal Architecture (Clean Architecture)  
**Database**: PostgreSQL with JPA/Hibernate  
**Auth**: JWT-based authentication  

## Domain Structure

### User Domain (`user/`)
- **Models**: User, UserProfile, Baby, UserRegion, TrustScore
- **Features**: JWT auth, user registration, baby info, region settings, trust scoring
- **Key Files**:
  - `user/domain/model/User.kt` - Main user aggregate root
  - `user/application/usecase/RegisterUserUseCase.kt` - User registration
  - `user/infrastructure/web/AuthController.kt` - Authentication endpoints
  - `user/infrastructure/web/UserController.kt` - User management endpoints

### Common Components (`common/`)
- **Base Classes**: AggregateRoot, BaseEntity, ValueObject
- **Exceptions**: BusinessException, ValidationException  
- **Web**: ApiResponse, ErrorCode, GlobalExceptionHandler
- **Types**: Email value object
- **Util**: RequestContextUtil for audit context

## Key File Patterns

### Domain Structure (Hexagonal)
```
{domain}/
├── application/dto/          # Request/Response DTOs
├── application/usecase/      # Use case implementations
├── domain/model/            # Pure domain models
├── domain/repository/       # Repository interfaces
├── infrastructure/persistence/entity/     # JPA entities
├── infrastructure/persistence/mapper/     # Domain ↔ Entity mappers
└── infrastructure/web/      # REST controllers
```

### Search Patterns
- Domain models: `**/domain/model/*.kt`
- Use cases: `**/application/usecase/*.kt`
- Controllers: `**/infrastructure/web/*.kt`
- Repositories: `**/infrastructure/persistence/*Repository.kt`
- Tests: `src/test/kotlin/**/*Test.kt`

## Architecture Rules

### Hexagonal Architecture
- **Domain Layer**: Pure business logic, no framework dependencies
- **Application Layer**: Use cases, DTOs, application services
- **Infrastructure Layer**: JPA entities, REST controllers, external adapters

### Key Conventions
- Domain models are pure Kotlin (no JPA annotations)
- JPA entities in `infrastructure/persistence/entity/`
- Mappers handle domain ↔ entity conversion
- Domain events for cross-aggregate communication
- Repository pattern with interfaces in domain layer

## Database & Migration

### Migration Files
- Location: `src/main/resources/db/migration/`
- Naming: `V{YYYYMMDD}_{HHMMSS}__{Description}.sql`
- Current: User tables, baby/region relationships, audit columns

### ID Strategies
- **Users**: UUID v7 (UuidCreator)
- **Child entities**: BIGSERIAL sequences (Baby, UserRegion)

## Security & Auth

### JWT Implementation
- Access + Refresh token pattern
- Security config: `config/SecurityConfig.kt:34`
- JWT service: `config/JwtService.kt`
- Request context: `common/util/RequestContextUtil.kt:15`

## Development Commands

```bash
# Build & Run
./gradlew clean build
./gradlew bootRun --args='--spring.profiles.active=local'

# Testing  
./gradlew test
./gradlew test --tests "*RegisterUserUseCaseTest*"

# Infrastructure
docker-compose up -d postgres redis minio
```

## Common Entry Points

### Controllers
- **Auth**: `user/infrastructure/web/AuthController.kt` - Login, refresh
- **User**: `user/infrastructure/web/UserController.kt` - Profile, baby, region management

### Core Services
- **User Registration**: `user/application/usecase/RegisterUserUseCase.kt:25`
- **Authentication**: `user/domain/service/AuthenticationService.kt:18`
- **JWT Processing**: `config/JwtService.kt:45`

### Exception Handling
- **Global Handler**: `common/web/GlobalExceptionHandler.kt:18`
- **Error Codes**: `common/web/ErrorCode.kt`
- **Business Exceptions**: `common/exception/BusinessException.kt`

## Testing Structure
- **Integration**: `*ApplicationTests.kt`
- **Use Cases**: `**/application/usecase/*Test.kt`
- **Domain Models**: `**/domain/model/*Test.kt`
- **Controllers**: `**/infrastructure/web/*Test.kt`

---
*Context optimized for Claude Code assistance - Updated 2025-08-29*