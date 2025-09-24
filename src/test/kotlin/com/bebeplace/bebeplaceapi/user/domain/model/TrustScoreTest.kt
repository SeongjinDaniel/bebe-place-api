package com.bebeplace.bebeplaceapi.user.domain.model

import org.junit.jupiter.api.DisplayName
import org.junit.jupiter.api.Test
import org.junit.jupiter.api.assertThrows
import kotlin.test.assertEquals

@DisplayName("TrustScore 값 객체 테스트")
class TrustScoreTest {
    
    @Test
    @DisplayName("기본 신뢰도 점수는 0이어야 한다")
    fun defaultTrustScoreShouldBeZero() {
        // when
        val trustScore = TrustScore()
        
        // then
        assertEquals(0, trustScore.getScore())
        assertEquals(0, trustScore.getTransactionCount())
        assertEquals(TrustScore.TrustLevel.BRONZE, trustScore.getLevel())
    }
    
    @Test
    @DisplayName("신뢰도 점수 증가 시 거래 횟수도 증가해야 한다")
    fun increaseShouldIncrementTransactionCount() {
        // given
        val trustScore = TrustScore()
        
        // when
        val increased = trustScore.increase(50)
        
        // then
        assertEquals(50, increased.getScore())
        assertEquals(1, increased.getTransactionCount())
    }
    
    @Test
    @DisplayName("신뢰도 점수는 1000을 초과할 수 없다")
    fun scoreShouldNotExceedMaximum() {
        // given
        val trustScore = TrustScore(950, 10)
        
        // when
        val increased = trustScore.increase(100)
        
        // then
        assertEquals(1000, increased.getScore())
        assertEquals(11, increased.getTransactionCount())
    }
    
    @Test
    @DisplayName("신뢰도 점수 감소 시 거래 횟수는 유지되어야 한다")
    fun decreaseShouldMaintainTransactionCount() {
        // given
        val trustScore = TrustScore(100, 5)
        
        // when
        val decreased = trustScore.decrease(30)
        
        // then
        assertEquals(70, decreased.getScore())
        assertEquals(5, decreased.getTransactionCount())
    }
    
    @Test
    @DisplayName("신뢰도 점수는 0 미만으로 떨어질 수 없다")
    fun scoreShouldNotBeBelowZero() {
        // given
        val trustScore = TrustScore(30, 3)
        
        // when
        val decreased = trustScore.decrease(50)
        
        // then
        assertEquals(0, decreased.getScore())
        assertEquals(3, decreased.getTransactionCount())
    }
    
    @Test
    @DisplayName("신뢰도 레벨이 점수에 따라 올바르게 계산되어야 한다")
    fun trustLevelShouldBeCalculatedCorrectly() {
        assertEquals(TrustScore.TrustLevel.BRONZE, TrustScore(0).getLevel())
        assertEquals(TrustScore.TrustLevel.BRONZE, TrustScore(199).getLevel())
        assertEquals(TrustScore.TrustLevel.SILVER, TrustScore(200).getLevel())
        assertEquals(TrustScore.TrustLevel.SILVER, TrustScore(499).getLevel())
        assertEquals(TrustScore.TrustLevel.GOLD, TrustScore(500).getLevel())
        assertEquals(TrustScore.TrustLevel.GOLD, TrustScore(799).getLevel())
        assertEquals(TrustScore.TrustLevel.PLATINUM, TrustScore(800).getLevel())
        assertEquals(TrustScore.TrustLevel.PLATINUM, TrustScore(1000).getLevel())
    }
    
    @Test
    @DisplayName("음수 점수로 생성하면 예외가 발생해야 한다")
    fun negativeScoreShouldThrowException() {
        assertThrows<IllegalArgumentException> {
            TrustScore(-1)
        }
    }
    
    @Test
    @DisplayName("1000을 초과하는 점수로 생성하면 예외가 발생해야 한다")
    fun scoreAboveMaximumShouldThrowException() {
        assertThrows<IllegalArgumentException> {
            TrustScore(1001)
        }
    }
    
    @Test
    @DisplayName("음수 거래 횟수로 생성하면 예외가 발생해야 한다")
    fun negativeTransactionCountShouldThrowException() {
        assertThrows<IllegalArgumentException> {
            TrustScore(100, -1)
        }
    }
}