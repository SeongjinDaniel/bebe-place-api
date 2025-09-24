package com.bebeplace.bebeplaceapi.product.application.usecase

import com.bebeplace.bebeplaceapi.product.application.dto.ProductListItemDto
import com.bebeplace.bebeplaceapi.product.application.dto.ProductListRequest
import com.bebeplace.bebeplaceapi.product.application.dto.ProductListResponse
import com.bebeplace.bebeplaceapi.product.domain.repository.ProductListFilter
import com.bebeplace.bebeplaceapi.product.domain.repository.ProductRepository
import com.bebeplace.bebeplaceapi.product.infrastructure.persistence.ProductJpaRepository
import org.springframework.stereotype.Service
import org.springframework.transaction.annotation.Transactional

@Service
@Transactional(readOnly = true)
class GetProductListUseCase(
    private val productRepository: ProductRepository,
    private val productJpaRepository: ProductJpaRepository
) {
    
    fun execute(request: ProductListRequest): ProductListResponse {
        val filter = ProductListFilter(
            status = request.status,
            sellerId = request.sellerId,
            category = request.category,
            sortType = request.sortType
        )
        
        val result = productRepository.findProductsWithCursor(
            cursor = request.cursor,
            size = request.size,
            filter = filter
        )
        
        val productListItems = result.products.map { product ->
            val entity = productJpaRepository.findById(product.getId().getValue()).orElseThrow()
            ProductListItemDto.fromDomain(product, entity.createdAt)
        }
        
        return ProductListResponse(
            products = productListItems,
            hasNext = result.hasNext,
            nextCursor = result.nextCursor
        )
    }
}