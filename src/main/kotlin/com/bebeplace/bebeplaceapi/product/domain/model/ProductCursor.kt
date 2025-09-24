package com.bebeplace.bebeplaceapi.product.domain.model

import java.time.LocalDateTime
import java.util.*

data class ProductCursor(
    val createdAt: LocalDateTime,
    val id: UUID
) {
    
    fun encode(): String {
        val cursorString = "$createdAt|$id"
        return Base64.getEncoder().encodeToString(cursorString.toByteArray())
    }
    
    companion object {
        fun decode(cursor: String): ProductCursor {
            val decoded = String(Base64.getDecoder().decode(cursor))
            val parts = decoded.split("|")
            
            require(parts.size == 2) { "Invalid cursor format" }
            
            return ProductCursor(
                createdAt = LocalDateTime.parse(parts[0]),
                id = UUID.fromString(parts[1])
            )
        }
        
        fun create(createdAt: LocalDateTime, id: UUID): ProductCursor {
            return ProductCursor(createdAt, id)
        }
    }
}