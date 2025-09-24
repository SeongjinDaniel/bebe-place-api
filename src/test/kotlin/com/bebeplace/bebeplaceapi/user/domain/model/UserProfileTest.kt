package com.bebeplace.bebeplaceapi.user.domain.model

import org.junit.jupiter.api.DisplayName
import org.junit.jupiter.api.Test
import org.junit.jupiter.api.assertThrows
import kotlin.test.assertEquals
import kotlin.test.assertNull

@DisplayName("UserProfile 값 객체 테스트")
class UserProfileTest {
    
    @Test
    @DisplayName("유효한 프로필을 생성할 수 있어야 한다")
    fun shouldCreateValidProfile() {
        // when
        val profile = UserProfile("testUser", "010-1234-5678", "https://example.com/image.jpg", "Test bio")
        
        // then
        assertEquals("testUser", profile.getNickname())
        assertEquals("010-1234-5678", profile.getPhoneNumber())
        assertEquals("https://example.com/image.jpg", profile.getProfileImageUrl())
        assertEquals("Test bio", profile.getBio())
    }
    
    @Test
    @DisplayName("선택적 필드들은 null일 수 있어야 한다")
    fun optionalFieldsCanBeNull() {
        // when
        val profile = UserProfile("testUser")
        
        // then
        assertEquals("testUser", profile.getNickname())
        assertNull(profile.getPhoneNumber())
        assertNull(profile.getProfileImageUrl())
        assertNull(profile.getBio())
    }
    
    // Edge Cases for Nickname
    @Test
    @DisplayName("빈 닉네임으로 생성하면 예외가 발생해야 한다")
    fun blankNicknameShouldThrowException() {
        assertThrows<IllegalArgumentException> {
            UserProfile("")
        }
    }
    
    @Test
    @DisplayName("공백만 있는 닉네임으로 생성하면 예외가 발생해야 한다")
    fun whitespaceOnlyNicknameShouldThrowException() {
        assertThrows<IllegalArgumentException> {
            UserProfile("   ")
        }
    }
    
    @Test
    @DisplayName("50자를 초과하는 닉네임으로 생성하면 예외가 발생해야 한다")
    fun nicknameTooLongShouldThrowException() {
        val longNickname = "a".repeat(51)
        assertThrows<IllegalArgumentException> {
            UserProfile(longNickname)
        }
    }
    
    @Test
    @DisplayName("정확히 50자인 닉네임은 허용되어야 한다")
    fun fiftyCharacterNicknameShouldBeAllowed() {
        val nickname = "a".repeat(50)
        val profile = UserProfile(nickname)
        assertEquals(nickname, profile.getNickname())
    }
    
    @Test
    @DisplayName("한 글자 닉네임은 허용되어야 한다")
    fun oneCharacterNicknameShouldBeAllowed() {
        val profile = UserProfile("a")
        assertEquals("a", profile.getNickname())
    }
    
    // Edge Cases for Phone Number
    @Test
    @DisplayName("유효한 휴대폰 번호 형식들이 허용되어야 한다")
    fun validPhoneNumberFormatsShouldBeAllowed() {
        val validFormats = listOf(
            "010-1234-5678",
            "010-123-4567",
            "01012345678",
            "0101234567",
            "016-1234-5678",
            "017-1234-5678",
            "018-1234-5678",
            "019-1234-5678"
        )
        
        validFormats.forEach { phoneNumber ->
            val profile = UserProfile("testUser", phoneNumber)
            assertEquals(phoneNumber, profile.getPhoneNumber())
        }
    }
    
    @Test
    @DisplayName("잘못된 휴대폰 번호 형식은 예외가 발생해야 한다")
    fun invalidPhoneNumberFormatShouldThrowException() {
        val invalidFormats = listOf(
            "02-1234-5678",  // 일반 전화번호
            "031-123-4567",  // 지역번호
            "010-12-5678",   // 가운데 자리 부족
            "010-12345-678", // 가운데 자리 초과
            "010-1234-567",  // 마지막 자리 부족
            "010-1234-56789", // 마지막 자리 초과
            "abc-1234-5678", // 문자 포함
            "010 1234 5678", // 공백 구분
            "010.1234.5678"  // 점 구분
        )
        
        invalidFormats.forEach { phoneNumber ->
            assertThrows<IllegalArgumentException>("Phone number $phoneNumber should be invalid") {
                UserProfile("testUser", phoneNumber)
            }
        }
    }
    
    // Edge Cases for Bio
    @Test
    @DisplayName("500자를 초과하는 bio로 생성하면 예외가 발생해야 한다")
    fun bioTooLongShouldThrowException() {
        val longBio = "a".repeat(501)
        assertThrows<IllegalArgumentException> {
            UserProfile("testUser", bio = longBio)
        }
    }
    
    @Test
    @DisplayName("정확히 500자인 bio는 허용되어야 한다")
    fun fiveHundredCharacterBioShouldBeAllowed() {
        val bio = "a".repeat(500)
        val profile = UserProfile("testUser", bio = bio)
        assertEquals(bio, profile.getBio())
    }
    
    @Test
    @DisplayName("빈 bio는 허용되어야 한다")
    fun emptyBioShouldBeAllowed() {
        val profile = UserProfile("testUser", bio = "")
        assertEquals("", profile.getBio())
    }
    
    // Update Methods Edge Cases
    @Test
    @DisplayName("닉네임 업데이트 시 유효성 검사가 적용되어야 한다")
    fun updateNicknameShouldValidate() {
        val profile = UserProfile("testUser")
        
        assertThrows<IllegalArgumentException> {
            profile.updateNickname("")
        }
        
        assertThrows<IllegalArgumentException> {
            profile.updateNickname("a".repeat(51))
        }
    }
    
    @Test
    @DisplayName("휴대폰 번호 업데이트 시 유효성 검사가 적용되어야 한다")
    fun updatePhoneNumberShouldValidate() {
        val profile = UserProfile("testUser")
        
        assertThrows<IllegalArgumentException> {
            profile.updatePhoneNumber("invalid-phone")
        }
        
        val updated = profile.updatePhoneNumber("010-1234-5678")
        assertEquals("010-1234-5678", updated.getPhoneNumber())
    }
    
    @Test
    @DisplayName("bio 업데이트 시 유효성 검사가 적용되어야 한다")
    fun updateBioShouldValidate() {
        val profile = UserProfile("testUser")
        
        assertThrows<IllegalArgumentException> {
            profile.updateBio("a".repeat(501))
        }
        
        val updated = profile.updateBio("Updated bio")
        assertEquals("Updated bio", updated.getBio())
    }
    
    @Test
    @DisplayName("프로필 이미지 URL을 null로 업데이트할 수 있어야 한다")
    fun shouldUpdateProfileImageUrlToNull() {
        val profile = UserProfile("testUser", profileImageUrl = "https://example.com/image.jpg")
        val updated = profile.updateProfileImageUrl(null)
        assertNull(updated.getProfileImageUrl())
    }
    
    // Unicode and Special Characters
    @Test
    @DisplayName("유니코드 문자가 포함된 닉네임이 허용되어야 한다")
    fun unicodeNicknameShouldBeAllowed() {
        val unicodeNickname = "테스트유저😀"
        val profile = UserProfile(unicodeNickname)
        assertEquals(unicodeNickname, profile.getNickname())
    }
    
    @Test
    @DisplayName("특수문자가 포함된 닉네임이 허용되어야 한다")
    fun specialCharacterNicknameShouldBeAllowed() {
        val specialNickname = "test_user-123"
        val profile = UserProfile(specialNickname)
        assertEquals(specialNickname, profile.getNickname())
    }
}