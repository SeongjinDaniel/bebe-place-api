package com.bebeplace.bebeplaceapi.product.infrastructure.persistence

import com.bebeplace.bebeplaceapi.product.domain.model.ProductCreationTracker
import com.bebeplace.bebeplaceapi.product.domain.model.ProductCreationStatus
import com.bebeplace.bebeplaceapi.product.domain.repository.ProductCreationTrackerRepository
import org.springframework.stereotype.Repository
import java.util.*

@Repository
class ProductCreationTrackerRepositoryImpl(
    private val jpaRepository: ProductCreationTrackerJpaRepository
) : ProductCreationTrackerRepository {
    
    override fun save(tracker: ProductCreationTracker): ProductCreationTracker {
        val entity = ProductCreationTrackerEntity.fromDomain(tracker)
        val savedEntity = jpaRepository.save(entity)
        return savedEntity.toDomain()
    }
    
    override fun findByProductId(productId: UUID): ProductCreationTracker? {
        return jpaRepository.findByProductId(productId)?.toDomain()
    }
    
    override fun findByCorrelationId(correlationId: UUID): ProductCreationTracker? {
        return jpaRepository.findByCorrelationId(correlationId)?.toDomain()
    }
    
    override fun findByStatus(status: ProductCreationStatus): List<ProductCreationTracker> {
        return jpaRepository.findByStatus(status).map { it.toDomain() }
    }
    
    override fun deleteByProductId(productId: UUID) {
        jpaRepository.deleteByProductId(productId)
    }
}