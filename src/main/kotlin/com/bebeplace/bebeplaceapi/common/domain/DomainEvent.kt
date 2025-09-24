package com.bebeplace.bebeplaceapi.common.domain

import com.github.f4b6a3.uuid.UuidCreator
import java.time.LocalDateTime

abstract class DomainEvent {
    val eventId: String = UuidCreator.getTimeOrderedEpoch().toString()
    val occurredAt: LocalDateTime = LocalDateTime.now()
}