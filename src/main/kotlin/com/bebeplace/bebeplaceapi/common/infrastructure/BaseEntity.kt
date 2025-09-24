package com.bebeplace.bebeplaceapi.common.infrastructure

import jakarta.persistence.*
import org.springframework.data.annotation.CreatedBy
import org.springframework.data.annotation.CreatedDate
import org.springframework.data.annotation.LastModifiedBy
import org.springframework.data.annotation.LastModifiedDate
import org.springframework.data.jpa.domain.support.AuditingEntityListener
import java.time.LocalDateTime
import java.util.*

@MappedSuperclass
@EntityListeners(AuditingEntityListener::class)
abstract class BaseEntity {
    
    @CreatedDate
    @Column(name = "created_at", nullable = false, updatable = false)
    var createdAt: LocalDateTime = LocalDateTime.now()
        protected set
    
    @LastModifiedDate
    @Column(name = "updated_at", nullable = false)
    var updatedAt: LocalDateTime = LocalDateTime.now()
        protected set
    
    @CreatedBy
    @Column(name = "created_by_user_id")
    var createdByUserId: UUID? = null
        protected set
    
    @LastModifiedBy
    @Column(name = "updated_by_user_id")
    var updatedByUserId: UUID? = null
        protected set
    
    @Column(name = "created_ip", length = 45)
    var createdIp: String? = null
    
    @Column(name = "updated_ip", length = 45)
    var updatedIp: String? = null
    
    fun updateAuditInfo(updatedByUserId: UUID?, updatedIp: String?) {
        this.updatedByUserId = updatedByUserId
        this.updatedIp = updatedIp
        this.updatedAt = LocalDateTime.now()
    }
    
    fun setCreationAuditInfo(createdByUserId: UUID?, createdIp: String?) {
        this.createdByUserId = createdByUserId
        this.createdIp = createdIp
    }
}