package com.bebeplace.bebeplaceapi.user.domain.model

import com.bebeplace.bebeplaceapi.common.domain.ValueObject
import java.time.LocalDate

data class UserProfile(
    private val nickname: String,
    private val phoneNumber: String? = null,
    private val profileImageUrl: String? = null,
    private val bio: String? = null,
    private val birthDate: LocalDate? = null
) : ValueObject() {
    
    init {
        require(nickname.isNotBlank()) { "Nickname cannot be blank" }
        require(nickname.length <= 50) { "Nickname cannot exceed 50 characters" }
        phoneNumber?.let { 
            require(it.matches(Regex("^01[016789]-?\\d{3,4}-?\\d{4}$"))) { "Invalid phone number format" }
        }
        bio?.let {
            require(it.length <= 500) { "Bio cannot exceed 500 characters" }
        }
        birthDate?.let {
            require(!it.isAfter(LocalDate.now())) { "Birth date cannot be in the future" }
        }
    }
    
    fun getNickname(): String = nickname
    fun getPhoneNumber(): String? = phoneNumber
    fun getProfileImageUrl(): String? = profileImageUrl
    fun getBio(): String? = bio
    fun getBirthDate(): LocalDate? = birthDate
    
    fun updateNickname(newNickname: String): UserProfile {
        return copy(nickname = newNickname)
    }
    
    fun updatePhoneNumber(newPhoneNumber: String?): UserProfile {
        return copy(phoneNumber = newPhoneNumber)
    }
    
    fun updateProfileImageUrl(newProfileImageUrl: String?): UserProfile {
        return copy(profileImageUrl = newProfileImageUrl)
    }
    
    fun updateBio(newBio: String?): UserProfile {
        return copy(bio = newBio)
    }
    
    fun updateBirthDate(newBirthDate: LocalDate?): UserProfile {
        return copy(birthDate = newBirthDate)
    }
    
    override fun equalityComponents(): List<Any?> = listOf(nickname, phoneNumber, profileImageUrl, bio, birthDate)
}