package com.bebeplace.bebeplaceapi.product.infrastructure.persistence

import org.springframework.data.jpa.repository.JpaRepository
import org.springframework.stereotype.Repository
import java.util.*

@Repository
interface ProductImageJpaRepository : JpaRepository<ProductImageEntity, Long> {
    fun findByProductIdOrderByOrderAsc(productId: UUID): List<ProductImageEntity>
    fun deleteByProductId(productId: UUID)
    fun findByProductIdAndIsMain(productId: UUID, isMain: Boolean): ProductImageEntity?
}