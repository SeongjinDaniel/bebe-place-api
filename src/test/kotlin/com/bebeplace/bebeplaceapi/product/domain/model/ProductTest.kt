package com.bebeplace.bebeplaceapi.product.domain.model

import com.bebeplace.bebeplaceapi.common.types.Money
import com.bebeplace.bebeplaceapi.product.domain.event.ProductCreated
import org.junit.jupiter.api.DisplayName
import org.junit.jupiter.api.Test
import org.junit.jupiter.api.assertThrows
import java.math.BigDecimal
import java.util.*
import kotlin.test.assertEquals
import kotlin.test.assertFalse
import kotlin.test.assertTrue

@DisplayName("Product 도메인 모델 테스트")
class ProductTest {
    
    private val productId = ProductId.generate()
    private val sellerId = UUID.randomUUID()
    private val price = Money(BigDecimal("10000"))
    private val shippingInfo = ShippingInfo(
        isIncluded = false,
        shippingCost = Money(BigDecimal("3000"))
    )
    
    @Test
    @DisplayName("상품을 생성할 수 있어야 한다")
    fun shouldCreateProduct() {
        // when
        val product = Product.create(
            id = productId,
            sellerId = sellerId,
            title = "테스트 상품",
            category = ProductCategory.BABY_CLOTHING,
            price = price,
            shippingInfo = shippingInfo,
            description = "테스트 상품 설명입니다.",
            productType = ProductType.USED,
            condition = ProductCondition.EXCELLENT
        )
        
        // then
        assertEquals(productId, product.getId())
        assertEquals(sellerId, product.getSellerId())
        assertEquals("테스트 상품", product.getTitle())
        assertEquals(ProductCategory.BABY_CLOTHING, product.getCategory())
        assertEquals(price, product.getPrice())
        assertEquals(shippingInfo, product.getShippingInfo())
        assertEquals("테스트 상품 설명입니다.", product.getDescription())
        assertEquals(ProductType.USED, product.getProductType())
        assertEquals(ProductCondition.EXCELLENT, product.getCondition())
        assertEquals(ProductStatus.ACTIVE, product.getStatus())
        assertEquals(0, product.getViewCount())
    }
    
    @Test
    @DisplayName("상품 생성 시 도메인 이벤트가 발행되어야 한다")
    fun shouldPublishDomainEventWhenCreated() {
        // when
        val product = Product.create(
            id = productId,
            sellerId = sellerId,
            title = "테스트 상품",
            category = ProductCategory.BABY_CLOTHING,
            price = price,
            shippingInfo = shippingInfo,
            description = "테스트 상품 설명입니다.",
            productType = ProductType.USED,
            condition = ProductCondition.EXCELLENT
        )
        
        // then
        val events = product.getDomainEvents()
        assertEquals(1, events.size)
        
        val event = events.first() as ProductCreated
        assertEquals(productId.getValue(), event.productId)
        assertEquals(sellerId, event.sellerId)
        assertEquals("테스트 상품", event.title)
        assertEquals(ProductCategory.BABY_CLOTHING, event.category)
        assertEquals(price, event.price)
    }
    
    @Test
    @DisplayName("상품 정보를 업데이트할 수 있어야 한다")
    fun shouldUpdateProductDetails() {
        // given
        val product = createTestProduct()
        val newPrice = Money(BigDecimal("15000"))
        val newShippingInfo = ShippingInfo(
            isIncluded = true,
            shippingCost = null
        )
        
        // when
        product.updateDetails(
            title = "업데이트된 상품",
            category = ProductCategory.TOYS_EDUCATIONAL,
            price = newPrice,
            shippingInfo = newShippingInfo,
            description = "업데이트된 설명",
            condition = ProductCondition.GOOD
        )
        
        // then
        assertEquals("업데이트된 상품", product.getTitle())
        assertEquals(ProductCategory.TOYS_EDUCATIONAL, product.getCategory())
        assertEquals(newPrice, product.getPrice())
        assertEquals(newShippingInfo, product.getShippingInfo())
        assertEquals("업데이트된 설명", product.getDescription())
        assertEquals(ProductCondition.GOOD, product.getCondition())
    }
    
    @Test
    @DisplayName("상품 상태를 변경할 수 있어야 한다")
    fun shouldChangeProductStatus() {
        // given
        val product = createTestProduct()
        
        // when & then
        product.changeStatus(ProductStatus.SOLD)
        assertEquals(ProductStatus.SOLD, product.getStatus())
        
        product.changeStatus(ProductStatus.ACTIVE)
        assertEquals(ProductStatus.ACTIVE, product.getStatus())
    }
    
    @Test
    @DisplayName("삭제된 상태에서는 다른 상태로 전환할 수 없어야 한다")
    fun shouldThrowExceptionForInvalidStatusTransition() {
        // given
        val product = createTestProduct()
        product.changeStatus(ProductStatus.DELETED)
        
        // when & then
        assertThrows<IllegalArgumentException> {
            product.changeStatus(ProductStatus.ACTIVE)
        }
    }
    
