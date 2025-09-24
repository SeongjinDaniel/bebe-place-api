package com.bebeplace.bebeplaceapi.product.application.service

import com.bebeplace.bebeplaceapi.common.types.Money
import com.bebeplace.bebeplaceapi.product.application.dto.CreateProductRequest
import com.bebeplace.bebeplaceapi.product.domain.model.*
import com.bebeplace.bebeplaceapi.product.domain.repository.ProductRepository
import org.slf4j.LoggerFactory
import org.springframework.stereotype.Service
import org.springframework.web.multipart.MultipartFile
import java.util.*

@Service
class ProductCreationService(
    private val productRepository: ProductRepository
) {
    
    private val logger = LoggerFactory.getLogger(this::class.java)
    
    fun createProduct(
        request: CreateProductRequest,
        sellerId: UUID,
        productId: ProductId
    ): Product {
        logger.info("Creating product with ID: ${productId.getValue()}")

        val shippingInfo = createShippingInfo(request)

        val product = Product.create(
            id = productId,
            sellerId = sellerId,
            title = request.title,
            category = request.category,
            price = Money(request.price),
            shippingInfo = shippingInfo,
            description = request.description,
            productType = request.productType,
            condition = request.condition
        )

        addAgeGroupsToProduct(product, request.ageGroups)
        return productRepository.save(product)
    }
    
    private fun createShippingInfo(request: CreateProductRequest): ShippingInfo {
        return if (request.shippingIncluded) {
            ShippingInfo.included()
        } else {
            ShippingInfo.separate(Money(request.shippingCost!!))
        }
    }
    
    private fun addAgeGroupsToProduct(product: Product, ageGroups: List<AgeGroup>) {
        ageGroups.forEach { ageGroup ->
            product.addAgeGroup(ageGroup)
        }
    }
}