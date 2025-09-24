package com.bebeplace.bebeplaceapi.product.domain.event

import com.bebeplace.bebeplaceapi.common.domain.DomainEvent
import com.bebeplace.bebeplaceapi.common.types.Money
import com.bebeplace.bebeplaceapi.product.domain.model.ProductCategory
import java.time.LocalDateTime
import java.util.*

data class ProductCreated(
    val productId: UUID,
    val sellerId: UUID,
    val title: String,
    val category: ProductCategory,
    val price: Money,
    val createdAt: LocalDateTime
) : DomainEvent()