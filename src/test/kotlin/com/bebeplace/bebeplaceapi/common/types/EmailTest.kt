package com.bebeplace.bebeplaceapi.common.types

import org.junit.jupiter.api.DisplayName
import org.junit.jupiter.api.Test
import org.junit.jupiter.api.assertThrows
import kotlin.test.assertEquals
import kotlin.test.assertFalse
import kotlin.test.assertTrue

@DisplayName("Email 값 객체 테스트")
class EmailTest {
    
    @Test
    @DisplayName("유효한 이메일로 생성할 수 있어야 한다")
    fun shouldCreateValidEmail() {
        val email = Email.of("test@example.com")
        assertEquals("test@example.com", email.getValue())
    }
    
    @Test
    @DisplayName("다양한 유효한 이메일 형식이 허용되어야 한다")
    fun validEmailFormatsShouldBeAllowed() {
        val validEmails = listOf(
            "user@example.com",
            "user.name@example.com",
            "user+tag@example.com",
            "user_name@example.com",
            "user-name@example.com",
            "123@example.com",
            "user@sub.example.com",
            "user@example-site.com",
            "user@example.co.kr",
            "a@b.co",
            "test@localhost.com"
        )
        
        validEmails.forEach { emailString ->
            val email = Email.of(emailString)
            assertEquals(emailString, email.getValue())
            assertTrue(Email.isValid(emailString), "Email $emailString should be valid")
        }
    }
    
    @Test
    @DisplayName("잘못된 이메일 형식은 예외가 발생해야 한다")
    fun invalidEmailFormatShouldThrowException() {
        val invalidEmails = listOf(
            "",                    // 빈 문자열
            "   ",                 // 공백만
            "user",                // @ 없음
            "@example.com",        // 로컬 부분 없음
            "user@",               // 도메인 부분 없음
            "user@.com",           // 도메인 시작이 점
            "user@com",            // TLD 없음
            "user@example.",       // TLD 끝이 점
            "user@example..com",   // 연속된 점
            "user.@example.com",   // 로컬 끝이 점
            ".user@example.com",   // 로컬 시작이 점
            "user..name@example.com", // 로컬에 연속된 점
            "user name@example.com",  // 공백 포함
            "user@exam ple.com",   // 도메인에 공백
            "user@",               // 불완전한 형식
            "user@example",        // TLD 없음
            "user@.example.com",   // 도메인 시작이 점
            "user@example.c",      // TLD 너무 짧음
            "user@-example.com",   // 도메인 시작이 하이픈
            "user@example-.com"    // 도메인 끝이 하이픈
        )
        
        invalidEmails.forEach { emailString ->
            assertThrows<IllegalArgumentException>("Email '$emailString' should be invalid") {
                Email.of(emailString)
            }
            assertFalse(Email.isValid(emailString), "Email '$emailString' should be invalid")
        }
    }
    
    @Test
    @DisplayName("이메일 검증 함수가 올바르게 동작해야 한다")
    fun isValidShouldWorkCorrectly() {
        assertTrue(Email.isValid("test@example.com"))
        assertFalse(Email.isValid("invalid-email"))
        assertFalse(Email.isValid(""))
    }
    
    @Test
    @DisplayName("동일한 이메일은 equals가 true를 반환해야 한다")
    fun sameEmailsShouldBeEqual() {
        val email1 = Email.of("test@example.com")
        val email2 = Email.of("test@example.com")
        
        assertEquals(email1, email2)
        assertEquals(email1.hashCode(), email2.hashCode())
    }
    
    @Test
    @DisplayName("다른 이메일은 equals가 false를 반환해야 한다")
    fun differentEmailsShouldNotBeEqual() {
        val email1 = Email.of("test1@example.com")
        val email2 = Email.of("test2@example.com")
        
        assertTrue(email1 != email2)
    }
    
    // Edge Cases for Special Characters
    @Test
    @DisplayName("특수문자가 포함된 유효한 이메일이 허용되어야 한다")
    fun specialCharactersInValidEmailShouldBeAllowed() {
        val specialEmails = listOf(
            "test+tag@example.com",
            "test-user@example.com",
            "test.user@example.com",
            "test_user@example.com",
            "123test@example.com",
            "test123@example.com"
        )
        
        specialEmails.forEach { emailString ->
            val email = Email.of(emailString)
            assertEquals(emailString, email.getValue())
        }
    }
    
    // Edge Cases for Domain
    @Test
    @DisplayName("다양한 도메인 형식이 허용되어야 한다")
    fun variousDomainFormatsShouldBeAllowed() {
        val domainEmails = listOf(
            "user@example.com",
            "user@sub.example.com",
            "user@sub.domain.example.com",
            "user@example-site.com",
            "user@123domain.com",
            "user@domain123.com"
        )
        
        domainEmails.forEach { emailString ->
            val email = Email.of(emailString)
            assertEquals(emailString, email.getValue())
        }
    }
    
    // Edge Cases for Length
    @Test
    @DisplayName("매우 긴 이메일이 허용되어야 한다")
    fun veryLongEmailShouldBeAllowed() {
        val longLocal = "a".repeat(64)  // 일반적인 로컬 부분 최대 길이
        val longEmail = "$longLocal@example.com"
        
        val email = Email.of(longEmail)
        assertEquals(longEmail, email.getValue())
    }
    
    @Test
    @DisplayName("대소문자 구분없이 이메일이 저장되어야 한다")
    fun emailShouldBeStoredAsIs() {
        val mixedCaseEmail = "Test.User@Example.COM"
        val email = Email.of(mixedCaseEmail)
        assertEquals(mixedCaseEmail, email.getValue())
    }
}