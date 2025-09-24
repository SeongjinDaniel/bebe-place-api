package com.bebeplace.bebeplaceapi.user.domain.model

enum class InterestCategory(private val displayName: String) {
    BABY_CLOTHING("아기의류"),
    GIRLS_CLOTHING("여아의류"),
    BOYS_CLOTHING("남아의류"),
    BABY_PRODUCTS("유아동 물품"),
    TOYS_EDUCATIONAL("교구/장난감"),
    FEEDING_WEANING("수유/이유 용품"),
    MOTHER_PRODUCTS("엄마 용품");
    
    fun getDisplayName(): String = displayName
}