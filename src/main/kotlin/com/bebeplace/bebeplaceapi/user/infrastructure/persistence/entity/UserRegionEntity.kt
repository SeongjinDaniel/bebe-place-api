package com.bebeplace.bebeplaceapi.user.infrastructure.persistence.entity

import com.bebeplace.bebeplaceapi.common.infrastructure.BaseEntity
import jakarta.persistence.*

@Entity
@Table(
    name = "user_regions",
    uniqueConstraints = [
        UniqueConstraint(columnNames = ["user_id", "region_code"]),
        UniqueConstraint(columnNames = ["user_id", "priority"])
    ]
)
class UserRegionEntity(
    @Id
    @GeneratedValue(strategy = GenerationType.SEQUENCE, generator = "user_region_seq")
    @SequenceGenerator(name = "user_region_seq", sequenceName = "user_region_sequence", allocationSize = 1)
    @Column(name = "id")
    val id: Long = 0L,
    
    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "user_id", nullable = false)
    val user: UserEntity,
    
    @Column(name = "region_code", nullable = false, length = 10)
    val regionCode: String,
    
    @Column(name = "sido", nullable = false, length = 50)
    val sido: String,
    
    @Column(name = "sigungu", nullable = false, length = 50)
    val sigungu: String,
    
    @Column(name = "dong", length = 50)
    val dong: String? = null,
    
    @Column(name = "priority", nullable = false)
    var priority: Int
) : BaseEntity()