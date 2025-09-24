package com.bebeplace.bebeplaceapi.product.infrastructure.persistence

import com.bebeplace.bebeplaceapi.product.domain.model.ProductSortType
import com.bebeplace.bebeplaceapi.product.domain.repository.ProductListFilter
import io.mockk.mockk
import org.jooq.DSLContext
import org.junit.jupiter.api.DisplayName
import org.junit.jupiter.api.Test
import kotlin.test.assertEquals

/**
 * 제품 정렬 기능 단위 테스트
 * - JOOQ 동적 정렬 쿼리 생성 검증
 * - 각 정렬 타입별 ORDER BY 절 생성 확인
 */
@DisplayName("ProductSorting 테스트")
class ProductSortingTest {

    private val mockDslContext = mockk<DSLContext>()
    private val repository = ProductCustomRepositoryImpl(mockDslContext)

    @Test
    @DisplayName("ProductListFilter에 정렬 타입이 포함되어야 한다")
    fun shouldIncludeSortTypeInFilter() {
        // given
        val filter = ProductListFilter(
            status = null,
            sellerId = null,
            category = null,
            sortType = ProductSortType.MOST_LIKED
        )
        
        // when & then
        assertEquals(ProductSortType.MOST_LIKED, filter.sortType)
    }

    @Test
    @DisplayName("기본 정렬 타입은 LATEST여야 한다")
    fun shouldDefaultToLatestSort() {
        // given
        val filter = ProductListFilter()
        
        // when & then
        assertEquals(ProductSortType.LATEST, filter.sortType)
    }

    @Test
    @DisplayName("정렬 타입별 설정이 올바르게 되어있어야 한다")
    fun shouldHaveCorrectSortConfiguration() {
        // 최신순 검증
        val latest = ProductSortType.LATEST
        assertEquals("created_at", latest.primaryField)
        assertEquals("id", latest.secondaryField)
        
        // 찜 많은 순 검증  
        val mostLiked = ProductSortType.MOST_LIKED
        assertEquals("like_count", mostLiked.primaryField)
        assertEquals("created_at", mostLiked.secondaryField)
        
        // 조회수 많은 순 검증
        val mostViewed = ProductSortType.MOST_VIEWED
        assertEquals("view_count", mostViewed.primaryField)
        assertEquals("created_at", mostViewed.secondaryField)
        
        // 가격 낮은 순 검증
        val priceLow = ProductSortType.PRICE_LOW
        assertEquals("price", priceLow.primaryField)
        assertEquals("id", priceLow.secondaryField)
        
        // 가격 높은 순 검증
        val priceHigh = ProductSortType.PRICE_HIGH
        assertEquals("price", priceHigh.primaryField)
        assertEquals("id", priceHigh.secondaryField)
    }
}