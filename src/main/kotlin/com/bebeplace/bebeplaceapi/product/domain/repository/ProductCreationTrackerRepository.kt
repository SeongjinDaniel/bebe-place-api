package com.bebeplace.bebeplaceapi.product.domain.repository

import com.bebeplace.bebeplaceapi.product.domain.model.ProductCreationTracker
import com.bebeplace.bebeplaceapi.product.domain.model.ProductCreationStatus
import java.util.*

interface ProductCreationTrackerRepository {
    fun save(tracker: ProductCreationTracker): ProductCreationTracker
    fun findByProductId(productId: UUID): ProductCreationTracker?
    fun findByCorrelationId(correlationId: UUID): ProductCreationTracker?
    fun findByStatus(status: ProductCreationStatus): List<ProductCreationTracker>
    fun deleteByProductId(productId: UUID)
}