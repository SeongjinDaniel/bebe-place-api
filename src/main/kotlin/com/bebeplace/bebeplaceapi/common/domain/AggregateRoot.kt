package com.bebeplace.bebeplaceapi.common.domain

import jakarta.persistence.*
import org.springframework.data.annotation.CreatedDate
import org.springframework.data.annotation.LastModifiedDate
import org.springframework.data.jpa.domain.support.AuditingEntityListener
import java.time.LocalDateTime

@MappedSuperclass
@EntityListeners(AuditingEntityListener::class)
abstract class AggregateRoot<ID> {
    
    @CreatedDate
    @Column(updatable = false)
    var createdAt: LocalDateTime = LocalDateTime.now()
        protected set
    
    @LastModifiedDate
    var updatedAt: LocalDateTime = LocalDateTime.now()
        protected set
    
    private val domainEvents = mutableListOf<DomainEvent>()
    
    protected fun addDomainEvent(event: DomainEvent) {
        domainEvents.add(event)
    }
    
    fun getDomainEvents(): List<DomainEvent> = domainEvents.toList()
    
    fun clearDomainEvents() {
        domainEvents.clear()
    }
    
    abstract fun getId(): ID?
}