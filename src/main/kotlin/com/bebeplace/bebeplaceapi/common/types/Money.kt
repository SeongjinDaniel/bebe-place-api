package com.bebeplace.bebeplaceapi.common.types

import com.bebeplace.bebeplaceapi.common.domain.ValueObject
import com.bebeplace.bebeplaceapi.common.exception.ValidationException
import java.math.BigDecimal
import java.math.RoundingMode

data class Money(
    val amount: BigDecimal
) : ValueObject() {
    
    init {
        validate()
    }
    
    private fun validate() {
        if (amount < BigDecimal.ZERO) {
            throw ValidationException("금액은 0 이상이어야 합니다.")
        }
        if (amount.scale() > 2) {
            throw ValidationException("금액은 소수점 2자리까지만 허용됩니다.")
        }
    }
    
    fun add(other: Money): Money = Money(amount.add(other.amount))
    
    fun subtract(other: Money): Money = Money(amount.subtract(other.amount))
    
    fun multiply(multiplier: BigDecimal): Money = 
        Money(amount.multiply(multiplier).setScale(2, RoundingMode.HALF_UP))
    
    fun isZero(): Boolean = amount.compareTo(BigDecimal.ZERO) == 0
    
    fun isPositive(): Boolean = amount.compareTo(BigDecimal.ZERO) > 0
    
    companion object {
        fun of(amount: Int): Money = Money(BigDecimal(amount))
        fun of(amount: Double): Money = Money(BigDecimal.valueOf(amount).setScale(2, RoundingMode.HALF_UP))
        fun zero(): Money = Money(BigDecimal.ZERO)
    }
    
    override fun equalityComponents(): List<Any?> = listOf(amount)
    
    override fun toString(): String = "₩${amount}"
}