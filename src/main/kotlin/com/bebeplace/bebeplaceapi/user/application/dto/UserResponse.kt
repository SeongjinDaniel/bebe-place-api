package com.bebeplace.bebeplaceapi.user.application.dto

import com.bebeplace.bebeplaceapi.user.domain.model.*
import java.time.LocalDate
import java.time.LocalDateTime
import java.util.*

data class UserResponse(
    val id: UUID,
    val email: String,
    val nickname: String,
    val phoneNumber: String?,
    val birthDate: LocalDate?,
    val profileImageUrl: String?,
    val bio: String?,
    val trustScore: Int,
    val trustLevel: String,
    val transactionCount: Int,
    val status: UserStatus,
    val lastLoginAt: LocalDateTime?,
    val lastTokenRefreshAt: LocalDateTime?,
    val babies: List<BabyResponse>,
    val regions: List<RegionResponse>,
    val createdAt: LocalDateTime,
    val updatedAt: LocalDateTime
) {
    companion object {
        fun from(user: User): UserResponse {
            return UserResponse(
                id = user.getId(),
                email = user.getEmail().getValue(),
                nickname = user.getProfile().getNickname(),
                phoneNumber = user.getProfile().getPhoneNumber(),
                birthDate = user.getProfile().getBirthDate(),
                profileImageUrl = user.getProfile().getProfileImageUrl(),
                bio = user.getProfile().getBio(),
                trustScore = user.getTrustScore().getScore(),
                trustLevel = user.getTrustScore().getLevel().name,
                transactionCount = user.getTrustScore().getTransactionCount(),
                status = user.getStatus(),
                lastLoginAt = user.getLastLoginAt(),
                lastTokenRefreshAt = user.getLastTokenRefreshAt(),
                babies = user.getBabies().map { BabyResponse.from(it) },
                regions = user.getRegions().map { RegionResponse.from(it) },
                createdAt = user.createdAt,
                updatedAt = user.updatedAt
            )
        }
    }
    
    data class BabyResponse(
        val id: Long,
        val name: String,
        val gender: BabyGender,
        val birthDate: LocalDate,
        val interests: Set<InterestCategory>,
        val ageInMonths: Int,
        val ageInYears: Int
    ) {
        companion object {
            fun from(baby: Baby): BabyResponse {
                return BabyResponse(
                    id = baby.getId(),
                    name = baby.getName(),
                    gender = baby.getGender(),
                    birthDate = baby.getBirthDate(),
                    interests = baby.getInterests(),
                    ageInMonths = baby.getAgeInMonths(),
                    ageInYears = baby.getAgeInYears()
                )
            }
        }
    }
    
    data class RegionResponse(
        val id: Long,
        val regionCode: String,
        val sido: String,
        val sigungu: String,
        val dong: String?,
        val priority: Int,
        val fullAddress: String,
        val shortAddress: String
    ) {
        companion object {
            fun from(userRegion: UserRegion): RegionResponse {
                return RegionResponse(
                    id = userRegion.getId(),
                    regionCode = userRegion.getRegionCode(),
                    sido = userRegion.getSido(),
                    sigungu = userRegion.getSigungu(),
                    dong = userRegion.getDong(),
                    priority = userRegion.getPriority(),
                    fullAddress = userRegion.getFullAddress(),
                    shortAddress = userRegion.getShortAddress()
                )
            }
        }
    }
}