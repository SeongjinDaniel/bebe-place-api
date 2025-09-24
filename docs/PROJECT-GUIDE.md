# PROJECT-GUIDE.md - BeBe Place API Project Guide

*This document is optimized for Claude Code usage and provides comprehensive project structure, conventions, and development guidelines for AI-assisted development.*

Current project structure and architectural patterns for the BeBe Place API (육아용품 거래 플랫폼).

## Project Overview

**Project**: BeBe Place API - 육아용품 거래 플랫폼 백엔드 API  
**Architecture**: Hexagonal Architecture (Clean Architecture)  
**Language**: Kotlin with Spring Boot  
**Database**: PostgreSQL with JPA/Hibernate + JOOQ  
**Query Library**: JOOQ for dynamic queries and complex SQL operations  
**Authentication**: JWT-based authentication  

## Directory Structure

### Root Structure
```
bebe-place-api/
├── build.gradle.kts           # Gradle build configuration (includes JOOQ)
├── docker-compose.yml         # Local development environment
├── docs/                      # Project documentation
│   ├── api.md                 # API documentation
│   ├── architecture-decision.md # Architecture decisions
│   └── context-map.md         # Domain context mapping
├── src/main/kotlin/com/bebeplace/bebeplaceapi/
├── src/main/generated/         # JOOQ generated classes (auto-generated)
└── src/test/kotlin/com/bebeplace/bebeplaceapi/
```

### Core Application Structure
```
src/main/kotlin/com/bebeplace/bebeplaceapi/
├── BebePlaceApiApplication.kt  # Spring Boot main application
├── config/                     # Configuration classes
│   ├── JpaConfig.kt           # JPA configuration
│   ├── SecurityConfig.kt      # Security configuration
│   ├── JwtConfig.kt           # JWT configuration
│   ├── JwtService.kt          # JWT service implementation
│   └── JwtAuthenticationFilter.kt # JWT authentication filter
├── common/                     # Shared components
└── [domain-modules]/          # Domain-specific modules
```

### Domain Module Structure (Hexagonal Architecture)

Each domain follows consistent hexagonal architecture pattern:

```
[domain-name]/
├── application/               # Application Layer
│   ├── dto/                   # Data Transfer Objects
│   ├── port/                  # Interface definitions
│   │   ├── input/             # Inbound ports (use cases)
│   │   └── output/            # Outbound ports (repositories)
│   ├── service/               # Application services
│   └── usecase/               # Use case implementations
├── domain/                    # Domain Layer (Business Logic)
│   ├── event/                 # Domain events
│   ├── model/                 # Domain models (entities, value objects)
│   ├── repository/            # Repository interfaces
│   └── service/               # Domain services
└── infrastructure/            # Infrastructure Layer
    ├── external/              # External service integrations
    ├── persistence/           # Data persistence
    │   ├── entity/            # JPA entities
    │   ├── mapper/            # Domain ↔ Entity mappers
    │   └── *JpaRepository.kt  # Repository implementations
    ├── messaging/             # Message handling (if applicable)
    └── web/                   # REST controllers
```

## Implemented Domains

### 1. User Domain (`user/`)
**Purpose**: 사용자 관리, 인증, 프로필, 아기 정보, 지역 설정

**Key Models**:
- `User` - 메인 사용자 aggregate root
- `UserProfile` - 사용자 프로필 정보
- `Baby` - 아기 정보 (성별, 생년월일, 관심 카테고리)
- `UserRegion` - 사용자 지역 설정 (최대 3개)
- `TrustScore` - 신뢰도 점수

**Key Features**:
- JWT 기반 인증 (로그인/토큰 갱신)
- 사용자 등록 및 프로필 관리
- 아기 정보 관리 (성별, 생년월일, 관심 물품)
- 지역 설정 관리 (우선순위별 최대 3개)
- 신뢰도 점수 시스템

### 2. Product Domain (`product/`)
**Purpose**: 상품 관리 (구조만 준비됨)

