package com.bebeplace.bebeplaceapi.product.infrastructure.persistence

import com.bebeplace.bebeplaceapi.product.domain.model.Product
import com.bebeplace.bebeplaceapi.product.domain.model.ProductCursor
import com.bebeplace.bebeplaceapi.product.domain.model.ProductId
import com.bebeplace.bebeplaceapi.product.domain.repository.ProductRepository
import com.bebeplace.bebeplaceapi.product.domain.repository.ProductListFilter
import com.bebeplace.bebeplaceapi.product.domain.repository.ProductListResult
import org.springframework.data.domain.Pageable
import org.springframework.stereotype.Repository
import java.util.*
import java.time.LocalDateTime

@Repository
class ProductRepositoryImpl(
    private val jpaRepository: ProductJpaRepository,
    private val ageGroupRepository: ProductAgeGroupJpaRepository
) : ProductRepository {
    
    override fun save(product: Product): Product {
        val entity = ProductEntity.fromDomain(product)
        val savedEntity = jpaRepository.save(entity)
        
        // 기존 AgeGroup 관계 삭제
        ageGroupRepository.deleteByProductId(product.getId().getValue())
        
        // 새로운 AgeGroup 관계 저장
        val ageGroupEntities = product.getAgeGroups().map { ageGroup ->
            ProductAgeGroupEntity.create(product.getId().getValue(), ageGroup)
        }
        ageGroupRepository.saveAll(ageGroupEntities)
        
        return loadProductWithAgeGroups(savedEntity)
    }
    
    override fun findById(id: ProductId): Product? {
        val entity = jpaRepository.findById(id.getValue()).orElse(null) ?: return null
        return loadProductWithAgeGroups(entity)
    }
    
    private fun loadProductWithAgeGroups(entity: ProductEntity): Product {
        val product = entity.toDomain()
        val ageGroupEntities = ageGroupRepository.findByProductId(entity.id)
        
        ageGroupEntities.forEach { ageGroupEntity ->
            product.addAgeGroup(ageGroupEntity.ageGroup)
        }
        
        return product
    }
    
    override fun findByIdAndSellerId(id: ProductId, sellerId: UUID): Product? {
        val entity = jpaRepository.findById(id.getValue()).orElse(null) ?: return null
        if (entity.sellerId != sellerId) return null
        return loadProductWithAgeGroups(entity)
    }
    
    override fun deleteById(id: ProductId) {
        ageGroupRepository.deleteByProductId(id.getValue())
        jpaRepository.deleteById(id.getValue())
    }
    
    override fun existsById(id: ProductId): Boolean {
        return jpaRepository.existsById(id.getValue())
    }
    
    override fun findAllBySellerId(sellerId: UUID): List<Product> {
        return jpaRepository.findBySellerId(sellerId).map { loadProductWithAgeGroups(it) }
    }
    
    override fun findAllByStatus(status: com.bebeplace.bebeplaceapi.product.domain.model.ProductStatus): List<Product> {
        // JPA repository에 status로 조회하는 메서드 추가 필요
        return jpaRepository.findAll().filter { it.status == status }.map { loadProductWithAgeGroups(it) }
    }
    
    override fun increaseViewCount(id: ProductId): Int {
        return jpaRepository.increaseViewCount(id.getValue())
    }
    
    override fun findProductsWithCursor(
        cursor: String?,
        size: Int,
        filter: ProductListFilter?
    ): ProductListResult {
        val productCursor = cursor?.let { ProductCursor.decode(it) }
        val cursorParams = CursorParams.create(
            productCursor?.createdAt,
            productCursor?.id,
            size
        )
        
        val entities = jpaRepository.findWithDynamicFilter(filter, cursorParams)
        
        val hasNext = entities.size > size
        val products = entities.take(size).map { entity: ProductEntity -> loadProductWithAgeGroups(entity) }
        val nextCursor = if (hasNext) {
            val lastEntity = entities[size - 1]
            ProductCursor.create(lastEntity.createdAt, lastEntity.id).encode()
        } else null
        
        return ProductListResult(products, hasNext, nextCursor)
    }
}