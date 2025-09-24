package com.bebeplace.bebeplaceapi.product.infrastructure.web

import com.bebeplace.bebeplaceapi.common.web.ApiResponse
import com.bebeplace.bebeplaceapi.product.application.dto.CreateProductRequest
import com.bebeplace.bebeplaceapi.product.application.dto.ProductCreationResult
import com.bebeplace.bebeplaceapi.product.application.dto.ProductCreationStatusResponse
import com.bebeplace.bebeplaceapi.product.application.dto.ProductResponse
import com.bebeplace.bebeplaceapi.product.application.dto.ProductListRequest
import com.bebeplace.bebeplaceapi.product.application.dto.ProductListResponse
import com.bebeplace.bebeplaceapi.product.infrastructure.web.dto.ProductListSearchRequest
import com.bebeplace.bebeplaceapi.product.application.usecase.CreateProductUseCase
import com.bebeplace.bebeplaceapi.product.application.usecase.GetProductCreationStatusUseCase
import com.bebeplace.bebeplaceapi.product.application.usecase.GetProductUseCase
import com.bebeplace.bebeplaceapi.product.application.usecase.GetProductListUseCase
import com.bebeplace.bebeplaceapi.product.domain.model.AgeGroup
import com.bebeplace.bebeplaceapi.product.domain.model.ProductCreationStatus
import io.swagger.v3.oas.annotations.Operation
import io.swagger.v3.oas.annotations.Parameter
import io.swagger.v3.oas.annotations.media.Content
import io.swagger.v3.oas.annotations.media.Schema
import io.swagger.v3.oas.annotations.responses.ApiResponse as SwaggerApiResponse
import io.swagger.v3.oas.annotations.responses.ApiResponses
import io.swagger.v3.oas.annotations.tags.Tag
import jakarta.validation.Valid
import org.slf4j.LoggerFactory
import org.springframework.http.HttpStatus
import org.springframework.http.ResponseEntity
import org.springframework.web.bind.annotation.*
import org.springframework.web.multipart.MultipartFile
import java.util.*

