package com.bebeplace.bebeplaceapi.common.config

import com.bebeplace.bebeplaceapi.common.util.RequestContextUtil
import org.springframework.context.annotation.Bean
import org.springframework.context.annotation.Configuration
import org.springframework.data.domain.AuditorAware
import org.springframework.data.jpa.repository.config.EnableJpaAuditing
import java.util.*

@Configuration
@EnableJpaAuditing(auditorAwareRef = "auditorProvider")
class JpaAuditingConfig(
    private val requestContextUtil: RequestContextUtil
) {
    
    @Bean
    fun auditorProvider(): AuditorAware<UUID> {
        return AuditorAware {
            try {
                Optional.of(requestContextUtil.getCurrentUserId())
            } catch (e: IllegalStateException) {
                Optional.empty()
            }
        }
    }
}