package com.bebeplace.bebeplaceapi.product.infrastructure.persistence

import java.time.LocalDateTime
import java.util.UUID

/**
 * 커서 기반 페이지네이션을 위한 파라미터 객체
 */
data class CursorParams(
    val createdAt: LocalDateTime?,
    val id: UUID?,
    val pageSize: Int
) {
    companion object {
        fun create(createdAt: LocalDateTime?, id: UUID?, size: Int): CursorParams {
            return CursorParams(createdAt, id, size + 1) // +1 for hasNext check
        }
    }
}