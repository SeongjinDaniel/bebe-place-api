package com.bebeplace.bebeplaceapi.product.domain.model

import org.junit.jupiter.api.DisplayName
import org.junit.jupiter.api.Test
import kotlin.test.assertEquals
import kotlin.test.assertFalse
import kotlin.test.assertTrue

/**
 * 제품 정렬 타입 테스트
 * - 각 정렬 타입의 속성과 동작 검증
 * - 정렬 방향과 필드 정보 확인
 */
@DisplayName("ProductSortType 테스트")
class ProductSortTypeTest {

    @Test
    @DisplayName("LATEST 정렬은 최신순 설정이 올바르게 되어있어야 한다")
    fun shouldConfigureLatestSortCorrectly() {
        // given & when
        val sortType = ProductSortType.LATEST
        
        // then
        assertEquals("최신순", sortType.description)
        assertEquals("created_at", sortType.primaryField)
        assertEquals("id", sortType.secondaryField)
        assertTrue(sortType.isDescending())
        assertFalse(sortType.hasSecondarySort())
    }

    @Test
    @DisplayName("MOST_LIKED 정렬은 찜 많은 순 설정이 올바르게 되어있어야 한다")
    fun shouldConfigureMostLikedSortCorrectly() {
        // given & when
        val sortType = ProductSortType.MOST_LIKED
        
        // then
        assertEquals("찜 많은 순", sortType.description)
        assertEquals("like_count", sortType.primaryField)
        assertEquals("created_at", sortType.secondaryField)
        assertTrue(sortType.isDescending())
        assertTrue(sortType.hasSecondarySort())
    }

    @Test
    @DisplayName("MOST_VIEWED 정렬은 조회수 많은 순 설정이 올바르게 되어있어야 한다")
    fun shouldConfigureMostViewedSortCorrectly() {
        // given & when
        val sortType = ProductSortType.MOST_VIEWED
        
        // then
        assertEquals("조회수 많은 순", sortType.description)
        assertEquals("view_count", sortType.primaryField)
        assertEquals("created_at", sortType.secondaryField)
        assertTrue(sortType.isDescending())
        assertTrue(sortType.hasSecondarySort())
    }

    @Test
    @DisplayName("PRICE_LOW 정렬은 가격 낮은 순 설정이 올바르게 되어있어야 한다")
    fun shouldConfigurePriceLowSortCorrectly() {
        // given & when
        val sortType = ProductSortType.PRICE_LOW
        
        // then
        assertEquals("가격 낮은 순", sortType.description)
        assertEquals("price", sortType.primaryField)
        assertEquals("id", sortType.secondaryField)
        assertFalse(sortType.isDescending()) // 가격 낮은 순은 오름차순
        assertFalse(sortType.hasSecondarySort())
    }

    @Test
    @DisplayName("PRICE_HIGH 정렬은 가격 높은 순 설정이 올바르게 되어있어야 한다")
    fun shouldConfigurePriceHighSortCorrectly() {
        // given & when
        val sortType = ProductSortType.PRICE_HIGH
        
        // then
        assertEquals("가격 높은 순", sortType.description)
        assertEquals("price", sortType.primaryField)
        assertEquals("id", sortType.secondaryField)
        assertTrue(sortType.isDescending())
        assertFalse(sortType.hasSecondarySort())
    }

    @Test
    @DisplayName("모든 정렬 타입이 정의되어 있어야 한다")
    fun shouldHaveAllSortTypes() {
        // given & when
        val sortTypes = ProductSortType.entries
        
        // then
        assertEquals(5, sortTypes.size)
        assertTrue(sortTypes.contains(ProductSortType.LATEST))
        assertTrue(sortTypes.contains(ProductSortType.MOST_LIKED))
        assertTrue(sortTypes.contains(ProductSortType.MOST_VIEWED))
        assertTrue(sortTypes.contains(ProductSortType.PRICE_LOW))
        assertTrue(sortTypes.contains(ProductSortType.PRICE_HIGH))
    }
}