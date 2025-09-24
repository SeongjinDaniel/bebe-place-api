package com.bebeplace.bebeplaceapi.product.application.usecase

import com.bebeplace.bebeplaceapi.common.exception.BusinessException
import com.bebeplace.bebeplaceapi.product.application.dto.ProductImageResponse
import com.bebeplace.bebeplaceapi.product.application.dto.ProductResponse
import com.bebeplace.bebeplaceapi.product.domain.model.ProductId
import com.bebeplace.bebeplaceapi.product.domain.repository.ProductRepository
import com.bebeplace.bebeplaceapi.product.domain.repository.ProductImageRepository
import org.slf4j.LoggerFactory
import org.springframework.stereotype.Service
import org.springframework.transaction.annotation.Transactional
import java.util.*

@Service
class GetProductUseCase(
    private val productRepository: ProductRepository,
    private val productImageRepository: ProductImageRepository
) {
    
    private val logger = LoggerFactory.getLogger(this::class.java)
    
    @Transactional(readOnly = true)
    fun execute(productId: UUID): ProductResponse {
        logger.debug("Getting product details for: ${productId}")
        
        val product = productRepository.findById(ProductId.of(productId))
            ?: throw BusinessException("상품을 찾을 수 없습니다: ${productId}")
        
        // 조회수 증가 (원자적 연산으로 동시성 문제 해결)
        productRepository.increaseViewCount(ProductId.of(productId))
        
        // 연관 데이터 조회 - Product 도메인에서 직접 가져오기
        val ageGroups = product.getAgeGroups().toList()
        
        // 실제 이미지 정보 조회
        val images = productImageRepository.findByProductId(productId).map { productImage ->
            ProductImageResponse(
                id = productImage.id ?: 0L,
                imageUrl = productImage.imageUrl,
                order = productImage.order,
                isMain = productImage.isMain,
                originalFilename = productImage.originalFilename,
                fileSize = productImage.fileSize
            )
        }
        
        return ProductResponse.from(product, ageGroups, images)
    }
    
    @Transactional(readOnly = true)
    fun executeForSeller(productId: UUID, sellerId: UUID): ProductResponse {
        logger.debug("Getting product details for seller: ${sellerId}, product: ${productId}")
        
        val product = productRepository.findByIdAndSellerId(ProductId.of(productId), sellerId)
            ?: throw BusinessException("상품을 찾을 수 없습니다: ${productId}")
        
        // 판매자가 본인 상품을 조회하는 경우는 조회수를 증가시키지 않음
        val ageGroups = product.getAgeGroups().toList()

        val images = productImageRepository.findByProductId(productId).map { productImage ->
            ProductImageResponse(
                id = productImage.id ?: 0L,
                imageUrl = productImage.imageUrl,
                order = productImage.order,
                isMain = productImage.isMain,
                originalFilename = productImage.originalFilename,
                fileSize = productImage.fileSize
            )
        }
        
        return ProductResponse.from(product, ageGroups, images)
    }
}