package com.bebeplace.bebeplaceapi.user.infrastructure.persistence.mapper

import com.bebeplace.bebeplaceapi.user.domain.model.*
import com.bebeplace.bebeplaceapi.user.infrastructure.persistence.entity.*
import org.springframework.stereotype.Component

@Component
class BabyMapper {
    
    fun toDomain(entity: BabyEntity): Baby {
        return Baby.restore(
            id = entity.id,
            userId = entity.user.id,
            name = entity.name,
            gender = entity.gender,
            birthDate = entity.birthDate,
            interests = entity.interests.toSet()
        )
    }
    
    fun toEntity(domain: Baby, userEntity: UserEntity): BabyEntity {
        val entity = BabyEntity(
            id = domain.getId(),
            user = userEntity,
            name = domain.getName(),
            gender = domain.getGender(),
            birthDate = domain.getBirthDate()
        )
        
        // Add interests
        domain.getInterests().forEach { interest ->
            entity.interests.add(interest)
        }
        
        return entity
    }
}