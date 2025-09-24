package com.bebeplace.bebeplaceapi.user.infrastructure.persistence

import com.bebeplace.bebeplaceapi.common.types.Email
import com.bebeplace.bebeplaceapi.common.util.RequestContextUtil
import com.bebeplace.bebeplaceapi.user.domain.model.User
import com.bebeplace.bebeplaceapi.user.domain.repository.UserRepository
import com.bebeplace.bebeplaceapi.user.infrastructure.persistence.entity.UserEntity
import com.bebeplace.bebeplaceapi.user.infrastructure.persistence.mapper.UserMapper
import org.springframework.data.jpa.repository.JpaRepository
import org.springframework.data.jpa.repository.Query
import org.springframework.stereotype.Repository
import java.util.UUID

interface UserEntityJpaRepository : JpaRepository<UserEntity, UUID> {
    
    fun findByEmail(email: String): UserEntity?
    
    fun existsByEmail(email: String): Boolean
    
    fun existsByNickname(nickname: String): Boolean
    
    @Query("SELECT u FROM UserEntity u LEFT JOIN FETCH u.babies LEFT JOIN FETCH u.regions WHERE u.id = :id")
    fun findByIdWithBabiesAndRegions(id: UUID): UserEntity?
    
    @Query("SELECT u FROM UserEntity u LEFT JOIN FETCH u.babies LEFT JOIN FETCH u.regions WHERE u.email = :email")
    fun findByEmailWithBabiesAndRegions(email: String): UserEntity?
}

@Repository
class UserJpaRepository(
    private val userEntityJpaRepository: UserEntityJpaRepository,
    private val userMapper: UserMapper,
    private val requestContextUtil: RequestContextUtil
) : UserRepository {
    
    override fun save(user: User): User {
        val entity = userMapper.toEntity(user)
        
        // IP 정보 자동 설정
        val currentIp = requestContextUtil.getCurrentClientIp()
        if (entity.createdIp == null) {
            entity.setCreationAuditInfo(entity.createdByUserId, currentIp)
        }
        entity.updateAuditInfo(entity.updatedByUserId, currentIp)
        
        val savedEntity = userEntityJpaRepository.save(entity)
        return userMapper.toDomain(savedEntity)
    }
    
    override fun findById(id: UUID): User? {
        return userEntityJpaRepository.findByIdWithBabiesAndRegions(id)?.let { entity ->
            userMapper.toDomain(entity)
        }
    }
    
    override fun findByEmail(email: Email): User? {
        return userEntityJpaRepository.findByEmailWithBabiesAndRegions(email.getValue())?.let { entity ->
            userMapper.toDomain(entity)
        }
    }
    
    override fun existsByEmail(email: Email): Boolean {
        return userEntityJpaRepository.existsByEmail(email.getValue())
    }
    
    override fun delete(user: User) {
        userEntityJpaRepository.deleteById(user.getId())
    }
    
    // 추가 메서드들 (필요시 사용)
    fun findByEmailEntity(email: String): UserEntity? {
        return userEntityJpaRepository.findByEmail(email)
    }
    
    fun existsByNickname(nickname: String): Boolean {
        return userEntityJpaRepository.existsByNickname(nickname)
    }
}