### 3. Transaction Domain (`transaction/`)
**Purpose**: 거래 관리 (구조만 준비됨)

### 4. Payment Domain (`payment/`)
**Purpose**: 결제 관리 (구조만 준비됨)

### 5. Chat Domain (`chat/`)
**Purpose**: 채팅 시스템 (구조만 준비됨)

## Common Components

### `common/` Structure
```
common/
├── config/                    # Common configurations
│   └── JpaAuditingConfig.kt   # JPA auditing setup
├── domain/                    # Base domain components
│   ├── AggregateRoot.kt       # Base aggregate root class
│   ├── DomainEvent.kt         # Domain event base
│   └── ValueObject.kt         # Value object base
├── exception/                 # Exception handling
│   ├── BusinessException.kt   # Business logic exceptions
│   └── ValidationException.kt # Validation exceptions
├── infrastructure/            # Common infrastructure
│   └── BaseEntity.kt          # JPA base entity with auditing
├── types/                     # Common value objects
│   └── Email.kt               # Email value object
├── util/                      # Utility classes
│   └── RequestContextUtil.kt  # Request context management
└── web/                       # Web layer commons
    ├── ApiResponse.kt         # Standard API response wrapper
    ├── ErrorCode.kt           # Centralized error codes
    ├── GlobalExceptionHandler.kt # Global exception handling
    └── PagedResponse.kt       # Paginated response wrapper
```

## Database Structure

### Database Migrations (`src/main/resources/db/migration/`)
- `V20241228_120000__Create_users_table.sql` - Users table creation
- `V20241228_140000__Add_user_birth_date_and_baby_region_tables.sql` - Baby/region tables
- `V20241228_150000__Add_last_login_and_token_refresh_columns.sql` - Login tracking
- `V20241228_160000__Change_baby_and_region_id_to_sequence.sql` - ID sequences

### Key Database Features
- PostgreSQL with BIGSERIAL sequences for Baby/UserRegion IDs
- UUID v7 for User IDs (using UuidCreator)
- JPA Auditing for automatic timestamp/user tracking
- IP address tracking for audit trail

## JOOQ Integration

### Overview
JOOQ (Java Object Oriented Querying) is used alongside JPA for complex queries, dynamic filtering, and performance-critical operations.

### Architecture Decision
- **JPA**: Simple CRUD operations, entity relationships, auditing
- **JOOQ**: Complex queries, dynamic filtering, cursor pagination, performance optimization
- **Hybrid Approach**: Best of both worlds

### Code Generation Setup

**Generated Classes Location**: `src/main/generated/com/bebeplace/bebeplaceapi/jooq/`

**Build Configuration** (`build.gradle.kts`):
```kotlin
// JOOQ Plugin
id("org.jooq.jooq-codegen-gradle") version "3.19.15"

// Dependencies
implementation("org.springframework.boot:spring-boot-starter-jooq")
implementation("org.jooq:jooq:3.19.15")

// Code Generation Configuration
jooq {
    configuration {
        generator {
            database {
                name = "org.jooq.meta.postgres.PostgresDatabase"
                inputSchema = "public"
            }
            target {
                packageName = "com.bebeplace.bebeplaceapi.jooq"
                directory = "src/main/generated"
            }
        }
    }
}
```

### Usage Patterns