@Tag(name = "Product", description = "상품 관리 API")
@RestController
@RequestMapping("/api/v1/products")
class ProductController(
    private val createProductUseCase: CreateProductUseCase,
    private val getProductUseCase: GetProductUseCase,
    private val getProductCreationStatusUseCase: GetProductCreationStatusUseCase,
    private val getProductListUseCase: GetProductListUseCase
) {
    
    private val logger = LoggerFactory.getLogger(this::class.java)
    
    @Operation(
        summary = "상품 목록 조회",
        description = "커서 기반 페이징을 사용하여 상품 목록을 조회합니다. 필터링 옵션을 제공합니다."
    )
    @ApiResponses(
        value = [
            SwaggerApiResponse(
                responseCode = "200",
                description = "상품 목록 조회 성공",
                content = [Content(schema = Schema(implementation = ProductListResponse::class))]
            ),
            SwaggerApiResponse(
                responseCode = "400",
                description = "잘못된 요청 파라미터"
            )
        ]
    )
    @GetMapping
    fun getProductList(
        @Valid @ModelAttribute searchRequest: ProductListSearchRequest
    ): ResponseEntity<ApiResponse<ProductListResponse>> {
        
        val request = ProductListRequest(
            cursor = searchRequest.cursor,
            size = searchRequest.size,
            status = searchRequest.status,
            sellerId = searchRequest.sellerId,
            category = searchRequest.category,
            sortType = searchRequest.sortType
        )
        
        logger.debug("Getting product list with cursor: {}, size: {}", request.cursor, request.size)
        
        val result = getProductListUseCase.execute(request)
        
        return ResponseEntity.ok(
            ApiResponse.success(
                data = result,
                message = "상품 목록 조회 성공"
            )
        )
    }
    
    @Operation(
        summary = "상품 등록",
        description = "새로운 상품을 등록합니다. 이미지 파일을 함께 업로드할 수 있습니다."
    )
    @ApiResponses(
        value = [
            SwaggerApiResponse(
                responseCode = "201",
                description = "상품 등록 성공",
                content = [Content(schema = Schema(implementation = ProductCreationResult::class))]
            ),
            SwaggerApiResponse(
                responseCode = "400",
                description = "잘못된 요청 데이터"
            ),
            SwaggerApiResponse(
                responseCode = "401",
                description = "인증 실패"
            )
        ]
    )
    @PostMapping(consumes = ["multipart/form-data"])
    fun createProduct(
        @Parameter(description = "상품 정보", required = true)
        @Valid @RequestPart("product") request: CreateProductRequest,
        
        @Parameter(description = "상품 이미지 파일들 (최대 10개)")
        @RequestPart("images", required = false) images: List<MultipartFile> = emptyList()
    ): ResponseEntity<ApiResponse<ProductCreationResult>> {
        
        logger.info("Creating product: ${request.title}")
        
        val result = createProductUseCase.execute(request, images)
        
        val responseStatus = HttpStatus.CREATED
        val responseMessage = if (result.status == ProductCreationStatus.COMPLETED.name) {
            "상품이 성공적으로 등록되었습니다."
        } else {
            "상품 등록이 완료되었습니다. 이미지 업로드가 진행 중입니다."
        }
        
        return ResponseEntity.status(responseStatus)
            .body(
                ApiResponse.success(
                    data = result,
                    message = responseMessage
                )
            )
    }
    
    @Operation(
        summary = "상품 상세 조회",
        description = "상품 ID로 특정 상품의 상세 정보를 조회합니다. 조회 시 조회수가 증가합니다."
    )
    @ApiResponses(
        value = [
            SwaggerApiResponse(
                responseCode = "200",
                description = "상품 조회 성공",
                content = [Content(schema = Schema(implementation = ProductResponse::class))]
            ),
            SwaggerApiResponse(
                responseCode = "404",
                description = "상품을 찾을 수 없음"
            )
        ]
    )
    @GetMapping("/{productId}")
    fun getProduct(
        @Parameter(description = "조회할 상품의 ID", required = true)
        @PathVariable productId: UUID
    ): ResponseEntity<ApiResponse<ProductResponse>> {
        
        logger.debug("Getting product: {}", productId)
        
        val product = getProductUseCase.execute(productId)
        
        return ResponseEntity.ok(
            ApiResponse.success(
                data = product,
                message = "상품 조회 성공"
            )
        )
    }
    
    @Operation(
        summary = "상품 등록 상태 조회",
        description = "상품 등록 진행 상태를 조회합니다. 이미지 업로드 등의 비동기 작업 상태를 확인할 수 있습니다."
    )
    @ApiResponses(
        value = [
            SwaggerApiResponse(
                responseCode = "200",
                description = "등록 상태 조회 성공",
                content = [Content(schema = Schema(implementation = ProductCreationStatusResponse::class))]
            ),
            SwaggerApiResponse(
                responseCode = "404",
                description = "상품 또는 등록 추적 정보를 찾을 수 없음"
            )
        ]
    )
    @GetMapping("/{productId}/creation-status")
    fun getProductCreationStatus(
        @Parameter(description = "상품 ID", required = true)
        @PathVariable productId: UUID,
        
        @Parameter(description = "상관 관계 ID (선택사항)")
        @RequestParam(required = false) correlationId: UUID?
    ): ResponseEntity<ApiResponse<ProductCreationStatusResponse>> {
        
        logger.debug("Getting product creation status: {}", productId)
        
        val status = if (correlationId != null) {
            getProductCreationStatusUseCase.execute(correlationId)
        } else {
            getProductCreationStatusUseCase.executeByProductId(productId)
        }
        
        return ResponseEntity.ok(
            ApiResponse.success(
                data = status,
                message = "상품 등록 상태 조회 성공"
            )
        )
    }
    
    @Operation(
        summary = "연령대 목록 조회",
        description = "상품 등록 시 사용할 수 있는 연령대 옵션들을 조회합니다."
    )
    @ApiResponses(
        value = [
            SwaggerApiResponse(
                responseCode = "200",
                description = "연령대 목록 조회 성공",
                content = [Content(schema = Schema(implementation = AgeGroupResponse::class))]
            )
        ]
    )
    @GetMapping("/age-groups")
    fun getAgeGroups(): ResponseEntity<ApiResponse<List<AgeGroupResponse>>> {
        
        val ageGroups = AgeGroup.entries.map { ageGroup ->
            AgeGroupResponse(
                code = ageGroup.name,
                displayName = ageGroup.displayName,
                minMonths = ageGroup.minMonths,
                maxMonths = ageGroup.maxMonths
            )
        }
        
        return ResponseEntity.ok(
            ApiResponse.success(
                data = ageGroups,
                message = "연령대 목록 조회 성공"
            )
        )
    }
}

@Schema(description = "연령대 응답")
data class AgeGroupResponse(
    @get:Schema(description = "연령대 코드", example = "NEWBORN")
    val code: String,
    
    @get:Schema(description = "연령대 표시명", example = "신생아")
    val displayName: String,
    
    @get:Schema(description = "최소 개월 수", example = "0")
    val minMonths: Int,
    
    @get:Schema(description = "최대 개월 수 (null이면 상한 없음)", example = "3")
    val maxMonths: Int?
)