package com.bebeplace.bebeplaceapi.common.util

import com.github.f4b6a3.uuid.UuidCreator
import jakarta.servlet.http.HttpServletRequest
import org.springframework.security.core.context.SecurityContextHolder
import org.springframework.stereotype.Component
import org.springframework.web.context.request.RequestContextHolder
import org.springframework.web.context.request.ServletRequestAttributes
import java.util.*

@Component
class RequestContextUtil {
    
    fun getCurrentUserId(): UUID {
        val authentication = SecurityContextHolder.getContext().authentication
            ?: throw IllegalStateException("인증 정보를 찾을 수 없습니다.")
        
        val username = authentication.name
            ?: throw IllegalStateException("사용자 정보를 찾을 수 없습니다.")
        
        return try {
            UuidCreator.fromString(username)
        } catch (e: IllegalArgumentException) {
            throw IllegalStateException("유효하지 않은 사용자 ID 형식입니다: $username")
        }
    }
    
    fun getCurrentClientIp(): String? {
        return try {
            val request = getCurrentRequest()
            getClientIpFromRequest(request)
        } catch (e: Exception) {
            null
        }
    }
    
    private fun getCurrentRequest(): HttpServletRequest {
        val requestAttributes = RequestContextHolder.currentRequestAttributes() as ServletRequestAttributes
        return requestAttributes.request
    }
    
    private fun getClientIpFromRequest(request: HttpServletRequest): String {
        val xForwardedFor = request.getHeader("X-Forwarded-For")
        if (!xForwardedFor.isNullOrBlank()) {
            return xForwardedFor.split(",")[0].trim()
        }
        
        val xRealIp = request.getHeader("X-Real-IP")
        if (!xRealIp.isNullOrBlank()) {
            return xRealIp
        }
        
        val xOriginalForwardedFor = request.getHeader("X-Original-Forwarded-For")
        if (!xOriginalForwardedFor.isNullOrBlank()) {
            return xOriginalForwardedFor.split(",")[0].trim()
        }
        
        return request.remoteAddr ?: "unknown"
    }
}