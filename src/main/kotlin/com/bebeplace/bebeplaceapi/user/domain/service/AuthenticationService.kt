package com.bebeplace.bebeplaceapi.user.domain.service

import com.bebeplace.bebeplaceapi.common.types.Email
import com.bebeplace.bebeplaceapi.user.domain.model.User
import com.bebeplace.bebeplaceapi.user.domain.repository.UserRepository
import org.springframework.security.crypto.password.PasswordEncoder
import org.springframework.stereotype.Service
import java.util.*

@Service
class AuthenticationService(
    private val userRepository: UserRepository,
    private val passwordEncoder: PasswordEncoder
) {
    
    fun authenticateUser(email: Email, rawPassword: String): User {
        val user = userRepository.findByEmail(email)
            ?: throw AuthenticationException("이메일 또는 비밀번호가 올바르지 않습니다.")
        
        if (!user.isActive()) {
            throw AuthenticationException("비활성화된 계정입니다.")
        }
        
        if (!user.validatePassword(rawPassword, passwordEncoder)) {
            throw AuthenticationException("이메일 또는 비밀번호가 올바르지 않습니다.")
        }
        
        return user
    }
    
    fun processSuccessfulLogin(user: User): User {
        user.updateLastLoginAt()
        return userRepository.save(user)
    }
    
    fun processTokenRefresh(userId: UUID): User {
        val user = userRepository.findById(userId)
            ?: throw AuthenticationException("사용자를 찾을 수 없습니다.")
        
        if (!user.isActive()) {
            throw AuthenticationException("비활성화된 계정입니다.")
        }
        
        user.updateLastTokenRefreshAt()
        return userRepository.save(user)
    }
}

class AuthenticationException(message: String) : IllegalArgumentException(message)