package com.bebeplace.bebeplaceapi.product.domain.model

import com.bebeplace.bebeplaceapi.common.domain.AggregateRoot
import com.bebeplace.bebeplaceapi.common.types.Money
import com.bebeplace.bebeplaceapi.product.domain.event.ProductCreated
import java.time.LocalDateTime
import java.util.*

class Product(
    private val id: ProductId,
    private val sellerId: UUID,
    private var title: String,
    private var category: ProductCategory,
    private var price: Money,
    private var shippingInfo: ShippingInfo,
    private var description: String,
    private var productType: ProductType,
    private var condition: ProductCondition,
    private var status: ProductStatus = ProductStatus.ACTIVE,
    private var viewCount: Int = 0,
    private var likeCount: Int = 0,
    private var commentCount: Int = 0,
    private val ageGroups: MutableSet<AgeGroup> = mutableSetOf()
) : AggregateRoot<ProductId>() {
    
    companion object {
        fun create(
            id: ProductId,
            sellerId: UUID,
            title: String,
            category: ProductCategory,
            price: Money,
            shippingInfo: ShippingInfo,
            description: String,
            productType: ProductType,
            condition: ProductCondition
        ): Product {
            val product = Product(
                id = id,
                sellerId = sellerId,
                title = title,
                category = category,
                price = price,
                shippingInfo = shippingInfo,
                description = description,
                productType = productType,
                condition = condition
            )
            
            // 도메인 이벤트 발행
            product.addDomainEvent(
                ProductCreated(
                    productId = id.getValue(),
                    sellerId = sellerId,
                    title = title,
                    category = category,
                    price = price,
                    createdAt = LocalDateTime.now()
                )
            )
            
            return product
        }
    }
    
    override fun getId(): ProductId = id
    
    // Getters
    fun getSellerId(): UUID = sellerId
    fun getTitle(): String = title
    fun getCategory(): ProductCategory = category
    fun getPrice(): Money = price
    fun getShippingInfo(): ShippingInfo = shippingInfo
    fun getDescription(): String = description
    fun getProductType(): ProductType = productType
    fun getCondition(): ProductCondition = condition
    fun getStatus(): ProductStatus = status
    fun getViewCount(): Int = viewCount
    fun getLikeCount(): Int = likeCount
    fun getCommentCount(): Int = commentCount
    
    // Business methods
    fun updateDetails(
        title: String,
        category: ProductCategory,
        price: Money,
        shippingInfo: ShippingInfo,
        description: String,
        condition: ProductCondition
    ) {
        this.title = title
        this.category = category
        this.price = price
        this.shippingInfo = shippingInfo
        this.description = description
        this.condition = condition
    }
    
    fun changeStatus(newStatus: ProductStatus) {
        require(status.canTransitionTo(newStatus)) {
            "Cannot transition from ${status.displayName} to ${newStatus.displayName}"
        }
        this.status = newStatus
    }
    
    fun increaseViewCount() {
        this.viewCount += 1
    }
    
    fun increaseLikeCount() {
        this.likeCount += 1
    }
    
    fun decreaseLikeCount() {
        if (this.likeCount > 0) {
            this.likeCount -= 1
        }
    }
    
    fun increaseCommentCount() {
        this.commentCount += 1
    }
    
    fun decreaseCommentCount() {
        if (this.commentCount > 0) {
            this.commentCount -= 1
        }
    }
    
    fun markAsSold() {
        changeStatus(ProductStatus.SOLD)
    }
    
    fun markAsInactive() {
        changeStatus(ProductStatus.INACTIVE)
    }
    
    fun reactivate() {
        require(status in setOf(ProductStatus.INACTIVE, ProductStatus.SOLD)) {
            "Cannot reactivate product with status: ${status.displayName}"
        }
        changeStatus(ProductStatus.ACTIVE)
    }
    
    fun isOwnedBy(userId: UUID): Boolean = sellerId == userId
    
    fun isActive(): Boolean = status == ProductStatus.ACTIVE
    
    fun isSold(): Boolean = status == ProductStatus.SOLD
    
    fun getTotalPrice(): Money = shippingInfo.getTotalCostWith(price)

    fun addAgeGroup(ageGroup: AgeGroup) {
        ageGroups.add(ageGroup)
    }
    
    fun removeAgeGroup(ageGroup: AgeGroup) {
        ageGroups.remove(ageGroup)
    }
    
    fun getAgeGroups(): Set<AgeGroup> = ageGroups.toSet()
    
    fun hasAgeGroup(ageGroup: AgeGroup): Boolean = ageGroups.contains(ageGroup)
}