**1. Dynamic Query Repository Pattern**:
```kotlin
// Interface
interface ProductCustomRepository {
    fun findWithDynamicFilter(
        filter: ProductListFilter?,
        cursorParams: CursorParams
    ): List<ProductEntity>
}

// JOOQ Implementation
@Repository
class ProductCustomRepositoryImpl : ProductCustomRepository {
    @PersistenceContext
    private lateinit var entityManager: EntityManager

    override fun findWithDynamicFilter(
        filter: ProductListFilter?,
        cursorParams: CursorParams
    ): List<ProductEntity> {
        val dsl = DSL.using(entityManager.unwrap(Connection::class.java))
        
        val conditions = mutableListOf<Condition>()
        filter?.status?.let { conditions.add(PRODUCT.STATUS.eq(it)) }
        filter?.sellerId?.let { conditions.add(PRODUCT.SELLER_ID.eq(it)) }
        filter?.category?.let { conditions.add(PRODUCT.CATEGORY.eq(it)) }
        
        // Cursor pagination
        cursorParams.createdAt?.let { createdAt ->
            conditions.add(
                PRODUCT.CREATED_AT.lt(createdAt)
                .or(PRODUCT.CREATED_AT.eq(createdAt)
                    .and(PRODUCT.ID.lt(cursorParams.id)))
            )
        }
        
        return dsl.selectFrom(PRODUCT)
            .where(conditions)
            .orderBy(PRODUCT.CREATED_AT.desc(), PRODUCT.ID.desc())
            .limit(cursorParams.pageSize)
            .fetchInto(ProductEntity::class.java)
    }
}
```

**2. Repository Integration Pattern**:
```kotlin
// JPA Repository extends JOOQ Custom Repository
interface ProductJpaRepository : JpaRepository<ProductEntity, UUID>, ProductCustomRepository

// Usage in Implementation
@Repository
class ProductRepositoryImpl(
    private val jpaRepository: ProductJpaRepository
) : ProductRepository {
    
    override fun findProductsWithCursor(
        cursor: String?,
        size: Int,
        filter: ProductListFilter?
    ): ProductListResult {
        val productCursor = cursor?.let { ProductCursor.decode(it) }
        val cursorParams = CursorParams.create(
            productCursor?.createdAt,
            productCursor?.id,
            size
        )
        
        // Use JOOQ for dynamic query
        val entities = jpaRepository.findWithDynamicFilter(filter, cursorParams)
        
        // Use JPA for domain mapping
        val products = entities.map { loadProductWithAgeGroups(it) }
        
        return ProductListResult(products, hasNext, nextCursor)
    }
}
```

### Development Workflow

**1. Schema Changes**:
```bash
# Update Flyway migration
# Run migration
./gradlew flywayMigrate

# Regenerate JOOQ classes
./gradlew jooqCodegen

# Build and test
./gradlew build
```

**2. Adding New Dynamic Queries**:
1. Create/update custom repository interface
2. Implement with JOOQ DSL
3. Use generated table/column references (e.g., `PRODUCT.STATUS`)
4. Test with integration tests

### Best Practices

**Do's**:
- Use JOOQ for complex WHERE conditions and dynamic queries
- Use generated table constants (e.g., `PRODUCT`, `USER`)
- Leverage JOOQ's type safety for complex joins
- Use `Condition` objects for reusable query parts
- Combine with JPA entities for domain mapping

**Don'ts**:
- Don't use JOOQ for simple CRUD operations (use JPA)
- Don't bypass JPA auditing for domain entities
- Don't write raw SQL strings (use JOOQ DSL)
- Don't forget to regenerate classes after schema changes

### Performance Benefits
- **Dynamic Queries**: No N+1 problems, precise SQL control
- **Cursor Pagination**: Efficient large dataset handling
- **Index Optimization**: Direct index usage hints
- **Join Optimization**: Explicit join strategies

## JPA + JOOQ 하이브리드 전략

### 기술 선택 가이드

**JPA 사용 케이스** (간편함과 ORM 기능 활용):
- ✅ **간단한 CRUD**: `findById()`, `save()`, `delete()`
- ✅ **단일 조건 쿼리**: `findByStatus()`, `findBySellerId()`
- ✅ **연관 관계**: `@OneToMany`, `@ManyToOne`, Lazy Loading
- ✅ **벌크 업데이트**: `@Modifying` 어노테이션 활용
- ✅ **트랜잭션 관리**: Spring 자동 통합

