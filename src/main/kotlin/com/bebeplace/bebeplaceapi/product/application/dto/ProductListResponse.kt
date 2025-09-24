package com.bebeplace.bebeplaceapi.product.application.dto

import com.bebeplace.bebeplaceapi.product.domain.model.Product
import com.bebeplace.bebeplaceapi.product.domain.model.ProductCondition
import com.fasterxml.jackson.annotation.JsonFormat
import io.swagger.v3.oas.annotations.media.Schema
import java.math.BigDecimal
import java.time.LocalDateTime
import java.util.*

@Schema(description = "상품 목록 아이템")
data class ProductListItemDto(
    @get:Schema(description = "상품 ID")
    val id: UUID,
    
    @get:Schema(description = "상품 제목", example = "유아용 원피스")
    val title: String,
    
    @get:Schema(description = "상품 상태", example = "EXCELLENT")
    val condition: ProductCondition,
    
    @get:Schema(description = "상품 가격", example = "25000")
    val price: BigDecimal,
    
    @get:Schema(description = "상품 등록 시간", example = "2024-12-25 10:30:00")
    @get:JsonFormat(pattern = "yyyy-MM-dd HH:mm:ss")
    val createdAt: LocalDateTime,
    
    @get:Schema(description = "좋아요 개수", example = "15")
    val likeCount: Int,
    
    @get:Schema(description = "댓글 개수", example = "3")
    val commentCount: Int
) {
    companion object {
        fun fromDomain(product: Product, createdAt: LocalDateTime): ProductListItemDto {
            return ProductListItemDto(
                id = product.getId().getValue(),
                title = product.getTitle(),
                condition = product.getCondition(),
                price = product.getPrice().amount,
                createdAt = createdAt,
                likeCount = product.getLikeCount(),
                commentCount = product.getCommentCount()
            )
        }
    }
}

@Schema(description = "상품 목록 응답")
data class ProductListResponse(
    @get:Schema(description = "상품 목록")
    val products: List<ProductListItemDto>,
    
    @get:Schema(description = "다음 페이지 존재 여부", example = "true")
    val hasNext: Boolean,
    
    @get:Schema(
        description = "다음 페이지 커서 (시간+ID 기반). 다음 페이지 요청 시 cursor 파라미터로 사용. 마지막 페이지인 경우 null", 
        example = "MjAyNC0xMi0yNVQwOTowMDowMHw1NTBlODQwMC1lMjliLTQxZDQtYTcxNi00NDY2NTU0NDAwMDA="
    )
    val nextCursor: String?
)