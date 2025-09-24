package com.bebeplace.bebeplaceapi.product.domain.model

enum class ProductCategory(val displayName: String) {
    BABY_CLOTHING("아기의류"),
    GIRLS_CLOTHING("여아의류"),
    BOYS_CLOTHING("남아의류"),
    BABY_PRODUCTS("유아동 물품"),
    TOYS_EDUCATIONAL("교구/장난감"),
    FEEDING_WEANING("수유/이유 용품"),
    MOTHER_PRODUCTS("엄마 용품"),
    SAFETY_PRODUCTS("안전용품"),
    BABY_CARE("아기케어"),
    STROLLERS_CARSEATS("유모차/카시트"),
    ETC("기타")
}