package com.bebeplace.bebeplaceapi.product.domain.model

import java.time.LocalDateTime
import java.util.*

enum class ProductCreationStatus(val displayName: String, val progress: Int) {
    PRODUCT_REGISTRATION_IN_PROGRESS("상품 등록 중", 25),
    IMAGE_UPLOAD_PENDING("이미지 업로드 대기 중", 75),
    COMPLETED("완료", 100),
    FAILED("실패", 0);
    
    val isProcessing: Boolean
        get() = this in listOf(PRODUCT_REGISTRATION_IN_PROGRESS, IMAGE_UPLOAD_PENDING)
        
    override fun toString(): String = displayName
}

data class ProductCreationTracker(
    val id: Long? = null,
    val productId: UUID,
    val correlationId: UUID,
    val status: ProductCreationStatus,
    val failureReason: String? = null,
    val createdAt: LocalDateTime = LocalDateTime.now(),
    val updatedAt: LocalDateTime = LocalDateTime.now()
) {
    
    // 편의 메서드들 - 메서드 체이닝 줄이기
    val currentStep: String
        get() = status.displayName
        
    val progress: Int
        get() = status.progress
        
    val isProcessing: Boolean
        get() = status.isProcessing
    
    fun markCompleted(): ProductCreationTracker {
        return copy(
            status = ProductCreationStatus.COMPLETED,
            updatedAt = LocalDateTime.now()
        )
    }
    
    fun markFailed(reason: String): ProductCreationTracker {
        return copy(
            status = ProductCreationStatus.FAILED,
            failureReason = reason,
            updatedAt = LocalDateTime.now()
        )
    }
    
    fun updateStatus(newStatus: ProductCreationStatus): ProductCreationTracker {
        return copy(
            status = newStatus,
            updatedAt = LocalDateTime.now()
        )
    }
    
    companion object {
        fun start(productId: UUID, correlationId: UUID): ProductCreationTracker {
            return ProductCreationTracker(
                productId = productId,
                correlationId = correlationId,
                status = ProductCreationStatus.PRODUCT_REGISTRATION_IN_PROGRESS
            )
        }
    }
}