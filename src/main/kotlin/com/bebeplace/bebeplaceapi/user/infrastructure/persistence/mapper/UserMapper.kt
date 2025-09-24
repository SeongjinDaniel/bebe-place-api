package com.bebeplace.bebeplaceapi.user.infrastructure.persistence.mapper

import com.bebeplace.bebeplaceapi.common.types.Email
import com.bebeplace.bebeplaceapi.user.domain.model.*
import com.bebeplace.bebeplaceapi.user.infrastructure.persistence.entity.*
import org.springframework.stereotype.Component
import java.util.*

@Component
class UserMapper(
    private val babyMapper: BabyMapper,
    private val userRegionMapper: UserRegionMapper
) {
    
    fun toDomain(entity: UserEntity): User {
        val babies = entity.babies.map { babyMapper.toDomain(it) }
        val regions = entity.regions.map { userRegionMapper.toDomain(it) }
        
        return User.restore(
            id = entity.id,
            email = Email.of(entity.email),
            password = entity.password,
            profile = UserProfile(
                nickname = entity.nickname,
                phoneNumber = entity.phoneNumber,
                profileImageUrl = entity.profileImageUrl,
                bio = entity.bio,
                birthDate = entity.birthDate
            ),
            trustScore = TrustScore(entity.trustScore, entity.transactionCount),
            status = entity.status,
            lastLoginAt = entity.lastLoginAt,
            lastTokenRefreshAt = entity.lastTokenRefreshAt,
            babies = babies,
            regions = regions
        )
    }
    
    fun toEntity(domain: User): UserEntity {
        val entity = UserEntity(
            id = domain.getId(),
            email = domain.getEmail().getValue(),
            password = domain.getPassword(),
            nickname = domain.getProfile().getNickname(),
            phoneNumber = domain.getProfile().getPhoneNumber(),
            profileImageUrl = domain.getProfile().getProfileImageUrl(),
            bio = domain.getProfile().getBio(),
            birthDate = domain.getProfile().getBirthDate(),
            trustScore = domain.getTrustScore().getScore(),
            transactionCount = domain.getTrustScore().getTransactionCount(),
            status = domain.getStatus(),
            lastLoginAt = domain.getLastLoginAt(),
            lastTokenRefreshAt = domain.getLastTokenRefreshAt()
        )
        
        // Add babies
        domain.getBabies().forEach { baby ->
            val babyEntity = babyMapper.toEntity(baby, entity)
            entity.babies.add(babyEntity)
        }
        
        // Add regions  
        domain.getRegions().forEach { region ->
            val regionEntity = userRegionMapper.toEntity(region, entity)
            entity.regions.add(regionEntity)
        }
        
        return entity
    }
}