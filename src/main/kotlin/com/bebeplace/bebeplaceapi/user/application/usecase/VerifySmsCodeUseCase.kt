package com.bebeplace.bebeplaceapi.user.application.usecase

import com.bebeplace.bebeplaceapi.common.types.PhoneNumber
import com.bebeplace.bebeplaceapi.user.application.service.SmsVerificationService
import org.springframework.stereotype.Service

@Service
class VerifySmsCodeUseCase(
    private val smsVerificationService: SmsVerificationService
) {

    fun execute(phoneNumber: PhoneNumber, verificationCode: String): Boolean {
        return smsVerificationService.verifyCode(phoneNumber, verificationCode)
    }
}