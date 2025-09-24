package com.bebeplace.bebeplaceapi.product.infrastructure.persistence

import org.springframework.data.jpa.repository.JpaRepository
import org.springframework.data.jpa.repository.Modifying
import org.springframework.data.jpa.repository.Query
import org.springframework.data.repository.query.Param
import org.springframework.stereotype.Repository
import org.springframework.transaction.annotation.Transactional
import java.util.*

@Repository
interface ProductJpaRepository : JpaRepository<ProductEntity, UUID>, ProductCustomRepository {
    
    // ✅ 간단한 JPA 메서드들은 유지 (단일 조건, 자동 쿼리 생성)
    fun findBySellerId(sellerId: UUID): List<ProductEntity>
    fun findByTitleContainingIgnoreCase(keyword: String): List<ProductEntity>
    fun findByCategory(category: com.bebeplace.bebeplaceapi.product.domain.model.ProductCategory): List<ProductEntity>
    
    // ✅ JPA가 효율적인 업데이트 연산은 유지
    @Modifying
    @Transactional
    @Query("UPDATE ProductEntity p SET p.viewCount = p.viewCount + 1 WHERE p.id = :id")
    fun increaseViewCount(@Param("id") id: UUID): Int
    
    // ❌ 복잡한 커서 페이지네이션 쿼리들은 제거 -> JOOQ로 통합됨
    // 이제 findWithDynamicFilter()로 모든 동적 쿼리 처리
}