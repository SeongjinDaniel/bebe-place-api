package com.bebeplace.bebeplaceapi.user.application.usecase

import com.bebeplace.bebeplaceapi.common.types.Email
import com.bebeplace.bebeplaceapi.user.application.service.TokenService
import com.bebeplace.bebeplaceapi.user.domain.service.AuthenticationService
import org.springframework.stereotype.Service
import org.springframework.transaction.annotation.Transactional
import java.time.LocalDateTime

@Service
@Transactional
class LoginUserUseCase(
    private val authenticationService: AuthenticationService,
    private val tokenService: TokenService
) {
    
    fun execute(command: LoginCommand): LoginResult {
        // 1. 사용자 인증
        val user = authenticationService.authenticateUser(
            Email.of(command.email), 
            command.password
        )
        
        // 2. 로그인 성공 처리
        val updatedUser = authenticationService.processSuccessfulLogin(user)
        
        // 3. 토큰 생성
        val tokenInfo = tokenService.generateAccessToken(updatedUser)
        
        // 4. 결과 반환
        return LoginResult(
            accessToken = tokenInfo.accessToken,
            tokenType = tokenInfo.tokenType,
            expiresIn = tokenInfo.expiresIn,
            userId = updatedUser.getId(),
            email = updatedUser.getEmail().getValue(),
            nickname = updatedUser.getProfile().getNickname(),
            lastLoginAt = updatedUser.getLastLoginAt()
        )
    }
}

data class LoginCommand(
    val email: String,
    val password: String
)

data class LoginResult(
    val accessToken: String,
    val tokenType: String,
    val expiresIn: Long,
    val userId: java.util.UUID,
    val email: String,
    val nickname: String,
    val lastLoginAt: LocalDateTime?
)