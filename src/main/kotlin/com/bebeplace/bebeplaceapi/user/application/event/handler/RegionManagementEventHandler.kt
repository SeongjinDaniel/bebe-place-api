package com.bebeplace.bebeplaceapi.user.application.event.handler

import com.bebeplace.bebeplaceapi.user.application.dto.RegisterUserRequest.RegionInfo
import com.bebeplace.bebeplaceapi.user.application.event.UserCreatedEvent
import com.bebeplace.bebeplaceapi.user.application.event.UserDeletedEvent
import com.bebeplace.bebeplaceapi.user.application.service.UserService
import com.bebeplace.bebeplaceapi.user.infrastructure.persistence.UserRegionEntityJpaRepository
import com.bebeplace.bebeplaceapi.user.infrastructure.persistence.entity.UserRegionEntity
import org.slf4j.LoggerFactory
import org.springframework.context.event.EventListener
import org.springframework.scheduling.annotation.Async
import org.springframework.stereotype.Component
import org.springframework.transaction.annotation.Propagation
import org.springframework.transaction.annotation.Transactional
import java.util.*

@Component
class RegionManagementEventHandler(
    private val userRegionRepository: UserRegionEntityJpaRepository,
    private val userService: UserService
) {
    private val logger = LoggerFactory.getLogger(RegionManagementEventHandler::class.java)

    @EventListener
    @Async
    @Transactional(propagation = Propagation.REQUIRES_NEW)
    fun handleUserCreated(event: UserCreatedEvent) {
        try {
            event.regions?.let { regions ->
                if (regions.isNotEmpty()) {
                    logger.info("사용자 생성 이벤트 처리 - 지역 정보 생성 시작: userId=${event.userId}, regionCount=${regions.size}")
                    
                    // 중복 제거 및 우선순위 정렬
                    val uniqueRegions = removeDuplicateRegions(regions)
                    
                    createUserRegionsBatch(event.userId, uniqueRegions)
                    
                    logger.info("사용자 생성 이벤트 처리 - 지역 정보 생성 완료: userId=${event.userId}")
                }
            }
        } catch (e: Exception) {
            logger.error("지역 정보 생성 중 오류 발생: userId=${event.userId}", e)
            // 지역 정보 생성 실패가 전체 회원가입을 막지 않도록 예외를 상위로 전파하지 않음
        }
    }

    @EventListener
    @Async
    @Transactional(propagation = Propagation.REQUIRES_NEW)
    fun handleUserDeleted(event: UserDeletedEvent) {
        try {
            logger.info("사용자 삭제 이벤트 처리 - 지역 정보 삭제: userId=${event.userId}")
            
            val deletedCount = userRegionRepository.deleteByUserId(event.userId)
            
            logger.info("사용자 삭제 이벤트 처리 완료: userId=${event.userId}, deletedRegions=${deletedCount}")
        } catch (e: Exception) {
            logger.error("지역 정보 삭제 중 오류 발생: userId=${event.userId}", e)
        }
    }
    
    private fun createUserRegionsBatch(userId: UUID, regions: List<RegionInfo>) {
        // UserEntity 조회 (한 번만)
        val userEntity = userService.findUserEntityById(userId)
        
        // 배치로 UserRegionEntity 생성
        val regionEntities = regions.map { regionInfo ->
            UserRegionEntity(
                user = userEntity,
                regionCode = regionInfo.regionCode,
                sido = regionInfo.sido,
                sigungu = regionInfo.sigungu,
                dong = regionInfo.dong,
                priority = regionInfo.priority
            )
        }

        userRegionRepository.saveAll(regionEntities)
        logger.debug("지역 정보 배치 생성 완료: count=${regionEntities.size}, userId=${userId}")
    }
    
    private fun removeDuplicateRegions(regions: List<RegionInfo>): List<RegionInfo> {
        // 중복된 지역 코드가 있을 경우 우선순위가 높은 것만 유지
        return regions
            .groupBy { it.regionCode }
            .values
            .map { duplicates ->
                duplicates.minByOrNull { it.priority } ?: duplicates.first()
            }
            .sortedBy { it.priority }
    }
}