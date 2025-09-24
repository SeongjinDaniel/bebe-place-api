package com.bebeplace.bebeplaceapi.product.infrastructure.persistence

import com.bebeplace.bebeplaceapi.product.domain.model.ProductCategory
import java.util.*

/**
 * 판매자별 통계 데이터
 */
data class TopSellerStats(
    val sellerId: UUID,
    val productCount: Long,
    val totalViewCount: Long
)

/**
 * 카테고리별 통계 데이터  
 */
data class CategoryStats(
    val category: ProductCategory,
    val productCount: Long,
    val averagePrice: Double,
    val totalViewCount: Long
)