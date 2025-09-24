package com.bebeplace.bebeplaceapi.product.infrastructure.persistence

import com.bebeplace.bebeplaceapi.product.domain.model.AgeGroup
import org.springframework.data.jpa.repository.JpaRepository
import org.springframework.stereotype.Repository
import java.util.*

@Repository
interface ProductAgeGroupJpaRepository : JpaRepository<ProductAgeGroupEntity, Long> {
    fun findByProductId(productId: UUID): List<ProductAgeGroupEntity>
    fun deleteByProductId(productId: UUID)
    fun findByAgeGroupIn(ageGroups: List<AgeGroup>): List<ProductAgeGroupEntity>
}