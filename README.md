# 베베플레이스 API 서버

베이비 용품 중고거래 플랫폼의 백엔드 API 서버입니다.

## 🚀 서버 실행 방법

### 1. 개발 환경 설정

#### 필요한 도구
- **Java 21** 이상
- **Gradle 8.x** 이상
- **MinIO Server** (이미지 저장소)

#### MinIO 서버 실행
```bash
# Docker로 MinIO 실행 (권장)
docker run -p 9000:9000 -p 9001:9001 \
  -e "MINIO_ROOT_USER=minioadmin" \
  -e "MINIO_ROOT_PASSWORD=minioadmin" \
  minio/minio server /data --console-address ":9001"
```

### 2. 로컬 개발 환경 실행

```bash
# H2 인메모리 데이터베이스 사용 (MinIO 연결 필요)
./gradlew bootRun --args='--spring.profiles.active=loc'
```

**또는 환경변수로 설정:**
```bash
export SPRING_PROFILES_ACTIVE=loc
./gradlew bootRun
```

### 3. 프로덕션 환경 실행

```bash
# 프로덕션 프로필로 실행 (PostgreSQL 및 외부 MinIO 필요)
./gradlew bootRun --args='--spring.profiles.active=prod'
```

## 🗄️ 데이터베이스 설정

### 로컬 개발 환경 (loc 프로필)
- **데이터베이스**: H2 인메모리
- **접속 URL**: `jdbc:h2:mem:bebeplace`
- **H2 콘솔**: http://localhost:8080/h2-console
- **계정**: sa / (빈 패스워드)

### 프로덕션 환경
- **데이터베이스**: PostgreSQL
- **마이그레이션**: Flyway 자동 실행

## 🔧 환경 설정

### application-loc.yml (로컬 개발)
```yaml
spring:
  datasource:
    url: jdbc:h2:mem:bebeplace
    driver-class-name: org.h2.Driver
    username: sa
    password: ""
  
  jpa:
    hibernate:
      ddl-auto: create-drop
    show-sql: true
    database-platform: org.hibernate.dialect.H2Dialect
  
  h2:
    console:
      enabled: true
      path: /h2-console
  
  flyway:
    enabled: false
```

## 📊 주요 기능

### 동시성 안전한 조회수 증가
- 제품 조회 시 원자적 연산으로 조회수 증가
- 테스트 코드로 동시성 검증 완료

### UUID v7 사용
- 시간 순서가 보장되는 UUID v7 사용 (`UuidCreator.getTimeOrderedEpoch()`)

## 🧪 테스트 실행

### 전체 테스트
```bash
./gradlew test
```

### 동시성 테스트
```bash
./gradlew test --tests="*ConcurrencyTest"
```

### 테스트 커버리지 (JaCoCo)
```bash
./gradlew jacocoTestReport
```

## 🐛 문제 해결

### MinIO 연결 오류
MinIO 서버가 실행되지 않으면 애플리케이션 시작에 실패합니다.

**해결 방법:**
1. MinIO Docker 컨테이너 실행 확인
2. 포트 9000이 사용 가능한지 확인
3. MinIO 설정 확인 (endpoint, credentials)

### H2 데이터베이스 연결 오류
H2 의존성이 누락되면 로컬 환경에서 실행이 실패합니다.

**해결 방법:**
```kotlin
// build.gradle.kts에 추가 확인
runtimeOnly("com.h2database:h2")
```

### JPA Entity 매핑 오류
String 필드에 @Enumerated 어노테이션이 잘못 사용된 경우

**해결 방법:**
String 타입 필드에서 @Enumerated 제거

## 📝 API 엔드포인트

### 헬스체크
- `GET /actuator/health` - 서버 상태 확인
- `GET /actuator/info` - 애플리케이션 정보

### 데이터베이스 콘솔 (로컬 환경)
- `GET /h2-console` - H2 데이터베이스 콘솔 접속

## 🏗️ 아키텍처

```
├── application/        # 애플리케이션 서비스 레이어
├── domain/            # 도메인 레이어 (엔티티, 레포지토리 인터페이스)
├── infrastructure/    # 인프라스트럭처 레이어 (구현체)
└── web/              # 웹 레이어 (컨트롤러)
```

## 📚 기술 스택

- **언어**: Kotlin
- **프레임워크**: Spring Boot 3.5.4
- **데이터베이스**: PostgreSQL (prod), H2 (dev)
- **ORM**: JPA/Hibernate + JOOQ (하이브리드)
- **쿼리 라이브러리**: JOOQ 3.19.15 (동적 쿼리, 성능 최적화)
- **이미지 저장소**: MinIO
- **테스트**: JUnit 5, MockK
- **빌드 도구**: Gradle
- **Java**: 21

## 👥 개발 가이드

자세한 테스트 가이드는 [docs/TESTING_GUIDE.md](docs/TESTING_GUIDE.md)를 참고하세요.

---

**문제 발생 시 확인사항:**
1. Java 21 설치 확인
2. MinIO 서버 실행 상태 확인
3. 포트 충돌 확인 (8080, 9000, 9001)
4. 환경변수 설정 확인# bebe-place-api
