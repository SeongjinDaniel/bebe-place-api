package com.bebeplace.bebeplaceapi.user.infrastructure.web

import com.bebeplace.bebeplaceapi.user.application.dto.RegisterUserRequest
import com.bebeplace.bebeplaceapi.user.application.dto.UserResponse
import com.bebeplace.bebeplaceapi.user.application.port.input.UserCommand
import com.bebeplace.bebeplaceapi.user.application.service.BabyService
import com.bebeplace.bebeplaceapi.user.application.service.RegionService
import com.bebeplace.bebeplaceapi.user.application.service.UserService
import com.bebeplace.bebeplaceapi.user.domain.model.UserStatus
import com.bebeplace.bebeplaceapi.user.domain.repository.UserRepository
import com.bebeplace.bebeplaceapi.common.event.DomainEventPublisher
import org.springframework.security.crypto.password.PasswordEncoder
import com.fasterxml.jackson.databind.ObjectMapper
import com.github.f4b6a3.uuid.UuidCreator
import org.junit.jupiter.api.DisplayName
import org.junit.jupiter.api.Test
import io.mockk.every
import org.springframework.test.context.ContextConfiguration
import org.springframework.beans.factory.annotation.Autowired
import org.springframework.boot.autoconfigure.security.servlet.SecurityAutoConfiguration
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest
import io.mockk.mockk
import org.springframework.boot.test.context.TestConfiguration
import org.springframework.context.annotation.Bean
import org.springframework.context.annotation.Primary
import org.springframework.http.MediaType
import org.springframework.test.web.servlet.MockMvc
import org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post
import org.springframework.test.web.servlet.result.MockMvcResultMatchers.*
import java.time.LocalDateTime

@WebMvcTest(
    controllers = [UserController::class],
    excludeAutoConfiguration = [SecurityAutoConfiguration::class]
)
@ContextConfiguration(classes = [UserController::class, UserControllerTest.TestConfig::class])
@DisplayName("UserController 통합 테스트")
class UserControllerTest {
    
    @Autowired
    private lateinit var mockMvc: MockMvc
    
    @Autowired
    private lateinit var objectMapper: ObjectMapper
    
    @Autowired
    private lateinit var userCommand: UserCommand
    
    @TestConfiguration
    class TestConfig {
        @Bean
        @Primary
        fun userCommand(): UserCommand = mockk(relaxed = true)
        
        @Bean
        @Primary  
        fun babyService(): BabyService = mockk(relaxed = true)
        
        @Bean
        @Primary
        fun regionService(): RegionService = mockk(relaxed = true)
        
        @Bean
        @Primary
        fun userService(): UserService = mockk(relaxed = true)
        
        @Bean
        @Primary
        fun userRepository(): UserRepository = mockk(relaxed = true)
        
        @Bean
        @Primary
        fun passwordEncoder(): PasswordEncoder = mockk(relaxed = true)
        
        @Bean
        @Primary
        fun eventPublisher(): DomainEventPublisher = mockk(relaxed = true)
    }
    
    @Test
    @DisplayName("유효한 요청으로 사용자를 생성할 수 있어야 한다")
    fun shouldCreateUserWithValidRequest() {
        // given
        val request = RegisterUserRequest(
            email = "test@example.com",
            password = "Password123!",
            nickname = "testUser",
            phoneNumber = "010-1234-5678"
        )
        
        val response = UserResponse(
            id = UuidCreator.getTimeOrderedEpoch(),
            email = "test@example.com",
            nickname = "testUser",
            phoneNumber = "010-1234-5678",
            birthDate = null,
            profileImageUrl = null,
            bio = null,
            trustScore = 0,
            trustLevel = "BRONZE",
            transactionCount = 0,
            status = UserStatus.ACTIVE,
            lastLoginAt = null,
            lastTokenRefreshAt = null,
            babies = emptyList(),
            regions = emptyList(),
            createdAt = LocalDateTime.now(),
            updatedAt = LocalDateTime.now()
        )
        
        every { userCommand.registerUser(any()) } returns response
        
        // when & then
        mockMvc.perform(
            post("/api/v1/users")
                .contentType(MediaType.APPLICATION_JSON)
                .content(objectMapper.writeValueAsString(request))
        )
            .andExpect(status().isCreated)
            .andExpect(content().contentType(MediaType.APPLICATION_JSON))
            .andExpect(jsonPath("$.success").value(true))
            .andExpect(jsonPath("$.data.email").value("test@example.com"))
            .andExpect(jsonPath("$.data.nickname").value("testUser"))
            .andExpect(jsonPath("$.data.phoneNumber").value("010-1234-5678"))
            .andExpect(jsonPath("$.data.trustScore").value(0))
            .andExpect(jsonPath("$.data.trustLevel").value("BRONZE"))
            .andExpect(jsonPath("$.message").value("회원가입이 완료되었습니다."))
    }
    
