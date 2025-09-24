package com.bebeplace.bebeplaceapi.product.application.service

import com.bebeplace.bebeplaceapi.product.domain.model.AgeGroup
import com.bebeplace.bebeplaceapi.product.infrastructure.persistence.ProductAgeGroupEntity
import com.bebeplace.bebeplaceapi.product.infrastructure.persistence.ProductAgeGroupJpaRepository
import org.slf4j.LoggerFactory
import org.springframework.stereotype.Service
import java.util.*

interface ProductAgeGroupManagementService {
    fun createAgeGroups(productId: UUID, ageGroups: List<AgeGroup>)
    fun deleteAgeGroupsByProductId(productId: UUID)
    fun findAgeGroupsByProductId(productId: UUID): List<AgeGroup>
}

@Service
class ProductAgeGroupManagementServiceImpl(
    private val productAgeGroupRepository: ProductAgeGroupJpaRepository
) : ProductAgeGroupManagementService {
    
    private val logger = LoggerFactory.getLogger(this::class.java)
    
    override fun createAgeGroups(productId: UUID, ageGroups: List<AgeGroup>) {
        logger.info("Creating ${ageGroups.size} age groups for product: ${productId}")
        
        val entities = ageGroups.map { ageGroup ->
            ProductAgeGroupEntity.create(productId, ageGroup)
        }
        
        productAgeGroupRepository.saveAll(entities)
        logger.debug("Successfully created age groups for product: ${productId}")
    }
    
    override fun deleteAgeGroupsByProductId(productId: UUID) {
        logger.info("Deleting age groups for product: ${productId}")
        
        productAgeGroupRepository.deleteByProductId(productId)
        logger.debug("Successfully deleted age groups for product: ${productId}")
    }
    
    override fun findAgeGroupsByProductId(productId: UUID): List<AgeGroup> {
        logger.debug("Finding age groups for product: ${productId}")
        
        val entities = productAgeGroupRepository.findByProductId(productId)
        val ageGroups = entities.map { it.ageGroup }
        
        logger.debug("Found ${ageGroups.size} age groups for product: ${productId}")
        return ageGroups
    }
}