package com.bebeplace.bebeplaceapi.common.web

import com.bebeplace.bebeplaceapi.common.exception.BusinessException
import com.bebeplace.bebeplaceapi.common.exception.ValidationException
import org.springframework.http.HttpStatus
import org.springframework.http.ResponseEntity
import org.springframework.validation.FieldError
import org.springframework.web.bind.MethodArgumentNotValidException
import org.springframework.web.bind.annotation.ExceptionHandler
import org.springframework.web.bind.annotation.RestControllerAdvice

@RestControllerAdvice(basePackages = ["com.bebeplace.bebeplaceapi.product", "com.bebeplace.bebeplaceapi.user"])
class GlobalExceptionHandler {
    
    @ExceptionHandler(MethodArgumentNotValidException::class)
    fun handleValidationException(ex: MethodArgumentNotValidException): ResponseEntity<ApiResponse<Any>> {
        val errors = ex.bindingResult.allErrors.map { error ->
            val fieldName = (error as FieldError).field
            val message = error.defaultMessage ?: "Validation error"
            ValidationErrorDetail(fieldName, message)
        }
        
        return ResponseEntity.badRequest()
            .body(ApiResponse.validationError(errors))
    }
    
    @ExceptionHandler(ValidationException::class)
    fun handleValidationException(ex: ValidationException): ResponseEntity<ApiResponse<Any>> {
        val errors = listOf(ValidationErrorDetail(ex.field ?: "unknown", ex.message ?: "Validation error"))
        return ResponseEntity.badRequest()
            .body(ApiResponse.validationError(errors))
    }
    
    @ExceptionHandler(BusinessException::class)
    fun handleBusinessException(ex: BusinessException): ResponseEntity<ApiResponse<Any>> {
        return ResponseEntity.badRequest()
            .body(ApiResponse.error(ex.errorCode, ex.message ?: "Business error"))
    }
    
    @ExceptionHandler(IllegalArgumentException::class)
    fun handleIllegalArgumentException(ex: IllegalArgumentException): ResponseEntity<ApiResponse<Any>> {
        return ResponseEntity.badRequest()
            .body(ApiResponse.error("DUPLICATE_EMAIL", ex.message ?: "Invalid argument"))
    }
    
    @ExceptionHandler(Exception::class)
    fun handleException(ex: Exception): ResponseEntity<ApiResponse<Any>> {
        return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR)
            .body(ApiResponse.error("INTERNAL_ERROR", "Internal server error"))
    }
}