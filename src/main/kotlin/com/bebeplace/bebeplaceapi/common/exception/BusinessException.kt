package com.bebeplace.bebeplaceapi.common.exception

open class BusinessException(
    message: String,
    val errorCode: String = "BUSINESS_ERROR"
) : RuntimeException(message)