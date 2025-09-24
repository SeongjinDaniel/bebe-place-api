# BebePlace REST API 문서

## 개요
BebePlace API는 Microsoft REST API 가이드라인을 따르는 중고거래 플랫폼 API입니다.

## 기본 정보
- **Base URL**: `http://localhost:8080/api/v1`
- **Content-Type**: `application/json`
- **Authentication**: JWT Bearer Token
- **API Version**: v1 (URI versioning)

## 설계 원칙
- **리소스 중심 설계**: 명사 기반 URL, 복수형 리소스명 사용
- **표준 HTTP 메서드**: GET, POST, PUT, DELETE의 의미적 사용
- **계층적 URL 구조**: `/collection/item/collection` 패턴
- **상태 코드 표준**: HTTP 상태 코드의 정확한 사용

## API 엔드포인트

### 🔐 Authentication API
```http
# JWT 토큰 발급 (로그인)
POST   /auth/tokens
# JWT 토큰 무효화 (로그아웃)
DELETE /auth/tokens
# JWT 토큰 갱신
PUT    /auth/tokens
```

### 👤 Users API
```http
# 사용자 생성 (회원가입)
POST   /users
# 사용자 목록 조회 (페이지네이션)
GET    /users?page=0&size=20&email=search
# 특정 사용자 조회
GET    /users/{id}
# 사용자 정보 전체 업데이트
PUT    /users/{id}
# 사용자 삭제
DELETE /users/{id}
```

### 📦 Products API
```http
# 상품 생성
POST   /products
# 상품 목록 조회 (페이지네이션, 검색)
GET    /products?page=0&size=20&category=electronics&search=keyword
# 특정 상품 조회
GET    /products/{id}
# 상품 정보 전체 업데이트
PUT    /products/{id}
# 상품 부분 업데이트
PATCH  /products/{id}
# 상품 삭제
DELETE /products/{id}

# 상품 이미지 관리
POST   /products/{id}/images
DELETE /products/{id}/images/{imageId}
```

### 📂 Categories API
```http
# 카테고리 목록 조회
GET    /categories
# 특정 카테고리 조회
GET    /categories/{id}
# 카테고리별 상품 조회
GET    /categories/{id}/products
```

### 💬 Chat API
```http
# 채팅방 생성
POST   /chat/rooms
# 채팅방 목록 조회
GET    /chat/rooms?page=0&size=20
# 특정 채팅방 조회
GET    /chat/rooms/{id}
# 채팅방 보관
DELETE /chat/rooms/{id}

# 메시지 관리
GET    /chat/rooms/{roomId}/messages?page=0&size=50
POST   /chat/rooms/{roomId}/messages
PUT    /chat/rooms/{roomId}/messages/{messageId}/read

# 가격 제안
POST   /chat/rooms/{roomId}/offers
PUT    /chat/rooms/{roomId}/offers/{offerId}/accept
PUT    /chat/rooms/{roomId}/offers/{offerId}/reject
```

### 💰 Transactions API
```http
# 거래 생성
POST   /transactions
# 거래 목록 조회
GET    /transactions?page=0&size=20&status=pending
# 특정 거래 조회
GET    /transactions/{id}
# 거래 상태 업데이트
PATCH  /transactions/{id}/status
```

### 💳 Payments API
```http
# 결제 처리
POST   /payments
# 결제 내역 조회
GET    /payments?page=0&size=20
# 특정 결제 조회
GET    /payments/{id}
# 환불 처리
POST   /payments/{id}/refund
```

## 요청/응답 예시

### 사용자 생성 (회원가입)
```http
POST /api/v1/users
Content-Type: application/json

{
  "email": "user@example.com",
  "password": "Password123!",
  "nickname": "사용자닉네임",
  "phoneNumber": "010-1234-5678"
}
```

