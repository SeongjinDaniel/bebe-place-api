package com.bebeplace.bebeplaceapi.product.infrastructure.persistence

import com.bebeplace.bebeplaceapi.product.domain.repository.ProductListFilter

interface ProductCustomRepository {
    /**
     * 동적 필터와 커서 기반 페이지네이션을 사용하여 상품을 조회합니다.
     */
    fun findWithDynamicFilter(
        filter: ProductListFilter?,
        cursorParams: CursorParams
    ): List<ProductEntity>
    
    // 🚀 추가 JOOQ 활용 메서드들 - 집계 쿼리와 통계
    
    /**
     * 특정 상태의 상품 수를 카운트합니다.
     */
    fun countByStatus(status: com.bebeplace.bebeplaceapi.product.domain.model.ProductStatus): Long
    
    /**
     * 상품 수가 많은 판매자 순위를 조회합니다.
     */
    fun findTopSellersByProductCount(limit: Int): List<TopSellerStats>
    
    /**
     * 카테고리별 상품 통계를 조회합니다.
     */
    fun findProductStatsByCategory(): List<CategoryStats>
}