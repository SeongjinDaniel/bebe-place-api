package com.bebeplace.bebeplaceapi.common.exception

class ValidationException(
    message: String,
    val field: String? = null
) : BusinessException(message, "VALIDATION_ERROR")