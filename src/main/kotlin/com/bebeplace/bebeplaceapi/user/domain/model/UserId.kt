package com.bebeplace.bebeplaceapi.user.domain.model

import com.github.f4b6a3.uuid.UuidCreator
import java.util.*

@JvmInline
value class UserId(private val value: UUID) {
    
    companion object {
        fun generate(): UserId = UserId(UuidCreator.getTimeOrderedEpoch())
        fun from(value: UUID): UserId = UserId(value)
    }
    
    fun getValue(): UUID = value
    override fun toString(): String = value.toString()
}