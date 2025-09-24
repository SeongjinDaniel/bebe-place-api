package com.bebeplace.bebeplaceapi.user.infrastructure.web

import com.bebeplace.bebeplaceapi.common.types.PhoneNumber
import com.bebeplace.bebeplaceapi.common.web.ApiResponse
import com.bebeplace.bebeplaceapi.user.application.dto.SendSmsVerificationRequest
import com.bebeplace.bebeplaceapi.user.application.dto.VerifySmsCodeRequest
import com.bebeplace.bebeplaceapi.user.application.usecase.SendSmsVerificationUseCase
import com.bebeplace.bebeplaceapi.user.application.usecase.VerifySmsCodeUseCase
import jakarta.validation.Valid
import org.springframework.http.ResponseEntity
import org.springframework.web.bind.annotation.*

@RestController
@RequestMapping("/api/v1/sms")
class SmsVerificationController(
    private val sendSmsVerificationUseCase: SendSmsVerificationUseCase,
    private val verifySmsCodeUseCase: VerifySmsCodeUseCase
) {

    @PostMapping("/send")
    fun sendVerificationCode(
        @Valid @RequestBody request: SendSmsVerificationRequest
    ): ResponseEntity<ApiResponse<Unit>> {
        val phoneNumber = PhoneNumber(request.phoneNumber)
        sendSmsVerificationUseCase.execute(phoneNumber)
        
        return ResponseEntity.ok(
            ApiResponse.success(
                data = Unit,
                message = "인증번호가 발송되었습니다."
            )
        )
    }

    @PostMapping("/verify")
    fun verifyCode(
        @Valid @RequestBody request: VerifySmsCodeRequest
    ): ResponseEntity<ApiResponse<Map<String, Boolean>>> {
        val phoneNumber = PhoneNumber(request.phoneNumber)
        val isValid = verifySmsCodeUseCase.execute(phoneNumber, request.verificationCode)
        
        return ResponseEntity.ok(
            ApiResponse.success(
                data = mapOf("verified" to isValid),
                message = "인증이 완료되었습니다."
            )
        )
    }
}