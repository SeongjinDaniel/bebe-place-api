package com.bebeplace.bebeplaceapi.product.infrastructure.web.dto

import com.bebeplace.bebeplaceapi.product.domain.model.ProductCategory
import com.bebeplace.bebeplaceapi.product.domain.model.ProductSortType
import com.bebeplace.bebeplaceapi.product.domain.model.ProductStatus
import io.swagger.v3.oas.annotations.media.Schema
import jakarta.validation.constraints.Max
import jakarta.validation.constraints.Min
import java.util.*

@Schema(description = "상품 목록 조회 요청")
data class ProductListSearchRequest(
    @get:Schema(
        description = "다음 페이지를 위한 커서 (시간+ID 기반). 이전 응답의 nextCursor 값을 그대로 사용. 첫 페이지는 생략",
        example = "MjAyNC0wMS0xNVQxMDozMDowMHwxMjM0NTY3OC05YWJjLTEyMzQtZWY1Ni0xMjM0NTY3ODkwYWI="
    )
    val cursor: String? = null,
    
    @get:Schema(description = "페이지 크기 (1-100)", example = "20", minimum = "1", maximum = "100")
    @field:Min(value = 1, message = "페이지 크기는 1 이상이어야 합니다")
    @field:Max(value = 100, message = "페이지 크기는 100 이하여야 합니다")
    val size: Int = 20,
    
    @get:Schema(description = "상품 상태 필터", example = "ACTIVE", allowableValues = ["ACTIVE", "INACTIVE", "SOLD_OUT", "HIDDEN"])
    val status: ProductStatus? = null,
    
    @get:Schema(description = "판매자 ID 필터", example = "123e4567-e89b-12d3-a456-426614174000")
    val sellerId: UUID? = null,
    
    @get:Schema(
        description = "상품 카테고리 필터", 
        example = "BABY_CLOTHING",
        allowableValues = [
            "BABY_CLOTHING", "GIRLS_CLOTHING", "BOYS_CLOTHING", "BABY_PRODUCTS", 
            "TOYS_EDUCATIONAL", "FEEDING_WEANING", "MOTHER_PRODUCTS", 
            "SAFETY_PRODUCTS", "BABY_CARE", "STROLLERS_CARSEATS", "ETC"
        ]
    )
    val category: ProductCategory? = null,
    
    @get:Schema(
        description = "정렬 기준", 
        example = "LATEST",
        allowableValues = ["LATEST", "MOST_LIKED", "MOST_VIEWED", "PRICE_LOW", "PRICE_HIGH"]
    )
    val sortType: ProductSortType = ProductSortType.LATEST
)