package com.bebeplace.bebeplaceapi.product.domain.model

import com.bebeplace.bebeplaceapi.common.domain.ValueObject
import com.github.f4b6a3.uuid.UuidCreator
import java.util.*

data class ProductId(
    private val value: UUID
) : ValueObject() {
    
    fun getValue(): UUID = value
    
    override fun toString(): String = value.toString()
    
    override fun equalityComponents(): List<Any?> = listOf(value)
    
    companion object {
        fun generate(): ProductId = ProductId(UuidCreator.getTimeOrderedEpoch())
        
        fun of(value: UUID): ProductId = ProductId(value)
        
        fun of(value: String): ProductId = ProductId(UuidCreator.fromString(value))
    }
}