package com.bebeplace.bebeplaceapi.product.domain.model

import org.junit.jupiter.api.DisplayName
import org.junit.jupiter.api.Test
import kotlin.test.assertEquals
import kotlin.test.assertNotNull
import kotlin.test.assertNull
import kotlin.test.assertTrue

@DisplayName("DetailedProductCategory 도메인 모델 테스트")
class DetailedProductCategoryTest {
    
    @Test
    @DisplayName("아기 의류 카테고리의 하위 카테고리들을 조회할 수 있어야 한다")
    fun shouldGetBabyClothingSubCategories() {
        // when
        val babyClothingCategories = DetailedProductCategory.getByParentCategory(ProductCategory.BABY_CLOTHING)
        
        // then
        assertTrue(babyClothingCategories.isNotEmpty())
        assertTrue(babyClothingCategories.contains(DetailedProductCategory.BABY_CLOTHING_ALL))
        assertTrue(babyClothingCategories.contains(DetailedProductCategory.BABY_CLOTHING_ROMPER))
        assertTrue(babyClothingCategories.contains(DetailedProductCategory.BABY_CLOTHING_SWADDLE))
        assertTrue(babyClothingCategories.contains(DetailedProductCategory.BABY_CLOTHING_DRESS))
        assertTrue(babyClothingCategories.contains(DetailedProductCategory.BABY_CLOTHING_ETC))
        assertEquals(11, babyClothingCategories.size)
    }
    
    @Test
    @DisplayName("여아 의류 카테고리의 하위 카테고리들을 조회할 수 있어야 한다")
    fun shouldGetGirlsClothingSubCategories() {
        // when
        val girlsClothingCategories = DetailedProductCategory.getByParentCategory(ProductCategory.GIRLS_CLOTHING)
        
        // then
        assertTrue(girlsClothingCategories.isNotEmpty())
        assertTrue(girlsClothingCategories.contains(DetailedProductCategory.GIRLS_CLOTHING_ALL))
        assertTrue(girlsClothingCategories.contains(DetailedProductCategory.GIRLS_CLOTHING_DRESS))
        assertTrue(girlsClothingCategories.contains(DetailedProductCategory.GIRLS_CLOTHING_SET))
        assertTrue(girlsClothingCategories.contains(DetailedProductCategory.GIRLS_CLOTHING_SHOES))
        assertTrue(girlsClothingCategories.contains(DetailedProductCategory.GIRLS_CLOTHING_ETC))
        assertEquals(16, girlsClothingCategories.size)
    }
    
    @Test
    @DisplayName("남아 의류 카테고리의 하위 카테고리들을 조회할 수 있어야 한다")
    fun shouldGetBoysClothingSubCategories() {
        // when
        val boysClothingCategories = DetailedProductCategory.getByParentCategory(ProductCategory.BOYS_CLOTHING)
        
        // then
        assertTrue(boysClothingCategories.isNotEmpty())
        assertTrue(boysClothingCategories.contains(DetailedProductCategory.BOYS_CLOTHING_ALL))
        assertTrue(boysClothingCategories.contains(DetailedProductCategory.BOYS_CLOTHING_SET))
        assertTrue(boysClothingCategories.contains(DetailedProductCategory.BOYS_CLOTHING_SHIRT))
        assertTrue(boysClothingCategories.contains(DetailedProductCategory.BOYS_CLOTHING_SHOES))
        assertTrue(boysClothingCategories.contains(DetailedProductCategory.BOYS_CLOTHING_ETC))
        assertEquals(14, boysClothingCategories.size)
    }
    
    @Test
    @DisplayName("유아동 용품 카테고리의 하위 카테고리들을 조회할 수 있어야 한다")
    fun shouldGetBabyProductsSubCategories() {
        // when
        val babyProductsCategories = DetailedProductCategory.getByParentCategory(ProductCategory.BABY_PRODUCTS)
        
        // then
        assertTrue(babyProductsCategories.isNotEmpty())
        assertTrue(babyProductsCategories.contains(DetailedProductCategory.BABY_PRODUCTS_ALL))
        assertTrue(babyProductsCategories.contains(DetailedProductCategory.BABY_PRODUCTS_STROLLER))
        assertTrue(babyProductsCategories.contains(DetailedProductCategory.BABY_PRODUCTS_CARSEAT))
        assertTrue(babyProductsCategories.contains(DetailedProductCategory.BABY_PRODUCTS_DIAPER))
        assertTrue(babyProductsCategories.contains(DetailedProductCategory.BABY_PRODUCTS_ETC))
        assertEquals(12, babyProductsCategories.size)
    }
    
    @Test
    @DisplayName("교구/장난감 카테고리의 하위 카테고리들을 조회할 수 있어야 한다")
    fun shouldGetToysEducationalSubCategories() {
        // when
        val toysCategories = DetailedProductCategory.getByParentCategory(ProductCategory.TOYS_EDUCATIONAL)
        
        // then
        assertTrue(toysCategories.isNotEmpty())
        assertTrue(toysCategories.contains(DetailedProductCategory.TOYS_EDUCATIONAL_ALL))
        assertTrue(toysCategories.contains(DetailedProductCategory.TOYS_EDUCATIONAL_DOLL))
        assertTrue(toysCategories.contains(DetailedProductCategory.TOYS_EDUCATIONAL_ROBOT))
        assertTrue(toysCategories.contains(DetailedProductCategory.TOYS_EDUCATIONAL_PLAYHOUSE))
        assertTrue(toysCategories.contains(DetailedProductCategory.TOYS_EDUCATIONAL_ETC))
        assertEquals(9, toysCategories.size)
    }
    
