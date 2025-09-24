package com.bebeplace.bebeplaceapi.product.domain.model

enum class AgeGroup(
    val displayName: String,
    val minMonths: Int,
    val maxMonths: Int?
) {
    NEWBORN_0_3("0~3개월", 0, 3),
    INFANT_4_7("4~7개월", 4, 7),
    INFANT_8_12("8~12개월", 8, 12),
    TODDLER_13_18("13~18개월", 13, 18),
    TODDLER_19_24("19~24개월", 19, 24),
    PRESCHOOL_3_4("3~4세", 36, 48),
    PRESCHOOL_5_6("5~6세", 60, 72),
    SCHOOL_7_PLUS("7세이상", 84, null);
    
    fun isInRange(months: Int): Boolean {
        return months >= minMonths && (maxMonths?.let { months <= it } ?: true)
    }
    
    fun getAgeRangeDescription(): String {
        return maxMonths?.let { "${minMonths}~${it}개월" } 
            ?: "${minMonths}개월 이상"
    }
    
    companion object {
        fun findByMonths(months: Int): List<AgeGroup> {
            return values().filter { it.isInRange(months) }
        }
    }
}