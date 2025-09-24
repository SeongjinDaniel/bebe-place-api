package com.bebeplace.bebeplaceapi.product.application.handler

import com.bebeplace.bebeplaceapi.product.application.service.ProductImageTransactionService
import com.bebeplace.bebeplaceapi.product.application.service.ProductImageRequestCacheService
import com.bebeplace.bebeplaceapi.product.domain.event.ProductImageUploadRequested
import com.bebeplace.bebeplaceapi.product.infrastructure.storage.ImageStorageService
import org.slf4j.LoggerFactory
import org.springframework.context.event.EventListener
import org.springframework.retry.annotation.Backoff
import org.springframework.retry.annotation.Recover
import org.springframework.retry.annotation.Retryable
import org.springframework.scheduling.annotation.Async
import org.springframework.stereotype.Component

@Component
class ProductImageUploadHandler(
    private val imageStorageService: ImageStorageService,
    private val productImageTransactionService: ProductImageTransactionService,
    private val requestCacheService: ProductImageRequestCacheService
) {
    
    private val logger = LoggerFactory.getLogger(this::class.java)
    
    @Async
    @EventListener
    @Retryable(
        value = [Exception::class],
        maxAttempts = 3,
        backoff = Backoff(delay = 1000, multiplier = 2.0)
    )
    fun handle(event: ProductImageUploadRequested) {
        logger.info("Starting async image upload for product: ${event.productId}")
        
        // 재시도를 위해 요청을 캐시
        requestCacheService.cacheRequest(event)
        
        try {
            // 이미지 업로드 처리
            val imageUrls = imageStorageService.uploadImages(event.images, event.productId)
            
            // 성공 처리 - 별도 서비스를 통한 트랜잭션 처리
            productImageTransactionService.handleUploadSuccess(event.productId, imageUrls)
            
        } catch (exception: Exception) {
            logger.error("Image upload failed for product: ${event.productId}", exception)
            
            // Spring Retry가 자동으로 재시도를 처리하므로 여기서는 예외를 다시 던져야 함
            throw exception
        }
    }
    
    @Recover
    fun handleFinalFailure(exception: Exception, event: ProductImageUploadRequested) {
        logger.error("Final image upload failure for product: ${event.productId}", exception)
        productImageTransactionService.handleUploadFailure(event.productId, exception.message ?: "Unknown error")
    }
}