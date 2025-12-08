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
<img width="1902" height="858" alt="image" src="https://github.com/user-attachments/assets/b8cd6585-15a3-4266-a9c0-eae0f79661f1" />
<br>

## 📌 Naming Rules

| 항목        | 규칙           |
| --------- | ------------ |
| Package   | `kebab-case` |
| Class     | `PascalCase` |
| Constant  | `UPPERCASE`  |
| Method    | `camelCase`  |
| Variables | `camelCase`  |
<br>


## 📁 Commit Convention

| Header       | 기능                   |
| ------------ | -------------------- |
| **Update**   | 변경 사항 업데이트           |
| **feat**     | 새로운 기능 추가            |
| **fix**      | 버그 수정                |
| **docs**     | 문서 수정                |
| **style**    | 코드 포맷팅               |
| **refactor** | 코드 리팩토링              |
| **test**     | 테스트 코드               |
| **chore**    | 빌드 업무 수정, 패키지 매니저 수정 |
| **comment**  | 주석 추가 및 변경           |
| **remove**   | 파일, 폴더 삭제            |
| **rename**   | 파일, 폴더명 수정           |
<br>


✔ 커밋 메시지 형식<br>

[티켓 번호] 작업 유형: 작업 내용<br><br>

✔ 예시<br>

[TKT-15] feat: 로그인 시 잠금 검증 기능 추가<br>

[TKT-17] chore: 라이브러리 추가<br>

[TKT-23] feat: 인증/인가 기능 추가<br>
<br>


## 🐬 Git Flow

<img width="1476" height="1038" alt="image" src="https://github.com/user-attachments/assets/4c56ca48-d36b-4132-a545-30960f410f0b" />

- **main** : 출시 가능한 프로덕션 코드의 브랜치

- **dev** : 다음 버전을 개발하는 브랜치

- **feat** : 이슈 단위로 기능을 개발하는 브랜치  
  - 브랜치 네이밍: `feat/#이슈번호/[topic]`

- **fix** : 이슈 단위로 버그를 수정하는 브랜치  
  - 브랜치 네이밍: `fix/#이슈번호/[topic]`

*브랜치명은 Jira 작업을 생성하면 자동으로 생성됩니다.
<br>
<br>


## 🧩 Issue Template

✨이슈 설명
<br><br>


🔥투두리스트
<br><br>


🔖기타 사항
<br><br>
<br>

## 🔀 PR Template

📝요약(Summary)<br>
이슈 번호 : #
<br><br>


🔨변경 사항(Changes)
<br><br>


😉리뷰 요구사항
<br><br>
<br>

## 📡 API Documentation

전체 API 명세 및 상세 스펙은 아래 링크에서 확인할 수 있습니다.

[📄 Full API Documentation (Notion)](https://www.notion.so/API-2ae9e3e335cc8097988ffe2a0e982fec?source=copy_link)
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

---

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
