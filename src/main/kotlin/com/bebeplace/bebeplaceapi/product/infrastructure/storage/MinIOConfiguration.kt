package com.bebeplace.bebeplaceapi.product.infrastructure.storage

import io.minio.MinioClient
import org.springframework.beans.factory.annotation.Value
import org.springframework.context.annotation.Bean
import org.springframework.context.annotation.Configuration

@Configuration
class MinIOConfiguration {
    
    @Value("\${minio.endpoint}")
    private lateinit var endpoint: String
    
    @Value("\${minio.access-key:minioadmin}")
    private lateinit var accessKey: String
    
    @Value("\${minio.secret-key:minioadmin}")
    private lateinit var secretKey: String
    
    @Bean
    fun minioClient(): MinioClient {
        return MinioClient.builder()
            .endpoint(endpoint)
            .credentials(accessKey, secretKey)
            .build()
    }
}