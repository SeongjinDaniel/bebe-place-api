package com.bebeplace.bebeplaceapi.config

import jakarta.servlet.FilterChain
import jakarta.servlet.http.HttpServletRequest
import jakarta.servlet.http.HttpServletResponse
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken
import org.springframework.security.core.context.SecurityContextHolder
import org.springframework.security.core.userdetails.User
import org.springframework.security.core.userdetails.UserDetails
import org.springframework.security.web.authentication.WebAuthenticationDetailsSource
import org.springframework.stereotype.Component
import org.springframework.web.filter.OncePerRequestFilter

@Component
class JwtAuthenticationFilter(
    private val jwtService: JwtService
) : OncePerRequestFilter() {
    
    override fun doFilterInternal(
        request: HttpServletRequest,
        response: HttpServletResponse,
        filterChain: FilterChain
    ) {
        val authHeader = request.getHeader("Authorization")
        
        if (authHeader == null || !authHeader.startsWith("Bearer ")) {
            filterChain.doFilter(request, response)
            return
        }
        
        try {
            val jwt = authHeader.substring(7)
            
            if (jwtService.validateToken(jwt) && SecurityContextHolder.getContext().authentication == null) {
                val userId = jwtService.getUserIdFromToken(jwt)
                
                if (userId != null) {
                    // SecurityContext에 인증 정보 설정
                    val userDetails: UserDetails = User.builder()
                        .username(userId)
                        .password("")
                        .authorities(emptyList())
                        .build()
                    
                    val authToken = UsernamePasswordAuthenticationToken(
                        userDetails,
                        null,
                        userDetails.authorities
                    )
                    authToken.details = WebAuthenticationDetailsSource().buildDetails(request)
                    SecurityContextHolder.getContext().authentication = authToken
                }
            }
        } catch (e: Exception) {
            logger.debug("JWT 토큰 검증 실패: ${e.message}")
        }
        
        filterChain.doFilter(request, response)
    }
}