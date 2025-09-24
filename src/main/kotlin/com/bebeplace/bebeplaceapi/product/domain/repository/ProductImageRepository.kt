package com.bebeplace.bebeplaceapi.product.domain.repository

import com.bebeplace.bebeplaceapi.product.domain.model.ProductImage
import java.util.*

interface ProductImageRepository {
    fun save(productImage: ProductImage): ProductImage
    fun findByProductId(productId: UUID): List<ProductImage>
    fun deleteByProductId(productId: UUID)
    fun deleteById(id: Long)
    fun findById(id: Long): ProductImage?
}