package com.bebeplace.bebeplaceapi.user.application.usecase

import com.bebeplace.bebeplaceapi.common.event.DomainEventPublisher
import com.bebeplace.bebeplaceapi.common.types.Email
import com.bebeplace.bebeplaceapi.user.application.dto.RegisterUserRequest
import com.bebeplace.bebeplaceapi.user.application.service.BabyService
import com.bebeplace.bebeplaceapi.user.application.service.RegionService
import com.bebeplace.bebeplaceapi.user.domain.model.User
import com.bebeplace.bebeplaceapi.user.domain.repository.UserRepository
import io.mockk.every
import io.mockk.mockk
import io.mockk.slot
import io.mockk.verify
import org.junit.jupiter.api.BeforeEach
import org.junit.jupiter.api.DisplayName
import org.junit.jupiter.api.Test
import org.junit.jupiter.api.assertThrows
import org.springframework.security.crypto.password.PasswordEncoder
import kotlin.test.assertEquals

@DisplayName("RegisterUserUseCase 테스트")
class RegisterUserUseCaseTest {
    
    private val userRepository = mockk<UserRepository>()
    private val passwordEncoder = mockk<PasswordEncoder>()
    private val eventPublisher = mockk<DomainEventPublisher>()
    private val babyService = mockk<BabyService>()
    private val regionService = mockk<RegionService>()
    private val useCase = RegisterUserUseCase(userRepository, passwordEncoder, eventPublisher, babyService, regionService)
    
    @BeforeEach
    fun setUp() {
        every { passwordEncoder.encode(any()) } returns "encoded-password"
        every { eventPublisher.publish(any()) } returns Unit
        every { babyService.createBabies(any(), any()) } returns Unit
        every { regionService.createRegions(any(), any()) } returns Unit
    }
    
    @Test
    @DisplayName("유효한 요청으로 사용자를 등록할 수 있어야 한다")
    fun shouldRegisterUserWithValidRequest() {
        // given
        val request = RegisterUserRequest(
            email = "test@example.com",
            password = "Password123!",
            nickname = "testUser",
            phoneNumber = "010-1234-5678"
        )
        
        val email = Email.of(request.email)
        every { userRepository.existsByEmail(email) } returns false
        
        val userSlot = slot<User>()
        every { userRepository.save(capture(userSlot)) } answers { userSlot.captured }
        
        // when
        val response = useCase.registerUser(request)
        
        // then
        verify { userRepository.existsByEmail(email) }
        verify { userRepository.save(any()) }
        verify { passwordEncoder.encode(request.password) }
        
        assertEquals(request.email, response.email)
        assertEquals(request.nickname, response.nickname)
        assertEquals(request.phoneNumber, response.phoneNumber)
        assertEquals(0, response.trustScore)
        assertEquals("BRONZE", response.trustLevel)
    }
    
    @Test
    @DisplayName("전화번호가 없어도 사용자를 등록할 수 있어야 한다")
    fun shouldRegisterUserWithoutPhoneNumber() {
        // given
        val request = RegisterUserRequest(
            email = "test@example.com",
            password = "Password123!",
            nickname = "testUser",
            phoneNumber = null
        )
        
        val email = Email.of(request.email)
        every { userRepository.existsByEmail(email) } returns false
        
        val userSlot = slot<User>()
        every { userRepository.save(capture(userSlot)) } answers { userSlot.captured }
        
        // when
        val response = useCase.registerUser(request)
        
        // then
        assertEquals(request.email, response.email)
        assertEquals(request.nickname, response.nickname)
        assertEquals(null, response.phoneNumber)
    }
    
    @Test
    @DisplayName("이미 존재하는 이메일로 등록하면 예외가 발생해야 한다")
    fun shouldThrowExceptionWhenEmailAlreadyExists() {
        // given
        val request = RegisterUserRequest(
            email = "existing@example.com",
            password = "Password123!",
            nickname = "testUser"
        )
        
        val email = Email.of(request.email)
        every { userRepository.existsByEmail(email) } returns true
        
        // when & then
        val exception = assertThrows<IllegalArgumentException> {
            useCase.registerUser(request)
        }
        
        assertEquals("이미 등록된 이메일입니다: ${request.email}", exception.message)
        verify { userRepository.existsByEmail(email) }
        verify(exactly = 0) { userRepository.save(any()) }
    }
    
    @Test
    @DisplayName("비밀번호가 암호화되어 저장되어야 한다")
    fun shouldEncodePasswordBeforeSaving() {
        // given
        val request = RegisterUserRequest(
            email = "test@example.com",
            password = "PlainPassword",
            nickname = "testUser"
        )
        
        val email = Email.of(request.email)
        every { userRepository.existsByEmail(email) } returns false
        
        val userSlot = slot<User>()
        every { userRepository.save(capture(userSlot)) } answers { userSlot.captured }
        
        // when
        useCase.registerUser(request)
        
        // then
        verify { passwordEncoder.encode("PlainPassword") }
        val savedUser = userSlot.captured
        assertEquals("encoded-password", savedUser.getPassword())
    }
    
