package com.bebeplace.bebeplaceapi.config

import io.jsonwebtoken.Jwts
import io.jsonwebtoken.security.Keys
import org.springframework.beans.factory.annotation.Value
import org.springframework.context.annotation.Bean
import org.springframework.context.annotation.Configuration
import javax.crypto.SecretKey

@Configuration
class JwtConfig {
    
    @Value("\${jwt.secret:mySecretKey1234567890123456789012345678901234567890}")
    private lateinit var secret: String
    
    @Value("\${jwt.expiration:86400000}") // 24시간
    val expiration: Long = 86400000
    
    @Bean
    fun jwtSigningKey(): SecretKey {
        return Keys.hmacShaKeyFor(secret.toByteArray())
    }
    
    @Bean
    fun jwtParser(jwtSigningKey: SecretKey) = Jwts.parser()
        .verifyWith(jwtSigningKey)
        .build()
}