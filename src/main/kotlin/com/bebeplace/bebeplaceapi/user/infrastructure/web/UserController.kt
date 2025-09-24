package com.bebeplace.bebeplaceapi.user.infrastructure.web

import com.bebeplace.bebeplaceapi.common.web.ApiResponse
import com.bebeplace.bebeplaceapi.common.web.PagedResponse
import com.bebeplace.bebeplaceapi.user.application.dto.RegisterUserRequest
import com.bebeplace.bebeplaceapi.user.application.dto.UserResponse
import com.bebeplace.bebeplaceapi.user.application.port.input.UserCommand
import jakarta.validation.Valid
import org.springframework.http.HttpStatus
import org.springframework.http.ResponseEntity
import org.springframework.web.bind.annotation.*
import java.util.*

@RestController
@RequestMapping("/api/v1/users")
class UserController(
    private val userCommand: UserCommand
) {
    
    @PostMapping
    fun registerUser(@Valid @RequestBody request: RegisterUserRequest): ResponseEntity<ApiResponse<UserResponse>> {
        return try {
            val userResponse = userCommand.registerUser(request)
            ResponseEntity.status(HttpStatus.CREATED)
                .body(ApiResponse.success(userResponse, "회원가입이 완료되었습니다."))
        } catch (e: IllegalArgumentException) {
            ResponseEntity.badRequest()
                .body(ApiResponse.error("DUPLICATE_EMAIL", e.message ?: "이미 등록된 이메일입니다."))
        } catch (e: Exception) {
            ResponseEntity.internalServerError()
                .body(ApiResponse.error("INTERNAL_ERROR", "회원가입 중 오류가 발생했습니다."))
        }
    }
    
    @GetMapping("/{id}")
    fun getUser(@PathVariable id: UUID): ResponseEntity<ApiResponse<UserResponse>> {
        // TODO: 구현 예정
        return ResponseEntity.notFound().build()
    }
    
    @PutMapping("/{id}")
    fun updateUser(
        @PathVariable id: UUID, 
        @Valid @RequestBody request: RegisterUserRequest
    ): ResponseEntity<ApiResponse<UserResponse>> {
        // TODO: 구현 예정
        return ResponseEntity.notFound().build()
    }
    
    @DeleteMapping("/{id}")
    fun deleteUser(@PathVariable id: UUID): ResponseEntity<Void> {
        // TODO: 구현 예정
        return ResponseEntity.notFound().build()
    }
    
    @GetMapping
    fun getUsers(
        @RequestParam(defaultValue = "0") page: Int,
        @RequestParam(defaultValue = "20") size: Int,
        @RequestParam(required = false) email: String?
    ): ResponseEntity<ApiResponse<PagedResponse<UserResponse>>> {
        // TODO: 구현 예정
        return ResponseEntity.notFound().build()
    }
}

