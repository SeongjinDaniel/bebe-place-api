package com.bebeplace.bebeplaceapi.user.infrastructure.persistence.entity

import com.bebeplace.bebeplaceapi.common.infrastructure.BaseEntity
import com.bebeplace.bebeplaceapi.user.domain.model.UserStatus
import com.github.f4b6a3.uuid.UuidCreator
import jakarta.persistence.*
import java.time.LocalDate
import java.time.LocalDateTime
import java.util.*

@Entity
@Table(name = "users")
class UserEntity(
    @Id
    @Column(name = "id")
    val id: UUID = UuidCreator.getTimeOrderedEpoch(),
    
    @Column(name = "email", unique = true, nullable = false)
    val email: String,
    
    @Column(name = "password", nullable = false)
    var password: String,
    
    @Column(name = "nickname", nullable = false, length = 100)
    var nickname: String,
    
    @Column(name = "phone_number", length = 20)
    var phoneNumber: String? = null,
    
    @Column(name = "profile_image_url")
    var profileImageUrl: String? = null,
    
    @Column(name = "bio")
    var bio: String? = null,
    
    @Column(name = "birth_date")
    var birthDate: LocalDate? = null,
    
    @Column(name = "trust_score", nullable = false)
    var trustScore: Int = 0,
    
    @Column(name = "transaction_count", nullable = false)
    var transactionCount: Int = 0,

    @Enumerated(EnumType.STRING)
    @Column(name = "status", nullable = false)
    var status: UserStatus = UserStatus.ACTIVE,
    
    @Column(name = "last_login_at")
    var lastLoginAt: LocalDateTime? = null,
    
    @Column(name = "last_token_refresh_at")
    var lastTokenRefreshAt: LocalDateTime? = null,
    
    @OneToMany(mappedBy = "user", fetch = FetchType.LAZY)
    val babies: MutableList<BabyEntity> = mutableListOf(),
    
    @OneToMany(mappedBy = "user", fetch = FetchType.LAZY)
    val regions: MutableList<UserRegionEntity> = mutableListOf()
) : BaseEntity()