**응답:**
```http
HTTP/1.1 201 Created
Content-Type: application/json

{
  "success": true,
  "data": {
    "id": "550e8400-e29b-41d4-a716-446655440000",
    "email": "user@example.com",
    "nickname": "사용자닉네임",
    "phoneNumber": "010-1234-5678",
    "trustScore": 0,
    "trustLevel": "BRONZE",
    "status": "ACTIVE",
    "createdAt": "2024-01-01T10:00:00",
    "updatedAt": "2024-01-01T10:00:00"
  },
  "message": "사용자가 성공적으로 생성되었습니다."
}
```

### JWT 토큰 발급 (로그인)
```http
POST /api/v1/auth/tokens
Content-Type: application/json

{
  "email": "user@example.com",
  "password": "Password123!"
}
```

**응답:**
```http
HTTP/1.1 201 Created
Content-Type: application/json

{
  "success": true,
  "data": {
    "accessToken": "eyJhbGciOiJIUzI1NiIsInR5cCI6IkpXVCJ9...",
    "tokenType": "Bearer",
    "expiresIn": 86400
  },
  "message": "인증 토큰이 발급되었습니다."
}
```

### JWT 토큰 갱신
```http
PUT /api/v1/auth/tokens
Authorization: Bearer {현재_토큰}
```

**응답:**
```http
HTTP/1.1 200 OK
Content-Type: application/json

{
  "success": true,
  "data": {
    "accessToken": "eyJhbGciOiJIUzI1NiIsInR5cCI6IkpXVCJ9...",
    "tokenType": "Bearer",
    "expiresIn": 86400
  },
  "message": "토큰이 갱신되었습니다."
}
```

### JWT 토큰 무효화 (로그아웃)
```http
DELETE /api/v1/auth/tokens
Authorization: Bearer {토큰}
```

**응답:**
```http
HTTP/1.1 204 No Content
```

### 페이지네이션된 사용자 목록 조회
```http
GET /api/v1/users?page=0&size=20&email=user
```

**응답:**
```http
HTTP/1.1 200 OK
Content-Type: application/json

{
  "success": true,
  "data": {
    "content": [
      {
        "id": "550e8400-e29b-41d4-a716-446655440000",
        "email": "user@example.com",
        "nickname": "사용자닉네임",
        "trustScore": 150,
        "trustLevel": "BRONZE"
      }
    ],
    "page": {
      "number": 0,
      "size": 20,
      "totalElements": 1,
      "totalPages": 1
    },
    "totalElements": 1,
    "totalPages": 1
  }
}
```

## WebSocket API

### 실시간 채팅
```
CONNECT /ws/chat
Authorization: Bearer {token}

SUBSCRIBE /topic/chat/rooms/{roomId}
SEND /app/chat/rooms/{roomId}/messages
```

## 에러 응답 구조

### 유효성 검증 오류
```json
{
  "success": false,
  "error": {
    "code": "VALIDATION_ERROR",
    "message": "입력값이 올바르지 않습니다."
  }
}
```

### 비즈니스 로직 오류
```json
{
  "success": false,
  "error": {
    "code": "DUPLICATE_EMAIL",
    "message": "이미 등록된 이메일입니다."
  }
}
```

## HTTP 상태 코드

| 코드 | 의미 | 사용 상황 |
|------|------|-----------|
| 200 | OK | 성공적인 GET, PUT, PATCH |
| 201 | Created | 성공적인 POST (리소스 생성) |
| 204 | No Content | 성공적인 DELETE |
| 400 | Bad Request | 잘못된 요청 데이터 |
| 401 | Unauthorized | 인증 실패 |
| 403 | Forbidden | 권한 없음 |
| 404 | Not Found | 리소스 없음 |
| 409 | Conflict | 리소스 충돌 (중복 등) |
| 422 | Unprocessable Entity | 유효성 검증 실패 |
| 500 | Internal Server Error | 서버 내부 오류 |

## 인증 헤더
모든 보호된 엔드포인트에는 다음 헤더가 필요합니다:
```
Authorization: Bearer {JWT_TOKEN}
```