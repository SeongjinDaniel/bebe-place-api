package com.bebeplace.bebeplaceapi.user.application.service

import com.bebeplace.bebeplaceapi.user.infrastructure.persistence.UserEntityJpaRepository
import com.bebeplace.bebeplaceapi.user.infrastructure.persistence.entity.UserEntity
import org.springframework.stereotype.Service
import java.util.*

@Service
class UserService(
    private val userEntityRepository: UserEntityJpaRepository
) {
    
    /**
     * UserEntity를 안전하게 조회하는 공통 메서드
     * @param userId 조회할 사용자 ID
     * @return UserEntity
     * @throws IllegalArgumentException 사용자를 찾을 수 없는 경우
     */
    fun findUserEntityById(userId: UUID): UserEntity {
        return userEntityRepository.findById(userId).orElseThrow {
            IllegalArgumentException("User not found: ${userId}")
        }
    }
    
    /**
     * UserEntity 존재 여부 확인
     * @param userId 확인할 사용자 ID
     * @return 존재 여부
     */
    fun existsById(userId: UUID): Boolean {
        return userEntityRepository.existsById(userId)
    }
}