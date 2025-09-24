package com.bebeplace.bebeplaceapi.product.domain.event

import com.bebeplace.bebeplaceapi.common.domain.DomainEvent
import org.springframework.web.multipart.MultipartFile
import java.util.*

// 단순한 이벤트들만 유지
data class ProductImageUploadRequested(
    val productId: UUID,
    val images: List<MultipartFile>
) : DomainEvent()

data class ProductImagesUploaded(
    val productId: UUID,
    val imageUrls: List<String>
) : DomainEvent()

data class ProductImageUploadFailed(
    val productId: UUID,
    val reason: String
) : DomainEvent()