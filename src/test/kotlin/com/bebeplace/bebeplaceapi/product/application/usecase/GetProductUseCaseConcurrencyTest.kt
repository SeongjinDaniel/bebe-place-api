package com.bebeplace.bebeplaceapi.product.application.usecase

import com.bebeplace.bebeplaceapi.product.domain.model.*
import com.bebeplace.bebeplaceapi.product.domain.repository.ProductImageRepository
import com.bebeplace.bebeplaceapi.product.domain.repository.ProductRepository
import com.bebeplace.bebeplaceapi.common.types.Money
import com.github.f4b6a3.uuid.UuidCreator
import io.mockk.*
import org.junit.jupiter.api.DisplayName
import org.junit.jupiter.api.Test
import org.junit.jupiter.api.BeforeEach
import java.math.BigDecimal
import java.util.*
import java.util.concurrent.CompletableFuture
import java.util.concurrent.Executors

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
        // Given: 특정 상품이 존재하고, 100명의 고객이 동시에 조회하는 상황
        val productId = UuidCreator.getTimeOrderedEpoch()
        val product = createTestProduct(productId)
        
        // 상품 조회 시 해당 상품이 반환되도록 설정
        every { productRepository.findById(any()) } returns product
        // 조회수 증가 연산이 정상적으로 수행되도록 설정  
        every { productRepository.increaseViewCount(any()) } returns 1
        // 상품 이미지는 빈 리스트로 설정
        every { productImageRepository.findByProductId(any()) } returns emptyList()

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

        // Then: 원자적 연산으로 인해 조회수가 정확히 100번 증가해야 함
        verify(exactly = threadCount) { productRepository.increaseViewCount(ProductId.of(productId)) }
        verify(exactly = threadCount) { productRepository.findById(ProductId.of(productId)) }
    }

    @Test
    @DisplayName("판매자가 본인 상품을 조회할 때는 조회수가 증가하지 않아야 한다")
    fun shouldNotIncreaseViewCountWhenSellerViewsOwnProduct() {
        // Given: 특정 판매자가 본인이 등록한 상품을 조회하는 상황
        val productId = UuidCreator.getTimeOrderedEpoch()
        val sellerId = UuidCreator.getTimeOrderedEpoch()
        val product = createTestProduct(productId, sellerId)
        
        // 판매자 전용 조회 시 해당 상품이 반환되도록 설정
        every { productRepository.findByIdAndSellerId(any(), any()) } returns product
        // 상품 이미지는 빈 리스트로 설정
        every { productImageRepository.findByProductId(any()) } returns emptyList()

        // When: 판매자가 50번 본인 상품을 조회 (관리 목적)
        val threadCount = 50
        val executor = Executors.newFixedThreadPool(threadCount)
        
        val futures = (1..threadCount).map {
            CompletableFuture.runAsync({
                getProductUseCase.executeForSeller(productId, sellerId)
            }, executor)
        }
        
        CompletableFuture.allOf(*futures.toTypedArray()).join()
        executor.shutdown()

        // Then: 판매자의 본인 상품 조회는 조회수에 영향을 주지 않아야 함 (비즈니스 규칙)
        verify(exactly = 0) { productRepository.increaseViewCount(any()) }
        verify(exactly = threadCount) { productRepository.findByIdAndSellerId(ProductId.of(productId), sellerId) }
    }

    @Test
    @DisplayName("단일 고객의 상품 조회 시 모든 데이터가 올바르게 조회되어야 한다")
    fun shouldRetrieveAllDataCorrectlyWhenSingleCustomerViewsProduct() {
        // Given: 일반 고객이 특정 상품을 단건 조회하는 상황
        val productId = UuidCreator.getTimeOrderedEpoch()
        val product = createTestProduct(productId)
        
        // 상품 정보 조회 설정
        every { productRepository.findById(any()) } returns product
        // 조회수 증가 연산 설정
        every { productRepository.increaseViewCount(any()) } returns 1
        // 상품 이미지 조회 설정
        every { productImageRepository.findByProductId(any()) } returns emptyList()

        // When: 고객이 상품을 조회
        getProductUseCase.execute(productId)

        // Then: 상품 조회의 모든 단계가 정확히 한 번씩 실행되어야 함
        
        // 1. 상품 기본 정보 조회
        verify(exactly = 1) { productRepository.findById(ProductId.of(productId)) }
        
        // 2. 조회수 증가 (비즈니스 규칙: 고객 조회 시에만)
        verify(exactly = 1) { productRepository.increaseViewCount(ProductId.of(productId)) }
        
        // 3. 상품 이미지 정보 조회
        verify(exactly = 1) { productImageRepository.findByProductId(productId) }
    }

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
}