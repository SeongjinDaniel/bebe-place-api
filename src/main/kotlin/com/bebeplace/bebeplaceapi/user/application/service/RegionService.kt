package com.bebeplace.bebeplaceapi.user.application.service

import com.bebeplace.bebeplaceapi.user.application.dto.RegisterUserRequest.RegionInfo
import com.bebeplace.bebeplaceapi.user.infrastructure.persistence.UserRegionEntityJpaRepository
import com.bebeplace.bebeplaceapi.user.infrastructure.persistence.entity.UserRegionEntity
import org.slf4j.LoggerFactory
import org.springframework.stereotype.Service
import org.springframework.transaction.annotation.Transactional
import java.util.*

@Service
@Transactional
class RegionService(
    private val userRegionRepository: UserRegionEntityJpaRepository,
    private val userService: UserService
) {
    private val logger = LoggerFactory.getLogger(RegionService::class.java)

    fun createRegions(userId: UUID, regions: List<RegionInfo>) {
        if (regions.isEmpty()) return
        
        logger.info("지역 정보 동기 생성 시작: userId=${userId}, regionCount=${regions.size}")
        
        // 중복 제거 및 우선순위 정렬
        val uniqueRegions = removeDuplicateRegions(regions)
        
        // UserEntity 조회 (한 번만)
        val userEntity = userService.findUserEntityById(userId)
        
        // 배치로 UserRegionEntity 생성
        val regionEntities = uniqueRegions.map { regionInfo ->
            UserRegionEntity(
                user = userEntity,
                regionCode = regionInfo.regionCode,
                sido = regionInfo.sido,
                sigungu = regionInfo.sigungu,
                dong = regionInfo.dong,
                priority = regionInfo.priority
            )
        }
        
        // 배치 저장 (같은 트랜잭션 내에서)
        userRegionRepository.saveAll(regionEntities)
        logger.info("지역 정보 동기 생성 완료: count=${regionEntities.size}, userId=${userId}")
    }
    
    fun deleteRegions(userId: UUID): Int {
        logger.info("지역 정보 삭제: userId=${userId}")
        return userRegionRepository.deleteByUserId(userId)
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