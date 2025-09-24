package com.bebeplace.bebeplaceapi.product.application.usecase

import com.bebeplace.bebeplaceapi.common.exception.BusinessException
import com.bebeplace.bebeplaceapi.product.application.dto.ProductCreationStatusResponse
import com.bebeplace.bebeplaceapi.product.domain.repository.ProductCreationTrackerRepository
import org.slf4j.LoggerFactory
import org.springframework.stereotype.Service
import org.springframework.transaction.annotation.Transactional
import java.util.*

@Service
class GetProductCreationStatusUseCase(
    private val trackerRepository: ProductCreationTrackerRepository
) {
    
    private val logger = LoggerFactory.getLogger(this::class.java)
    
    @Transactional(readOnly = true)
    fun execute(correlationId: UUID): ProductCreationStatusResponse {
        logger.debug("Getting product creation status for correlation: $correlationId")
        
        val tracker = trackerRepository.findByCorrelationId(correlationId)
            ?: throw BusinessException("상품 등록 정보를 찾을 수 없습니다: $correlationId")
        
        return ProductCreationStatusResponse.from(tracker)
    }
    
    @Transactional(readOnly = true)
    fun executeByProductId(productId: UUID): ProductCreationStatusResponse {
        logger.debug("Getting product creation status for product: $productId")
        
        val tracker = trackerRepository.findByProductId(productId)
            ?: throw BusinessException("상품 등록 정보를 찾을 수 없습니다: $productId")
        
        return ProductCreationStatusResponse.from(tracker)
    }
}