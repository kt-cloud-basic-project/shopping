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

# ShoppingFourU
`ShoppingFourU` 는 온라인 쇼핑몰 운영에 필요한 기능들을 완전하게 구현한 E-commerce 플랫폼입니다.
회원 관리부터 상품 등록, 장바구니, 주문/결제까지 실제 서비스와 동일한 흐름을 제공하며
사용자 경험과 관리자 운영 경험을 모두 담아낸 쇼핑 서비스입니다.


## 👥 팀원

| 이름 | GITHUB | 역할 |
| --- | --- | --- |
| 🦔 양정요 | https://github.com/jungyoyang | 팀원 |
| 🤭 이동석 | https://github.com/DaveLee-b | 팀원 |
| 🤓 전지민 | https://github.com/jeemin65-pixel | 팀원 |
| 🐨 정문영 | https://github.com/munyeong0103 | 팀장 |
| 🫠 정종한 |  https://github.com/jong15325/  |  팀원  |

## 🏛 Architecture

📜 ERD 설계도
<img width="1902" height="858" alt="image" src="https://github.com/user-attachments/assets/b8cd6585-15a3-4266-a9c0-eae0f79661f1" />



## 📌 Naming Rules

| 항목        | 규칙           |
| --------- | ------------ |
| Package   | `kebab-case` |
| Class     | `PascalCase` |
| Constant  | `UPPERCASE`  |
| Method    | `camelCase`  |
| Variables | `camelCase`  |



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



✔ 커밋 메시지 형식

[티켓 번호] 작업 유형: 작업 내용

✔ 예시

[TKT-15] feat: 로그인 시 잠금 검증 기능 추가

[TKT-17] chore: 라이브러리 추가

[TKT-23] feat: 인증/인가 기능 추가



## 🐬 Git Flow

<img width="1476" height="1038" alt="image" src="https://github.com/user-attachments/assets/4c56ca48-d36b-4132-a545-30960f410f0b" />

- **main** : 출시 가능한 프로덕션 코드의 브랜치

- **dev** : 다음 버전을 개발하는 브랜치

- **feat** : 이슈 단위로 기능을 개발하는 브랜치  
  - 브랜치 네이밍: `feat/#이슈번호/[topic]`

- **fix** : 이슈 단위로 버그를 수정하는 브랜치  
  - 브랜치 네이밍: `fix/#이슈번호/[topic]`

*브랜치명은 Jira 작업을 생성하면 자동으로 생성됩니다.




## 🧩 Issue Template

✨이슈 설명
user 관련 기능을 구현합니다.

🔥투두리스트
- [ ] user 회원가입
- [ ] user 로그인

🔖기타 사항


## 🔀 PR Template

📝요약(Summary)
이슈 번호 : #번호

🔨변경 사항(Changes)
user 회원가입 기능을 개발했습니다

😉리뷰 요구사항
메서드 구조가 잘 이루어졌는지

