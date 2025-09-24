package com.bebeplace.bebeplaceapi.user.infrastructure.persistence

import com.bebeplace.bebeplaceapi.user.infrastructure.persistence.entity.UserRegionEntity
import org.springframework.data.jpa.repository.JpaRepository
import org.springframework.data.jpa.repository.Modifying
import org.springframework.data.jpa.repository.Query
import org.springframework.stereotype.Repository
import org.springframework.transaction.annotation.Transactional
import java.util.*

@Repository
interface UserRegionEntityJpaRepository : JpaRepository<UserRegionEntity, Long> {
    
    fun findByUserId(userId: UUID): List<UserRegionEntity>
    
    @Modifying
    @Transactional
    @Query("DELETE FROM UserRegionEntity r WHERE r.user.id = :userId")
    fun deleteByUserId(userId: UUID): Int
    
    fun existsByUserId(userId: UUID): Boolean
}