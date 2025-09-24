package com.bebeplace.bebeplaceapi.product.domain.repository

import com.bebeplace.bebeplaceapi.product.domain.model.Product
import com.bebeplace.bebeplaceapi.product.domain.model.ProductCategory
import com.bebeplace.bebeplaceapi.product.domain.model.ProductId
import com.bebeplace.bebeplaceapi.product.domain.model.ProductSortType
import com.bebeplace.bebeplaceapi.product.domain.model.ProductStatus
import java.util.*

data class ProductListFilter(
    val status: ProductStatus? = null,
    val sellerId: UUID? = null,
    val category: ProductCategory? = null,
    val sortType: ProductSortType = ProductSortType.LATEST
)

data class ProductListResult(
    val products: List<Product>,
    val hasNext: Boolean,
    val nextCursor: String?
)

interface ProductRepository {
    fun save(product: Product): Product
    fun findById(id: ProductId): Product?
    fun findByIdAndSellerId(id: ProductId, sellerId: UUID): Product?
    fun existsById(id: ProductId): Boolean
    fun deleteById(id: ProductId)
    fun findAllBySellerId(sellerId: UUID): List<Product>
    fun findAllByStatus(status: ProductStatus): List<Product>
    
    fun increaseViewCount(id: ProductId): Int
    
    fun findProductsWithCursor(
        cursor: String?,
        size: Int,
        filter: ProductListFilter? = null
    ): ProductListResult
}