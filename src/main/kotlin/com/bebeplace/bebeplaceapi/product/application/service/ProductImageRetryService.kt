package com.bebeplace.bebeplaceapi.product.application.service

import com.bebeplace.bebeplaceapi.product.domain.event.ProductImageUploadRequested
import org.slf4j.LoggerFactory
import org.springframework.context.ApplicationEventPublisher
import org.springframework.scheduling.annotation.Scheduled
import org.springframework.stereotype.Service
import java.time.LocalDateTime
import java.util.*
import java.util.concurrent.ConcurrentHashMap
import java.util.concurrent.ConcurrentLinkedQueue

@Service
class ProductImageRetryService(
    private val applicationEventPublisher: ApplicationEventPublisher
) {
    
    private val logger = LoggerFactory.getLogger(this::class.java)
    
    // 재시도 큐: 실패한 업로드 요청들을 저장
    private val retryQueue = ConcurrentLinkedQueue<RetryRequest>()
    
    // 재시도 횟수 추적 (productId -> 재시도 횟수)
    private val retryCount = ConcurrentHashMap<UUID, Int>()
    
    companion object {
        private const val MAX_RETRY_COUNT = 3
        private const val RETRY_DELAY_MINUTES = 5L
    }
    
    data class RetryRequest(
        val productId: UUID,
        val originalRequest: ProductImageUploadRequested,
        val failureReason: String,
        val scheduledRetryTime: LocalDateTime,
        val attemptCount: Int
    )
    
    fun addToRetryQueue(productId: UUID, originalRequest: ProductImageUploadRequested, failureReason: String) {
        val currentRetryCount = retryCount.getOrDefault(productId, 0) + 1
        
        if (currentRetryCount > MAX_RETRY_COUNT) {
            logger.error("Max retry count exceeded for product: $productId, giving up retry")
            retryCount.remove(productId)
            return
        }
        
        retryCount[productId] = currentRetryCount
        
        val retryRequest = RetryRequest(
            productId = productId,
            originalRequest = originalRequest,
            failureReason = failureReason,
            scheduledRetryTime = LocalDateTime.now().plusMinutes(RETRY_DELAY_MINUTES * currentRetryCount), // 지수적 백오프
            attemptCount = currentRetryCount
        )
        
        retryQueue.offer(retryRequest)
        
        logger.info("Added product $productId to retry queue (attempt $currentRetryCount/$MAX_RETRY_COUNT), scheduled for retry at ${retryRequest.scheduledRetryTime}")
    }
    
    @Scheduled(fixedRate = 60000) // 1분마다 실행
    fun processRetryQueue() {
        val now = LocalDateTime.now()
        val readyRequests = mutableListOf<RetryRequest>()
        
        // 재시도 시간이 된 요청들 수집
        while (true) {
            val request = retryQueue.peek() ?: break
            if (request.scheduledRetryTime.isAfter(now)) {
                break
            }
            retryQueue.poll()?.let { readyRequests.add(it) }
        }
        
        if (readyRequests.isNotEmpty()) {
            logger.info("Processing ${readyRequests.size} retry requests")
        }
        
        // 재시도 실행
        readyRequests.forEach { retryRequest ->
            try {
                logger.info("Retrying image upload for product: ${retryRequest.productId} (attempt ${retryRequest.attemptCount})")
                applicationEventPublisher.publishEvent(retryRequest.originalRequest)
            } catch (exception: Exception) {
                logger.error("Failed to publish retry event for product: ${retryRequest.productId}", exception)
                // 재시도 실패 시 다시 큐에 추가
                addToRetryQueue(retryRequest.productId, retryRequest.originalRequest, "Retry publication failed: ${exception.message}")
            }
        }
    }
    
    fun removeFromRetryTracking(productId: UUID) {
        retryCount.remove(productId)
        logger.debug("Removed product ${productId} from retry tracking (successful upload)")
    }
    
    fun getRetryQueueSize(): Int = retryQueue.size
    
    fun getRetryCount(productId: UUID): Int = retryCount.getOrDefault(productId, 0)
}