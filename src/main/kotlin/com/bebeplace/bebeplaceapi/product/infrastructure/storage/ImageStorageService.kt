package com.bebeplace.bebeplaceapi.product.infrastructure.storage

import org.springframework.web.multipart.MultipartFile
import java.util.*

interface ImageStorageService {
    fun uploadImages(images: List<MultipartFile>, productId: UUID): List<String>
    fun deleteImages(imageUrls: List<String>)
}