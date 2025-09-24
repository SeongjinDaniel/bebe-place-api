package com.bebeplace.bebeplaceapi.common.web

data class ApiResponse<T>(
    val success: Boolean,
    val data: T? = null,
    val message: String? = null,
    val error: ErrorInfo? = null
) {
    companion object {
        fun <T> success(data: T, message: String? = null): ApiResponse<T> {
            return ApiResponse(success = true, data = data, message = message)
        }
        
        fun <T> error(code: String, message: String): ApiResponse<T> {
            return ApiResponse(
                success = false,
                error = ErrorInfo(code, message)
            )
        }
        
        fun <T> validationError(errors: List<ValidationErrorDetail>): ApiResponse<T> {
            return ApiResponse(
                success = false,
                error = ErrorInfo("VALIDATION_ERROR", "입력값이 올바르지 않습니다."),
                data = null
            )
        }
    }
}

data class ErrorInfo(
    val code: String,
    val message: String
)

data class ValidationErrorDetail(
    val field: String,
    val message: String
)