package com.bebeplace.bebeplaceapi.product.application.dto

data class SellerProductsRequest(
    val sellerId: String,
    val sortBy: String = "createdAt",
    val sortDir: String = "desc",
    val page: Int = 0,
    val size: Int = 20
) {
    fun toPageable(): org.springframework.data.domain.Pageable {
        val sort = org.springframework.data.domain.Sort.by(
            if (sortDir.lowercase() == "desc") 
                org.springframework.data.domain.Sort.Direction.DESC 
            else 
                org.springframework.data.domain.Sort.Direction.ASC,
            sortBy
        )
        return org.springframework.data.domain.PageRequest.of(page, size, sort)
    }
}