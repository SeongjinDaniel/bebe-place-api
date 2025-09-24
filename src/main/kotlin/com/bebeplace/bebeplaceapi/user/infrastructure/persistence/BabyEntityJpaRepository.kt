package com.bebeplace.bebeplaceapi.user.infrastructure.persistence

import com.bebeplace.bebeplaceapi.user.infrastructure.persistence.entity.BabyEntity
import org.springframework.data.jpa.repository.JpaRepository
import org.springframework.data.jpa.repository.Modifying
import org.springframework.data.jpa.repository.Query
import org.springframework.stereotype.Repository
import org.springframework.transaction.annotation.Transactional
import java.util.*

@Repository
interface BabyEntityJpaRepository : JpaRepository<BabyEntity, Long> {
    
    fun findByUserId(userId: UUID): List<BabyEntity>
    
    @Modifying
    @Transactional
    @Query("DELETE FROM BabyEntity b WHERE b.user.id = :userId")
    fun deleteByUserId(userId: UUID): Int
    
    fun existsByUserId(userId: UUID): Boolean
}