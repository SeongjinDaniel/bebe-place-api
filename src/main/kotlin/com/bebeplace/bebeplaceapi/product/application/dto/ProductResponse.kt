package com.bebeplace.bebeplaceapi.product.application.dto

import com.bebeplace.bebeplaceapi.product.domain.model.*
import io.swagger.v3.oas.annotations.media.Schema
import java.math.BigDecimal
import java.time.LocalDateTime
import java.util.*

@Schema(description = "상품 상세 응답")
data class ProductResponse(
    @get:Schema(description = "상품 ID")
    val id: UUID,
    
    @get:Schema(description = "판매자 ID")
    val sellerId: UUID,
    
    @get:Schema(description = "상품 제목", example = "유아용 원피스")
    val title: String,
    
    @get:Schema(description = "상품 카테고리", example = "CLOTHING")
    val category: ProductCategory,
    
    @get:Schema(description = "상품 가격", example = "25000")
    val price: BigDecimal,
    
    @get:Schema(description = "배송비 포함 여부", example = "true")
    val shippingIncluded: Boolean,
    
    @get:Schema(description = "배송비", example = "3000")
    val shippingCost: BigDecimal?,
    
    @get:Schema(description = "상품 설명", example = "아이가 입기 좋은 원피스입니다.")
    val description: String,
    
    @get:Schema(description = "상품 유형", example = "USED")
    val productType: ProductType,
    
    @get:Schema(description = "상품 상태", example = "EXCELLENT")
    val condition: ProductCondition,
    
    @get:Schema(description = "상품 판매 상태", example = "ACTIVE")
    val status: ProductStatus,
    
    @get:Schema(description = "조회수", example = "42")
    val viewCount: Int,
    
    @get:Schema(description = "대상 연령대 목록")
    val ageGroups: List<AgeGroup>,
    
    @get:Schema(description = "상품 이미지 목록")
    val images: List<ProductImageResponse>,
    
    @get:Schema(description = "등록 시간", example = "2024-12-25T10:30:00")
    val createdAt: LocalDateTime
) {
    
    companion object {
        fun from(
            product: Product,
            ageGroups: List<AgeGroup> = emptyList(),
            images: List<ProductImageResponse> = emptyList()
        ): ProductResponse {
            return ProductResponse(
                id = product.getId().getValue(),
                sellerId = product.getSellerId(),
                title = product.getTitle(),
                category = product.getCategory(),
                price = product.getPrice().amount,
                shippingIncluded = product.getShippingInfo().isIncluded,
                shippingCost = product.getShippingInfo().shippingCost?.amount,
                description = product.getDescription(),
                productType = product.getProductType(),
                condition = product.getCondition(),
                status = product.getStatus(),
                viewCount = product.getViewCount(),
                ageGroups = ageGroups,
                images = images,
                createdAt = product.createdAt
            )
        }
    }
}

@Schema(description = "상품 이미지 응답")
data class ProductImageResponse(
    @get:Schema(description = "이미지 ID")
    val id: Long,
    
    @get:Schema(description = "이미지 URL", example = "https://api.bebeplace.com/images/product-123.jpg")
    val imageUrl: String,
    
    @get:Schema(description = "이미지 순서", example = "1")
    val order: Int,
    
    @get:Schema(description = "대표 이미지 여부", example = "true")
    val isMain: Boolean,
    
    @get:Schema(description = "원본 파일명", example = "product-image.jpg")
    val originalFilename: String,
    
    @get:Schema(description = "파일 크기 (바이트)", example = "1024000")
    val fileSize: Long
)

@Schema(description = "상품 등록 결과")
data class ProductCreationResult(
    @get:Schema(description = "등록된 상품 ID")
    val productId: UUID,
    
    @get:Schema(description = "상관 관계 ID")
    val correlationId: UUID,
    
    @get:Schema(description = "등록 상태", example = "PROCESSING")
    val status: String,
    
    @get:Schema(description = "결과 메시지", example = "상품 등록이 시작되었습니다.")
    val message: String = "상품 등록이 시작되었습니다. 상태를 확인해주세요."
)

@Schema(description = "상품 등록 상태 응답")
data class ProductCreationStatusResponse(
    @get:Schema(description = "상품 ID")
    val productId: UUID,
    
    @get:Schema(description = "상관 관계 ID")
    val correlationId: UUID,
    
    @get:Schema(description = "등록 상태", example = "PROCESSING")
    val status: String,
    
    @get:Schema(description = "현재 단계", example = "IMAGE_UPLOAD")
    val currentStep: String,
    
    @get:Schema(description = "진행률 (0-100)", example = "75")
    val progress: Int,
    
    @get:Schema(description = "실패 사유 (실패 시에만)")
    val failureReason: String?,
    
    @get:Schema(description = "예상 완료 시간", example = "2024-12-25T10:35:00")
    val estimatedCompletion: LocalDateTime?
) {
    companion object {
        fun from(tracker: ProductCreationTracker): ProductCreationStatusResponse {
            val estimatedCompletion = if (tracker.isProcessing) {
                tracker.createdAt.plusMinutes(5) // 예상 완료 시간: 생성 후 5분
            } else {
                tracker.updatedAt
            }
            
            return ProductCreationStatusResponse(
                productId = tracker.productId,
                correlationId = tracker.correlationId,
                status = tracker.status.name,
                currentStep = tracker.currentStep,
                progress = tracker.progress,
                failureReason = tracker.failureReason,
                estimatedCompletion = estimatedCompletion
            )
        }
    }
}