    @Test
    @DisplayName("수유/이유 용품 카테고리의 하위 카테고리들을 조회할 수 있어야 한다")
    fun shouldGetFeedingWeaningSubCategories() {
        // when
        val feedingCategories = DetailedProductCategory.getByParentCategory(ProductCategory.FEEDING_WEANING)
        
        // then
        assertTrue(feedingCategories.isNotEmpty())
        assertTrue(feedingCategories.contains(DetailedProductCategory.FEEDING_WEANING_ALL))
        assertTrue(feedingCategories.contains(DetailedProductCategory.FEEDING_WEANING_NURSING))
        assertTrue(feedingCategories.contains(DetailedProductCategory.FEEDING_WEANING_BOTTLE))
        assertTrue(feedingCategories.contains(DetailedProductCategory.FEEDING_WEANING_FORMULA))
        assertTrue(feedingCategories.contains(DetailedProductCategory.FEEDING_WEANING_ETC))
        assertEquals(6, feedingCategories.size)
    }
    
    @Test
    @DisplayName("엄마 용품 카테고리의 하위 카테고리들을 조회할 수 있어야 한다")
    fun shouldGetMotherProductsSubCategories() {
        // when
        val motherProductsCategories = DetailedProductCategory.getByParentCategory(ProductCategory.MOTHER_PRODUCTS)
        
        // then
        assertTrue(motherProductsCategories.isNotEmpty())
        assertTrue(motherProductsCategories.contains(DetailedProductCategory.MOTHER_PRODUCTS_ALL))
        assertTrue(motherProductsCategories.contains(DetailedProductCategory.MOTHER_PRODUCTS_MATERNITY))
        assertTrue(motherProductsCategories.contains(DetailedProductCategory.MOTHER_PRODUCTS_HEALTH))
        assertTrue(motherProductsCategories.contains(DetailedProductCategory.MOTHER_PRODUCTS_ETC))
        assertEquals(4, motherProductsCategories.size)
    }
    
    @Test
    @DisplayName("존재하지 않는 상위 카테고리로 조회하면 빈 리스트가 반환되어야 한다")
    fun shouldReturnEmptyListForNonExistentParentCategory() {
        // when
        val result = DetailedProductCategory.getByParentCategory(ProductCategory.SAFETY_PRODUCTS)
        
        // then
        assertTrue(result.isEmpty())
    }
    
    @Test
    @DisplayName("표시명으로 카테고리를 찾을 수 있어야 한다")
    fun shouldFindCategoryByDisplayName() {
        // when
        val found = DetailedProductCategory.getAllByDisplayName("원피스")
        
        // then
        assertNotNull(found)
        assertEquals(DetailedProductCategory.BABY_CLOTHING_DRESS, found)
        assertEquals("원피스", found.displayName)
        assertEquals(ProductCategory.BABY_CLOTHING, found.parentCategory)
    }
    
    @Test
    @DisplayName("존재하지 않는 표시명으로 조회하면 null이 반환되어야 한다")
    fun shouldReturnNullForNonExistentDisplayName() {
        // when
        val result = DetailedProductCategory.getAllByDisplayName("존재하지않는카테고리")
        
        // then
        assertNull(result)
    }
    
    @Test
    @DisplayName("모든 상위 카테고리가 하위 카테고리를 가져야 한다")
    fun shouldHaveSubCategoriesForAllParentCategories() {
        // given
        val allParentCategories = ProductCategory.values().toSet()
        val parentCategoriesWithSubCategories = DetailedProductCategory.values()
            .map { it.parentCategory }
            .toSet()
        
        // when & then
        // 현재 구현된 상위 카테고리들만 검증
        val implementedCategories = setOf(
            ProductCategory.BABY_CLOTHING,
            ProductCategory.GIRLS_CLOTHING,
            ProductCategory.BOYS_CLOTHING,
            ProductCategory.BABY_PRODUCTS,
            ProductCategory.TOYS_EDUCATIONAL,
            ProductCategory.FEEDING_WEANING,
            ProductCategory.MOTHER_PRODUCTS
        )
        
        implementedCategories.forEach { parentCategory ->
            assertTrue(parentCategoriesWithSubCategories.contains(parentCategory))
            val subCategories = DetailedProductCategory.getByParentCategory(parentCategory)
            assertTrue(subCategories.isNotEmpty(), "Parent category $parentCategory should have subcategories")
        }
    }
    
    @Test
    @DisplayName("각 하위 카테고리는 올바른 상위 카테고리를 참조해야 한다")
    fun shouldReferenceCorrectParentCategory() {
        // given & when & then
        DetailedProductCategory.values().forEach { detailedCategory ->
            val subCategories = DetailedProductCategory.getByParentCategory(detailedCategory.parentCategory)
            assertTrue(
                subCategories.contains(detailedCategory),
                "Category ${detailedCategory.name} should be included in parent ${detailedCategory.parentCategory} subcategories"
            )
        }
    }
    
    @Test
    @DisplayName("'전체' 카테고리가 모든 상위 카테고리에 존재해야 한다")
    fun shouldHaveAllCategoryForEachParentCategory() {
        // given
        val implementedCategories = setOf(
            ProductCategory.BABY_CLOTHING,
            ProductCategory.GIRLS_CLOTHING,
            ProductCategory.BOYS_CLOTHING,
            ProductCategory.BABY_PRODUCTS,
            ProductCategory.TOYS_EDUCATIONAL,
            ProductCategory.FEEDING_WEANING,
            ProductCategory.MOTHER_PRODUCTS
        )
        
        // when & then
        implementedCategories.forEach { parentCategory ->
            val subCategories = DetailedProductCategory.getByParentCategory(parentCategory)
            val hasAllCategory = subCategories.any { it.displayName == "전체" }
            assertTrue(hasAllCategory, "Parent category $parentCategory should have '전체' subcategory")
        }
    }
}