    @Test
    @DisplayName("저장된 사용자의 프로필 정보가 올바르게 설정되어야 한다")
    fun shouldSetUserProfileCorrectly() {
        // given
        val request = RegisterUserRequest(
            email = "test@example.com",
            password = "Password123!",
            nickname = "testNickname",
            phoneNumber = "010-9876-5432"
        )
        
        val email = Email.of(request.email)
        every { userRepository.existsByEmail(email) } returns false
        
        val userSlot = slot<User>()
        every { userRepository.save(capture(userSlot)) } answers { userSlot.captured }
        
        // when
        useCase.registerUser(request)
        
        // then
        val savedUser = userSlot.captured
        assertEquals("testNickname", savedUser.getProfile().getNickname())
        assertEquals("010-9876-5432", savedUser.getProfile().getPhoneNumber())
        assertEquals(null, savedUser.getProfile().getProfileImageUrl())
        assertEquals(null, savedUser.getProfile().getBio())
    }
    
    @Test
    @DisplayName("도메인 이벤트가 발행되어야 한다")
    fun shouldPublishDomainEvent() {
        // given
        val request = RegisterUserRequest(
            email = "test@example.com",
            password = "Password123!",
            nickname = "testUser"
        )
        
        val email = Email.of(request.email)
        every { userRepository.existsByEmail(email) } returns false
        
        val userSlot = slot<User>()
        every { userRepository.save(capture(userSlot)) } answers { userSlot.captured }
        
        // when
        useCase.registerUser(request)
        
        // then
        val savedUser = userSlot.captured
        assertEquals(1, savedUser.getDomainEvents().size)
    }
    
    // Edge Cases
    @Test
    @DisplayName("대소문자가 섞인 이메일도 처리할 수 있어야 한다")
    fun shouldHandleMixedCaseEmail() {
        // given
        val request = RegisterUserRequest(
            email = "Test.User@Example.COM",
            password = "Password123!",
            nickname = "testUser"
        )
        
        val email = Email.of(request.email)
        every { userRepository.existsByEmail(email) } returns false
        
        val userSlot = slot<User>()
        every { userRepository.save(capture(userSlot)) } answers { userSlot.captured }
        
        // when
        val response = useCase.registerUser(request)
        
        // then
        assertEquals("Test.User@Example.COM", response.email)
    }
    
    @Test
    @DisplayName("특수문자가 포함된 닉네임을 처리할 수 있어야 한다")
    fun shouldHandleSpecialCharactersInNickname() {
        // given
        val request = RegisterUserRequest(
            email = "test@example.com",
            password = "Password123!",
            nickname = "test_user-123"
        )
        
        val email = Email.of(request.email)
        every { userRepository.existsByEmail(email) } returns false
        
        val userSlot = slot<User>()
        every { userRepository.save(capture(userSlot)) } answers { userSlot.captured }
        
        // when
        val response = useCase.registerUser(request)
        
        // then
        assertEquals("test_user-123", response.nickname)
    }
    
    @Test
    @DisplayName("매우 긴 비밀번호도 처리할 수 있어야 한다")
    fun shouldHandleVeryLongPassword() {
        // given
        val longPassword = "VeryLong" + "Password123!".repeat(10)
        val request = RegisterUserRequest(
            email = "test@example.com",
            password = longPassword,
            nickname = "testUser"
        )
        
        val email = Email.of(request.email)
        every { userRepository.existsByEmail(email) } returns false
        every { passwordEncoder.encode(longPassword) } returns "encoded-long-password"
        
        val userSlot = slot<User>()
        every { userRepository.save(capture(userSlot)) } answers { userSlot.captured }
        
        // when
        useCase.registerUser(request)
        
        // then
        verify { passwordEncoder.encode(longPassword) }
        val savedUser = userSlot.captured
        assertEquals("encoded-long-password", savedUser.getPassword())
    }
    
    @Test
    @DisplayName("Repository 예외가 발생하면 전파되어야 한다")
    fun shouldPropagateRepositoryExceptions() {
        // given
        val request = RegisterUserRequest(
            email = "test@example.com",
            password = "Password123!",
            nickname = "testUser"
        )
        
        val email = Email.of(request.email)
        every { userRepository.existsByEmail(email) } returns false
        every { userRepository.save(any()) } throws RuntimeException("Database error")
        
        // when & then
        assertThrows<RuntimeException> {
            useCase.registerUser(request)
        }
    }
    
    @Test
    @DisplayName("빈 문자열 비밀번호도 암호화되어야 한다")
    fun shouldEncodeEmptyPassword() {
        // given
        val request = RegisterUserRequest(
            email = "test@example.com",
            password = "",
            nickname = "testUser"
        )
        
        val email = Email.of(request.email)
        every { userRepository.existsByEmail(email) } returns false
        every { passwordEncoder.encode("") } returns "encoded-empty"
        
        val userSlot = slot<User>()
        every { userRepository.save(capture(userSlot)) } answers { userSlot.captured }
        
        // when
        useCase.registerUser(request)
        
        // then
        verify { passwordEncoder.encode("") }
        val savedUser = userSlot.captured
        assertEquals("encoded-empty", savedUser.getPassword())
    }
}