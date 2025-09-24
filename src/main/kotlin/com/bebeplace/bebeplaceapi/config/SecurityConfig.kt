package com.bebeplace.bebeplaceapi.config

import org.springframework.context.annotation.Bean
import org.springframework.context.annotation.Configuration
import org.springframework.core.env.Environment
import org.springframework.http.HttpMethod
import org.springframework.security.config.annotation.web.builders.HttpSecurity
import org.springframework.security.config.annotation.web.configuration.EnableWebSecurity
import org.springframework.security.config.http.SessionCreationPolicy
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder
import org.springframework.security.crypto.password.PasswordEncoder
import org.springframework.security.web.SecurityFilterChain
import org.springframework.web.cors.CorsConfiguration
import org.springframework.web.cors.CorsConfigurationSource
import org.springframework.web.cors.UrlBasedCorsConfigurationSource

@Configuration
@EnableWebSecurity
class SecurityConfig(private val environment: Environment) {
    
    @Bean
    fun passwordEncoder(): PasswordEncoder = BCryptPasswordEncoder()
    
    @Bean
    fun securityFilterChain(http: HttpSecurity): SecurityFilterChain {
        val isLocalProfile = environment.activeProfiles.contains("loc")
        return http
            .cors { it.configurationSource(corsConfigurationSource()) }
            .csrf { it.disable() }
            .headers { it.frameOptions { frameOptions -> frameOptions.disable() } } // H2 Console용
            .sessionManagement { it.sessionCreationPolicy(SessionCreationPolicy.STATELESS) }
            .authorizeHttpRequests { auth ->
                if (isLocalProfile) {
                    // 로컬 개발환경에서는 모든 요청 허용
                    auth.anyRequest().permitAll()
                } else {
                    auth
                        // 회원가입은 인증 없이 허용 (REST 표준: POST /users)
                        .requestMatchers(HttpMethod.POST, "/api/v1/users").permitAll()
                        .requestMatchers(HttpMethod.POST, "/api/v1/auth/tokens").permitAll()
                        
                        // 공개 리소스
                        .requestMatchers(HttpMethod.GET, "/api/v1/products/**").permitAll()
                        .requestMatchers(HttpMethod.GET, "/api/v1/categories/**").permitAll()
                        
                        // Actuator endpoints
                        .requestMatchers("/actuator/**").permitAll()
                        
                        // H2 Console (개발용)
                        .requestMatchers("/h2-console/**").permitAll()
                        
                        // Swagger UI (개발용)
                        .requestMatchers("/swagger-ui/**", "/v3/api-docs/**").permitAll()
                        
                        // 나머지는 인증 필요
                        .anyRequest().authenticated()
                }
            }
            .build()
    }
    
    @Bean
    fun corsConfigurationSource(): CorsConfigurationSource {
        val configuration = CorsConfiguration()
        configuration.allowedOriginPatterns = listOf("*")
        configuration.allowedMethods = listOf("GET", "POST", "PUT", "DELETE", "PATCH", "OPTIONS")
        configuration.allowedHeaders = listOf("*")
        configuration.allowCredentials = true
        
        val source = UrlBasedCorsConfigurationSource()
        source.registerCorsConfiguration("/**", configuration)
        return source
    }
}