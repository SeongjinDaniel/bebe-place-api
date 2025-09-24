package com.bebeplace.bebeplaceapi.product.application.service

import com.bebeplace.bebeplaceapi.product.domain.model.ProductImage
import com.bebeplace.bebeplaceapi.product.domain.repository.ProductImageRepository
import org.springframework.stereotype.Service
import java.util.*

@Service
class ProductImageService(
    private val productImageRepository: ProductImageRepository
) {
    
    fun findImagesByProductId(productId: UUID): List<ProductImage> {
        return productImageRepository.findByProductId(productId)
    }
    
    fun findImageUrlsByProductId(productId: UUID): List<String> {
        return productImageRepository.findByProductId(productId)
            .map { it.imageUrl }
    }
    
    fun saveProductImage(productImage: ProductImage): ProductImage {
        return productImageRepository.save(productImage)
    }
    
    fun saveProductImages(productImages: List<ProductImage>): List<ProductImage> {
        return productImages.map { productImageRepository.save(it) }
    }
    
    fun deleteImagesByProductId(productId: UUID) {
        productImageRepository.deleteByProductId(productId)
    }
    
    fun deleteImage(imageId: Long) {
        productImageRepository.deleteById(imageId)
    }
}