package com.bebeplace.bebeplaceapi.common.types

import com.bebeplace.bebeplaceapi.common.domain.ValueObject
import jakarta.persistence.Embeddable

@Embeddable
data class Email(
    private val value: String
) : ValueObject() {
    
    init {
        require(isValid(value)) { "Invalid email format: $value" }
    }
    
    fun getValue(): String = value
    
    override fun equalityComponents(): List<Any?> = listOf(value)
    
    companion object {
        private val EMAIL_REGEX = "^[A-Za-z0-9+_.-]+@[A-Za-z0-9.-]+\\.[A-Za-z]{2,}$".toRegex()
        
        fun isValid(email: String): Boolean {
            return email.matches(EMAIL_REGEX)
        }
        
        fun of(email: String): Email = Email(email)
    }
}