package com.bebeplace.bebeplaceapi.user.domain.model

enum class BabyGender(private val displayName: String) {
    MALE("남아"),
    FEMALE("여아");
    
    fun getDisplayName(): String = displayName
}