    @Test
    @DisplayName("조회수를 증가시킬 수 있어야 한다")
    fun shouldIncreaseViewCount() {
        // given
        val product = createTestProduct()
        
        // when
        product.increaseViewCount()
        product.increaseViewCount()
        
        // then
        assertEquals(2, product.getViewCount())
    }
    
    @Test
    @DisplayName("상품을 판매완료로 표시할 수 있어야 한다")
    fun shouldMarkAsSold() {
        // given
        val product = createTestProduct()
        
        // when
        product.markAsSold()
        
        // then
        assertTrue(product.isSold())
        assertEquals(ProductStatus.SOLD, product.getStatus())
    }
    
    @Test
    @DisplayName("상품을 비활성화할 수 있어야 한다")
    fun shouldMarkAsInactive() {
        // given
        val product = createTestProduct()
        
        // when
        product.markAsInactive()
        
        // then
        assertFalse(product.isActive())
        assertEquals(ProductStatus.INACTIVE, product.getStatus())
    }
    
    @Test
    @DisplayName("비활성화된 상품을 다시 활성화할 수 있어야 한다")
    fun shouldReactivateInactiveProduct() {
        // given
        val product = createTestProduct()
        product.markAsInactive()
        
        // when
        product.reactivate()
        
        // then
        assertTrue(product.isActive())
        assertEquals(ProductStatus.ACTIVE, product.getStatus())
    }
    
    @Test
    @DisplayName("판매완료된 상품을 다시 활성화할 수 있어야 한다")
    fun shouldReactivateSoldProduct() {
        // given
        val product = createTestProduct()
        product.markAsSold()
        
        // when
        product.reactivate()
        
        // then
        assertTrue(product.isActive())
        assertEquals(ProductStatus.ACTIVE, product.getStatus())
    }
    
    @Test
    @DisplayName("활성 상품은 재활성화할 수 없어야 한다")
    fun shouldNotReactivateActiveProduct() {
        // given
        val product = createTestProduct()
        
        // when & then
        assertThrows<IllegalArgumentException> {
            product.reactivate()
        }
    }
    
    @Test
    @DisplayName("상품 소유자를 확인할 수 있어야 한다")
    fun shouldCheckProductOwnership() {
        // given
        val product = createTestProduct()
        val otherUserId = UUID.randomUUID()
        
        // when & then
        assertTrue(product.isOwnedBy(sellerId))
        assertFalse(product.isOwnedBy(otherUserId))
    }
    
    @Test
    @DisplayName("총 가격을 계산할 수 있어야 한다")
    fun shouldCalculateTotalPrice() {
        // given
        val product = createTestProduct()
        val expectedTotal = Money(BigDecimal("13000")) // 10000 + 3000
        
        // when
        val totalPrice = product.getTotalPrice()
        
        // then
        assertEquals(expectedTotal, totalPrice)
    }
    
    @Test
    @DisplayName("연령대를 추가할 수 있어야 한다")
    fun shouldAddAgeGroup() {
        // given
        val product = createTestProduct()
        
        // when
        product.addAgeGroup(AgeGroup.NEWBORN_0_3)
        product.addAgeGroup(AgeGroup.INFANT_4_7)
        
        // then
        assertEquals(2, product.getAgeGroups().size)
        assertTrue(product.hasAgeGroup(AgeGroup.NEWBORN_0_3))
        assertTrue(product.hasAgeGroup(AgeGroup.INFANT_4_7))
    }
    
    @Test
    @DisplayName("연령대를 제거할 수 있어야 한다")
    fun shouldRemoveAgeGroup() {
        // given
        val product = createTestProduct()
        product.addAgeGroup(AgeGroup.NEWBORN_0_3)
        product.addAgeGroup(AgeGroup.INFANT_4_7)
        
        // when
        product.removeAgeGroup(AgeGroup.NEWBORN_0_3)
        
        // then
        assertEquals(1, product.getAgeGroups().size)
        assertFalse(product.hasAgeGroup(AgeGroup.NEWBORN_0_3))
        assertTrue(product.hasAgeGroup(AgeGroup.INFANT_4_7))
    }
    
    @Test
    @DisplayName("동일한 연령대를 중복 추가해도 하나만 유지되어야 한다")
    fun shouldNotDuplicateAgeGroup() {
        // given
        val product = createTestProduct()
        
        // when
        product.addAgeGroup(AgeGroup.NEWBORN_0_3)
        product.addAgeGroup(AgeGroup.NEWBORN_0_3)
        
        // then
        assertEquals(1, product.getAgeGroups().size)
        assertTrue(product.hasAgeGroup(AgeGroup.NEWBORN_0_3))
    }
    
    private fun createTestProduct(): Product {
        return Product.create(
            id = productId,
            sellerId = sellerId,
            title = "테스트 상품",
            category = ProductCategory.BABY_CLOTHING,
            price = price,
            shippingInfo = shippingInfo,
            description = "테스트 상품 설명입니다.",
            productType = ProductType.USED,
            condition = ProductCondition.EXCELLENT
        )
    }
}