```kotlin
// ✅ JPA 사용 예시
interface ProductJpaRepository : JpaRepository<ProductEntity, UUID> {
    fun findBySellerId(sellerId: UUID): List<ProductEntity>  // 단순 조건
    
    @Modifying @Query("UPDATE ProductEntity p SET p.viewCount = p.viewCount + 1 WHERE p.id = :id")
    fun increaseViewCount(@Param("id") id: UUID): Int  // 효율적 업데이트
}
```

**JOOQ 사용 케이스** (성능과 타입 안전성 중시):
- ⚡ **동적 WHERE 조건**: 필터 조합, 검색 쿼리
- ⚡ **복잡한 JOIN**: 다중 테이블 조인, 서브쿼리
- ⚡ **집계 쿼리**: GROUP BY, 윈도우 함수, 통계
- ⚡ **커서 페이지네이션**: 대용량 데이터 효율적 처리
- ⚡ **성능 최적화**: 정확한 SQL 제어, 인덱스 힌트

```kotlin
// ⚡ JOOQ 사용 예시
@Repository
class ProductCustomRepositoryImpl(private val dsl: DSLContext) : ProductCustomRepository {
    
    fun findWithDynamicFilter(filter: ProductListFilter?, cursorParams: CursorParams): List<ProductEntity> {
        val conditions = mutableListOf<Condition>()
        
        // 동적 조건 생성
        filter?.status?.let { conditions.add(status.eq(it.name)) }
        filter?.sellerId?.let { conditions.add(sellerId.eq(it)) }
        
        return dsl.selectFrom(products)
            .where(conditions)  // 타입 안전한 동적 쿼리
            .orderBy(createdAt.desc(), id.desc())
            .limit(cursorParams.pageSize)
            .fetch { record -> /* 매핑 */ }
    }
}
```

### 하이브리드 아키텍처 패턴

**Repository 통합**:
```kotlin
// 인터페이스 통합
interface ProductJpaRepository : JpaRepository<ProductEntity, UUID>, ProductCustomRepository

// 도메인 Repository 구현
@Repository  
class ProductRepositoryImpl(
    private val jpaRepository: ProductJpaRepository  // JPA + JOOQ 통합
) : ProductRepository {
    
    override fun save(product: Product) = jpaRepository.save(entity).toDomain()  // JPA
    override fun findWithCursor(filter) = jpaRepository.findWithDynamicFilter(filter)  // JOOQ
}
```

### 성능 모니터링

**JOOQ 쿼리 로깅**:
```kotlin
companion object {
    private val logger = LoggerFactory.getLogger(ProductCustomRepositoryImpl::class.java)
}

override fun findWithDynamicFilter(...): List<ProductEntity> {
    logger.debug("🔍 JOOQ 동적 쿼리 시작 - filter: {}, pageSize: {}", filter, pageSize)
    
    val executionTime = measureTimeMillis {
        // JOOQ 쿼리 실행
    }
    
    logger.info("⚡ JOOQ 쿼리 완료 - {}ms, {} 건 조회", executionTime, entities.size)
    return entities
}
```

### 트랜잭션 통합

JPA와 JOOQ는 동일한 DataSource를 공유하므로 하나의 트랜잭션 내에서 함께 사용 가능:

```kotlin
@Transactional
class ProductService {
    fun complexOperation() {
        jpaRepository.save(entity)           // JPA 저장
        customRepository.findComplex(filter) // JOOQ 조회
        // 동일한 트랜잭션에서 롤백/커밋됨
    }
}
```

### 성능 비교 가이드

| 쿼리 유형 | JPA | JOOQ | 권장 |
|-----------|-----|------|------|
| 단일 엔티티 CRUD | ⚡ 빠름 | ⚠️ 보통 | **JPA** |
| 동적 필터링 | ❌ 복잡 | ⚡ 빠름 | **JOOQ** |
| 연관관계 매핑 | ⚡ 자동 | ❌ 수동 | **JPA** |
| 집계/통계 쿼리 | ❌ 어려움 | ⚡ 쉬움 | **JOOQ** |
| 대용량 배치 | ⚠️ 보통 | ⚡ 빠름 | **JOOQ** |

