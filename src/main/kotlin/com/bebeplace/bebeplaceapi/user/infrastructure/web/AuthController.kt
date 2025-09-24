package com.bebeplace.bebeplaceapi.user.infrastructure.web

import com.bebeplace.bebeplaceapi.common.web.ApiResponse
import com.bebeplace.bebeplaceapi.common.web.ErrorCode
import com.bebeplace.bebeplaceapi.user.application.service.InvalidTokenException
import com.bebeplace.bebeplaceapi.user.application.usecase.LoginCommand
import com.bebeplace.bebeplaceapi.user.application.usecase.LoginUserUseCase
import com.bebeplace.bebeplaceapi.user.application.usecase.RefreshTokenUseCase
import com.bebeplace.bebeplaceapi.user.domain.service.AuthenticationException
import jakarta.servlet.http.HttpServletRequest
import jakarta.validation.Valid
import jakarta.validation.constraints.Email
import jakarta.validation.constraints.NotBlank
import org.springframework.http.HttpStatus
import org.springframework.http.ResponseEntity
import org.springframework.web.bind.annotation.*
import java.time.LocalDateTime

@RestController
@RequestMapping("/api/v1/auth")
class AuthController(
    private val loginUserUseCase: LoginUserUseCase,
    private val refreshTokenUseCase: RefreshTokenUseCase
) {
    
    /**
     * JWT 토큰 발급
     * POST /auth/tokens - 토큰 리소스 생성
     */
    @PostMapping("/tokens")
    fun createToken(@Valid @RequestBody request: LoginRequest): ResponseEntity<ApiResponse<LoginResponse>> {
        try {
            val command = LoginCommand(request.email, request.password)
            val result = loginUserUseCase.execute(command)
            
            val response = LoginResponse(
                accessToken = result.accessToken,
                tokenType = result.tokenType,
                expiresIn = result.expiresIn,
                user = UserInfo(
                    userId = result.userId,
                    email = result.email,
                    nickname = result.nickname,
                    lastLoginAt = result.lastLoginAt
                )
            )
            
            return ResponseEntity.status(HttpStatus.CREATED)
                .body(ApiResponse.success(response, "로그인되었습니다."))
        } catch (e: AuthenticationException) {
            return ResponseEntity.status(HttpStatus.UNAUTHORIZED)
                .body(ApiResponse.error(ErrorCode.LOGIN_FAILED, e.message ?: "로그인에 실패했습니다."))
        } catch (e: IllegalArgumentException) {
            return ResponseEntity.status(HttpStatus.BAD_REQUEST)
                .body(ApiResponse.error(ErrorCode.VALIDATION_ERROR, e.message ?: "잘못된 요청입니다."))
        }
    }
    
    /**
     * JWT 토큰 무효화 (블랙리스트 등록)
     * DELETE /auth/tokens - 현재 토큰 무효화
     */
    @DeleteMapping("/tokens")
    fun revokeToken(): ResponseEntity<Unit> {
        // TODO: 토큰 무효화 로직 구현 (블랙리스트 등록)
        return ResponseEntity.noContent().build()
    }
    
    /**
     * 토큰 갱신
     * PUT /auth/tokens - 기존 토큰으로 새 토큰 발급
     */
    @PutMapping("/tokens")
    fun refreshToken(request: HttpServletRequest): ResponseEntity<ApiResponse<RefreshTokenResponse>> {
        try {
            val authHeader = request.getHeader("Authorization")
            if (authHeader == null || !authHeader.startsWith("Bearer ")) {
                return ResponseEntity.status(HttpStatus.UNAUTHORIZED)
                    .body(ApiResponse.error(ErrorCode.MISSING_AUTHORIZATION, "Authorization 헤더가 필요합니다."))
            }
            
            val token = authHeader.substring(7)
            val result = refreshTokenUseCase.execute(token)
            
            val response = RefreshTokenResponse(
                accessToken = result.accessToken,
                tokenType = result.tokenType,
                expiresIn = result.expiresIn,
                userId = result.userId,
                lastTokenRefreshAt = result.lastTokenRefreshAt
            )
            
            return ResponseEntity.ok(
                ApiResponse.success(response, "토큰이 갱신되었습니다.")
            )
        } catch (e: InvalidTokenException) {
            return ResponseEntity.status(HttpStatus.UNAUTHORIZED)
                .body(ApiResponse.error(ErrorCode.INVALID_TOKEN, e.message ?: "유효하지 않은 토큰입니다."))
        } catch (e: AuthenticationException) {
            return ResponseEntity.status(HttpStatus.UNAUTHORIZED)
                .body(ApiResponse.error(ErrorCode.TOKEN_REFRESH_FAILED, e.message ?: "토큰 갱신에 실패했습니다."))
        } catch (e: IllegalArgumentException) {
            return ResponseEntity.status(HttpStatus.BAD_REQUEST)
                .body(ApiResponse.error(ErrorCode.VALIDATION_ERROR, e.message ?: "잘못된 요청입니다."))
        }
    }
}

data class LoginRequest(
    @field:NotBlank(message = "이메일은 필수입니다")
    @field:Email(message = "올바른 이메일 형식이 아닙니다")
    val email: String,
    
    @field:NotBlank(message = "비밀번호는 필수입니다")
    val password: String
)

data class LoginResponse(
    val accessToken: String,
    val tokenType: String,
    val expiresIn: Long,
    val user: UserInfo
)

data class RefreshTokenResponse(
    val accessToken: String,
    val tokenType: String,
    val expiresIn: Long,
    val userId: java.util.UUID,
    val lastTokenRefreshAt: LocalDateTime?
)

data class UserInfo(
    val userId: java.util.UUID,
    val email: String,
    val nickname: String,
    val lastLoginAt: LocalDateTime?
)