package com.bebeplace.bebeplaceapi.user.application.usecase

import com.bebeplace.bebeplaceapi.user.application.service.TokenService
import com.bebeplace.bebeplaceapi.user.domain.service.AuthenticationService
import org.springframework.stereotype.Service
import org.springframework.transaction.annotation.Transactional
import java.time.LocalDateTime
import java.util.*

@Service
@Transactional
class RefreshTokenUseCase(
    private val authenticationService: AuthenticationService,
    private val tokenService: TokenService
) {
    
    fun execute(currentToken: String): RefreshTokenResult {
        // 1. 토큰 유효성 검증 및 사용자 ID 추출
        val userId = tokenService.validateAndExtractUserId(currentToken)
        
        // 2. 토큰 갱신 처리
        val updatedUser = authenticationService.processTokenRefresh(userId)
        
        // 3. 새 토큰 생성
        val tokenInfo = tokenService.generateAccessToken(updatedUser)
        
        // 4. 결과 반환
        return RefreshTokenResult(
            accessToken = tokenInfo.accessToken,
            tokenType = tokenInfo.tokenType,
            expiresIn = tokenInfo.expiresIn,
            userId = updatedUser.getId(),
            lastTokenRefreshAt = updatedUser.getLastTokenRefreshAt()
        )
    }
}

data class RefreshTokenResult(
    val accessToken: String,
    val tokenType: String,
    val expiresIn: Long,
    val userId: UUID,
    val lastTokenRefreshAt: LocalDateTime?
)