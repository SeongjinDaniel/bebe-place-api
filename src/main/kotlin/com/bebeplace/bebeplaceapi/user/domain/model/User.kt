package com.bebeplace.bebeplaceapi.user.domain.model

import com.bebeplace.bebeplaceapi.common.domain.AggregateRoot
import com.bebeplace.bebeplaceapi.common.types.Email
import com.bebeplace.bebeplaceapi.common.types.PhoneNumber
import com.bebeplace.bebeplaceapi.user.domain.event.UserRegistered
import org.springframework.security.crypto.password.PasswordEncoder
import java.time.LocalDateTime
import java.util.UUID

class User(
    private val id: UUID = UserId.generate().getValue(),
    private val email: Email = Email.of(""),
    private val phoneNumber: PhoneNumber? = null,
    private var password: String = "",
    private var profile: UserProfile = UserProfile(nickname = ""),
    private var trustScore: TrustScore = TrustScore(),
    private var status: UserStatus = UserStatus.ACTIVE,
    private var lastLoginAt: LocalDateTime? = null,
    private var lastTokenRefreshAt: LocalDateTime? = null,
    private val babies: MutableList<Baby> = mutableListOf(),
    private val regions: MutableList<UserRegion> = mutableListOf()
) : AggregateRoot<UUID>() {
    
    companion object {
        private const val MIN_PASSWORD_LENGTH = 1
        private const val MAX_PASSWORD_LENGTH = 72  // BCrypt limitation: max 72 bytes
        
        fun create(
            email: Email,
            phoneNumber: PhoneNumber?,
            rawPassword: String,
            profile: UserProfile,
            passwordEncoder: PasswordEncoder
        ): User {
            // 비밀번호 검증
            require(rawPassword.isNotBlank()) { "비밀번호는 빈 문자열일 수 없습니다" }
            require(rawPassword.length >= MIN_PASSWORD_LENGTH) { "비밀번호는 최소 ${MIN_PASSWORD_LENGTH}자 이상이어야 합니다" }
            require(rawPassword.length <= MAX_PASSWORD_LENGTH) { "비밀번호는 최대 ${MAX_PASSWORD_LENGTH}자 이하여야 합니다" }
            
            val user = User(
                id = UserId.generate().getValue(),
                email = email,
                phoneNumber = phoneNumber,
                password = passwordEncoder.encode(rawPassword),
                profile = profile
            )
            
            user.addDomainEvent(UserRegistered(user.id, email.getValue()))
            return user
        }
        
        fun restore(
            id: UUID,
            email: Email,
            phoneNumber: PhoneNumber? = null,
            password: String,
            profile: UserProfile,
            trustScore: TrustScore,
            status: UserStatus,
            lastLoginAt: LocalDateTime? = null,
            lastTokenRefreshAt: LocalDateTime? = null,
            babies: List<Baby> = emptyList(),
            regions: List<UserRegion> = emptyList()
        ): User {
            val user = User(
                id = id,
                email = email,
                phoneNumber = phoneNumber,
                password = password,
                profile = profile,
                trustScore = trustScore,
                status = status,
                lastLoginAt = lastLoginAt,
                lastTokenRefreshAt = lastTokenRefreshAt,
                babies = babies.toMutableList(),
                regions = regions.toMutableList()
            )
            
            return user
        }
    }
    
    override fun getId(): UUID = id
    
    fun getEmail(): Email = email
    fun getEmailValue(): String = email.getValue()
    fun getPhoneNumber(): PhoneNumber? = phoneNumber
    fun getPassword(): String = password
    fun getProfile(): UserProfile = profile
    fun getNickname(): String = profile.getNickname()
    fun getTrustScore(): TrustScore = trustScore
    fun getStatus(): UserStatus = status
    fun getLastLoginAt(): LocalDateTime? = lastLoginAt
    fun getLastTokenRefreshAt(): LocalDateTime? = lastTokenRefreshAt
    
    fun updateProfile(newProfile: UserProfile) {
        this.profile = newProfile
    }
    
    fun updatePassword(newPassword: String, passwordEncoder: PasswordEncoder) {
        this.password = passwordEncoder.encode(newPassword)
    }
    
    fun increaseTrustScore(points: Int) {
        this.trustScore = trustScore.increase(points)
    }
    
    fun decreaseTrustScore(points: Int) {
        this.trustScore = trustScore.decrease(points)
    }
    
    fun suspend() {
        this.status = UserStatus.SUSPENDED
    }
    
    fun activate() {
        this.status = UserStatus.ACTIVE
    }
    
    fun delete() {
        this.status = UserStatus.DELETED
    }
    
    fun isActive(): Boolean = status == UserStatus.ACTIVE
    
    fun validatePassword(rawPassword: String, passwordEncoder: PasswordEncoder): Boolean {
        return passwordEncoder.matches(rawPassword, password)
    }
    
    fun updateLastLoginAt() {
        this.lastLoginAt = LocalDateTime.now()
    }
    
    fun updateLastTokenRefreshAt() {
        this.lastTokenRefreshAt = LocalDateTime.now()
    }
    
    // Baby management methods
    fun getBabies(): List<Baby> = babies.toList()
    
    fun addBaby(baby: Baby) {
        babies.add(baby)
    }
    
    fun removeBaby(babyId: Long) {
        babies.removeAll { it.getId() == babyId }
    }
    
    fun getBaby(babyId: Long): Baby? {
        return babies.find { it.getId() == babyId }
    }
    
    // Region management methods
    fun getRegions(): List<UserRegion> = regions.toList()
    
    fun addRegion(region: UserRegion) {
        require(regions.size < UserRegion.MAX_REGIONS_PER_USER) { 
            "Cannot add more than ${UserRegion.MAX_REGIONS_PER_USER} regions" 
        }
        
        // Remove existing region with same priority and update others
        val existingRegionWithSamePriority = regions.find { it.getPriority() == region.getPriority() }
        existingRegionWithSamePriority?.let { regions.remove(it) }
        
        regions.add(region)
        regions.sortBy { it.getPriority() }
    }
    
    fun removeRegion(regionId: Long) {
        regions.removeAll { it.getId() == regionId }
    }
    
    fun getRegion(regionId: Long): UserRegion? {
        return regions.find { it.getId() == regionId }
    }
    
    fun updateRegionPriorities(regionPriorities: Map<Long, Int>) {
        regionPriorities.forEach { (regionId, priority) ->
            regions.find { it.getId() == regionId }?.updatePriority(priority)
        }
        regions.sortBy { it.getPriority() }
    }
    
    fun getPrimaryRegion(): UserRegion? {
        return regions.find { it.getPriority() == 1 }
    }
}