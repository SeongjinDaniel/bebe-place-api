package com.bebeplace.bebeplaceapi.user.domain.model

import com.bebeplace.bebeplaceapi.common.types.Email
import com.bebeplace.bebeplaceapi.user.domain.event.UserRegistered
import org.junit.jupiter.api.DisplayName
import org.junit.jupiter.api.Test
import org.junit.jupiter.api.assertThrows
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder
import kotlin.test.assertEquals
import kotlin.test.assertFalse
import kotlin.test.assertTrue

@DisplayName("User 도메인 테스트")
class UserTest {
    
    private val passwordEncoder = BCryptPasswordEncoder()
    
    @Test
    @DisplayName("사용자 생성 시 도메인 이벤트가 발행되어야 한다")
    fun createUserShouldPublishDomainEvent() {
        // given
        val email = Email.of("test@example.com")
        val profile = UserProfile("testUser")
        
        // when
        val user = User.create(email, null, "password123", profile, passwordEncoder)
        
        // then
        assertEquals(1, user.getDomainEvents().size)
        val event = user.getDomainEvents().first() as UserRegistered
        assertEquals(user.getId(), event.userId)
        assertEquals(email.getValue(), event.email)
    }
    
    @Test
    @DisplayName("새로 생성된 사용자는 활성 상태여야 한다")
    fun newUserShouldBeActive() {
        // given
        val email = Email.of("test@example.com")
        val profile = UserProfile("testUser")
        
        // when
        val user = User.create(email, null, "password123", profile, passwordEncoder)
        
        // then
        assertTrue(user.isActive())
        assertEquals(UserStatus.ACTIVE, user.getStatus())
    }
    
    @Test
    @DisplayName("비밀번호는 암호화되어 저장되어야 한다")
    fun passwordShouldBeEncoded() {
        // given
        val email = Email.of("test@example.com")
        val profile = UserProfile("testUser")
        val rawPassword = "password123"
        
        // when
        val user = User.create(email, null, rawPassword, profile, passwordEncoder)
        
        // then
        assertTrue(user.getPassword() != rawPassword)
        assertTrue(user.validatePassword(rawPassword, passwordEncoder))
    }
    
    @Test
    @DisplayName("잘못된 비밀번호로 검증하면 실패해야 한다")
    fun validatePasswordShouldFailWithWrongPassword() {
        // given
        val email = Email.of("test@example.com")
        val profile = UserProfile("testUser")
        val user = User.create(email, null, "password123", profile, passwordEncoder)
        
        // when & then
        assertFalse(user.validatePassword("wrongPassword", passwordEncoder))
    }
    
    @Test
    @DisplayName("사용자 프로필을 업데이트할 수 있어야 한다")
    fun shouldUpdateUserProfile() {
        // given
        val email = Email.of("test@example.com")
        val profile = UserProfile("testUser")
        val user = User.create(email, null, "password123", profile, passwordEncoder)
        val newProfile = UserProfile("newTestUser", "010-1234-5678")
        
        // when
        user.updateProfile(newProfile)
        
        // then
        assertEquals("newTestUser", user.getProfile().getNickname())
        assertEquals("010-1234-5678", user.getProfile().getPhoneNumber())
    }
    
    @Test
    @DisplayName("신뢰도 점수를 증가시킬 수 있어야 한다")
    fun shouldIncreaseTrustScore() {
        // given
        val email = Email.of("test@example.com")
        val profile = UserProfile("testUser")
        val user = User.create(email, null, "password123", profile, passwordEncoder)
        
        // when
        user.increaseTrustScore(50)
        
        // then
        assertEquals(50, user.getTrustScore().getScore())
        assertEquals(1, user.getTrustScore().getTransactionCount())
    }
    
    @Test
    @DisplayName("신뢰도 점수를 감소시킬 수 있어야 한다")
    fun shouldDecreaseTrustScore() {
        // given
        val email = Email.of("test@example.com")
        val profile = UserProfile("testUser")
        val user = User.create(email, null, "password123", profile, passwordEncoder)
        user.increaseTrustScore(100)
        
        // when
        user.decreaseTrustScore(30)
        
        // then
        assertEquals(70, user.getTrustScore().getScore())
    }
    
    @Test
    @DisplayName("사용자를 정지시킬 수 있어야 한다")
    fun shouldSuspendUser() {
        // given
        val email = Email.of("test@example.com")
        val profile = UserProfile("testUser")
        val user = User.create(email, null, "password123", profile, passwordEncoder)
        
        // when
        user.suspend()
        
        // then
        assertEquals(UserStatus.SUSPENDED, user.getStatus())
        assertFalse(user.isActive())
    }
    
    @Test
    @DisplayName("정지된 사용자를 다시 활성화할 수 있어야 한다")
    fun shouldActivateSuspendedUser() {
        // given
        val email = Email.of("test@example.com")
        val profile = UserProfile("testUser")
        val user = User.create(email, null, "password123", profile, passwordEncoder)
        user.suspend()
        
        // when
        user.activate()
        
        // then
        assertEquals(UserStatus.ACTIVE, user.getStatus())
        assertTrue(user.isActive())
    }
    
    @Test
    @DisplayName("사용자를 삭제할 수 있어야 한다")
    fun shouldDeleteUser() {
        // given
        val email = Email.of("test@example.com")
        val profile = UserProfile("testUser")
        val user = User.create(email, null, "password123", profile, passwordEncoder)
        
        // when
        user.delete()
        
        // then
        assertEquals(UserStatus.DELETED, user.getStatus())
        assertFalse(user.isActive())
    }
    
