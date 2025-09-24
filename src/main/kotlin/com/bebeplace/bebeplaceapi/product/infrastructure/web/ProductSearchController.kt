package com.bebeplace.bebeplaceapi.product.infrastructure.web

import com.bebeplace.bebeplaceapi.common.web.ApiResponse
import com.bebeplace.bebeplaceapi.common.web.PagedResponse
import com.bebeplace.bebeplaceapi.product.application.dto.ProductResponse
import com.bebeplace.bebeplaceapi.product.application.dto.ProductSearchRequest
import com.bebeplace.bebeplaceapi.product.application.dto.SellerProductsRequest
import com.bebeplace.bebeplaceapi.product.domain.model.AgeGroup
import org.slf4j.LoggerFactory
import org.springframework.http.ResponseEntity
import org.springframework.web.bind.annotation.*

@RestController
@RequestMapping("/api/v1/products")
class ProductSearchController {
    
    private val logger = LoggerFactory.getLogger(this::class.java)
    
    @GetMapping("/search")
    fun searchProducts(searchRequest: ProductSearchRequest): ResponseEntity<ApiResponse<PagedResponse<ProductResponse>>> {
        
        logger.debug("Searching products with filters - keyword: ${searchRequest.keyword}, category: ${searchRequest.category}")
        
        // TODO: 실제 검색 로직 구현
        // val pageable = searchRequest.toPageable()
        // val result = productSearchService.searchProducts(searchRequest, pageable)
        
        // 임시 빈 결과 반환
        val emptyResult = PagedResponse.of<ProductResponse>(
            content = emptyList(),
            pageNumber = searchRequest.page,
            pageSize = searchRequest.size,
            totalElements = 0L
        )
        
        return ResponseEntity.ok(
            ApiResponse.success(
                data = emptyResult,
                message = "상품 검색 완료"
            )
        )
    }
    
    @GetMapping("/seller/{sellerId}")
    fun getProductsBySeller(
        @PathVariable sellerId: String,
        sellerRequest: SellerProductsRequest
    ): ResponseEntity<ApiResponse<PagedResponse<ProductResponse>>> {
        
        logger.debug("Getting products by seller: $sellerId")
        
        // sellerId를 DTO에 설정
        val request = sellerRequest.copy(sellerId = sellerId)
        
        // TODO: 판매자별 상품 조회 로직 구현
        // val pageable = request.toPageable()
        // val result = productService.getProductsBySeller(request, pageable)
        
        val emptyResult = PagedResponse.of<ProductResponse>(
            content = emptyList(),
            pageNumber = request.page,
            pageSize = request.size,
            totalElements = 0L
        )
        
        return ResponseEntity.ok(
            ApiResponse.success(
                data = emptyResult,
                message = "판매자 상품 조회 완료"
            )
        )
    }
}