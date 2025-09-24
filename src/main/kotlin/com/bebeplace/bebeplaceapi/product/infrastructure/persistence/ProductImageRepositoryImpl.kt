package com.bebeplace.bebeplaceapi.product.infrastructure.persistence

import com.bebeplace.bebeplaceapi.product.domain.model.ProductImage
import com.bebeplace.bebeplaceapi.product.domain.repository.ProductImageRepository
import org.springframework.stereotype.Repository
import java.util.*

@Repository
class ProductImageRepositoryImpl(
    private val jpaRepository: ProductImageJpaRepository
) : ProductImageRepository {
    
    override fun save(productImage: ProductImage): ProductImage {
        val entity = ProductImageEntity.fromDomain(productImage)
        val savedEntity = jpaRepository.save(entity)
        return savedEntity.toDomain()
    }
    
    override fun findByProductId(productId: UUID): List<ProductImage> {
        return jpaRepository.findByProductIdOrderByOrderAsc(productId)
            .map { it.toDomain() }
    }
    
    override fun deleteByProductId(productId: UUID) {
        jpaRepository.deleteByProductId(productId)
    }
    
    override fun deleteById(id: Long) {
        jpaRepository.deleteById(id)
    }
    
    override fun findById(id: Long): ProductImage? {
        return jpaRepository.findById(id)
            .map { it.toDomain() }
            .orElse(null)
    }
}