package com.bebeplace.bebeplaceapi.config

import io.jsonwebtoken.Claims
import io.jsonwebtoken.JwtParser
import io.jsonwebtoken.Jwts
import org.springframework.stereotype.Service
import javax.crypto.SecretKey
import java.util.*

@Service
class JwtService(
    private val jwtSigningKey: SecretKey,
    private val jwtParser: JwtParser,
    private val jwtConfig: JwtConfig
) {
    
    fun generateToken(userId: String, email: String): String {
        val now = Date()
        val expiryDate = Date(now.time + jwtConfig.expiration)
        
        return Jwts.builder()
            .subject(userId)
            .claim("email", email)
            .issuedAt(now)
            .expiration(expiryDate)
            .signWith(jwtSigningKey)
            .compact()
    }
    
    fun generateToken(userId: String): String {
        val now = Date()
        val expiryDate = Date(now.time + jwtConfig.expiration)
        
        return Jwts.builder()
            .subject(userId)
            .issuedAt(now)
            .expiration(expiryDate)
            .signWith(jwtSigningKey)
            .compact()
    }
    
    fun validateToken(token: String): Boolean {
        return try {
            jwtParser.parseSignedClaims(token)
            true
        } catch (e: Exception) {
            false
        }
    }
    
    fun getUserIdFromToken(token: String): String? {
        return try {
            val claims = jwtParser.parseSignedClaims(token).payload
            claims.subject
        } catch (e: Exception) {
            null
        }
    }
    
    fun getEmailFromToken(token: String): String? {
        return try {
            val claims = jwtParser.parseSignedClaims(token).payload
            claims["email"] as String
        } catch (e: Exception) {
            null
        }
    }
    
    fun isTokenExpired(token: String): Boolean {
        return try {
            val claims = jwtParser.parseSignedClaims(token).payload
            claims.expiration.before(Date())
        } catch (e: Exception) {
            true
        }
    }
    
    // UseCase에서 사용할 메서드들 추가
    fun isTokenValid(token: String): Boolean = validateToken(token)
    fun extractUserId(token: String): String = getUserIdFromToken(token) 
        ?: throw IllegalArgumentException("토큰에서 사용자 ID를 추출할 수 없습니다.")
    fun getExpirationTime(): Long = jwtConfig.expiration
}