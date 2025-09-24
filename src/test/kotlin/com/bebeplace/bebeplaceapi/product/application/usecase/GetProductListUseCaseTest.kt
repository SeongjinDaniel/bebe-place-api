package com.bebeplace.bebeplaceapi.product.application.usecase

import com.bebeplace.bebeplaceapi.product.application.dto.ProductListRequest
import com.bebeplace.bebeplaceapi.product.domain.model.*
import com.bebeplace.bebeplaceapi.product.domain.repository.ProductListFilter
import com.bebeplace.bebeplaceapi.product.domain.repository.ProductListResult
import com.bebeplace.bebeplaceapi.product.domain.repository.ProductRepository
import com.bebeplace.bebeplaceapi.product.infrastructure.persistence.ProductEntity
import com.bebeplace.bebeplaceapi.product.infrastructure.persistence.ProductJpaRepository
import com.bebeplace.bebeplaceapi.common.types.Money
import io.mockk.every
import io.mockk.mockk
import io.mockk.verify
import org.junit.jupiter.api.DisplayName
import org.junit.jupiter.api.Test
import org.junit.jupiter.api.assertThrows
import org.springframework.boot.test.context.SpringBootTest
import org.springframework.test.context.ActiveProfiles
import java.math.BigDecimal
import java.util.*
import kotlin.test.assertEquals
import kotlin.test.assertFalse
import kotlin.test.assertNotNull
import kotlin.test.assertNull
import kotlin.test.assertTrue

@DisplayName("Product List Use Case Tests - 상품 목록 조회 도메인 로직 검증")
class GetProductListUseCaseTest {

    private val productRepository = mockk<ProductRepository>()
    private val productJpaRepository = mockk<ProductJpaRepository>()
    private val useCase = GetProductListUseCase(productRepository, productJpaRepository)

    @Test
    @DisplayName("상품 목록 조회 성공 - 필터링된 상품들이 올바른 도메인 정보와 함께 반환되어야 한다")
    fun shouldReturnProductListSuccessfully() {
        // Given - 활성 상태의 아기 의류 상품 도메인 객체 준비
        val request = ProductListRequest(
            cursor = null,
            size = 10,
            status = ProductStatus.ACTIVE
        )
        
        val productId = ProductId(UUID.randomUUID())
        val sellerId = UUID.randomUUID()
        
        val product = Product(
            id = productId,
            sellerId = sellerId,
            title = "Test Product",
            category = ProductCategory.BABY_CLOTHING,
            price = Money(BigDecimal("10000")),
            shippingInfo = ShippingInfo.included(),
            description = "Test Description",
            productType = ProductType.USED,
            condition = ProductCondition.EXCELLENT,
            status = ProductStatus.ACTIVE,
            viewCount = 0,
            likeCount = 5,
            commentCount = 3
        )
        
        val productEntity = ProductEntity(
            id = productId.getValue(),
            sellerId = sellerId,
            title = "Test Product",
            category = ProductCategory.BABY_CLOTHING,
            price = BigDecimal("10000"),
            shippingIncluded = true,
            shippingCost = null,
            description = "Test Description",
            productType = ProductType.USED,
            condition = ProductCondition.EXCELLENT,
            status = ProductStatus.ACTIVE,
            viewCount = 0,
            likeCount = 5,
            commentCount = 3
        )
        
        val expectedResult = ProductListResult(
            products = listOf(product),
            hasNext = false,
            nextCursor = null
        )
        
        every { 
            productRepository.findProductsWithCursor(
                cursor = null,
                size = 10,
                filter = ProductListFilter(status = ProductStatus.ACTIVE, sellerId = null, category = null, sortType = ProductSortType.LATEST)
            )
        } returns expectedResult
        
        every { 
            productJpaRepository.findById(productId.getValue()) 
        } returns Optional.of(productEntity)

        // When - 상품 목록 조회 유스케이스 실행
        val result = useCase.execute(request)

        // Then - 도메인 정보가 올바르게 매핑되어 반환되는지 검증
        assertEquals(1, result.products.size)
        assertEquals("Test Product", result.products[0].title)
        assertEquals(ProductCondition.EXCELLENT, result.products[0].condition)
        assertEquals(BigDecimal("10000"), result.products[0].price)
        assertEquals(5, result.products[0].likeCount)
        assertEquals(3, result.products[0].commentCount)
        // createdAt은 자동으로 설정되므로 null이 아닌지만 확인
        assertNotNull(result.products[0].createdAt)
        assertFalse(result.hasNext)
        assertNull(result.nextCursor)
        
        verify {
            productRepository.findProductsWithCursor(
                cursor = null,
                size = 10,
                filter = ProductListFilter(status = ProductStatus.ACTIVE, sellerId = null, category = null, sortType = ProductSortType.LATEST)
            )
        }
        verify { productJpaRepository.findById(productId.getValue()) }
    }

    @Test
    @DisplayName("빈 상품 목록 반환 - 조건에 맞는 상품이 없을 때 빈 목록이 반환되어야 한다")
    fun shouldReturnEmptyListWhenNoProductsFound() {
        // Given - 빈 결과를 반환하는 리포지토리 설정
        val request = ProductListRequest(size = 10)
        
        val expectedResult = ProductListResult(
            products = emptyList(),
            hasNext = false,
            nextCursor = null
        )
        
        every { 
            productRepository.findProductsWithCursor(
                cursor = null,
                size = 10,
                filter = ProductListFilter(status = null, sellerId = null, category = null, sortType = ProductSortType.LATEST)
            )
        } returns expectedResult

        // When - 상품 목록 조회 유스케이스 실행
        val result = useCase.execute(request)

        // Then - 빈 목록과 페이지네이션 정보 검증
        assertTrue(result.products.isEmpty())
        assertFalse(result.hasNext)
        assertNull(result.nextCursor)
    }

