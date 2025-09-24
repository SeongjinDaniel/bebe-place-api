package com.bebeplace.bebeplaceapi.product.infrastructure.persistence

import com.bebeplace.bebeplaceapi.common.infrastructure.BaseEntity
import com.bebeplace.bebeplaceapi.common.types.Money
import com.bebeplace.bebeplaceapi.product.domain.model.*
import jakarta.persistence.*
import java.math.BigDecimal
import java.util.*

@Entity
@Table(name = "products")
data class ProductEntity(
    @Id
    @Column(name = "id")
    val id: UUID,
    
    @Column(name = "seller_id", nullable = false)
    val sellerId: UUID,
    
    @Column(nullable = false, length = 255)
    val title: String,
    
    @Enumerated(EnumType.STRING)
    @Column(nullable = false)
    val category: ProductCategory,
    
    @Column(nullable = false, precision = 10, scale = 2)
    val price: BigDecimal,
    
    // ShippingInfo 관련 필드들
    @Column(name = "shipping_included", nullable = false)
    val shippingIncluded: Boolean,
    
    @Column(name = "shipping_cost", precision = 10, scale = 2)
    val shippingCost: BigDecimal?,
    
    @Column(nullable = false, columnDefinition = "TEXT")
    val description: String,
    
    @Enumerated(EnumType.STRING)
    @Column(name = "product_type", nullable = false)
    val productType: ProductType,
    
    @Enumerated(EnumType.STRING)
    @Column(nullable = false)
    val condition: ProductCondition,
    
    @Enumerated(EnumType.STRING)
    @Column(nullable = false)
    val status: ProductStatus = ProductStatus.ACTIVE,
    
    @Column(name = "view_count", nullable = false)
    val viewCount: Int = 0,
    
    @Column(name = "like_count", nullable = false)
    val likeCount: Int = 0,
    
    @Column(name = "comment_count", nullable = false)
    val commentCount: Int = 0
) : BaseEntity() {
    
    fun toDomain(): Product {
        val productId = ProductId(id)
        val moneyPrice = Money(price)
        val shippingInfo = if (shippingIncluded) {
            ShippingInfo.included()
        } else {
            ShippingInfo.separate(Money(shippingCost!!))
        }
        
        // Product 생성자를 직접 호출하여 도메인 객체 생성
        return Product(
            id = productId,
            sellerId = sellerId,
            title = title,
            category = category,
            price = moneyPrice,
            shippingInfo = shippingInfo,
            description = description,
            productType = productType,
            condition = condition,
            status = status,
            viewCount = viewCount,
            likeCount = likeCount,
            commentCount = commentCount
        )
    }
    
    companion object {
        fun fromDomain(product: Product): ProductEntity {
            return ProductEntity(
                id = product.getId().getValue(),
                sellerId = product.getSellerId(),
                title = product.getTitle(),
                category = product.getCategory(),
                price = product.getPrice().amount,
                shippingIncluded = product.getShippingInfo().isIncluded,
                shippingCost = product.getShippingInfo().shippingCost?.amount,
                description = product.getDescription(),
                productType = product.getProductType(),
                condition = product.getCondition(),
                status = product.getStatus(),
                viewCount = product.getViewCount(),
                likeCount = product.getLikeCount(),
                commentCount = product.getCommentCount()
            )
        }
    }
}