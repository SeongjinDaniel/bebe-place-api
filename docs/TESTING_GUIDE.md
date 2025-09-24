# 베베플레이스 API 테스트 가이드

## 개요

베베플레이스 API의 테스트 작성 및 실행 가이드입니다. 이 프로젝트는 **도메인 중심 테스트 접근법**을 채택하여 비즈니스 로직의 정확성과 동시성 안전성을 보장합니다.

## UUID 생성 가이드

### Time-Ordered UUID 사용
베베플레이스 프로젝트에서는 **시간 순서가 보장된 UUID v7**을 사용합니다:

```kotlin
import com.github.f4b6a3.uuid.UuidCreator

// ✅ 올바른 방법
val productId = UuidCreator.getTimeOrderedEpoch()
val userId = UuidCreator.getTimeOrderedEpoch()

// ❌ 사용하지 않음
val productId = UUID.randomUUID()
```

**장점**:
- **시간 순서 보장**: 생성 시간 순으로 정렬 가능
- **데이터베이스 성능**: B-Tree 인덱스 최적화
- **디버깅 편의성**: 생성 시간 추적 가능

## 테스트 철학

### 1. 도메인 중심 테스트
- **비즈니스 규칙 검증**: 도메인 로직이 올바르게 동작하는지 확인
- **실제 사용자 시나리오**: 고객과 판매자의 실제 행동 패턴 반영
- **의미 있는 테스트명**: `@DisplayName`으로 비즈니스 의도 명확화

### 2. 동시성 안전성 보장
- **Race Condition 방지**: 다중 스레드 환경에서의 안전성 검증
- **원자적 연산 검증**: 데이터베이스 레벨의 일관성 확인
- **실제 트래픽 시뮬레이션**: 대량 동시 요청 상황 테스트

## 테스트 구조

### 디렉토리 구조
```
src/test/kotlin/
├── com/bebeplace/bebeplaceapi/
│   ├── product/
│   │   ├── application/
│   │   │   ├── dto/           # DTO 변환 및 검증 테스트
│   │   │   └── usecase/       # 비즈니스 로직 테스트
│   │   ├── domain/
│   │   │   └── model/         # 도메인 모델 테스트
│   │   └── infrastructure/
│   │       └── web/           # 컨트롤러 통합 테스트
│   └── user/                  # 사용자 도메인 테스트
```

### 테스트 분류
1. **Unit Tests**: 도메인 모델, UseCase 단위 테스트
2. **Integration Tests**: 컨트롤러, 데이터베이스 통합 테스트  
3. **Concurrency Tests**: 동시성 안전성 테스트

## 동시성 테스트 작성 가이드

### 기본 구조

```kotlin
import com.github.f4b6a3.uuid.UuidCreator

@DisplayName("상품 조회 UseCase 동시성 테스트")
class GetProductUseCaseConcurrencyTest {
    
    private val productRepository = mockk<ProductRepository>()
    private val productImageRepository = mockk<ProductImageRepository>()
    private lateinit var getProductUseCase: GetProductUseCase

    @BeforeEach
    fun setUp() {
        getProductUseCase = GetProductUseCase(productRepository, productImageRepository)
    }
    
    @Test
    @DisplayName("동시에 여러 고객이 같은 상품을 조회할 때 조회수가 정확히 증가해야 한다")
    fun shouldIncreaseViewCountAccuratelyWhenMultipleCustomersViewProductConcurrently() {
        // Given: 비즈니스 상황 설명
        // When: 동시성 시나리오 실행
        // Then: 예상 결과 검증
    }
}
```

### 핵심 패턴

#### 1. 도메인 중심 Given-When-Then
```kotlin
// Given: 특정 상품이 존재하고, 100명의 고객이 동시에 조회하는 상황
val productId = UUID.randomUUID()
val product = createTestProduct(productId)

// 상품 조회 시 해당 상품이 반환되도록 설정
every { productRepository.findById(any()) } returns product
// 조회수 증가 연산이 정상적으로 수행되도록 설정  
every { productRepository.increaseViewCount(any()) } returns 1
```

#### 2. 멀티스레드 테스트 실행
```kotlin
// When: 100개의 스레드가 동시에 상품을 조회
val threadCount = 100
val executor = Executors.newFixedThreadPool(threadCount)

val futures = (1..threadCount).map {
    CompletableFuture.runAsync({
        getProductUseCase.execute(productId)
    }, executor)
}

// 모든 스레드의 작업 완료를 대기
CompletableFuture.allOf(*futures.toTypedArray()).join()
executor.shutdown()
```

#### 3. 비즈니스 규칙 검증
```kotlin
// Then: 원자적 연산으로 인해 조회수가 정확히 100번 증가해야 함
verify(exactly = threadCount) { productRepository.increaseViewCount(ProductId.of(productId)) }
verify(exactly = threadCount) { productRepository.findById(ProductId.of(productId)) }
```

## 테스트 작성 규칙

### 1. 명명 규칙
- **클래스명**: `{도메인}{기능}Test` (예: `GetProductUseCaseConcurrencyTest`)
- **메소드명**: 영어로 작성된 의도 중심 명명 (예: `shouldIncreaseViewCountAccuratelyWhenMultipleCustomersViewProductConcurrently`)
- **@DisplayName**: 비즈니스 관점의 한글 상황 설명

### 2. 주석 작성 원칙
```kotlin
// Given: 비즈니스 상황을 구체적으로 설명
val productId = UuidCreator.getTimeOrderedEpoch() // 특정 상품
val sellerId = UuidCreator.getTimeOrderedEpoch() // 해당 상품의 판매자

// When: 실제 사용자의 행동 패턴 설명  
val threadCount = 50 // 판매자가 50번 본인 상품을 조회 (관리 목적)

// Then: 기대하는 비즈니스 결과와 그 이유 설명
// 판매자의 본인 상품 조회는 조회수에 영향을 주지 않아야 함 (비즈니스 규칙)
```

