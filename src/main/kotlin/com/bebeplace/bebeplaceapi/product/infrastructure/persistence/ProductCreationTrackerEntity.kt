package com.bebeplace.bebeplaceapi.product.infrastructure.persistence

import com.bebeplace.bebeplaceapi.product.domain.model.ProductCreationStatus
import com.bebeplace.bebeplaceapi.product.domain.model.ProductCreationTracker
import jakarta.persistence.*
import java.time.LocalDateTime
import java.util.*

@Entity
@Table(name = "product_creation_logs")
data class ProductCreationTrackerEntity(
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    val id: Long? = null,
    
    @Column(name = "product_id", nullable = false)
    val productId: UUID,
    
    @Column(name = "correlation_id", nullable = false)
    val correlationId: UUID,
    
    @Enumerated(EnumType.STRING)
    @Column(nullable = false)
    val status: ProductCreationStatus,
    
    @Column(name = "failure_reason")
    val failureReason: String? = null,
    
    @Column(name = "created_at", nullable = false)
    val createdAt: LocalDateTime = LocalDateTime.now(),
    
    @Column(name = "updated_at", nullable = false)
    val updatedAt: LocalDateTime = LocalDateTime.now()
) {
    fun toDomain(): ProductCreationTracker {
        return ProductCreationTracker(
            id = id,
            productId = productId,
            correlationId = correlationId,
            status = status,
            failureReason = failureReason,
            createdAt = createdAt,
            updatedAt = updatedAt
        )
    }
    
    companion object {
        fun fromDomain(tracker: ProductCreationTracker): ProductCreationTrackerEntity {
            return ProductCreationTrackerEntity(
                id = tracker.id,
                productId = tracker.productId,
                correlationId = tracker.correlationId,
                status = tracker.status,
                failureReason = tracker.failureReason,
                createdAt = tracker.createdAt,
                updatedAt = tracker.updatedAt
            )
        }
    }
}