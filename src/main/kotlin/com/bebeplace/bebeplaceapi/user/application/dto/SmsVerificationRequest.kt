package com.bebeplace.bebeplaceapi.user.application.dto

import jakarta.validation.constraints.NotBlank
import jakarta.validation.constraints.Pattern

data class SendSmsVerificationRequest(
    @field:NotBlank(message = "휴대폰 번호는 필수입니다.")
    @field:Pattern(
        regexp = "^01[0-9]{8,9}$",
        message = "올바른 휴대폰 번호 형식이 아닙니다. (01012345678)"
    )
    val phoneNumber: String
)

data class VerifySmsCodeRequest(
    @field:NotBlank(message = "휴대폰 번호는 필수입니다.")
    @field:Pattern(
        regexp = "^01[0-9]{8,9}$",
        message = "올바른 휴대폰 번호 형식이 아닙니다. (01012345678)"
    )
    val phoneNumber: String,

    @field:NotBlank(message = "인증번호는 필수입니다.")
    @field:Pattern(
        regexp = "^[0-9]{6}$",
        message = "인증번호는 6자리 숫자여야 합니다."
    )
    val verificationCode: String
)