package com.bebeplace.bebeplaceapi.product.application.service

import com.bebeplace.bebeplaceapi.product.domain.event.ProductImageUploadFailed
import com.bebeplace.bebeplaceapi.product.domain.event.ProductImagesUploaded
import com.bebeplace.bebeplaceapi.product.domain.repository.ProductCreationTrackerRepository
import org.slf4j.LoggerFactory
import org.springframework.context.ApplicationEventPublisher
import org.springframework.stereotype.Service
import org.springframework.transaction.annotation.Transactional
import java.util.*

@Service
class ProductImageTransactionService(
    private val trackerRepository: ProductCreationTrackerRepository,
    private val applicationEventPublisher: ApplicationEventPublisher,
    private val retryService: ProductImageRetryService,
    private val requestCacheService: ProductImageRequestCacheService
) {
    
    private val logger = LoggerFactory.getLogger(this::class.java)
    
    @Transactional
    fun handleUploadSuccess(productId: UUID, imageUrls: List<String>) {
        logger.info("Image upload successful for product: $productId, urls: $imageUrls")
        
        // 추적 레코드를 완료 상태로 업데이트
        val tracker = trackerRepository.findByProductId(productId)
        if (tracker != null) {
            val completedTracker = tracker.markCompleted()
            trackerRepository.save(completedTracker)
        }
        
        // 재시도 추적 및 캐시에서 제거 (성공했으므로)
        retryService.removeFromRetryTracking(productId)
        requestCacheService.removeCachedRequest(productId)
        
        // 성공 이벤트 발행
        applicationEventPublisher.publishEvent(
            ProductImagesUploaded(
                productId = productId,
                imageUrls = imageUrls
            )
        )
    }
    
    @Transactional
    fun handleUploadFailure(productId: UUID, reason: String) {
        logger.error("Image upload permanently failed for product: $productId, reason: $reason")
        
        // 추적 레코드를 최종 실패로 처리
        val tracker = trackerRepository.findByProductId(productId)
        if (tracker != null) {
            val failedTracker = tracker.markFailed("Image upload failed after retries: $reason")
            trackerRepository.save(failedTracker)
        }
        
        // 최종 실패 이벤트 발행
        applicationEventPublisher.publishEvent(
            ProductImageUploadFailed(
                productId = productId,
                reason = "Image upload failed after 3 attempts: $reason"
            )
        )
    }
}