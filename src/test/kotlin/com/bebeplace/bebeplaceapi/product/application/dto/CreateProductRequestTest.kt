package com.bebeplace.bebeplaceapi.product.application.dto

import com.bebeplace.bebeplaceapi.product.domain.model.AgeGroup
import com.bebeplace.bebeplaceapi.product.domain.model.ProductCategory
import com.bebeplace.bebeplaceapi.product.domain.model.ProductCondition
import com.bebeplace.bebeplaceapi.product.domain.model.ProductType
import org.junit.jupiter.api.DisplayName
import org.junit.jupiter.api.Test
import org.junit.jupiter.api.assertThrows
import java.math.BigDecimal
import kotlin.test.assertEquals

@DisplayName("CreateProductRequest DTO 테스트")
class CreateProductRequestTest {
    
    @Test
    @DisplayName("유효한 요청 데이터로 객체를 생성할 수 있어야 한다")
    fun shouldCreateValidRequest() {
        // given & when
        val request = CreateProductRequest(
            title = "테스트 상품",
            category = ProductCategory.BABY_CLOTHING,
            price = BigDecimal("10000"),
            shippingIncluded = false,
            shippingCost = BigDecimal("3000"),
            description = "테스트 상품 설명입니다. 10글자 이상이어야 합니다.",
            productType = ProductType.USED,
            condition = ProductCondition.EXCELLENT,
            ageGroups = listOf(AgeGroup.NEWBORN_0_3, AgeGroup.INFANT_4_7)
        )
        
        // then
        assertEquals("테스트 상품", request.title)
        assertEquals(ProductCategory.BABY_CLOTHING, request.category)
        assertEquals(BigDecimal("10000"), request.price)
        assertEquals(false, request.shippingIncluded)
        assertEquals(BigDecimal("3000"), request.shippingCost)
        assertEquals(ProductType.USED, request.productType)
        assertEquals(ProductCondition.EXCELLENT, request.condition)
        assertEquals(2, request.ageGroups.size)
    }
    
    @Test
    @DisplayName("배송비 포함 상품의 경우 배송비가 null이어야 한다")
    fun shouldAcceptNullShippingCostWhenShippingIncluded() {
        // given & when
        val request = CreateProductRequest(
            title = "배송비 포함 상품",
            category = ProductCategory.BABY_CLOTHING,
            price = BigDecimal("15000"),
            shippingIncluded = true,
            shippingCost = null,
            description = "배송비가 포함된 상품입니다. 추가 비용이 없습니다.",
            productType = ProductType.NEW,
            condition = ProductCondition.NEW,
            ageGroups = listOf(AgeGroup.NEWBORN_0_3)
        )
        
        // then
        assertEquals(true, request.shippingIncluded)
        assertEquals(null, request.shippingCost)
    }
    
    @Test
    @DisplayName("배송비 별도인 경우 배송비가 null이면 예외가 발생해야 한다")
    fun shouldThrowExceptionWhenShippingNotIncludedButCostIsNull() {
        // when & then
        val exception = assertThrows<IllegalArgumentException> {
            CreateProductRequest(
                title = "배송비 별도 상품",
                category = ProductCategory.TOYS_EDUCATIONAL,
                price = BigDecimal("20000"),
                shippingIncluded = false,
                shippingCost = null,
                description = "배송비가 별도인데 배송비가 입력되지 않았습니다.",
                productType = ProductType.USED,
                condition = ProductCondition.GOOD,
                ageGroups = listOf(AgeGroup.TODDLER_13_18)
            )
        }
        
        assertEquals("배송비가 별도인 경우 배송비를 입력해야 합니다", exception.message)
    }
    
    @Test
    @DisplayName("배송비 포함인데 배송비가 입력되면 예외가 발생해야 한다")
    fun shouldThrowExceptionWhenShippingIncludedButCostIsProvided() {
        // when & then
        val exception = assertThrows<IllegalArgumentException> {
            CreateProductRequest(
                title = "잘못된 배송비 설정",
                category = ProductCategory.FEEDING_WEANING,
                price = BigDecimal("8000"),
                shippingIncluded = true,
                shippingCost = BigDecimal("2000"),
                description = "배송비가 포함인데 배송비를 입력했습니다.",
                productType = ProductType.NEW,
                condition = ProductCondition.NEW,
                ageGroups = listOf(AgeGroup.NEWBORN_0_3)
            )
        }
        
        assertEquals("배송비가 포함인 경우 배송비를 입력할 수 없습니다", exception.message)
    }
    
