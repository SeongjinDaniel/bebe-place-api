package com.bebeplace.bebeplaceapi.config

import io.jsonwebtoken.JwtParser
import io.jsonwebtoken.Jwts
import io.jsonwebtoken.security.Keys
import javax.crypto.SecretKey
import io.mockk.every
import io.mockk.mockk
import org.junit.jupiter.api.BeforeEach
import org.junit.jupiter.api.DisplayName
import org.junit.jupiter.api.Test
import kotlin.test.assertEquals
import kotlin.test.assertFalse
import kotlin.test.assertNotNull
import kotlin.test.assertNull
import kotlin.test.assertTrue

@DisplayName("JwtService 테스트")
class JwtServiceTest {
    
    private lateinit var jwtService: JwtService
    private lateinit var jwtSigningKey: SecretKey
    private lateinit var jwtParser: JwtParser
    private lateinit var jwtConfig: JwtConfig
    
    @BeforeEach
    fun setUp() {
        jwtSigningKey = Keys.hmacShaKeyFor("mySecretKey1234567890123456789012345678901234567890".toByteArray())
        jwtParser = Jwts.parser().verifyWith(jwtSigningKey).build()
        jwtConfig = mockk<JwtConfig>()
        every { jwtConfig.expiration } returns 86400000L // 24시간
        
        jwtService = JwtService(jwtSigningKey, jwtParser, jwtConfig)
    }
    
    @Test
    @DisplayName("유효한 JWT 토큰을 생성할 수 있어야 한다")
    fun shouldGenerateValidJwtToken() {
        // given
        val userId = "user-123"
        val email = "test@example.com"
        
        // when
        val token = jwtService.generateToken(userId, email)
        
        // then
        assertNotNull(token)
        assertTrue(token.isNotEmpty())
        assertTrue(jwtService.validateToken(token))
    }
    
    @Test
    @DisplayName("토큰에서 사용자 ID를 추출할 수 있어야 한다")
    fun shouldExtractUserIdFromToken() {
        // given
        val userId = "user-123"
        val email = "test@example.com"
        val token = jwtService.generateToken(userId, email)
        
        // when
        val extractedUserId = jwtService.getUserIdFromToken(token)
        
        // then
        assertEquals(userId, extractedUserId)
    }
    
    @Test
    @DisplayName("토큰에서 이메일을 추출할 수 있어야 한다")
    fun shouldExtractEmailFromToken() {
        // given
        val userId = "user-123"
        val email = "test@example.com"
        val token = jwtService.generateToken(userId, email)
        
        // when
        val extractedEmail = jwtService.getEmailFromToken(token)
        
        // then
        assertEquals(email, extractedEmail)
    }
    
    @Test
    @DisplayName("잘못된 토큰을 검증하면 false를 반환해야 한다")
    fun shouldReturnFalseForInvalidToken() {
        // given
        val invalidToken = "invalid.jwt.token"
        
        // when
        val isValid = jwtService.validateToken(invalidToken)
        
        // then
        assertFalse(isValid)
    }
    
    @Test
    @DisplayName("다른 키로 서명된 토큰을 검증하면 false를 반환해야 한다")
    fun shouldReturnFalseForTokenSignedWithDifferentKey() {
        // given
        val differentKey = Keys.hmacShaKeyFor("differentSecretKey123456789012345678901234567890".toByteArray())
        val tokenWithDifferentKey = Jwts.builder()
            .subject("user-123")
            .claim("email", "test@example.com")
            .signWith(differentKey)
            .compact()
        
        // when
        val isValid = jwtService.validateToken(tokenWithDifferentKey)
        
        // then
        assertFalse(isValid)
    }
    
    @Test
    @DisplayName("만료된 토큰을 검증하면 false를 반환해야 한다")
    fun shouldReturnFalseForExpiredToken() {
        // given
        val expiredConfig = mockk<JwtConfig>()
        every { expiredConfig.expiration } returns -1L // 이미 만료됨
        
        val expiredJwtService = JwtService(jwtSigningKey, jwtParser, expiredConfig)
        val token = expiredJwtService.generateToken("user-123", "test@example.com")
        
        // when
        val isValid = jwtService.validateToken(token)
        val isExpired = jwtService.isTokenExpired(token)
        
        // then
        assertFalse(isValid)
        assertTrue(isExpired)
    }
    
    @Test
    @DisplayName("새로 생성된 토큰은 만료되지 않아야 한다")
    fun shouldReturnFalseForNonExpiredToken() {
        // given
        val userId = "user-123"
        val email = "test@example.com"
        val token = jwtService.generateToken(userId, email)
        
        // when
        val isExpired = jwtService.isTokenExpired(token)
        
        // then
        assertFalse(isExpired)
    }
    
