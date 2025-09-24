package com.bebeplace.bebeplaceapi.user.application.usecase

import com.bebeplace.bebeplaceapi.common.event.DomainEventPublisher
import com.bebeplace.bebeplaceapi.common.types.Email
import com.bebeplace.bebeplaceapi.common.types.PhoneNumber
import com.bebeplace.bebeplaceapi.user.application.dto.RegisterUserRequest
import com.bebeplace.bebeplaceapi.user.application.dto.UserResponse
import com.bebeplace.bebeplaceapi.user.application.event.UserCreatedEvent
import com.bebeplace.bebeplaceapi.user.application.port.input.UserCommand
import com.bebeplace.bebeplaceapi.user.application.service.BabyService
import com.bebeplace.bebeplaceapi.user.application.service.RegionService
import com.bebeplace.bebeplaceapi.user.domain.model.Baby
import com.bebeplace.bebeplaceapi.user.domain.model.User
import com.bebeplace.bebeplaceapi.user.domain.model.UserProfile
import com.bebeplace.bebeplaceapi.user.domain.model.UserRegion
import com.bebeplace.bebeplaceapi.user.domain.repository.UserRepository
import org.slf4j.LoggerFactory
import org.springframework.security.crypto.password.PasswordEncoder
import org.springframework.stereotype.Service
import org.springframework.transaction.annotation.Transactional

@Service
@Transactional
class RegisterUserUseCase(
    private val userRepository: UserRepository,
    private val passwordEncoder: PasswordEncoder,
    private val eventPublisher: DomainEventPublisher,
    private val babyService: BabyService,
    private val regionService: RegionService
) : UserCommand {
    private val logger = LoggerFactory.getLogger(RegisterUserUseCase::class.java)
    
    override fun registerUser(request: RegisterUserRequest): UserResponse {
        val email = Email.of(request.email)

        if (userRepository.existsByEmail(email)) {
            throw IllegalArgumentException("이미 등록된 이메일입니다: ${request.email}")
        }

        val phoneNumber = request.phoneNumber?.let { PhoneNumber(it) }

        val userProfile = UserProfile(
            nickname = request.nickname,
            phoneNumber = request.phoneNumber,
            birthDate = request.birthDate,
            profileImageUrl = request.profileImageUrl,
            bio = request.bio
        )

        val user = User.create(
            email = email,
            phoneNumber = phoneNumber,
            rawPassword = request.password,
            profile = userProfile,
            passwordEncoder = passwordEncoder
        )

        // 아기 정보 추가
        request.babies?.forEach { babyInfo ->
            val baby = Baby.create(
                userId = user.getId(),
                name = babyInfo.name,
                gender = babyInfo.gender,
                birthDate = babyInfo.birthDate,
                interests = babyInfo.interests
            )
            user.addBaby(baby)
        }

        // 지역 정보 추가
        request.regions?.forEach { regionInfo ->
            val userRegion = UserRegion.create(
                userId = user.getId(),
                regionCode = regionInfo.regionCode,
                sido = regionInfo.sido,
                sigungu = regionInfo.sigungu,
                dong = regionInfo.dong,
                priority = regionInfo.priority
            )
            user.addRegion(userRegion)
        }

        val savedUser = userRepository.save(user)

        // 핵심 데이터 동기 처리 (같은 트랜잭션)
        request.babies?.let { babies ->
            babyService.createBabies(savedUser.getId(), babies)
        }
        
        request.regions?.let { regions ->
            regionService.createRegions(savedUser.getId(), regions)
        }

        // 부가 기능만 비동기 이벤트로 처리 (Analytics)
        try {
            val userCreatedEvent = UserCreatedEvent(
                userId = savedUser.getId(),
                email = savedUser.getEmailValue(),
                nickname = savedUser.getNickname(),
                babies = request.babies,
                regions = request.regions
            )
            
            eventPublisher.publish(userCreatedEvent)
            logger.info("사용자 분석 이벤트 발행 완료: userId=${savedUser.getId()}")
            
        } catch (e: Exception) {
            logger.error("사용자 분석 이벤트 발행 중 오류 발생: userId=${savedUser.getId()}", e)
            // 분석 이벤트 실패는 핵심 기능에 영향주지 않음
        }

        return UserResponse.from(savedUser)
    }
}