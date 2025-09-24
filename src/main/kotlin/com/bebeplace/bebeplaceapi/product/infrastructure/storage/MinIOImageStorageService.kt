package com.bebeplace.bebeplaceapi.product.infrastructure.storage

import io.minio.MinioClient
import io.minio.PutObjectArgs
import io.minio.RemoveObjectArgs
import io.minio.BucketExistsArgs
import io.minio.MakeBucketArgs
import org.slf4j.LoggerFactory
import org.springframework.beans.factory.annotation.Value
import org.springframework.stereotype.Service
import org.springframework.web.multipart.MultipartFile
import java.time.LocalDateTime
import java.time.format.DateTimeFormatter
import java.util.*

@Service
class MinIOImageStorageService(
    private val minioClient: MinioClient,
    @Value("\${minio.bucket.product-images:product-images}") private val bucketName: String,
    @Value("\${minio.endpoint:http://localhost:9000}") private val endpoint: String
) : ImageStorageService {
    
    private val logger = LoggerFactory.getLogger(this::class.java)
    
    init {
        ensureBucketExists()
    }
    
    override fun uploadImages(images: List<MultipartFile>, productId: UUID): List<String> {
        logger.info("Uploading ${images.size} images for product: ${productId}")
        
        val uploadedUrls = mutableListOf<String>()
        
        images.forEachIndexed { index, image ->
            try {
                val imageUrl = uploadSingleImage(image, productId, index)
                uploadedUrls.add(imageUrl)
                logger.debug("Uploaded image ${index} for product $productId: $imageUrl")
                
            } catch (exception: Exception) {
                logger.error("Failed to upload image ${index} for product $productId", exception)
                
                // 실패한 경우 이미 업로드된 이미지들 정리
                if (uploadedUrls.isNotEmpty()) {
                    try {
                        deleteImages(uploadedUrls)
                    } catch (cleanupException: Exception) {
                        logger.error("Failed to cleanup uploaded images", cleanupException)
                    }
                }
                
                throw IllegalStateException("이미지 업로드에 실패했습니다: ${exception.message}", exception)
            }
        }
        
        logger.info("Successfully uploaded ${uploadedUrls.size} images for product: ${productId}")
        return uploadedUrls
    }
    
    override fun deleteImages(imageUrls: List<String>) {
        logger.info("Deleting ${imageUrls.size} images")
        
        imageUrls.forEach { url ->
            try {
                deleteSingleImage(url)
                logger.debug("Deleted image: ${url}")
            } catch (exception: Exception) {
                logger.error("Failed to delete image: $url", exception)
            }
        }
    }
    
    private fun uploadSingleImage(image: MultipartFile, productId: UUID, index: Int): String {
        // 파일명 생성: products/{productId}/image_{index}_{timestamp}.{extension}
        val timestamp = LocalDateTime.now().format(DateTimeFormatter.ofPattern("yyyyMMdd_HHmmss"))
        val extension = image.originalFilename?.substringAfterLast('.', "jpg") ?: "jpg"
        val objectName = "products/${productId}/image_${index}_${timestamp}.${extension}"
        
        // MinIO에 파일 업로드
        minioClient.putObject(
            PutObjectArgs.builder()
                .bucket(bucketName)
                .`object`(objectName)
                .stream(image.inputStream, image.size, -1)
                .contentType(image.contentType ?: "image/jpeg")
                .build()
        )
        
        logger.info("Successfully uploaded to MinIO - Bucket: $bucketName, Object: $objectName")
        
        // 실제 MinIO URL 반환
        return "$endpoint/$bucketName/$objectName"
    }
    
    private fun deleteSingleImage(imageUrl: String) {
        try {
            val objectName = extractObjectNameFromUrl(imageUrl)
            minioClient.removeObject(
                RemoveObjectArgs.builder()
                    .bucket(bucketName)
                    .`object`(objectName)
                    .build()
            )
            logger.info("Successfully deleted from MinIO: ${objectName}")
        } catch (exception: Exception) {
            logger.error("Failed to delete from MinIO: ${imageUrl}", exception)
            throw exception
        }
    }
    
    private fun extractObjectNameFromUrl(imageUrl: String): String {
        // URL에서 object name 추출: http://localhost:9000/bucket-name/object-name
        val urlParts = imageUrl.substringAfter("/$bucketName/")
        return urlParts
    }
    
    private fun ensureBucketExists() {
        try {
            val bucketExists = minioClient.bucketExists(
                BucketExistsArgs.builder()
                    .bucket(bucketName)
                    .build()
            )
            
            if (!bucketExists) {
                minioClient.makeBucket(
                    MakeBucketArgs.builder()
                        .bucket(bucketName)
                        .build()
                )
                logger.info("Created MinIO bucket: $bucketName")
            } else {
                logger.info("MinIO bucket already exists: $bucketName")
            }
        } catch (exception: Exception) {
            logger.error("Failed to ensure bucket exists: $bucketName", exception)
            throw IllegalStateException("MinIO bucket 설정에 실패했습니다: ${exception.message}", exception)
        }
    }
}