    @Test
    @DisplayName("전화번호 없이도 사용자를 생성할 수 있어야 한다")
    fun shouldCreateUserWithoutPhoneNumber() {
        // given
        val request = RegisterUserRequest(
            email = "test@example.com",
            password = "Password123!",
            nickname = "testUser",
            phoneNumber = null
        )
        
        val response = UserResponse(
            id = UuidCreator.getTimeOrderedEpoch(),
            email = "test@example.com",
            nickname = "testUser",
            phoneNumber = null,
            birthDate = null,
            profileImageUrl = null,
            bio = null,
            trustScore = 0,
            trustLevel = "BRONZE",
            transactionCount = 0,
            status = UserStatus.ACTIVE,
            lastLoginAt = null,
            lastTokenRefreshAt = null,
            babies = emptyList(),
            regions = emptyList(),
            createdAt = LocalDateTime.now(),
            updatedAt = LocalDateTime.now()
        )
        
        every { userCommand.registerUser(any()) } returns response
        
        // when & then
        mockMvc.perform(
            post("/api/v1/users")
                .contentType(MediaType.APPLICATION_JSON)
                .content(objectMapper.writeValueAsString(request))
        )
            .andExpect(status().isCreated)
            .andExpect(jsonPath("$.success").value(true))
            .andExpect(jsonPath("$.data.phoneNumber").isEmpty)
    }
    
    @Test
    @DisplayName("잘못된 이메일 형식으로 요청하면 400 에러가 발생해야 한다")
    fun shouldReturn400ForInvalidEmailFormat() {
        // given
        val request = RegisterUserRequest(
            email = "invalid-email",
            password = "Password123!",
            nickname = "testUser"
        )
        
        // when & then
        mockMvc.perform(
            post("/api/v1/users")
                .contentType(MediaType.APPLICATION_JSON)
                .content(objectMapper.writeValueAsString(request))
        )
            .andExpect(status().isBadRequest)
            .andExpect(jsonPath("$.success").value(false))
            .andExpect(jsonPath("$.error.code").value("VALIDATION_ERROR"))
    }
    
    @Test
    @DisplayName("빈 닉네임으로 요청하면 400 에러가 발생해야 한다")
    fun shouldReturn400ForBlankNickname() {
        // given
        val request = RegisterUserRequest(
            email = "test@example.com",
            password = "Password123!",
            nickname = ""
        )
        
        // when & then
        mockMvc.perform(
            post("/api/v1/users")
                .contentType(MediaType.APPLICATION_JSON)
                .content(objectMapper.writeValueAsString(request))
        )
            .andExpect(status().isBadRequest)
            .andExpect(jsonPath("$.success").value(false))
            .andExpect(jsonPath("$.error.code").value("VALIDATION_ERROR"))
    }
    
    @Test
    @DisplayName("잘못된 비밀번호 형식으로 요청하면 400 에러가 발생해야 한다")
    fun shouldReturn400ForInvalidPasswordFormat() {
        // given
        val request = RegisterUserRequest(
            email = "test@example.com",
            password = "weak",  // 너무 약한 비밀번호
            nickname = "testUser"
        )
        
        // when & then
        mockMvc.perform(
            post("/api/v1/users")
                .contentType(MediaType.APPLICATION_JSON)
                .content(objectMapper.writeValueAsString(request))
        )
            .andExpect(status().isBadRequest)
            .andExpect(jsonPath("$.success").value(false))
            .andExpect(jsonPath("$.error.code").value("VALIDATION_ERROR"))
    }
    
    @Test
    @DisplayName("잘못된 전화번호 형식으로 요청하면 400 에러가 발생해야 한다")
    fun shouldReturn400ForInvalidPhoneNumberFormat() {
        // given
        val request = RegisterUserRequest(
            email = "test@example.com",
            password = "Password123!",
            nickname = "testUser",
            phoneNumber = "invalid-phone"
        )
        
        // when & then
        mockMvc.perform(
            post("/api/v1/users")
                .contentType(MediaType.APPLICATION_JSON)
                .content(objectMapper.writeValueAsString(request))
        )
            .andExpect(status().isBadRequest)
            .andExpect(jsonPath("$.success").value(false))
            .andExpect(jsonPath("$.error.code").value("VALIDATION_ERROR"))
    }
    
    @Test
    @DisplayName("이미 존재하는 이메일로 요청하면 400 에러가 발생해야 한다")
    fun shouldReturn400ForDuplicateEmail() {
        // given
        val request = RegisterUserRequest(
            email = "existing@example.com",
            password = "Password123!",
            nickname = "testUser"
        )
        
        every { userCommand.registerUser(any()) } throws IllegalArgumentException("이미 등록된 이메일입니다: existing@example.com")
        
        // when & then
        mockMvc.perform(
            post("/api/v1/users")
                .contentType(MediaType.APPLICATION_JSON)
                .content(objectMapper.writeValueAsString(request))
        )
            .andExpect(status().isBadRequest)
            .andExpect(jsonPath("$.success").value(false))
            .andExpect(jsonPath("$.error.code").value("DUPLICATE_EMAIL"))
            .andExpect(jsonPath("$.error.message").value("이미 등록된 이메일입니다: existing@example.com"))
    }
    
