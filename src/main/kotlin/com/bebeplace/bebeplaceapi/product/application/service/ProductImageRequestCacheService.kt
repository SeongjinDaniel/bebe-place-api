package com.bebeplace.bebeplaceapi.product.application.service

import com.bebeplace.bebeplaceapi.product.domain.event.ProductImageUploadRequested
import org.slf4j.LoggerFactory
import org.springframework.stereotype.Service
import java.time.LocalDateTime
import java.util.*
import java.util.concurrent.ConcurrentHashMap

@Service
class ProductImageRequestCacheService {
    
    private val logger = LoggerFactory.getLogger(this::class.java)
    
    // 업로드 요청을 임시 저장 (productId -> 요청 정보)
    private val requestCache = ConcurrentHashMap<UUID, CachedRequest>()
    
    data class CachedRequest(
        val request: ProductImageUploadRequested,
        val cachedAt: LocalDateTime
    )
    
    companion object {
        // 캐시 보존 시간: 1시간
        private const val CACHE_RETENTION_HOURS = 1L
    }
    
    fun cacheRequest(request: ProductImageUploadRequested) {
        val cachedRequest = CachedRequest(
            request = request,
            cachedAt = LocalDateTime.now()
        )
        
        requestCache[request.productId] = cachedRequest
        logger.debug("Cached upload request for product: ${request.productId}")
    }
    
    fun getCachedRequest(productId: UUID): ProductImageUploadRequested? {
        val cached = requestCache[productId]
        
        if (cached == null) {
            logger.debug("No cached request found for product: $productId")
            return null
        }
        
        // 캐시 만료 확인
        if (cached.cachedAt.isBefore(LocalDateTime.now().minusHours(CACHE_RETENTION_HOURS))) {
            logger.warn("Cached request expired for product: $productId, removing from cache")
            requestCache.remove(productId)
            return null
        }
        
        return cached.request
    }
    
    fun removeCachedRequest(productId: UUID) {
        requestCache.remove(productId)
        logger.debug("Removed cached request for product: $productId")
    }
    
    fun getCacheSize(): Int = requestCache.size
    
    // 주기적으로 만료된 캐시 정리
    fun cleanupExpiredCache() {
        val now = LocalDateTime.now()
        val expiredKeys = requestCache.entries
            .filter { it.value.cachedAt.isBefore(now.minusHours(CACHE_RETENTION_HOURS)) }
            .map { it.key }
        
        expiredKeys.forEach { productId ->
            requestCache.remove(productId)
            logger.debug("Cleaned up expired cache for product: $productId")
        }
        
        if (expiredKeys.isNotEmpty()) {
            logger.info("Cleaned up ${expiredKeys.size} expired cache entries")
        }
    }
}