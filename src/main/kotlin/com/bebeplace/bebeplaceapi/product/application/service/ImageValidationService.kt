package com.bebeplace.bebeplaceapi.product.application.service

import org.slf4j.LoggerFactory
import org.springframework.stereotype.Service
import org.springframework.web.multipart.MultipartFile

@Service
class ImageValidationService {
    
    private val logger = LoggerFactory.getLogger(this::class.java)
    
    companion object {
        private const val MAX_IMAGES = 10
        private const val MAX_FILE_SIZE = 10 * 1024 * 1024L // 10MB
        private val ALLOWED_EXTENSIONS = setOf("jpg", "jpeg", "png", "webp")
        private val ALLOWED_CONTENT_TYPES = setOf("image/jpeg", "image/png", "image/webp")
    }
    
    fun validateImages(images: List<MultipartFile>) {
        logger.debug("Validating ${images.size} images")
        
        validateImageCount(images)
        images.forEach { file ->
            validateImageFile(file)
        }
        
        logger.debug("All images validated successfully")
    }
    
    private fun validateImageCount(images: List<MultipartFile>) {
        require(images.size <= MAX_IMAGES) { "이미지는 최대 ${MAX_IMAGES}개까지 업로드할 수 있습니다" }
    }
    
    private fun validateImageFile(file: MultipartFile) {
        validateFileSize(file)
        validateFileExtension(file)
        validateContentType(file)
    }
    
    private fun validateFileSize(file: MultipartFile) {
        require(file.size <= MAX_FILE_SIZE) { "이미지 파일 크기는 10MB 이하여야 합니다: ${file.originalFilename}" }
    }
    
    private fun validateFileExtension(file: MultipartFile) {
        val filename = file.originalFilename ?: ""
        val extension = filename.substringAfterLast('.', "").lowercase()

        require(extension in ALLOWED_EXTENSIONS) { "지원되지 않는 파일 형식입니다: ${extension}" }
    }
    
    private fun validateContentType(file: MultipartFile) {
        val contentType = file.contentType

        require(contentType in ALLOWED_CONTENT_TYPES) { "지원되지 않는 파일 형식입니다: ${contentType}" }
    }
}