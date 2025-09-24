package com.bebeplace.bebeplaceapi.user.domain.model

import java.util.UUID

class UserRegion(
    private val id: Long = 0L,
    private val userId: UUID,
    private val regionCode: String,
    private val sido: String,
    private val sigungu: String,
    private val dong: String? = null,
    private var priority: Int = 1
) {
    
    companion object {
        const val MAX_REGIONS_PER_USER = 3
        
        fun create(
            userId: UUID,
            regionCode: String,
            sido: String,
            sigungu: String,
            dong: String? = null,
            priority: Int = 1
        ): UserRegion {
            require(regionCode.isNotBlank()) { "Region code cannot be blank" }
            require(sido.isNotBlank()) { "Sido cannot be blank" }
            require(sigungu.isNotBlank()) { "Sigungu cannot be blank" }
            require(priority in 1..MAX_REGIONS_PER_USER) { 
                "Priority must be between 1 and $MAX_REGIONS_PER_USER" 
            }
            
            return UserRegion(
                userId = userId,
                regionCode = regionCode,
                sido = sido,
                sigungu = sigungu,
                dong = dong,
                priority = priority
            )
        }
        
        fun restore(
            id: Long,
            userId: UUID,
            regionCode: String,
            sido: String,
            sigungu: String,
            dong: String? = null,
            priority: Int = 1
        ): UserRegion {
            return UserRegion(
                id = id,
                userId = userId,
                regionCode = regionCode,
                sido = sido,
                sigungu = sigungu,
                dong = dong,
                priority = priority
            )
        }
    }
    
    fun getId(): Long = id
    fun getUserId(): UUID = userId
    fun getRegionCode(): String = regionCode
    fun getSido(): String = sido
    fun getSigungu(): String = sigungu
    fun getDong(): String? = dong
    fun getPriority(): Int = priority
    
    fun updatePriority(newPriority: Int) {
        require(newPriority in 1..MAX_REGIONS_PER_USER) { 
            "Priority must be between 1 and $MAX_REGIONS_PER_USER" 
        }
        this.priority = newPriority
    }
    
    fun getFullAddress(): String {
        return if (dong != null) {
            "$sido $sigungu $dong"
        } else {
            "$sido $sigungu"
        }
    }
    
    fun getShortAddress(): String {
        return "$sido $sigungu"
    }
}