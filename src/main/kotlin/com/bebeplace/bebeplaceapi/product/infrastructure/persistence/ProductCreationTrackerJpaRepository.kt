package com.bebeplace.bebeplaceapi.product.infrastructure.persistence

import com.bebeplace.bebeplaceapi.product.domain.model.ProductCreationStatus
import org.springframework.data.jpa.repository.JpaRepository
import org.springframework.stereotype.Repository
import java.util.*

@Repository
interface ProductCreationTrackerJpaRepository : JpaRepository<ProductCreationTrackerEntity, Long> {
    fun findByProductId(productId: UUID): ProductCreationTrackerEntity?
    fun findByCorrelationId(correlationId: UUID): ProductCreationTrackerEntity?
    fun findByStatus(status: ProductCreationStatus): List<ProductCreationTrackerEntity>
    fun deleteByProductId(productId: UUID)
}