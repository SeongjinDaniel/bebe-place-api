package com.bebeplace.bebeplaceapi.product.application.dto

import com.bebeplace.bebeplaceapi.product.domain.model.AgeGroup
import com.bebeplace.bebeplaceapi.product.domain.model.ProductCategory
import com.bebeplace.bebeplaceapi.product.domain.model.ProductCondition
import com.bebeplace.bebeplaceapi.product.domain.model.ProductType
import io.swagger.v3.oas.annotations.media.Schema
import jakarta.validation.constraints.*
import java.math.BigDecimal

@Schema(description = "상품 등록 요청")
data class CreateProductRequest(
    @field:NotBlank(message = "제목은 필수입니다")
    @field:Size(min = 1, max = 200, message = "제목은 1-200자 사이여야 합니다")
    @get:Schema(description = "상품 제목", example = "유아용 원피스", required = true)
    val title: String,
    
    @field:NotNull(message = "카테고리는 필수입니다")
    @get:Schema(description = "상품 카테고리", example = "CLOTHING", required = true)
    val category: ProductCategory,
    
    @field:NotNull(message = "가격은 필수입니다")
    @field:DecimalMin(value = "0.0", inclusive = false, message = "가격은 0보다 커야 합니다")
    @field:DecimalMax(value = "99999999.99", message = "가격은 99,999,999.99원 이하여야 합니다")
    @get:Schema(description = "상품 가격", example = "25000", required = true)
    val price: BigDecimal,
    
    @field:NotNull(message = "배송비 포함 여부는 필수입니다")
    @get:Schema(description = "배송비 포함 여부", example = "true", required = true)
    val shippingIncluded: Boolean,
    
    @field:DecimalMin(value = "0.0", inclusive = true, message = "배송비는 0 이상이어야 합니다")
    @field:DecimalMax(value = "999999.99", message = "배송비는 999,999.99원 이하여야 합니다")
    @get:Schema(description = "배송비 (배송비 별도인 경우만)", example = "3000")
    val shippingCost: BigDecimal?,
    
    @field:NotBlank(message = "상품 설명은 필수입니다")
    @field:Size(min = 10, max = 2000, message = "상품 설명은 10-2000자 사이여야 합니다")
    @get:Schema(description = "상품 설명", example = "아이가 입기 좋은 원피스입니다. 상태 양호합니다.", required = true)
    val description: String,
    
    @field:NotNull(message = "상품 유형은 필수입니다")
    @get:Schema(description = "상품 유형", example = "USED", required = true)
    val productType: ProductType,
    
    @field:NotNull(message = "상품 상태는 필수입니다")
    @get:Schema(description = "상품 상태", example = "EXCELLENT", required = true)
    val condition: ProductCondition,
    
    @field:NotEmpty(message = "연령대를 최소 1개 이상 선택해야 합니다")
    @field:Size(max = 8, message = "연령대는 최대 8개까지 선택 가능합니다")
    @get:Schema(description = "대상 연령대 목록", example = "[\"TODDLER_12_18M\", \"TODDLER_18_24M\"]", required = true)
    val ageGroups: List<AgeGroup>
) {
    
    init {
        // 배송비 관련 검증
        if (!shippingIncluded && shippingCost == null) {
            throw IllegalArgumentException("배송비가 별도인 경우 배송비를 입력해야 합니다")
        }
        if (shippingIncluded && shippingCost != null) {
            throw IllegalArgumentException("배송비가 포함인 경우 배송비를 입력할 수 없습니다")
        }
        
        // 연령대 중복 검증
        if (ageGroups.distinct().size != ageGroups.size) {
            throw IllegalArgumentException("중복된 연령대를 선택할 수 없습니다")
        }
    }
}