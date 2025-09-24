package com.bebeplace.bebeplaceapi.product.application.handler

import com.bebeplace.bebeplaceapi.product.application.service.ProductImageRetryService
import com.bebeplace.bebeplaceapi.product.application.service.ProductImageRequestCacheService
import com.bebeplace.bebeplaceapi.product.domain.event.ProductImageUploadFailed
import com.bebeplace.bebeplaceapi.product.domain.repository.ProductCreationTrackerRepository
import org.slf4j.LoggerFactory
import org.springframework.context.event.EventListener
import org.springframework.stereotype.Component

@Component
class ProductImageFailureHandler(
    private val retryService: ProductImageRetryService,
    private val trackerRepository: ProductCreationTrackerRepository,
    private val requestCacheService: ProductImageRequestCacheService
) {
    
    private val logger = LoggerFactory.getLogger(this::class.java)
    
    @EventListener
    fun handle(event: ProductImageUploadFailed) {
        logger.error(
            """
            |=== PRODUCT IMAGE UPLOAD FAILED ===
            |Product ID: ${event.productId}
            |Failure Reason: ${event.reason}
            |Timestamp: ${event.occurredAt}
            |Event ID: ${event.eventId}
            |================================
            """.trimMargin()
        )
        
        try {
            // 캐시된 원본 요청 정보로 재시도 큐에 추가
            val originalRequest = requestCacheService.getCachedRequest(event.productId)
            if (originalRequest != null) {
                logger.info("Adding product ${event.productId} to retry queue using cached request")
                retryService.addToRetryQueue(event.productId, originalRequest, event.reason)
            } else {
                logger.warn("Cannot retry image upload for product ${event.productId}: original request not found in cache")
            }
            
            // 실패 상세 정보 로깅
            val tracker = trackerRepository.findByProductId(event.productId)
            logFailureDetails(event, tracker?.toString() ?: "No tracker found")
            
        } catch (exception: Exception) {
            logger.error("Error handling ProductImageUploadFailed event for product: ${event.productId}", exception)
        }
    }
    
    private fun logFailureDetails(event: ProductImageUploadFailed, trackerInfo: String) {
        logger.error(
            """
            |=== FAILURE ANALYSIS ===
            |Product ID: ${event.productId}
            |Reason: ${event.reason}
            |Tracker Info: $trackerInfo
            |Current Retry Count: ${retryService.getRetryCount(event.productId)}
            |Queue Size: ${retryService.getRetryQueueSize()}
            |========================
            """.trimMargin()
        )
    }
}