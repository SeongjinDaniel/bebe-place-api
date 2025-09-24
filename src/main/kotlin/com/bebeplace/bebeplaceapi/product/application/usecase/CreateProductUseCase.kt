package com.bebeplace.bebeplaceapi.product.application.usecase

import com.bebeplace.bebeplaceapi.common.util.RequestContextUtil
import com.bebeplace.bebeplaceapi.product.application.dto.CreateProductRequest
import com.bebeplace.bebeplaceapi.product.application.dto.ProductCreationResult
import com.bebeplace.bebeplaceapi.product.application.service.ImageValidationService
import com.bebeplace.bebeplaceapi.product.application.service.ProductCreationService
import com.bebeplace.bebeplaceapi.product.application.service.ProductTrackingService
import com.bebeplace.bebeplaceapi.product.domain.model.ProductId
import org.slf4j.LoggerFactory
import org.springframework.stereotype.Service
import org.springframework.transaction.annotation.Transactional
import org.springframework.web.multipart.MultipartFile
import java.util.*

@Service
class CreateProductUseCase(
    private val productCreationService: ProductCreationService,
    private val imageValidationService: ImageValidationService,
    private val productTrackingService: ProductTrackingService,
    private val requestContextUtil: RequestContextUtil
) {
    
    private val logger = LoggerFactory.getLogger(this::class.java)
    
    @Transactional
    fun execute(
        request: CreateProductRequest,
        images: List<MultipartFile>
    ): ProductCreationResult {
        
        val currentUserId = getCurrentUserId()
        val productId = ProductId.generate()
        
        logger.info("Starting product creation for user: $currentUserId, productId: ${productId.getValue()}")
        
        // 1. 이미지 검증
        imageValidationService.validateImages(images)
        
        // 2. 추적 시작
        val tracker = productTrackingService.startTracking(productId)
        
        // 3. 상품 생성 (핵심 비즈니스 로직)
        productCreationService.createProduct(request, currentUserId, productId)
        
        // 4. 이미지 업로드 처리 (비동기)
        productTrackingService.processImageUpload(productId, images, tracker)
        
        // 5. 상태 결정
        val status = productTrackingService.getTrackingStatus(productId, images)
        
        logger.info("Product created successfully: ${productId.getValue()}")
        
        return ProductCreationResult(
            productId = productId.getValue(),
            correlationId = tracker.correlationId,
            status = status
        )
    }
    
    private fun getCurrentUserId(): UUID {
        return requestContextUtil.getCurrentUserId()
    }
    
    
}