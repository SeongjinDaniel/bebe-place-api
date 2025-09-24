package com.bebeplace.bebeplaceapi.product.domain.model

import com.bebeplace.bebeplaceapi.common.domain.ValueObject
import com.bebeplace.bebeplaceapi.common.exception.ValidationException
import com.bebeplace.bebeplaceapi.common.types.Money

data class ShippingInfo(
    val isIncluded: Boolean,
    val shippingCost: Money?
) : ValueObject() {
    
    init {
        validate()
    }
    
    private fun validate() {
        if (!isIncluded && shippingCost == null) {
            throw ValidationException("배송비가 별도인 경우 배송비를 입력해야 합니다.")
        }
        if (isIncluded && shippingCost != null) {
            throw ValidationException("배송비가 포함된 경우 배송비를 입력할 수 없습니다.")
        }
        val cost = shippingCost
        if (cost != null && !cost.isPositive() && !cost.isZero()) {
            throw ValidationException("배송비는 0 이상이어야 합니다.")
        }
    }
    
    fun getTotalCostWith(productPrice: Money): Money {
        return if (isIncluded) {
            productPrice
        } else {
            productPrice.add(shippingCost ?: Money.zero())
        }
    }
    
    fun getShippingDescription(): String {
        return if (isIncluded) {
            "배송비 포함"
        } else {
            "배송비 별도 ${shippingCost ?: Money.zero()}"
        }
    }
    
    companion object {
        fun included(): ShippingInfo = ShippingInfo(isIncluded = true, shippingCost = null)
        
        fun separate(cost: Money): ShippingInfo = ShippingInfo(isIncluded = false, shippingCost = cost)
        
        fun free(): ShippingInfo = ShippingInfo(isIncluded = false, shippingCost = Money.zero())
    }
    
    override fun equalityComponents(): List<Any?> = listOf(isIncluded, shippingCost)
}