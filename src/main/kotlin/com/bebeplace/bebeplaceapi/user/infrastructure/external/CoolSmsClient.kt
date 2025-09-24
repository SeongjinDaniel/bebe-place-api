package com.bebeplace.bebeplaceapi.user.infrastructure.external

import com.bebeplace.bebeplaceapi.common.types.PhoneNumber
import com.bebeplace.bebeplaceapi.user.domain.service.SmsService
import net.nurigo.sdk.NurigoApp
import net.nurigo.sdk.message.model.Message
import net.nurigo.sdk.message.request.SingleMessageSendingRequest
import net.nurigo.sdk.message.service.DefaultMessageService
import org.slf4j.LoggerFactory
import org.springframework.beans.factory.annotation.Value
import org.springframework.stereotype.Component
import jakarta.annotation.PostConstruct

@Component
class CoolSmsClient(
    @Value("\${coolsms.from}") private val from: String,
    @Value("\${coolsms.api.key}") private val apiKey: String,
    @Value("\${coolsms.api.secret}") private val apiSecretKey: String
) : SmsService {

    private val logger = LoggerFactory.getLogger(this::class.java)
    private lateinit var messageService: DefaultMessageService

    @PostConstruct
    private fun init() {
        this.messageService = NurigoApp.initialize(apiKey, apiSecretKey, "https://api.coolsms.co.kr")
        logger.info("CoolSMS client initialized")
    }

    override fun sendVerificationCode(phoneNumber: PhoneNumber, verificationCode: String): Boolean {
        return try {
            val message = Message().apply {
                from = this@CoolSmsClient.from
                to = phoneNumber.getFormattedValue()
                text = "[베베플레이스]\n\n인증번호 $verificationCode 를 입력하세요."
            }

            val response = messageService.sendOne(SingleMessageSendingRequest(message))
            logger.info("SMS sent successfully to ${phoneNumber.getFormattedValue()}: ${response?.messageId}")
            true
        } catch (e: Exception) {
            logger.error("Failed to send SMS to ${phoneNumber.getFormattedValue()}", e)
            false
        }
    }
}