package com.bebeplace.bebeplaceapi.product.application.service

import com.bebeplace.bebeplaceapi.product.domain.event.ProductImageUploadRequested
import com.bebeplace.bebeplaceapi.product.domain.model.ProductCreationTracker
import com.bebeplace.bebeplaceapi.product.domain.model.ProductCreationStatus
import com.bebeplace.bebeplaceapi.product.domain.model.ProductId
import com.bebeplace.bebeplaceapi.product.domain.repository.ProductCreationTrackerRepository
import com.github.f4b6a3.uuid.UuidCreator
import org.slf4j.LoggerFactory
import org.springframework.context.ApplicationEventPublisher
import org.springframework.stereotype.Service
import org.springframework.web.multipart.MultipartFile
import java.util.*

@Service
class ProductTrackingService(
    private val trackerRepository: ProductCreationTrackerRepository,
    private val applicationEventPublisher: ApplicationEventPublisher
) {
    
    private val logger = LoggerFactory.getLogger(this::class.java)
    
    fun startTracking(productId: ProductId): ProductCreationTracker {
        val correlationId = UuidCreator.getTimeOrderedEpoch()
        val tracker = ProductCreationTracker.start(productId.getValue(), correlationId)
        
        val savedTracker = trackerRepository.save(tracker)
        logger.info("Started tracking for product: ${productId.getValue()}, correlation: $correlationId")
        
        return savedTracker
    }
    
    fun processImageUpload(
        productId: ProductId,
        images: List<MultipartFile>,
        tracker: ProductCreationTracker
    ): ProductCreationTracker {
        return if (images.isNotEmpty()) {
            // 비동기 이미지 업로드 이벤트 발행
            applicationEventPublisher.publishEvent(
                ProductImageUploadRequested(
                    productId = productId.getValue(),
                    images = images
                )
            )
            
            // 추적 상태 업데이트
            val updatedTracker = tracker.updateStatus(ProductCreationStatus.IMAGE_UPLOAD_PENDING)
            trackerRepository.save(updatedTracker)
        } else {
            // 이미지가 없으면 즉시 완료 처리
            val completedTracker = tracker.markCompleted()
            trackerRepository.save(completedTracker)
        }
    }
    
    fun getTrackingStatus(productId: ProductId, images: List<MultipartFile>): String {
        return if (images.isEmpty()) "COMPLETED" else "PROCESSING"
    }
}