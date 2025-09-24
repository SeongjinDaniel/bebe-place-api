package com.bebeplace.bebeplaceapi.user.domain.model

import java.time.LocalDate
import java.util.UUID

class Baby(
    private val id: Long = 0L,
    private val userId: UUID,
    private val name: String,
    private val gender: BabyGender,
    private val birthDate: LocalDate,
    private val interests: MutableSet<InterestCategory> = mutableSetOf()
) {
    
    companion object {
        fun create(
            userId: UUID,
            name: String,
            gender: BabyGender,
            birthDate: LocalDate,
            interests: Set<InterestCategory> = emptySet()
        ): Baby {
            require(name.isNotBlank()) { "Baby name cannot be blank" }
            require(birthDate.isBefore(LocalDate.now()) || birthDate.isEqual(LocalDate.now())) { 
                "Birth date cannot be in the future" 
            }
            
            return Baby(
                userId = userId,
                name = name,
                gender = gender,
                birthDate = birthDate,
                interests = interests.toMutableSet()
            )
        }
        
        fun restore(
            id: Long,
            userId: UUID,
            name: String,
            gender: BabyGender,
            birthDate: LocalDate,
            interests: Set<InterestCategory> = emptySet()
        ): Baby {
            return Baby(
                id = id,
                userId = userId,
                name = name,
                gender = gender,
                birthDate = birthDate,
                interests = interests.toMutableSet()
            )
        }
    }
    
    fun getId(): Long = id
    fun getUserId(): UUID = userId
    fun getName(): String = name
    fun getGender(): BabyGender = gender
    fun getBirthDate(): LocalDate = birthDate
    fun getInterests(): Set<InterestCategory> = interests.toSet()
    
    fun addInterest(interest: InterestCategory) {
        interests.add(interest)
    }
    
    fun removeInterest(interest: InterestCategory) {
        interests.remove(interest)
    }
    
    fun updateInterests(newInterests: Set<InterestCategory>) {
        interests.clear()
        interests.addAll(newInterests)
    }
    
    fun getAgeInMonths(): Int {
        val currentDate = LocalDate.now()
        return (currentDate.year - birthDate.year) * 12 + (currentDate.monthValue - birthDate.monthValue)
    }
    
    fun getAgeInYears(): Int {
        return LocalDate.now().year - birthDate.year
    }
}