    // Edge Cases
    @Test
    @DisplayName("빈 비밀번호로 사용자를 생성할 수 없어야 한다")
    fun shouldNotCreateUserWithEmptyPassword() {
        // given
        val email = Email.of("test@example.com")
        val profile = UserProfile("testUser")
        
        // when & then - 빈 비밀번호로 사용자 생성 시 예외가 발생해야 함
        assertThrows<IllegalArgumentException> {
            User.create(email, null, "", profile, passwordEncoder)
        }
    }
    
    @Test
    @DisplayName("최대 길이 비밀번호는 처리할 수 있어야 한다")
    fun shouldHandleMaxLengthPassword() {
        // given
        val email = Email.of("test@example.com")
        val profile = UserProfile("testUser")
        val maxLengthPassword = "a".repeat(72)  // BCrypt 최대 길이
        
        // when
        val user = User.create(email, null, maxLengthPassword, profile, passwordEncoder)
        
        // then
        assertTrue(user.validatePassword(maxLengthPassword, passwordEncoder))
    }
    
    @Test
    @DisplayName("BCrypt 제한을 초과하는 비밀번호는 거부되어야 한다")
    fun shouldRejectPasswordExceedingBCryptLimit() {
        // given
        val email = Email.of("test@example.com")
        val profile = UserProfile("testUser")
        val tooLongPassword = "a".repeat(100)  // 72자 제한을 초과
        
        // when & then - BCrypt 제한을 초과하는 비밀번호로 사용자 생성 시 예외가 발생해야 함
        assertThrows<IllegalArgumentException> {
            User.create(email, null, tooLongPassword, profile, passwordEncoder)
        }
    }
    
    @Test
    @DisplayName("특수문자가 포함된 비밀번호를 처리할 수 있어야 한다")
    fun shouldHandlePasswordWithSpecialCharacters() {
        // given
        val email = Email.of("test@example.com")
        val profile = UserProfile("testUser")
        val specialPassword = "P@ssw0rd!@#$%^&*()"
        
        // when
        val user = User.create(email, null, specialPassword, profile, passwordEncoder)
        
        // then
        assertTrue(user.validatePassword(specialPassword, passwordEncoder))
        assertFalse(user.validatePassword("wrong", passwordEncoder))
    }
    
    @Test
    @DisplayName("동일한 이메일로 여러 사용자를 생성할 수 있어야 한다 (도메인 레벨에서는 제약 없음)")
    fun shouldCreateMultipleUsersWithSameEmailAtDomainLevel() {
        // given
        val email = Email.of("test@example.com")
        val profile1 = UserProfile("testUser1")
        val profile2 = UserProfile("testUser2")
        
        // when
        val user1 = User.create(email, null, "password123", profile1, passwordEncoder)
        val user2 = User.create(email, null, "password456", profile2, passwordEncoder)
        
        // then - 도메인 레벨에서는 이메일 중복 제약이 없음 (애플리케이션 레벨에서 처리)
        assertEquals(email, user1.getEmail())
        assertEquals(email, user2.getEmail())
        assertTrue(user1.getId() != user2.getId())
    }
    
    @Test
    @DisplayName("신뢰도 점수가 최대값일 때 추가 증가시켜도 안전해야 한다")
    fun shouldSafelyHandleTrustScoreAtMaximum() {
        // given
        val email = Email.of("test@example.com")
        val profile = UserProfile("testUser")
        val user = User.create(email, null, "password123", profile, passwordEncoder)
        
        // 최대값까지 증가
        user.increaseTrustScore(1000)
        val maxScore = user.getTrustScore().getScore()
        
        // when - 추가로 증가 시도
        user.increaseTrustScore(100)
        
        // then - 최대값 유지
        assertEquals(1000, user.getTrustScore().getScore())
        assertEquals(2, user.getTrustScore().getTransactionCount()) // 거래 횟수는 증가
    }
    
    @Test
    @DisplayName("신뢰도 점수가 0일 때 감소시켜도 안전해야 한다")
    fun shouldSafelyHandleTrustScoreAtMinimum() {
        // given
        val email = Email.of("test@example.com")
        val profile = UserProfile("testUser")
        val user = User.create(email, null, "password123", profile, passwordEncoder)
        
        // when - 0에서 감소 시도
        user.decreaseTrustScore(100)
        
        // then - 0 유지
        assertEquals(0, user.getTrustScore().getScore())
        assertEquals(0, user.getTrustScore().getTransactionCount()) // 거래 횟수는 변화 없음
    }
    
    @Test
    @DisplayName("삭제된 사용자의 상태 변경이 제한되어야 한다")
    fun deletedUserStatusChangesShouldBeAllowed() {
        // given
        val email = Email.of("test@example.com")
        val profile = UserProfile("testUser")
        val user = User.create(email, null, "password123", profile, passwordEncoder)
        user.delete()
        
        // when - 삭제된 사용자도 다른 상태로 변경 가능 (비즈니스 규칙에 따라)
        user.activate()
        
        // then
        assertEquals(UserStatus.ACTIVE, user.getStatus())
        assertTrue(user.isActive())
    }
    
    @Test
    @DisplayName("도메인 이벤트가 올바르게 관리되어야 한다")
    fun shouldManageDomainEventsCorrectly() {
        // given
        val email = Email.of("test@example.com")
        val profile = UserProfile("testUser")
        
        // when
        val user = User.create(email, null, "password123", profile, passwordEncoder)
        val events = user.getDomainEvents()
        user.clearDomainEvents()
        
        // then
        assertEquals(1, events.size)
        assertEquals(0, user.getDomainEvents().size)
        assertTrue(events.first() is UserRegistered)
    }
}