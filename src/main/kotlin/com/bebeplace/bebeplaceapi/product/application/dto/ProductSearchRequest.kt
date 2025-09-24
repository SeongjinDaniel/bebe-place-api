package com.bebeplace.bebeplaceapi.product.application.dto

import com.bebeplace.bebeplaceapi.product.domain.model.AgeGroup
import com.bebeplace.bebeplaceapi.product.domain.model.ProductCategory
import com.bebeplace.bebeplaceapi.product.domain.model.ProductCondition
import com.bebeplace.bebeplaceapi.product.domain.model.ProductType
import org.springframework.data.domain.Sort
import java.math.BigDecimal

data class ProductSearchRequest(
    val keyword: String? = null,
    val category: ProductCategory? = null,
    val ageGroups: List<AgeGroup>? = null,
    val productType: ProductType? = null,
    val condition: ProductCondition? = null,
    val minPrice: BigDecimal? = null,
    val maxPrice: BigDecimal? = null,
    val shippingIncluded: Boolean? = null,
    val sortBy: String = "createdAt",
    val sortDir: String = "desc",
    val page: Int = 0,
    val size: Int = 20
) {
    fun toPageable(): org.springframework.data.domain.Pageable {
        val sort = Sort.by(
            if (sortDir.lowercase() == "desc") 
                org.springframework.data.domain.Sort.Direction.DESC 
            else 
                org.springframework.data.domain.Sort.Direction.ASC,
            sortBy
        )
        return org.springframework.data.domain.PageRequest.of(page, size, sort)
    }
}