package com.bebeplace.bebeplaceapi.product.domain.model

enum class ProductStatus(val displayName: String) {
    ACTIVE("판매중"),
    SOLD("판매완료"),
    INACTIVE("비활성"),
    DELETED("삭제됨");
    
    
    fun canTransitionTo(newStatus: ProductStatus): Boolean {
        return when (this) {
            ACTIVE -> newStatus in setOf(SOLD, INACTIVE, DELETED)
            SOLD -> newStatus in setOf(ACTIVE, DELETED)
            INACTIVE -> newStatus in setOf(ACTIVE, DELETED)
            DELETED -> false
        }
    }
}