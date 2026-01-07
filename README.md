![Java](https://img.shields.io/badge/Java_21-007396?style=flat&logo=java&logoColor=white)
![Spring Boot](https://img.shields.io/badge/Spring_Boot_3.x-6DB33F?style=flat&logo=springboot&logoColor=white)
![JPA](https://img.shields.io/badge/JPA-59666C?style=flat)
![MySQL](https://img.shields.io/badge/MySQL-4479A1?style=flat&logo=mysql&logoColor=white)
![Swagger](https://img.shields.io/badge/Swagger-85EA2D?style=flat&logo=swagger&logoColor=black)
![Docker](https://img.shields.io/badge/Docker-2496ED?style=flat&logo=docker&logoColor=white)
![IntelliJ](https://img.shields.io/badge/IntelliJ_IDEA-000000?style=flat&logo=intellijidea&logoColor=white)
![GitHub](https://img.shields.io/badge/GitHub-181717?style=flat&logo=github&logoColor=white)
![Gradle](https://img.shields.io/badge/Gradle-02303A?style=flat&logo=gradle&logoColor=white)
![Discord](https://img.shields.io/badge/Discord-5865F2?style=flat&logo=discord&logoColor=white)
![Notion](https://img.shields.io/badge/Notion-000000?style=flat&logo=notion&logoColor=white)
![Jira](https://img.shields.io/badge/Jira-0052CC?style=flat&logo=jira&logoColor=white)
![GitHub Issues](https://img.shields.io/badge/GitHub_Issues-181717?style=flat&logo=github&logoColor=white)
![Postman](https://img.shields.io/badge/Postman-FF6C37?style=flat&logo=postman&logoColor=white)
![Swagger UI](https://img.shields.io/badge/Swagger_UI-85EA2D?style=flat&logo=swagger&logoColor=black)
<br><br>

## 📑 Table of Contents

- [ShoppingFourU](#shoppingfouru)
- [👥 Team Member](#-team-member)
- [🏛 Architecture](#-architecture)
- [📌 Convention](#-convention)
- [📡 API Documentation](#-api-documentation)
- [🔥 Troubleshooting](#-troubleshooting)
  - [불필요한 쿼리 생성 문제]
  - [JWT 필터 예외 처리 문제]
- [🚀 기본 프로젝트 이후 보완점](#-기본-프로젝트-이후-보완점)
  - [Git · Slack 알림 & Bot 에러 연동]
  - [API 접근 제한 (Rate Limiting)]
  - [Domain Refactoring]
    - [할인 · 멤버십 정책 모듈화]
    - [주문 시 배송지 검증 강화]
  - [주문–결제 프로세스 리팩토링 & 동시성 제어]
  - [낙관적 락 (Optimistic Lock) 도입]
  - [재고 예약 시스템 도입]
  - [찜 (Wishlist) 도메인 추가]
- [🚀 새로운 기술 도입 사항 및 선정 이유](#-새로운-기술-도입-사항-및-선정-이유)
  - [상품 할인 시 이메일 알림 시스템 도입]
  - [OpenAI 기반 FAQ 챗봇 도입]
  - [ELK + 모니터링 시스템 도입]


# ShoppingFourU
`ShoppingFourU` 는 온라인 쇼핑몰 운영에 필요한 기능들을 완전하게 구현한 E-commerce 플랫폼입니다.<br>
회원 관리부터 상품 등록, 장바구니, 주문/결제까지 실제 서비스와 동일한 흐름을 제공하며<br>
사용자 경험과 관리자 운영 경험을 모두 담아낸 쇼핑 서비스입니다.<br>
<br>

## 👥 Team Member

| 이름 | GITHUB | 역할 |
| --- | --- | --- |
| 🦔 양정요 | https://github.com/jungyoyang | 팀원 |
| 🤭 이동석 | https://github.com/DaveLee-b | 팀원 |
| 🤓 전지민 | https://github.com/jeemin65-pixel | 팀원 |
| 🐨 정문영 | https://github.com/munyeong0103 | 팀장 |
| 🫠 정종한 |  https://github.com/jong15325/  |  팀원  |
<br>

## 🏛 Architecture

📜 ERD 설계도
<img width="2398" height="1454" alt="basic-project-shopping_last_last_copy" src="https://github.com/user-attachments/assets/ad08aeba-4c13-4351-b23e-630429d8f662" />
<br>

## 📌 Convention
자세한 컨벤션사항은 노션을 통해 확인 해주세요!
https://vivid-thyme-ac6.notion.site/Commit-Convention-2e19e3e335cc80d7bf16c7377e4ddeac?source=copy_link


## 📡 API Documentation

전체 API 명세 및 상세 스펙은 아래 링크에서 확인할 수 있습니다.

[📄 Full API Documentation (Notion)](https://www.notion.so/API-2ae9e3e335cc8097988ffe2a0e982fec?source=copy_link)
<br>
[📄 Swagger Documentation](http://shoppingfouru.ap-northeast-2.elasticbeanstalk.com/swagger-ui/index.html)
<br><br><br>


## 🔥 Troubleshooting

### 불필요한 쿼리 생성 문제

문제 상황<br>
- 할인 정보 조회 시 멤버십 정보도 필요함
- Lazy Loading으로 인해 2번의 쿼리 발생 (할인 조회 → 멤버십 조회)
- 목록 조회에서는 N+1 문제로 이어질 수 있는 구조
<br>

해결 방법<br>
- @EntityGraph 로 Fetch Join 적용
- 1번의 쿼리로 통합하여 성능 개선
- 쿼리 횟수 50% 감소 (2회 → 1회)
<br>


### JWT 필터 예외 처리 문제

문제 상황<br>
- 토큰이 필요 없는 요청에서도 토큰 검증 예외 발생
- Authorization 헤더가 없는 경우에도 예외를 던져 Controller 로직까지 전달되지 못함

기존 문제 코드
```java
private String resolveToken(HttpServletRequest request) {
    String header = request.getHeader(AUTH_HEADER);

    if (!StringUtils.hasText(header) || !header.startsWith(BEARER_PREFIX)) {
        throw new CustomException(ErrorCode.INVALID_JWT_TOKEN);
    }
    return header.substring(BEARER_PREFIX.length());
}


```

해결방법<br>
```java
if (!StringUtils.hasText(header) || !header.startsWith(BEARER_PREFIX)) {
    return null;
}
return header.substring(BEARER_PREFIX.length());
```

<br>
- Authorization 헤더가 없거나 Bearer 로 시작하지 않으면
예외를 던지지 않고 null 반환<br>
- 이렇게 하면 Security에서 인증없이 통과시키고
Controller 로직까지 정상적으로 전달됨
<br><br>

## 🚀 기본 프로젝트 이후 보완점

기본 프로젝트 구현 이후,  
실제 운영 환경에서 발생할 수 있는 문제들을 가정하고 **안정성과 확장성**을 중심으로 구조를 보완했습니다.  
단순 기능 구현을 넘어서 **운영 관점의 품질 개선**에 초점을 맞췄습니다.

### 1. Git · Slack 알림 & Bot 에러 연동


### 2. API 접근 제한 (Rate Limiting)
#### 설계 목표

**1차 방어**
- Global Filter 기반 IP 단위 제한
- 초당 100회 / 최대 10,000 IP 기준

**2차 방어**
- AOP 기반 Controller 단위 접근 제어
- API 특성에 따라 IP / 사용자별 제한 정책 분리 적용

#### Bucket4j 선택 이유
- 메서드별로 서로 다른 제한 정책 적용 가능
- Redis 사용이 제한적인 환경 고려
- Caffeine 기반 캐시로 **경량 · 고성능 · 동시성 안정성 확보**


### 3. Domain Refactoring

#### 1️⃣ 할인 · 멤버십 정책 모듈화

#### 2️⃣ 주문 시 배송지 검증 강화


### 4. 주문–결제 프로세스 리팩토링 & 동시성 제어
#### 개선된 프로세스

**주문 생성 API**
- 상품 구매 가능 여부 확인
- 주문 정보 저장  
  👉 상태: `PENDING_PAYMENT`

**결제 생성 API**
- 외부 결제 모듈 호출
- **(Transaction)**
  - 결제 정보 저장
  - 상품 재고 차감
  - 주문 상태 변경 → `PAID`

> 💡  
> 외부 결제 API 호출은 트랜잭션에 포함하지 않고,  
> DB 변경 로직만 원자적으로 묶어 **락 점유 및 성능 저하를 방지**


### 5. 낙관적 락 (Optimistic Lock) 도입

- 상품 엔티티에 `@Version` 필드 추가
- 동시 재고 차감 시 커밋 시점에 충돌 감지
- 충돌 발생 시 예외 처리 후 재시도 전략 적용


### 6. 재고 예약 시스템 도입

#### 도입 이유
- 동시 주문 환경에서 초과 판매 방지
- 결제 완료 전 구매 권한 선점 필요

#### 프로세스

**주문 생성 시**
- 재고 차감
- 재고 예약 정보 저장
- 주문 상태: `PENDING_PAYMENT`

**결제 성공 시**
- 재고 예약 해제
- 주문 상태 변경: `PAID`

**결제 실패 / 예약 만료 시**
- 예약 정보 삭제
- 재고 복구

### 7. 찜 (Wishlist) 도메인 추가

<br>

##  🚀 새로운 기술 도입 사항 및 선정 이유

### 1. 상품 할인 시 이메일 알림 시스템 도입


#### 이메일 선정 이유
- 대부분의 사용자가 이메일을 보유하고 있어 접근성이 높음
- 회원가입 시 이미 이메일 정보를 수집하므로  
  추가 정보 입력이나 외부 서비스 참여를 요구하지 않아도 됨
- Slack, Discord 등은 별도의 프로그램 사용 및 공간 참여가 필요하여  
  사용자 알림 목적과 맞지 않아 제외
- 카카오톡 채널의 경우
  - 사업자 등록 필수
  - 유료 메시지 발송
  - 카카오 계정 관련 정보 추가 관리 필요  
  → 운영 비용 및 복잡성 증가로 인해 제외

### 2. OpenAI 기반 FAQ 챗봇 도입

#### 데이터 구성
- 도메인별 자주 묻는 질문과 답변을 **JSON 파일로 정리**
- 해당 데이터를 기반으로 **벡터 스토어(Vector Store) 연동**

#### 기능 범위
- FAQ JSON 데이터 기반 질의응답 제공
- 서비스 담당 도메인 외 질문에 대해서는  
  응답을 제한하여 **비의도적 답변 방지**

### 3. ELK + 모니터링 시스템 도입

#### 역할 분리 및 활용

**ELK Stack**
- 에러 로그 수집
- 요청 흐름 및 장애 원인 추적

**Prometheus + Grafana**
- EC2 및 Docker 컨테이너 리소스 모니터링
- CPU / 메모리 / 서비스 부하 상태 실시간 시각화

#### 도입 효과
- 로그와 메트릭을 분리하여 분석 가능
- 서비스 장애 발생 시 원인 파악 속도 향상
- 운영 관점에서 시스템 상태를 한눈에 파악 가능


