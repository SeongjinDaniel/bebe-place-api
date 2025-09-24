package com.bebeplace.bebeplaceapi.user.application.service

import com.bebeplace.bebeplaceapi.common.exception.BusinessException
import com.bebeplace.bebeplaceapi.common.types.PhoneNumber
import com.bebeplace.bebeplaceapi.user.domain.service.SmsService
import com.github.benmanes.caffeine.cache.Cache
import com.github.benmanes.caffeine.cache.Caffeine
import org.slf4j.LoggerFactory
import org.springframework.stereotype.Service
import java.time.Duration
import kotlin.random.Random

@Service
class SmsVerificationService(
    private val smsService: SmsService
) {
    private val logger = LoggerFactory.getLogger(this::class.java)
    
    private val verificationCache: Cache<String, String> = Caffeine.newBuilder()
        .expireAfterWrite(Duration.ofMinutes(3))
        .maximumSize(10000)
        .recordStats()
        .build()

    fun sendVerificationCode(phoneNumber: PhoneNumber) {
        val verificationCode = generateVerificationCode()
        val key = getKey(phoneNumber)
        
        val success = smsService.sendVerificationCode(phoneNumber, verificationCode)
        if (!success) {
            throw BusinessException("SMS 전송에 실패했습니다.")
        }
        
        verificationCache.put(key, verificationCode)
        logger.info("SMS verification code sent to ${phoneNumber.getFormattedValue()}")
    }

    fun verifyCode(phoneNumber: PhoneNumber, inputCode: String): Boolean {
        val key = getKey(phoneNumber)
        val cachedCode = verificationCache.getIfPresent(key) ?: throw BusinessException("인증번호를 요청하세요.")

        if (cachedCode != inputCode) {
            throw BusinessException("유효하지 않은 인증번호입니다.")
        }
        
        // 인증 성공 시 캐시에서 삭제
        verificationCache.invalidate(key)
        logger.info("SMS verification successful and removed from cache for ${phoneNumber.getFormattedValue()}")
        
        return true
    }

    fun hasVerificationCode(phoneNumber: PhoneNumber): Boolean {
        val key = getKey(phoneNumber)
        return verificationCache.getIfPresent(key) != null
    }

    private fun generateVerificationCode(): String {
        return (100000..999999).random().toString()
    }

    private fun getKey(phoneNumber: PhoneNumber): String {
        return "sms:${phoneNumber.getFormattedValue()}"
    }
    
    fun getCacheStats() = verificationCache.stats()
    
    fun getCacheSize() = verificationCache.estimatedSize()
}