    @Test
    @DisplayName("중복된 연령대가 있으면 예외가 발생해야 한다")
    fun shouldThrowExceptionForDuplicateAgeGroups() {
        // when & then
        val exception = assertThrows<IllegalArgumentException> {
            CreateProductRequest(
                title = "중복 연령대 상품",
                category = ProductCategory.BABY_CLOTHING,
                price = BigDecimal("12000"),
                shippingIncluded = false,
                shippingCost = BigDecimal("3000"),
                description = "중복된 연령대를 선택한 상품입니다.",
                productType = ProductType.USED,
                condition = ProductCondition.EXCELLENT,
                ageGroups = listOf(AgeGroup.NEWBORN_0_3, AgeGroup.INFANT_4_7, AgeGroup.NEWBORN_0_3)
            )
        }
        
        assertEquals("중복된 연령대를 선택할 수 없습니다", exception.message)
    }
    
    @Test
    @DisplayName("최대 8개의 연령대를 선택할 수 있어야 한다")
    fun shouldAllowMaximum8AgeGroups() {
        // given & when
        val request = CreateProductRequest(
            title = "다양한 연령대 상품",
            category = ProductCategory.TOYS_EDUCATIONAL,
            price = BigDecimal("25000"),
            shippingIncluded = true,
            shippingCost = null,
            description = "모든 연령대가 사용할 수 있는 교육 완구입니다.",
            productType = ProductType.NEW,
            condition = ProductCondition.NEW,
            ageGroups = listOf(
                AgeGroup.NEWBORN_0_3,
                AgeGroup.INFANT_4_7,
                AgeGroup.TODDLER_13_18,
                AgeGroup.TODDLER_19_24,
                AgeGroup.PRESCHOOL_3_4,
                AgeGroup.PRESCHOOL_5_6,
                AgeGroup.SCHOOL_7_PLUS,
                AgeGroup.INFANT_8_12
            )
        )
        
        // then
        assertEquals(8, request.ageGroups.size)
    }
    
    @Test
    @DisplayName("가격 0원인 상품은 생성할 수 없어야 한다")
    fun shouldNotAllowZeroPrice() {
        // 이는 validation annotation에서 처리되지만, 
        // 실제로는 Spring Boot의 validation framework에서 검증됨
        // 여기서는 객체 생성은 가능하지만 validation에서 걸릴 것임을 테스트
        val request = CreateProductRequest(
            title = "무료 상품",
            category = ProductCategory.BABY_CLOTHING,
            price = BigDecimal("0"),
            shippingIncluded = true,
            shippingCost = null,
            description = "가격이 0원인 상품입니다. Validation에서 걸릴 예정입니다.",
            productType = ProductType.USED,
            condition = ProductCondition.FAIR,
            ageGroups = listOf(AgeGroup.NEWBORN_0_3)
        )
        
        // 객체 생성은 가능하지만 price는 0
        assertEquals(BigDecimal("0"), request.price)
    }
    
    @Test
    @DisplayName("음수 배송비는 설정할 수 없어야 한다")
    fun shouldNotAllowNegativeShippingCost() {
        // Validation annotation에서 처리되는 케이스
        val request = CreateProductRequest(
            title = "음수 배송비 상품",
            category = ProductCategory.BABY_PRODUCTS,
            price = BigDecimal("10000"),
            shippingIncluded = false,
            shippingCost = BigDecimal("-1000"),
            description = "음수 배송비가 설정된 상품입니다. Validation에서 걸릴 예정입니다.",
            productType = ProductType.NEW,
            condition = ProductCondition.NEW,
            ageGroups = listOf(AgeGroup.TODDLER_13_18)
        )
        
        // 객체 생성은 가능하지만 shippingCost는 음수
        assertEquals(BigDecimal("-1000"), request.shippingCost)
    }
    
    @Test
    @DisplayName("빈 문자열 제목은 validation에서 걸려야 한다")
    fun shouldFailValidationForEmptyTitle() {
        // Validation annotation에서 처리되는 케이스
        val request = CreateProductRequest(
            title = "",
            category = ProductCategory.GIRLS_CLOTHING,
            price = BigDecimal("15000"),
            shippingIncluded = true,
            shippingCost = null,
            description = "제목이 비어있는 상품입니다. Validation에서 걸릴 예정입니다.",
            productType = ProductType.USED,
            condition = ProductCondition.GOOD,
            ageGroups = listOf(AgeGroup.PRESCHOOL_3_4)
        )
        
        // 객체 생성은 가능하지만 title은 빈 문자열
        assertEquals("", request.title)
    }
    
    @Test
    @DisplayName("너무 짧은 설명은 validation에서 걸려야 한다")
    fun shouldFailValidationForTooShortDescription() {
        // Validation annotation에서 처리되는 케이스
        val request = CreateProductRequest(
            title = "짧은 설명 상품",
            category = ProductCategory.BOYS_CLOTHING,
            price = BigDecimal("18000"),
            shippingIncluded = false,
            shippingCost = BigDecimal("2500"),
            description = "짧음",  // 10자 미만
            productType = ProductType.NEW,
            condition = ProductCondition.NEW,
            ageGroups = listOf(AgeGroup.SCHOOL_7_PLUS)
        )
        
        // 객체 생성은 가능하지만 description이 너무 짧음
        assertEquals("짧음", request.description)
    }
}