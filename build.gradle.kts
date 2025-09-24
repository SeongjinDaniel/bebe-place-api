plugins {
    kotlin("jvm") version "1.9.25"
    kotlin("plugin.spring") version "1.9.25"
    id("org.springframework.boot") version "3.5.4"
    id("io.spring.dependency-management") version "1.1.7"
    kotlin("plugin.jpa") version "1.9.25"
    id("org.jooq.jooq-codegen-gradle") version "3.19.15"
}

group = "com.bebeplace"
version = "0.0.1-SNAPSHOT"
description = "bebe-place-api"

java {
    toolchain {
        languageVersion = JavaLanguageVersion.of(21)
    }
}

configurations {
    compileOnly {
        extendsFrom(configurations.annotationProcessor.get())
    }
}

repositories {
    mavenCentral()
}

dependencies {
    implementation("org.springframework.boot:spring-boot-starter-data-jpa")
    implementation("org.springframework.boot:spring-boot-starter-security")
    implementation("org.springframework.boot:spring-boot-starter-web")
    implementation("org.springframework.boot:spring-boot-starter-validation")
    implementation("org.springframework.boot:spring-boot-starter-actuator")
    implementation("com.fasterxml.jackson.module:jackson-module-kotlin")
    implementation("org.jetbrains.kotlin:kotlin-reflect")
    
    // JWT
    implementation("io.jsonwebtoken:jjwt-api:0.12.3")
    implementation("io.jsonwebtoken:jjwt-impl:0.12.3")
    implementation("io.jsonwebtoken:jjwt-jackson:0.12.3")
    
    // UUID v7
    implementation("com.github.f4b6a3:uuid-creator:5.3.7")
    
    // SMS
    implementation("net.nurigo:sdk:4.3.0")
    
    // Cache
    implementation("org.springframework.boot:spring-boot-starter-cache")
    implementation("com.github.ben-manes.caffeine:caffeine")
    
    // Retry
    implementation("org.springframework.retry:spring-retry")
    implementation("org.springframework:spring-aspects")
    
    // MinIO Client
    implementation("io.minio:minio:8.5.7")
    
    // Swagger/OpenAPI
    implementation("org.springdoc:springdoc-openapi-starter-webmvc-ui:2.2.0")
    
    // JOOQ
    implementation("org.springframework.boot:spring-boot-starter-jooq")
    implementation("org.jooq:jooq:3.19.15")
    implementation("org.jooq:jooq-meta:3.19.15")
    implementation("org.jooq:jooq-codegen:3.19.15")
    jooqCodegen("org.postgresql:postgresql")
    jooqCodegen("com.h2database:h2")
    
    // Database
    runtimeOnly("org.postgresql:postgresql")
    implementation("org.flywaydb:flyway-core")
    implementation("org.flywaydb:flyway-database-postgresql")
    
    compileOnly("org.projectlombok:lombok")
    annotationProcessor("org.projectlombok:lombok")
    testImplementation("org.springframework.boot:spring-boot-starter-test")
    testImplementation("org.jetbrains.kotlin:kotlin-test-junit5")
    testImplementation("org.springframework.security:spring-security-test")
    testImplementation("io.mockk:mockk:1.13.8")
    testImplementation("com.h2database:h2")
    runtimeOnly("com.h2database:h2")
    testRuntimeOnly("org.junit.platform:junit-platform-launcher")
}

kotlin {
    compilerOptions {
        freeCompilerArgs.addAll("-Xjsr305=strict")
    }
}

allOpen {
    annotation("jakarta.persistence.Entity")
    annotation("jakarta.persistence.MappedSuperclass")
    annotation("jakarta.persistence.Embeddable")
}

tasks.withType<Test> {
    useJUnitPlatform()
}

// JOOQ Code Generation Configuration
jooq {
    configuration {
        generator {
            database {
                name = "org.jooq.meta.h2.H2Database"
                inputSchema = "PUBLIC"
                includes = ".*"
                excludes = ""
                properties {
                    property {
                        key = "dialect"
                        value = "H2"
                    }
                }
            }
            
            target {
                packageName = "com.bebeplace.bebeplaceapi.jooq"
                directory = "src/main/generated"
                encoding = "UTF-8"
            }
            
            generate {
                isDeprecated = false
                isRecords = true
                isImmutablePojos = true
                isFluentSetters = true
                isJavaTimeTypes = true
                isDaos = false
                isValidationAnnotations = true
                isSpringAnnotations = true
            }
        }
        
        jdbc {
            driver = "org.h2.Driver"
            url = "jdbc:h2:mem:bebeplace"
            user = "sa"
            password = ""
        }
    }
}

// JOOQ 코드 생성을 컴파일 전에 실행
tasks.named("compileKotlin") {
    dependsOn("jooqCodegen")
}

// Generated 소스 디렉토리 추가
sourceSets {
    main {
        java {
            srcDirs("src/main/generated")
        }
    }
}
