package com.bebeplace.bebeplaceapi.common.event

import com.github.f4b6a3.uuid.UuidCreator
import java.time.LocalDateTime
import java.util.*

interface DomainEvent {
    val eventId: UUID
    val timestamp: LocalDateTime
    val eventType: String
}

abstract class BaseDomainEvent(
    override val eventId: UUID = UuidCreator.getTimeOrderedEpoch(),
    override val timestamp: LocalDateTime = LocalDateTime.now()
) : DomainEvent