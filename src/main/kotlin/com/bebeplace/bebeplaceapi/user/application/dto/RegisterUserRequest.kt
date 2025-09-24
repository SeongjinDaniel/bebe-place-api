package com.bebeplace.bebeplaceapi.user.application.dto

import com.bebeplace.bebeplaceapi.user.domain.model.BabyGender
import com.bebeplace.bebeplaceapi.user.domain.model.InterestCategory
import jakarta.validation.Valid
import jakarta.validation.constraints.*
import java.time.LocalDate

data class RegisterUserRequest(
    @field:NotBlank(message = "이메일은 필수입니다")
    @field:Email(message = "올바른 이메일 형식이 아닙니다")
    val email: String,
    
    @field:NotBlank(message = "비밀번호는 필수입니다")
    @field:Size(min = 8, max = 20, message = "비밀번호는 8-20자 사이여야 합니다")
    @field:Pattern(
        regexp = "^(?=.*[a-z])(?=.*[A-Z])(?=.*\\d)(?=.*[@\$!%*?&])[A-Za-z\\d@\$!%*?&]+$",
        message = "비밀번호는 대소문자, 숫자, 특수문자를 포함해야 합니다"
    )
    val password: String,
    
    @field:NotBlank(message = "닉네임은 필수입니다")
    @field:Size(min = 2, max = 50, message = "닉네임은 2-50자 사이여야 합니다")
    val nickname: String,
    
    @field:Pattern(
        regexp = "^01[0-9]{8,9}$",
        message = "올바른 휴대폰 번호 형식이 아닙니다. (01012345678)"
    )
    val phoneNumber: String? = null,
    
    // 사용자 프로필 추가 정보
    @field:Past(message = "생년월일은 과거 날짜여야 합니다")
    val birthDate: LocalDate? = null,
    
    val profileImageUrl: String? = null,
    
    @field:Size(max = 500, message = "소개는 500자 이하여야 합니다")
    val bio: String? = null,
    
    // 아기 정보 (선택적)
    @field:Valid
    val babies: List<BabyInfo>? = null,
    
    // 지역 정보 (선택적, 최대 3개)
    @field:Valid
    @field:Size(max = 3, message = "지역은 최대 3개까지 설정할 수 있습니다")
    val regions: List<RegionInfo>? = null
) {
    data class BabyInfo(
        @field:NotBlank(message = "아기 이름은 필수입니다")
        @field:Size(min = 1, max = 50, message = "아기 이름은 1-50자 사이여야 합니다")
        val name: String,
        
        @field:NotNull(message = "아기 성별은 필수입니다")
        val gender: BabyGender,
        
        @field:NotNull(message = "아기 생년월일은 필수입니다")
        @field:PastOrPresent(message = "아기 생년월일은 현재 또는 과거 날짜여야 합니다")
        val birthDate: LocalDate,
        
        val interests: Set<InterestCategory> = emptySet()
    )
    
    data class RegionInfo(
        @field:NotBlank(message = "지역 코드는 필수입니다")
        @field:Size(max = 10, message = "지역 코드는 10자 이하여야 합니다")
        val regionCode: String,
        
        @field:NotBlank(message = "시도는 필수입니다")
        @field:Size(max = 50, message = "시도는 50자 이하여야 합니다")
        val sido: String,
        
        @field:NotBlank(message = "시군구는 필수입니다")
        @field:Size(max = 50, message = "시군구는 50자 이하여야 합니다")
        val sigungu: String,
        
        @field:Size(max = 50, message = "동은 50자 이하여야 합니다")
        val dong: String? = null,
        
        @field:NotNull(message = "우선순위는 필수입니다")
        @field:Min(value = 1, message = "우선순위는 1 이상이어야 합니다")
        @field:Max(value = 3, message = "우선순위는 3 이하여야 합니다")
        val priority: Int
    )
}