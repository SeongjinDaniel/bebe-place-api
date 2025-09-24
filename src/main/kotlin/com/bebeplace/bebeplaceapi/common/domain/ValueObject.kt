package com.bebeplace.bebeplaceapi.common.domain

import jakarta.persistence.Embeddable

@Embeddable
abstract class ValueObject {
    
    override fun equals(other: Any?): Boolean {
        if (this === other) return true
        if (other == null || this::class != other::class) return false
        return this.equalityComponents() == (other as ValueObject).equalityComponents()
    }
    
    override fun hashCode(): Int {
        return equalityComponents().hashCode()
    }
    
    protected abstract fun equalityComponents(): List<Any?>
}