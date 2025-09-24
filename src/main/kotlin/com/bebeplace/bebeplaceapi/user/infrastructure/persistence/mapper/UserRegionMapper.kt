package com.bebeplace.bebeplaceapi.user.infrastructure.persistence.mapper

import com.bebeplace.bebeplaceapi.user.domain.model.UserRegion
import com.bebeplace.bebeplaceapi.user.infrastructure.persistence.entity.UserEntity
import com.bebeplace.bebeplaceapi.user.infrastructure.persistence.entity.UserRegionEntity
import org.springframework.stereotype.Component

@Component
class UserRegionMapper {
    
    fun toDomain(entity: UserRegionEntity): UserRegion {
        return UserRegion.restore(
            id = entity.id,
            userId = entity.user.id,
            regionCode = entity.regionCode,
            sido = entity.sido,
            sigungu = entity.sigungu,
            dong = entity.dong,
            priority = entity.priority
        )
    }
    
    fun toEntity(domain: UserRegion, userEntity: UserEntity): UserRegionEntity {
        return UserRegionEntity(
            id = domain.getId(),
            user = userEntity,
            regionCode = domain.getRegionCode(),
            sido = domain.getSido(),
            sigungu = domain.getSigungu(),
            dong = domain.getDong(),
            priority = domain.getPriority()
        )
    }
}