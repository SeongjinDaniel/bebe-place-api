package com.bebeplace.bebeplaceapi.product.infrastructure.persistence

import com.bebeplace.bebeplaceapi.common.infrastructure.BaseEntity
import com.bebeplace.bebeplaceapi.product.domain.model.ProductImage
import jakarta.persistence.*
import java.util.*

@Entity
@Table(name = "product_images")
data class ProductImageEntity(
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    val id: Long? = null,
    
    @Column(name = "product_id", nullable = false)
    val productId: UUID,
    
    @Column(name = "image_url", nullable = false, length = 500)
    val imageUrl: String,
    
    @Column(name = "original_filename", nullable = false, length = 255)
    val originalFilename: String,
    
    @Column(name = "file_size", nullable = false)
    val fileSize: Long,
    
    @Column(name = "display_order", nullable = false)
    val order: Int,
    
    @Column(name = "is_main", nullable = false)
    val isMain: Boolean = false
) {
    
    fun toDomain(): ProductImage {
        return ProductImage(
            id = id,
            productId = productId,
            imageUrl = imageUrl,
            originalFilename = originalFilename,
            fileSize = fileSize,
            order = order,
            isMain = isMain
        )
    }
    
    companion object {
        fun fromDomain(productImage: ProductImage): ProductImageEntity {
            return ProductImageEntity(
                id = productImage.id,
                productId = productImage.productId,
                imageUrl = productImage.imageUrl,
                originalFilename = productImage.originalFilename,
                fileSize = productImage.fileSize,
                order = productImage.order,
                isMain = productImage.isMain
            )
        }
    }
}