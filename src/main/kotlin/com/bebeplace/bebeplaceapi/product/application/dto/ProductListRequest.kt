package com.bebeplace.bebeplaceapi.product.application.dto

import com.bebeplace.bebeplaceapi.product.domain.model.ProductCategory
import com.bebeplace.bebeplaceapi.product.domain.model.ProductSortType
import com.bebeplace.bebeplaceapi.product.domain.model.ProductStatus
import io.swagger.v3.oas.annotations.media.Schema
import jakarta.validation.constraints.Max
import jakarta.validation.constraints.Min
import java.util.*

@Schema(description = "상품 목록 조회 요청")
data class ProductListRequest(
    @get:Schema(
        description = "다음 페이지를 위한 커서 (시간+ID 기반). 이전 응답의 nextCursor 값을 그대로 사용하세요. 첫 페이지는 null", 
        example = "MjAyNC0xMi0yNVQxMDowMDowMHw1NTBlODQwMC1lMjliLTQxZDQtYTcxNi00NDY2NTU0NDAwMDA="
    )
    val cursor: String? = null,
    
    @field:Min(1, message = "Size must be at least 1")
    @field:Max(100, message = "Size must not exceed 100")
    @get:Schema(description = "페이지 크기 (1-100)", example = "20", minimum = "1", maximum = "100")
    val size: Int = 20,
    
    @get:Schema(description = "상품 상태 필터", example = "ACTIVE")
    val status: ProductStatus? = null,
    
    @get:Schema(description = "판매자 ID 필터")
    val sellerId: UUID? = null,
    
    @get:Schema(description = "상품 카테고리 필터", example = "CLOTHING")
    val category: ProductCategory? = null,
    
    @get:Schema(description = "정렬 기준", example = "LATEST")
    val sortType: ProductSortType = ProductSortType.LATEST
)