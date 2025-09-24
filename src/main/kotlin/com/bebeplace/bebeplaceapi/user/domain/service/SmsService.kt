package com.bebeplace.bebeplaceapi.user.domain.service

import com.bebeplace.bebeplaceapi.common.types.PhoneNumber

interface SmsService {
    fun sendVerificationCode(phoneNumber: PhoneNumber, verificationCode: String): Boolean
}