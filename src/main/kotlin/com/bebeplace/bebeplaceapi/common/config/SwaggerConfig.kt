package com.bebeplace.bebeplaceapi.common.config

import io.swagger.v3.oas.models.OpenAPI
import io.swagger.v3.oas.models.info.Contact
import io.swagger.v3.oas.models.info.Info
import io.swagger.v3.oas.models.info.License
import io.swagger.v3.oas.models.security.SecurityRequirement
import io.swagger.v3.oas.models.security.SecurityScheme
import io.swagger.v3.oas.models.servers.Server
import org.springframework.context.annotation.Bean
import org.springframework.context.annotation.Configuration

@Configuration
class SwaggerConfig {

    @Bean
    fun openAPI(): OpenAPI {
        return OpenAPI()
            .info(
                Info()
                    .title("BebePlace API")
                    .description("BebePlace 상품 거래 플랫폼 API 문서")
                    .version("v1.0.0")
                    .contact(
                        Contact()
                            .name("BebePlace Team")
                            .email("contact@bebeplace.com")
                    )
                    .license(
                        License()
                            .name("MIT License")
                            .url("https://opensource.org/licenses/MIT")
                    )
            )
            .servers(
                listOf(
                    Server().url("http://localhost:8080").description("개발 서버"),
                    Server().url("https://api.bebeplace.com").description("운영 서버")
                )
            )
            .addSecurityItem(SecurityRequirement().addList("bearerAuth"))
            .components(
                io.swagger.v3.oas.models.Components()
                    .addSecuritySchemes(
                        "bearerAuth",
                        SecurityScheme()
                            .type(SecurityScheme.Type.HTTP)
                            .scheme("bearer")
                            .bearerFormat("JWT")
                            .description("JWT 토큰을 입력해주세요. 'Bearer ' 접두사는 생략하고 토큰만 입력하면 됩니다.")
                    )
            )
    }
}