package com.bebeplace.bebeplaceapi.user.application.service

import com.bebeplace.bebeplaceapi.user.application.dto.RegisterUserRequest.BabyInfo
import com.bebeplace.bebeplaceapi.user.infrastructure.persistence.BabyEntityJpaRepository
import com.bebeplace.bebeplaceapi.user.infrastructure.persistence.entity.BabyEntity
import org.slf4j.LoggerFactory
import org.springframework.stereotype.Service
import org.springframework.transaction.annotation.Transactional
import java.util.*

@Service
@Transactional
class BabyService(
    private val babyRepository: BabyEntityJpaRepository,
    private val userService: UserService
) {
    private val logger = LoggerFactory.getLogger(BabyService::class.java)

    fun createBabies(userId: UUID, babies: List<BabyInfo>) {
        if (babies.isEmpty()) return
        
        logger.info("베이비 정보 동기 생성 시작: userId=${userId}, babyCount=${babies.size}")
        
        // UserEntity 조회 (한 번만)
        val userEntity = userService.findUserEntityById(userId)
        
        // 배치로 BabyEntity 생성
        val babyEntities = babies.map { babyInfo ->
            BabyEntity(
                user = userEntity,
                name = babyInfo.name,
                gender = babyInfo.gender,
                birthDate = babyInfo.birthDate,
                interests = babyInfo.interests.toMutableSet()
            )
        }
        
        // 배치 저장 (같은 트랜잭션 내에서)
        babyRepository.saveAll(babyEntities)
        logger.info("베이비 정보 동기 생성 완료: count=${babyEntities.size}, userId=${userId}")
    }
    
    fun deleteBabies(userId: UUID): Int {
        logger.info("베이비 정보 삭제: userId=${userId}")
        return babyRepository.deleteByUserId(userId)
    }
}