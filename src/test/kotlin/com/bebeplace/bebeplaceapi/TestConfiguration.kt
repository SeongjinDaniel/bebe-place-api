package com.bebeplace.bebeplaceapi

import org.springframework.boot.test.context.TestConfiguration
import org.springframework.context.annotation.Bean
import org.springframework.context.annotation.Primary
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder
import org.springframework.security.crypto.password.PasswordEncoder
import org.springframework.test.context.ActiveProfiles

@TestConfiguration
@ActiveProfiles("test")
class TestConfiguration {
    
    @Bean
    @Primary
    fun testPasswordEncoder(): PasswordEncoder {
        // 테스트에서는 빠른 해싱을 위해 라운드 수를 줄임
        return BCryptPasswordEncoder(4)
    }
}