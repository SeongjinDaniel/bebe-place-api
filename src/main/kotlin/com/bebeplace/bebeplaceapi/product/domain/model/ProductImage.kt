package com.bebeplace.bebeplaceapi.product.domain.model

import java.time.LocalDateTime
import java.util.*

data class ProductImage(
    val id: Long? = null,
    val productId: UUID,
    val imageUrl: String,
    val originalFilename: String,
    val fileSize: Long,
    val order: Int,
    val isMain: Boolean = false,
    val createdAt: LocalDateTime = LocalDateTime.now()
) {
    companion object {
        fun create(
            productId: UUID,
            imageUrl: String,
            originalFilename: String,
            fileSize: Long,
            order: Int,
            isMain: Boolean = false
        ): ProductImage {
            return ProductImage(
                productId = productId,
                imageUrl = imageUrl,
                originalFilename = originalFilename,
                fileSize = fileSize,
                order = order,
                isMain = isMain
            )
        }
    }
}