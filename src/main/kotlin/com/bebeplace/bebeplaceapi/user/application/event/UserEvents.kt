package com.bebeplace.bebeplaceapi.user.application.event

import com.bebeplace.bebeplaceapi.common.event.BaseDomainEvent
import com.bebeplace.bebeplaceapi.user.application.dto.RegisterUserRequest.BabyInfo
import com.bebeplace.bebeplaceapi.user.application.dto.RegisterUserRequest.RegionInfo
import java.util.*

data class UserCreatedEvent(
    val userId: UUID,
    val email: String,
    val nickname: String,
    val babies: List<BabyInfo>? = null,
    val regions: List<RegionInfo>? = null
) : BaseDomainEvent() {
    override val eventType: String = "UserCreated"
}

data class UserUpdatedEvent(
    val userId: UUID,
    val email: String,
    val nickname: String,
    val babies: List<BabyInfo>? = null,
    val regions: List<RegionInfo>? = null,
    val previousBabies: List<BabyInfo>? = null,
    val previousRegions: List<RegionInfo>? = null
) : BaseDomainEvent() {
    override val eventType: String = "UserUpdated"
}

data class UserDeletedEvent(
    val userId: UUID,
    val email: String
) : BaseDomainEvent() {
    override val eventType: String = "UserDeleted"
}

data class BabiesUpdatedEvent(
    val userId: UUID,
    val babies: List<BabyInfo>,
    val action: BabyAction
) : BaseDomainEvent() {
    override val eventType: String = "BabiesUpdated"
}

data class RegionsUpdatedEvent(
    val userId: UUID,
    val regions: List<RegionInfo>,
    val action: RegionAction
) : BaseDomainEvent() {
    override val eventType: String = "RegionsUpdated"
}

enum class BabyAction { CREATED, UPDATED, DELETED }
enum class RegionAction { CREATED, UPDATED, DELETED }