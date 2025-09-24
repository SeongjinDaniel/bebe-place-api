package com.bebeplace.bebeplaceapi.user.domain.model

import com.bebeplace.bebeplaceapi.common.domain.ValueObject
import jakarta.persistence.Column
import jakarta.persistence.Embeddable

@Embeddable
data class TrustScore(
    @Column(name = "score")
    private val score: Int = 0,
    
    @Column(name = "transaction_count")
    private val transactionCount: Int = 0
) : ValueObject() {
    
    init {
        require(score >= 0) { "Trust score cannot be negative" }
        require(score <= 1000) { "Trust score cannot exceed 1000" }
        require(transactionCount >= 0) { "Transaction count cannot be negative" }
    }
    
    fun getScore(): Int = score
    fun getTransactionCount(): Int = transactionCount
    fun getLevel(): TrustLevel = when (score) {
        in 0..199 -> TrustLevel.BRONZE
        in 200..499 -> TrustLevel.SILVER
        in 500..799 -> TrustLevel.GOLD
        else -> TrustLevel.PLATINUM
    }
    
    fun increase(points: Int): TrustScore {
        val newScore = minOf(score + points, 1000)
        return copy(score = newScore, transactionCount = transactionCount + 1)
    }
    
    fun decrease(points: Int): TrustScore {
        val newScore = maxOf(score - points, 0)
        return copy(score = newScore)
    }
    
    override fun equalityComponents(): List<Any?> = listOf(score, transactionCount)
    
    enum class TrustLevel {
        BRONZE, SILVER, GOLD, PLATINUM
    }
}