## Architectural Patterns

### 1. Hexagonal Architecture (Ports & Adapters)
- **Domain Layer**: Pure business logic, framework-agnostic
- **Application Layer**: Use cases, application services, DTOs
- **Infrastructure Layer**: Framework-specific implementations

### 2. Domain-Driven Design (DDD)
- Aggregate roots with clear boundaries
- Domain events for cross-aggregate communication
- Value objects for type safety
- Domain services for complex business logic

### 3. CQRS Elements
- Separate read/write models where beneficial
- Query optimization with JOIN FETCH

### 4. Repository Pattern
- Domain repository interfaces in domain layer
- JPA implementations in infrastructure layer
- Domain mappers for entity ↔ model conversion

## Key Conventions

### Naming Conventions
- **Classes**: PascalCase (`UserJpaRepository`)
- **Files**: Match class names (`UserJpaRepository.kt`)
- **Packages**: kebab-case for directories, camelCase for packages
- **Database**: snake_case (`user_regions`)

### Code Organization
- Domain models are pure Kotlin classes (no JPA annotations)
- JPA entities in infrastructure/persistence/entity/
- Mappers handle domain ↔ entity conversion
- Use cases orchestrate application flow
- Domain services contain business logic

### UUID Standards (CRITICAL)
**ALWAYS use UuidCreator for UUID operations:**

**✅ CORRECT Usage:**
```text
// Import UuidCreator
import com.github.f4b6a3.uuid.UuidCreator

// Generate new UUIDs (UUID v7 - time-ordered)
val newId = UuidCreator.getTimeOrderedEpoch()

// Parse UUID strings
val parsedId = UuidCreator.fromString("uuid-string")
```

**❌ INCORRECT Usage - Never use java.util.UUID:**
```text
// DON'T import java.util.UUID for generation/parsing
import java.util.UUID

// DON'T use these methods
val badId = UUID.randomUUID()
val badParsed = UUID.fromString(string)
```

**Benefits of UuidCreator:**
- **UUID v7**: Time-ordered UUIDs for better database performance
- **Sortable**: Natural sorting by creation time
- **Index-friendly**: Better B-tree index performance compared to UUID v4
- **Compatible**: Fully compatible with java.util.UUID type

### String Interpolation Convention
**ALWAYS use `${}` syntax for string interpolation in strings:**

**✅ CORRECT Usage:**
```kotlin
val message = "User ${user.name} has ${user.points} points"
val query = "SELECT * FROM users WHERE id = ${userId}"
val logMessage = "Processing ${action} for user ${user.id}"
```

**❌ INCORRECT Usage - Never use string concatenation:**
```kotlin
val message = "User " + user.name + " has " + user.points + " points"
val query = "SELECT * FROM users WHERE id = " + userId
```

### Database Conventions
- **UUID v7** for main entity IDs (Users, Products, etc.) using `UuidCreator.getTimeOrderedEpoch()`
- **BIGSERIAL** sequences for child entities (Baby, UserRegion)
- Flyway migrations with timestamp naming: `V{YYYYMMDD}_{HHMMSS}__{Description}.sql`

## Security & Authentication

### JWT Implementation
- Access token + Refresh token pattern
- **UuidCreator for UUID v7** - better performance and time-ordered IDs
- SecurityContext-based user authentication via `RequestContextUtil.getCurrentUserId()`
- Request context tracking for audit
- IP address logging in BaseEntity

### Security Features
- Spring Security with JWT filter
- Password encoding with BCrypt
- CORS configuration
- Request/response audit logging

## Testing Structure

### Test Organization
```
src/test/kotlin/
├── BebePlaceApiApplicationTests.kt    # Integration tests
├── TestConfiguration.kt              # Test configurations
├── common/types/                      # Common type tests
├── config/                           # Configuration tests
└── user/                             # User domain tests
    ├── application/usecase/          # Use case tests
    ├── domain/model/                 # Domain model tests
    └── infrastructure/web/           # Controller integration tests
```

