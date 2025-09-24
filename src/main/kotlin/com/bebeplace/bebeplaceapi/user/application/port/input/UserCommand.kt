package com.bebeplace.bebeplaceapi.user.application.port.input

import com.bebeplace.bebeplaceapi.user.application.dto.RegisterUserRequest
import com.bebeplace.bebeplaceapi.user.application.dto.UserResponse

interface UserCommand {
    fun registerUser(request: RegisterUserRequest): UserResponse
}