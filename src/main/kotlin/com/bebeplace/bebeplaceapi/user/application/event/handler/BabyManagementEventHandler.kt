package com.bebeplace.bebeplaceapi.user.application.event.handler

import com.bebeplace.bebeplaceapi.user.application.dto.RegisterUserRequest.BabyInfo
import com.bebeplace.bebeplaceapi.user.application.event.UserCreatedEvent
import com.bebeplace.bebeplaceapi.user.application.event.UserDeletedEvent
import com.bebeplace.bebeplaceapi.user.application.service.UserService
import com.bebeplace.bebeplaceapi.user.infrastructure.persistence.BabyEntityJpaRepository
import com.bebeplace.bebeplaceapi.user.infrastructure.persistence.entity.BabyEntity
import org.slf4j.LoggerFactory
import org.springframework.context.event.EventListener
import org.springframework.scheduling.annotation.Async
import org.springframework.stereotype.Component
import org.springframework.transaction.annotation.Propagation
import org.springframework.transaction.annotation.Transactional
import java.util.*

@Component
class BabyManagementEventHandler(
    private val babyRepository: BabyEntityJpaRepository,
    private val userService: UserService
) {
    private val logger = LoggerFactory.getLogger(BabyManagementEventHandler::class.java)

    @EventListener
    @Async
    @Transactional(propagation = Propagation.REQUIRES_NEW)
    fun handleUserCreated(event: UserCreatedEvent) {
        try {
            event.babies?.let { babies ->
                if (babies.isNotEmpty()) {
                    logger.info("사용자 생성 이벤트 처리 - 베이비 정보 생성 시작: userId=${event.userId}, babyCount=${babies.size}")
                    
                    createBabiesBatch(event.userId, babies)
                    
                    logger.info("사용자 생성 이벤트 처리 - 베이비 정보 생성 완료: userId=${event.userId}")
                }
            }
        } catch (e: Exception) {
            logger.error("베이비 정보 생성 중 오류 발생: userId=${event.userId}", e)
            // 베이비 정보 생성 실패가 전체 회원가입을 막지 않도록 예외를 상위로 전파하지 않음
        }
    }

    @EventListener
    @Async
    @Transactional(propagation = Propagation.REQUIRES_NEW)
    fun handleUserDeleted(event: UserDeletedEvent) {
        try {
            logger.info("사용자 삭제 이벤트 처리 - 베이비 정보 삭제: userId=${event.userId}")
            
            val deletedCount = babyRepository.deleteByUserId(event.userId)
            
            logger.info("사용자 삭제 이벤트 처리 완료: userId=${event.userId}, deletedBabies=${deletedCount}")
        } catch (e: Exception) {
            logger.error("베이비 정보 삭제 중 오류 발생: userId=${event.userId}", e)
        }
    }
    
    private fun createBabiesBatch(userId: UUID, babies: List<BabyInfo>) {
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

        babyRepository.saveAll(babyEntities)
        logger.debug("베이비 정보 배치 생성 완료: count=${babyEntities.size}, userId=${userId}")
    }
}