package com.bebeplace.bebeplaceapi.product.infrastructure.persistence

import com.bebeplace.bebeplaceapi.common.infrastructure.BaseEntity
import com.bebeplace.bebeplaceapi.product.domain.model.ProductSortType
import com.bebeplace.bebeplaceapi.product.domain.repository.ProductListFilter
import org.jooq.Condition
import org.jooq.DSLContext
import org.jooq.OrderField
import org.jooq.impl.DSL
import org.slf4j.LoggerFactory
import org.springframework.stereotype.Repository
import java.math.BigDecimal
import java.time.LocalDateTime
import java.util.*
import kotlin.system.measureTimeMillis

@Repository
class ProductCustomRepositoryImpl(
    private val dsl: DSLContext
) : ProductCustomRepository {
    
    companion object {
        private val logger = LoggerFactory.getLogger(ProductCustomRepositoryImpl::class.java)
        
        // JOOQ 테이블과 컬럼 정의 (실제 스키마 기반)
        private val products = DSL.table("products")
        private val id = DSL.field("id", UUID::class.java)
        private val sellerId = DSL.field("seller_id", UUID::class.java)
        private val title = DSL.field("title", String::class.java)
        private val category = DSL.field("category", String::class.java)
        private val price = DSL.field("price", BigDecimal::class.java)
        private val shippingIncluded = DSL.field("shipping_included", Boolean::class.java)
        private val shippingCost = DSL.field("shipping_cost", BigDecimal::class.java)
        private val description = DSL.field("description", String::class.java)
        private val productType = DSL.field("product_type", String::class.java)
        private val condition = DSL.field("condition", String::class.java)
        private val status = DSL.field("status", String::class.java)
        private val viewCount = DSL.field("view_count", Int::class.java)
        private val likeCount = DSL.field("like_count", Int::class.java)
        private val commentCount = DSL.field("comment_count", Int::class.java)
        private val createdAt = DSL.field("created_at", LocalDateTime::class.java)
        private val updatedAt = DSL.field("updated_at", LocalDateTime::class.java)
    }
    
    /**
     * 정렬 타입에 따른 ORDER BY 절 생성
     */
    private fun buildOrderBy(sortType: ProductSortType): List<OrderField<*>> {
        return when (sortType) {
            ProductSortType.LATEST -> 
                listOf(createdAt.desc(), id.desc())
            
            ProductSortType.MOST_LIKED -> 
                listOf(likeCount.desc(), createdAt.desc(), id.desc())
            
            ProductSortType.MOST_VIEWED -> 
                listOf(viewCount.desc(), createdAt.desc(), id.desc())
            
            ProductSortType.PRICE_LOW -> 
                listOf(price.asc(), createdAt.desc(), id.desc())
            
            ProductSortType.PRICE_HIGH -> 
                listOf(price.desc(), createdAt.desc(), id.desc())
        }
    }
    
    override fun findWithDynamicFilter(
        filter: ProductListFilter?,
        cursorParams: CursorParams
    ): List<ProductEntity> {
        logger.debug("🔍 JOOQ 동적 쿼리 시작 - filter: {}, pageSize: {}", filter, cursorParams.pageSize)
        
        // 동적 조건 생성
        val conditions = mutableListOf<Condition>()
        
        // 필터 조건
        filter?.let { f ->
            f.status?.let { statusValue ->
                conditions.add(status.eq(statusValue.name))
            }
            
            f.sellerId?.let { sellerIdValue ->
                conditions.add(sellerId.eq(sellerIdValue))
            }
            
            f.category?.let { categoryValue ->
                conditions.add(category.eq(categoryValue.name))
            }
        }
        
        // 커서 페이지네이션 조건
        val cursorCreatedAtValue = cursorParams.createdAt
        val cursorIdValue = cursorParams.id
        
        if (cursorCreatedAtValue != null && cursorIdValue != null) {
            conditions.add(
                createdAt.lt(cursorCreatedAtValue)
                    .or(createdAt.eq(cursorCreatedAtValue).and(id.lt(cursorIdValue)))
            )
        }
        
        logger.debug("🎯 JOOQ 쿼리 조건 생성 완료 - {} 개 조건", conditions.size)
        
        // 정렬 순서 결정
        val sortType = filter?.sortType ?: ProductSortType.LATEST
        val orderFields = buildOrderBy(sortType)
        
        logger.debug("📊 JOOQ 정렬 적용 - sortType: {}, orderFields: {}", sortType, orderFields.size)
        
        val entities: List<ProductEntity>
        val executionTime = measureTimeMillis {
            // JOOQ 쿼리 실행
            entities = dsl.selectFrom(products)  // 🎯 모든 컬럼 자동 선택
                .where(conditions)
                .orderBy(orderFields)
                .limit(cursorParams.pageSize)
                .fetch { record ->  // 🚀 인라인 매핑
                    ProductEntity(
                        id = record[id],
                        sellerId = record[sellerId],
                        title = record[title],
                        category = com.bebeplace.bebeplaceapi.product.domain.model.ProductCategory.valueOf(
                            record[category]
                        ),
                        price = record[price],
                        shippingIncluded = record[shippingIncluded],
                        shippingCost = record[shippingCost],
                        description = record[description],
                        productType = com.bebeplace.bebeplaceapi.product.domain.model.ProductType.valueOf(
                            record[productType]
                        ),
                        condition = com.bebeplace.bebeplaceapi.product.domain.model.ProductCondition.valueOf(
                            record[condition]
                        ),
                        status = com.bebeplace.bebeplaceapi.product.domain.model.ProductStatus.valueOf(
                            record[status]
                        ),
                        viewCount = record[viewCount],
                        likeCount = record[likeCount],
                        commentCount = record[commentCount]
                    ).apply {
                        setAuditFields(
                            createdAt = record["created_at"],
                            updatedAt = record["updated_at"]
                        )
                    }
                }
        }
        
        logger.info("⚡ JOOQ 쿼리 완료 - {}ms, {} 건 조회", executionTime, entities.size)
        
        return entities
    }
    
    // 🚀 추가 JOOQ 활용 메서드들 - 집계 쿼리 최적화
    
    override fun countByStatus(status: com.bebeplace.bebeplaceapi.product.domain.model.ProductStatus): Long {
        logger.debug("📊 JOOQ 상태별 카운트 - status: {}", status)
        
        val count = dsl.selectCount()
            .from(products)
            .where(Companion.status.eq(status.name))  // Use Companion object reference to avoid conflict
            .fetchOne(0, Long::class.java) ?: 0L
            
        logger.debug("📊 상태별 카운트 결과 - {}: {} 건", status, count)
        return count
    }
    
    override fun findTopSellersByProductCount(limit: Int): List<TopSellerStats> {
        logger.debug("🏆 JOOQ 인기 판매자 통계 - limit: {}", limit)
        
        val productCountField = DSL.count().`as`("product_count")
        val totalViewCountField = DSL.sum(viewCount).`as`("total_view_count")
        
        val stats = dsl.select(
            sellerId,
            productCountField,
            totalViewCountField
        )
            .from(products)
            .where(Companion.status.eq("ACTIVE"))
            .groupBy(sellerId)
            .orderBy(productCountField.desc())
            .limit(limit)
            .fetch { record ->
                TopSellerStats(
                    sellerId = record[sellerId],
                    productCount = (record[productCountField] as? Number)?.toLong() ?: 0L,
                    totalViewCount = (record[totalViewCountField] as? Number)?.toLong() ?: 0L
                )
            }
            
        logger.info("🏆 인기 판매자 통계 완료 - {} 명 조회", stats.size)
        return stats
    }
    
    override fun findProductStatsByCategory(): List<CategoryStats> {
        logger.debug("📈 JOOQ 카테고리별 통계 조회")
        
        val productCountField = DSL.count().`as`("product_count")
        val averagePriceField = DSL.avg(price).`as`("average_price")  
        val totalViewCountField = DSL.sum(viewCount).`as`("total_view_count")
        
        val stats = dsl.select(
            category,
            productCountField,
            averagePriceField,
            totalViewCountField
        )
            .from(products)
            .where(Companion.status.eq("ACTIVE"))
            .groupBy(category)
            .orderBy(productCountField.desc())
            .fetch { record ->
                CategoryStats(
                    category = com.bebeplace.bebeplaceapi.product.domain.model.ProductCategory.valueOf(
                        record[category]
                    ),
                    productCount = (record[productCountField] as? Number)?.toLong() ?: 0L,
                    averagePrice = (record[averagePriceField] as? Number)?.toDouble() ?: 0.0,
                    totalViewCount = (record[totalViewCountField] as? Number)?.toLong() ?: 0L
                )
            }
            
        logger.info("📈 카테고리별 통계 완료 - {} 개 카테고리", stats.size)
        return stats
    }
}

// ProductEntity에 audit 필드를 설정하기 위한 확장 함수
private fun ProductEntity.setAuditFields(createdAt: Any?, updatedAt: Any?) {
    try {
        val baseEntityClass = BaseEntity::class.java
        
        (createdAt as? LocalDateTime)?.let {
            val createdAtField = baseEntityClass.getDeclaredField("createdAt")
            createdAtField.isAccessible = true
            createdAtField[this] = it
        }
        
        (updatedAt as? LocalDateTime)?.let {
            val updatedAtField = baseEntityClass.getDeclaredField("updatedAt")
            updatedAtField.isAccessible = true
            updatedAtField[this] = it
        }
    } catch (e: Exception) {
        // Audit 필드 설정 실패 시 로그만 남기고 계속 진행
        println("Warning: Failed to set audit fields: ${e.message}")
    }
}