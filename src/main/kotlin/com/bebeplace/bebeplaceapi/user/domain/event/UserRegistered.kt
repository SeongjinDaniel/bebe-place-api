package com.bebeplace.bebeplaceapi.user.domain.event

import com.bebeplace.bebeplaceapi.common.domain.DomainEvent
import java.util.*

class UserRegistered(
    val userId: UUID,
    val email: String
) : DomainEvent()