### 3. 테스트 데이터 생성
```kotlin
/**
 * 테스트용 상품 도메인 객체 생성
 * 
 * @param productId 상품 식별자
 * @param sellerId 판매자 식별자 (기본값: 랜덤 UUID)
 * @return 테스트에 사용할 Product 도메인 객체
 */
private fun createTestProduct(
    productId: UUID,
    sellerId: UUID = UuidCreator.getTimeOrderedEpoch()
): Product {
    return Product.create(
        id = ProductId.of(productId),
        sellerId = sellerId,
        title = "테스트 상품",
        category = ProductCategory.TOYS_EDUCATIONAL,
        price = Money(BigDecimal("10000")),
        shippingInfo = ShippingInfo.separate(Money(BigDecimal("3000"))),
        description = "테스트 상품 설명",
        productType = ProductType.USED,
        condition = ProductCondition.EXCELLENT
    )
}
```

## 비즈니스 시나리오별 테스트 예시

### 1. 고객 상품 조회 시나리오
```kotlin
@Test
@DisplayName("동시에 여러 고객이 같은 상품을 조회할 때 조회수가 정확히 증가해야 한다")
fun shouldIncreaseViewCountAccuratelyWhenMultipleCustomersViewProductConcurrently()
```
- **비즈니스 규칙**: 고객이 상품을 조회하면 조회수가 증가
- **동시성 이슈**: Race Condition으로 인한 조회수 누락 방지
- **검증 내용**: 원자적 연산으로 정확한 조회수 증가

### 2. 판매자 상품 조회 시나리오  
```kotlin
@Test
@DisplayName("판매자가 본인 상품을 조회할 때는 조회수가 증가하지 않아야 한다")
fun shouldNotIncreaseViewCountWhenSellerViewsOwnProduct()
```
- **비즈니스 규칙**: 판매자의 본인 상품 조회는 조회수에 영향 없음
- **목적**: 관리 및 수정을 위한 조회와 실제 고객 관심도 구분
- **검증 내용**: 조회수 증가 메소드가 호출되지 않음

### 3. 단일 조회 완성도 시나리오
```kotlin
@Test  
@DisplayName("단일 고객의 상품 조회 시 모든 데이터가 올바르게 조회되어야 한다")
fun shouldRetrieveAllDataCorrectlyWhenSingleCustomerViewsProduct()
```
- **비즈니스 플로우**: 상품 정보 → 조회수 증가 → 이미지 정보
- **완성도 검증**: 모든 단계가 정확히 한 번씩 실행
- **데이터 일관성**: 연관된 모든 정보의 정상 조회

## 테스트 도구 및 설정

### 사용 기술 스택
- **테스트 프레임워크**: JUnit 5
- **Mocking**: MockK (Kotlin 친화적)
- **비동기 처리**: CompletableFuture
- **Spring Boot**: @SpringBootTest, @WebMvcTest
- **데이터베이스**: H2 (테스트용)

### Gradle 설정
```kotlin
dependencies {
    testImplementation("org.springframework.boot:spring-boot-starter-test")
    testImplementation("org.jetbrains.kotlin:kotlin-test-junit5")
    testImplementation("io.mockk:mockk:1.13.8")
    testImplementation("com.h2database:h2")
}

tasks.withType<Test> {
    useJUnitPlatform()
}
```

## 테스트 실행 방법

### 전체 테스트 실행
```bash
./gradlew test
```

### 특정 도메인 테스트 실행
```bash
./gradlew test --tests "*Product*"
```

### 동시성 테스트만 실행
```bash
./gradlew test --tests "*Concurrency*"
```

### 테스트 리포트 확인
```bash
# 테스트 실행 후 리포트 확인
open build/reports/tests/test/index.html
```

## 성능 기준

### 동시성 테스트 기준
- **Thread Count**: 50-100개 동시 실행
- **실행 시간**: 1초 이내 완료
- **성공률**: 100% 통과
- **검증 정확도**: 예상 호출 횟수와 정확히 일치

### 품질 기준
- **테스트 커버리지**: 도메인 로직 90% 이상
- **비즈니스 시나리오 커버리지**: 주요 사용자 여정 100%
- **동시성 안전성**: 모든 상태 변경 로직에 동시성 테스트 포함

## 트러블슈팅

### 자주 발생하는 문제

#### 1. MockK 설정 오류
```kotlin
// 잘못된 방법
@Mock
private lateinit var repository: Repository

// 올바른 방법  
private val repository = mockk<Repository>()
```

#### 2. 비동기 테스트 타이밍 이슈
```kotlin
// CompletableFuture 완료 대기 필수
CompletableFuture.allOf(*futures.toTypedArray()).join()
executor.shutdown()
```

#### 3. 도메인 객체 생성 오류
```kotlin
// 실제 도메인의 팩토리 메소드 사용
Product.create(...) // ✓
Product(...) // ✗ 직접 생성자 호출 지양
```

## 베스트 프랙티스

### 1. 테스트 가독성
- 비즈니스 언어 사용
- 도메인 전문가도 이해할 수 있는 테스트명
- Given-When-Then 패턴 준수

### 2. 테스트 안정성  
- 외부 의존성 최소화
- 명확한 Mock 설정
- 테스트 간 격리 보장

### 3. 유지보수성
- 테스트 데이터 생성 함수 재사용
- 공통 설정을 @BeforeEach로 분리
- 의미 있는 검증 메시지

이 가이드를 따라 작성된 테스트는 베베플레이스의 비즈니스 로직 안전성과 동시성 문제 해결을 보장합니다.