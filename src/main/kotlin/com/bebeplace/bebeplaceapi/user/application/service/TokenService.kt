package com.bebeplace.bebeplaceapi.user.application.service

import com.bebeplace.bebeplaceapi.config.JwtService
import com.bebeplace.bebeplaceapi.user.domain.model.User
import com.github.f4b6a3.uuid.UuidCreator
import org.springframework.stereotype.Service
import java.util.*

@Service
class TokenService(
    private val jwtService: JwtService
) {
    
    fun generateAccessToken(user: User): TokenInfo {
        val accessToken = jwtService.generateToken(user.getId().toString())
        
        return TokenInfo(
            accessToken = accessToken,
            tokenType = "Bearer",
            expiresIn = jwtService.getExpirationTime()
        )
    }
    
    fun validateAndExtractUserId(token: String): UUID {
        if (!jwtService.isTokenValid(token)) {
            throw InvalidTokenException("유효하지 않은 토큰입니다.")
        }
        
        val userId = jwtService.extractUserId(token)
        return UuidCreator.fromString(userId)
    }
}

data class TokenInfo(
    val accessToken: String,
    val tokenType: String,
    val expiresIn: Long
)

class InvalidTokenException(message: String) : IllegalArgumentException(message)