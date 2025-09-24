package com.bebeplace.bebeplaceapi.common.types

import com.bebeplace.bebeplaceapi.common.domain.ValueObject
import com.bebeplace.bebeplaceapi.common.exception.ValidationException

data class PhoneNumber(
    val value: String
) : ValueObject() {
    
    init {
        validate()
    }
    
    private fun validate() {
        val phoneRegex = "^01[0-9]{8,9}$".toRegex()
        if (!phoneRegex.matches(value.replace("-", ""))) {
            throw ValidationException("올바른 휴대폰 번호 형식이 아닙니다. (01012345678)")
        }
    }
    
    fun getFormattedValue(): String = value.replace("-", "")
    
    override fun toString(): String = value
    
    override fun equalityComponents(): List<Any?> = listOf(value)
}