    @Test
    @DisplayName("잘못된 토큰에서 사용자 ID 추출하면 null을 반환해야 한다")
    fun shouldReturnNullForInvalidTokenUserId() {
        // given
        val invalidToken = "invalid.jwt.token"
        
        // when
        val userId = jwtService.getUserIdFromToken(invalidToken)
        
        // then
        assertEquals(null, userId)
    }
    
    @Test
    @DisplayName("잘못된 토큰에서 이메일 추출하면 null을 반환해야 한다")
    fun shouldReturnNullForInvalidTokenEmail() {
        // given
        val invalidToken = "invalid.jwt.token"
        
        // when
        val email = jwtService.getEmailFromToken(invalidToken)
        
        // then
        assertEquals(null, email)
    }
    
    @Test
    @DisplayName("잘못된 토큰의 만료 여부 확인하면 true를 반환해야 한다")
    fun shouldReturnTrueForInvalidTokenExpiry() {
        // given
        val invalidToken = "invalid.jwt.token"
        
        // when
        val isExpired = jwtService.isTokenExpired(invalidToken)
        
        // then
        assertTrue(isExpired)
    }
    
    // Edge Cases
    @Test
    @DisplayName("빈 문자열 사용자 ID로 토큰을 생성하면 null이 반환되어야 한다")
    fun shouldReturnNullWhenExtractingEmptyUserId() {
        // given
        val userId = ""
        val email = "test@example.com"
        
        // when
        val token = jwtService.generateToken(userId, email)
        val extractedUserId = jwtService.getUserIdFromToken(token)
        
        // then - JWT 스펙상 빈 문자열 subject는 null로 처리됨
        assertNull(extractedUserId)
    }
    
    @Test
    @DisplayName("빈 문자열 이메일로 토큰을 생성할 수 있어야 한다")
    fun shouldGenerateTokenWithEmptyEmail() {
        // given
        val userId = "user-123"
        val email = ""
        
        // when
        val token = jwtService.generateToken(userId, email)
        val extractedEmail = jwtService.getEmailFromToken(token)
        
        // then
        assertEquals(email, extractedEmail)
    }
    
    @Test
    @DisplayName("매우 긴 사용자 ID로 토큰을 생성할 수 있어야 한다")
    fun shouldGenerateTokenWithVeryLongUserId() {
        // given
        val userId = "user-" + "a".repeat(1000)
        val email = "test@example.com"
        
        // when
        val token = jwtService.generateToken(userId, email)
        val extractedUserId = jwtService.getUserIdFromToken(token)
        
        // then
        assertEquals(userId, extractedUserId)
    }
    
    @Test
    @DisplayName("특수문자가 포함된 사용자 정보로 토큰을 생성할 수 있어야 한다")
    fun shouldGenerateTokenWithSpecialCharacters() {
        // given
        val userId = "user-123@#$%"
        val email = "test+tag@example-site.com"
        
        // when
        val token = jwtService.generateToken(userId, email)
        val extractedUserId = jwtService.getUserIdFromToken(token)
        val extractedEmail = jwtService.getEmailFromToken(token)
        
        // then
        assertEquals(userId, extractedUserId)
        assertEquals(email, extractedEmail)
    }
    
    @Test
    @DisplayName("유니코드 문자가 포함된 정보로 토큰을 생성할 수 있어야 한다")
    fun shouldGenerateTokenWithUnicodeCharacters() {
        // given
        val userId = "사용자-123"
        val email = "테스트@example.com"
        
        // when
        val token = jwtService.generateToken(userId, email)
        val extractedUserId = jwtService.getUserIdFromToken(token)
        val extractedEmail = jwtService.getEmailFromToken(token)
        
        // then
        assertEquals(userId, extractedUserId)
        assertEquals(email, extractedEmail)
    }
    
    @Test
    @DisplayName("null 문자열로 사용자 ID 추출을 시도하면 null을 반환해야 한다")
    fun shouldReturnNullForNullTokenUserId() {
        // when
        val userId = jwtService.getUserIdFromToken("")
        
        // then
        assertEquals(null, userId)
    }
    
    @Test
    @DisplayName("짧은 문자열로 토큰 검증을 시도하면 false를 반환해야 한다")
    fun shouldReturnFalseForShortInvalidToken() {
        // given
        val shortToken = "abc"
        
        // when
        val isValid = jwtService.validateToken(shortToken)
        
        // then
        assertFalse(isValid)
    }
}