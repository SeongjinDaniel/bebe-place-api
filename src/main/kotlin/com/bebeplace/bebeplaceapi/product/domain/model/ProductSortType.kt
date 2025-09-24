package com.bebeplace.bebeplaceapi.product.domain.model

/**
 * 제품 목록 정렬 타입
 * 커서 기반 페이지네이션과 함께 사용되는 정렬 기준
 */
enum class ProductSortType(
    val description: String,
    val primaryField: String,
    val secondaryField: String = "id"
) {
    /**
     * 최신순 정렬 (기본값)
     * created_at DESC, id DESC
     */
    LATEST("최신순", "created_at"),
    
    /**
     * 찜 많은 순 정렬
     * like_count DESC, created_at DESC, id DESC
     */
    MOST_LIKED("찜 많은 순", "like_count", "created_at"),
    
    /**
     * 조회수 많은 순 정렬
     * view_count DESC, created_at DESC, id DESC  
     */
    MOST_VIEWED("조회수 많은 순", "view_count", "created_at"),
    
    /**
     * 가격 낮은 순 정렬
     * price ASC, created_at DESC, id DESC
     */
    PRICE_LOW("가격 낮은 순", "price"),
    
    /**
     * 가격 높은 순 정렬
     * price DESC, created_at DESC, id DESC
     */
    PRICE_HIGH("가격 높은 순", "price");
    
    /**
     * 정렬 방향이 내림차순인지 확인
     */
    fun isDescending(): Boolean = when (this) {
        PRICE_LOW -> false
        else -> true
    }
    
    /**
     * 2차 정렬 기준이 있는지 확인
     */
    fun hasSecondarySort(): Boolean = when (this) {
        MOST_LIKED, MOST_VIEWED -> true
        else -> false
    }
}