    @Test
    @DisplayName("커서 기반 페이지네이션 동작 검증 - 다음 페이지 커서와 함께 상품 목록이 올바르게 반환되어야 한다")
    fun shouldReturnListWithCursorForPagination() {
        // Given - 커서 기반 페이지네이션을 위한 교육용 장난감 상품 준비
        val cursor = "encoded-cursor"
        val nextCursor = "next-encoded-cursor"
        val request = ProductListRequest(
            cursor = cursor,
            size = 5
        )
        
        val productId = ProductId(UUID.randomUUID())
        val sellerId = UUID.randomUUID()
        
        val product = Product(
            id = productId,
            sellerId = sellerId,
            title = "Test Product",
            category = ProductCategory.TOYS_EDUCATIONAL,
            price = Money(BigDecimal("5000")),
            shippingInfo = ShippingInfo.separate(Money(BigDecimal("3000"))),
            description = "Test Description",
            productType = ProductType.NEW,
            condition = ProductCondition.NEW,
            status = ProductStatus.ACTIVE,
            viewCount = 10,
            likeCount = 2,
            commentCount = 1
        )
        
        val productEntity = ProductEntity(
            id = productId.getValue(),
            sellerId = sellerId,
            title = "Test Product",
            category = ProductCategory.TOYS_EDUCATIONAL,
            price = BigDecimal("5000"),
            shippingIncluded = false,
            shippingCost = BigDecimal("3000"),
            description = "Test Description",
            productType = ProductType.NEW,
            condition = ProductCondition.NEW,
            status = ProductStatus.ACTIVE,
            viewCount = 10,
            likeCount = 2,
            commentCount = 1
        )
        
        val expectedResult = ProductListResult(
            products = listOf(product),
            hasNext = true,
            nextCursor = nextCursor
        )
        
        every { 
            productRepository.findProductsWithCursor(
                cursor = cursor,
                size = 5,
                filter = ProductListFilter(status = null, sellerId = null, category = null, sortType = ProductSortType.LATEST)
            )
        } returns expectedResult
        
        every { 
            productJpaRepository.findById(productId.getValue()) 
        } returns Optional.of(productEntity)

        // When - 커서를 포함한 상품 목록 조회 실행
        val result = useCase.execute(request)

        // Then - 페이지네이션 정보와 다음 커서 존재 여부 검증
        assertEquals(1, result.products.size)
        assertEquals("Test Product", result.products[0].title)
        assertTrue(result.hasNext)
        assertEquals(nextCursor, result.nextCursor)
    }

    @Test
    @DisplayName("찜 많은 순 정렬 요청 - MOST_LIKED 정렬 타입이 올바르게 전달되어야 한다")
    fun shouldHandleMostLikedSortType() {
        // Given - 찜 많은 순 정렬 요청
        val request = ProductListRequest(
            cursor = null,
            size = 10,
            sortType = ProductSortType.MOST_LIKED
        )
        
        val expectedResult = ProductListResult(
            products = emptyList(),
            hasNext = false,
            nextCursor = null
        )
        
        every { 
            productRepository.findProductsWithCursor(
                cursor = null,
                size = 10,
                filter = ProductListFilter(status = null, sellerId = null, category = null, sortType = ProductSortType.MOST_LIKED)
            )
        } returns expectedResult

        // When - 찜 많은 순 정렬로 상품 목록 조회
        val result = useCase.execute(request)

        // Then - 올바른 정렬 타입으로 호출되었는지 검증
        verify {
            productRepository.findProductsWithCursor(
                cursor = null,
                size = 10,
                filter = ProductListFilter(status = null, sellerId = null, category = null, sortType = ProductSortType.MOST_LIKED)
            )
        }
    }

    @Test
    @DisplayName("가격 낮은 순 정렬 요청 - PRICE_LOW 정렬 타입이 올바르게 전달되어야 한다")
    fun shouldHandlePriceLowSortType() {
        // Given - 가격 낮은 순 정렬 요청
        val request = ProductListRequest(
            cursor = null,
            size = 10,
            sortType = ProductSortType.PRICE_LOW,
            category = ProductCategory.BABY_CLOTHING
        )
        
        val expectedResult = ProductListResult(
            products = emptyList(),
            hasNext = false,
            nextCursor = null
        )
        
        every { 
            productRepository.findProductsWithCursor(
                cursor = null,
                size = 10,
                filter = ProductListFilter(status = null, sellerId = null, category = ProductCategory.BABY_CLOTHING, sortType = ProductSortType.PRICE_LOW)
            )
        } returns expectedResult

        // When - 가격 낮은 순 정렬로 상품 목록 조회
        val result = useCase.execute(request)

        // Then - 올바른 정렬 타입과 카테고리 필터로 호출되었는지 검증
        verify {
            productRepository.findProductsWithCursor(
                cursor = null,
                size = 10,
                filter = ProductListFilter(status = null, sellerId = null, category = ProductCategory.BABY_CLOTHING, sortType = ProductSortType.PRICE_LOW)
            )
        }
    }
}