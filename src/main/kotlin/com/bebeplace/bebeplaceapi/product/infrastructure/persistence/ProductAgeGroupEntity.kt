package com.bebeplace.bebeplaceapi.product.infrastructure.persistence

import com.bebeplace.bebeplaceapi.common.infrastructure.BaseEntity
import com.bebeplace.bebeplaceapi.product.domain.model.AgeGroup
import jakarta.persistence.*
import java.util.*

@Entity
@Table(name = "product_age_groups")
data class ProductAgeGroupEntity(
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    val id: Long? = null,
    
    @Column(name = "product_id", nullable = false)
    val productId: UUID,
    
    @Enumerated(EnumType.STRING)
    @Column(name = "age_group", nullable = false)
    val ageGroup: AgeGroup
) {
    companion object {
        fun create(productId: UUID, ageGroup: AgeGroup): ProductAgeGroupEntity {
            return ProductAgeGroupEntity(
                productId = productId,
                ageGroup = ageGroup
            )
        }
    }
}