    @Test
    @DisplayName("서버 내부 오류가 발생하면 500 에러가 반환되어야 한다")
    fun shouldReturn500ForInternalServerError() {
        // given
        val request = RegisterUserRequest(
            email = "test@example.com",
            password = "Password123!",
            nickname = "testUser"
        )
        
        every { userCommand.registerUser(any()) } throws RuntimeException("Database connection failed")
        
        // when & then
        mockMvc.perform(
            post("/api/v1/users")
                .contentType(MediaType.APPLICATION_JSON)
                .content(objectMapper.writeValueAsString(request))
        )
            .andExpect(status().isInternalServerError)
            .andExpect(jsonPath("$.success").value(false))
            .andExpect(jsonPath("$.error.code").value("INTERNAL_ERROR"))
            .andExpect(jsonPath("$.error.message").value("회원가입 중 오류가 발생했습니다."))
    }
    
    @Test
    @DisplayName("Content-Type이 application/json이 아니면 415 에러가 발생해야 한다")
    fun shouldReturn415ForInvalidContentType() {
        // given
        val request = RegisterUserRequest(
            email = "test@example.com",
            password = "Password123!",
            nickname = "testUser"
        )
        
        // when & then
        mockMvc.perform(
            post("/api/v1/users")
                .contentType(MediaType.TEXT_PLAIN)
                .content(objectMapper.writeValueAsString(request))
        )
            .andExpect(status().isUnsupportedMediaType)
    }
    
    @Test
    @DisplayName("빈 요청 본문으로 요청하면 400 에러가 발생해야 한다")
    fun shouldReturn400ForEmptyRequestBody() {
        // when & then
        mockMvc.perform(
            post("/api/v1/users")
                .contentType(MediaType.APPLICATION_JSON)
                .content("")
        )
            .andExpect(status().isBadRequest)
    }
    
    @Test
    @DisplayName("잘못된 JSON 형식으로 요청하면 400 에러가 발생해야 한다")
    fun shouldReturn400ForMalformedJson() {
        // when & then
        mockMvc.perform(
            post("/api/v1/users")
                .contentType(MediaType.APPLICATION_JSON)
                .content("{ \"email\": \"test@example.com\", \"invalid\": }")
        )
            .andExpect(status().isBadRequest)
    }
    
    // Edge Cases
    @Test
    @DisplayName("최대 길이의 닉네임으로 요청할 수 있어야 한다")
    fun shouldAcceptMaxLengthNickname() {
        // given
        val maxNickname = "a".repeat(50)
        val request = RegisterUserRequest(
            email = "test@example.com",
            password = "Password123!",
            nickname = maxNickname
        )
        
        val response = UserResponse(
            id = UuidCreator.getTimeOrderedEpoch(),
            email = "test@example.com",
            nickname = maxNickname,
            phoneNumber = null,
            birthDate = null,
            profileImageUrl = null,
            bio = null,
            trustScore = 0,
            trustLevel = "BRONZE",
            transactionCount = 0,
            status = UserStatus.ACTIVE,
            lastLoginAt = null,
            lastTokenRefreshAt = null,
            babies = emptyList(),
            regions = emptyList(),
            createdAt = LocalDateTime.now(),
            updatedAt = LocalDateTime.now()
        )
        
        every { userCommand.registerUser(any()) } returns response
        
        // when & then
        mockMvc.perform(
            post("/api/v1/users")
                .contentType(MediaType.APPLICATION_JSON)
                .content(objectMapper.writeValueAsString(request))
        )
            .andExpect(status().isCreated)
            .andExpect(jsonPath("$.data.nickname").value(maxNickname))
    }
    
    @Test
    @DisplayName("유니코드 문자가 포함된 닉네임을 처리할 수 있어야 한다")
    fun shouldHandleUnicodeNickname() {
        // given
        val unicodeNickname = "테스트유저😀"
        val request = RegisterUserRequest(
            email = "test@example.com",
            password = "Password123!",
            nickname = unicodeNickname
        )
        
        val response = UserResponse(
            id = UuidCreator.getTimeOrderedEpoch(),
            email = "test@example.com",
            nickname = unicodeNickname,
            phoneNumber = null,
            birthDate = null,
            profileImageUrl = null,
            bio = null,
            trustScore = 0,
            trustLevel = "BRONZE",
            transactionCount = 0,
            status = UserStatus.ACTIVE,
            lastLoginAt = null,
            lastTokenRefreshAt = null,
            babies = emptyList(),
            regions = emptyList(),
            createdAt = LocalDateTime.now(),
            updatedAt = LocalDateTime.now()
        )
        
        every { userCommand.registerUser(any()) } returns response
        
        // when & then
        mockMvc.perform(
            post("/api/v1/users")
                .contentType(MediaType.APPLICATION_JSON)
                .content(objectMapper.writeValueAsString(request))
        )
            .andExpect(status().isCreated)
            .andExpect(jsonPath("$.data.nickname").value(unicodeNickname))
    }
}