## Development Guidelines

### Code Quality
- Kotlin idioms and conventions
- Immutable data classes where possible
- Null safety with proper nullable types
- Extension functions for common operations

### Error Handling
- Centralized ErrorCode management
- Business exceptions for domain violations
- Global exception handler for consistent responses
- Validation at appropriate layers

### Performance
- JOIN FETCH for N+1 query prevention
- Sequence-based IDs for better performance
- Connection pooling and JPA optimizations
- Efficient mapper implementations

## Claude Code Integration Notes

### Development Workflow
- **File Navigation**: Use glob patterns like `**/*Service.kt` or `user/**/*.kt` for searching
- **Code References**: Reference locations as `file_path:line_number` (e.g., `user/domain/model/User.kt:25`)
- **Testing**: Run tests with `./gradlew test` or specific test classes
- **Build**: Use `./gradlew build` for full builds, `./gradlew bootRun` to start locally

### Common File Patterns
- **Domain Models**: `src/main/kotlin/com/bebeplace/bebeplaceapi/{domain}/domain/model/*.kt`
- **Use Cases**: `src/main/kotlin/com/bebeplace/bebeplaceapi/{domain}/application/usecase/*.kt`  
- **Controllers**: `src/main/kotlin/com/bebeplace/bebeplaceapi/{domain}/infrastructure/web/*.kt`
- **Repositories**: `src/main/kotlin/com/bebeplace/bebeplaceapi/{domain}/infrastructure/persistence/*Repository.kt`
- **Tests**: `src/test/kotlin/com/bebeplace/bebeplaceapi/{domain}/**/*Test.kt`

### Architecture Context for AI
- **Always follow hexagonal architecture**: Keep domain layer pure, infrastructure adapters separate
- **Event-driven patterns**: Use domain events (`user/domain/event/`) for cross-aggregate communication
- **Mapper pattern**: Convert between domain models and JPA entities using mappers in `infrastructure/persistence/mapper/`
- **UUID Standards**: **ALWAYS use UuidCreator** - never `java.util.UUID` for generation/parsing
- **Security context**: JWT-based auth with user context available via `RequestContextUtil.getCurrentUserId()`

## Future Expansion

### Prepared Domains
- Product domain for 상품 관리
- Transaction domain for 거래 처리  
- Payment domain for 결제 시스템
- Chat domain for 실시간 채팅

### Scalability Considerations
- Microservice-ready domain boundaries
- Event-driven architecture support
- Database per domain capability
- API versioning support

---

## Quick Reference for Claude Code

### Key Commands
```bash
# Build and test
./gradlew clean build
./gradlew test
./gradlew bootRun --args='--spring.profiles.active=local'

# Infrastructure
docker-compose up -d postgres redis minio
docker-compose down

# Database migration status
./gradlew flywayInfo
```

### Important Files for Context
- **Main Application**: `src/main/kotlin/com/bebeplace/bebeplaceapi/BebePlaceApiApplication.kt`
- **Security Config**: `src/main/kotlin/com/bebeplace/bebeplaceapi/config/SecurityConfig.kt:34`
- **Global Exception Handler**: `src/main/kotlin/com/bebeplace/bebeplaceapi/common/web/GlobalExceptionHandler.kt:18`
- **Database Migrations**: `src/main/resources/db/migration/*.sql`
- **Application Config**: `src/main/resources/application.yml`

### Domain Entry Points
- **User Domain**: `src/main/kotlin/com/bebeplace/bebeplaceapi/user/infrastructure/web/UserController.kt`
- **Auth Domain**: `src/main/kotlin/com/bebeplace/bebeplaceapi/user/infrastructure/web/AuthController.kt`

*Last updated for Claude Code compatibility: 2025-09-03*