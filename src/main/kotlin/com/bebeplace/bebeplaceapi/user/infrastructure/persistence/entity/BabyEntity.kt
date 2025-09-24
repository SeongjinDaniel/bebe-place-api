package com.bebeplace.bebeplaceapi.user.infrastructure.persistence.entity

import com.bebeplace.bebeplaceapi.common.infrastructure.BaseEntity
import com.bebeplace.bebeplaceapi.user.domain.model.BabyGender
import com.bebeplace.bebeplaceapi.user.domain.model.InterestCategory
import jakarta.persistence.*
import java.time.LocalDate

@Entity
@Table(name = "babies")
class BabyEntity(
    @Id
    @GeneratedValue(strategy = GenerationType.SEQUENCE, generator = "baby_seq")
    @SequenceGenerator(name = "baby_seq", sequenceName = "baby_sequence", allocationSize = 1)
    @Column(name = "id")
    val id: Long = 0L,
    
    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "user_id", nullable = false)
    val user: UserEntity,
    
    @Column(name = "name", nullable = false, length = 50)
    var name: String,

    @Enumerated(EnumType.STRING)
    @Column(name = "gender", nullable = false)
    var gender: BabyGender,
    
    @Column(name = "birth_date", nullable = false)
    var birthDate: LocalDate,
    
    @ElementCollection(fetch = FetchType.LAZY)
    @CollectionTable(
        name = "baby_interests",
        joinColumns = [JoinColumn(name = "baby_id")]
    )
    @Enumerated(EnumType.STRING)
    @Column(name = "interest_category")
    val interests: MutableSet<InterestCategory> = mutableSetOf()
) : BaseEntity()