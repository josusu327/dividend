# 📌 Dividend 프로젝트
## 👾 프로젝트 설명
✅ 미국 주식 배당금 정보를 제공하는 API 서비스이다.

## ⚙ 개발 환경
- **운영체제** : macOS
- **통합개발환경(IDE)** : IntelliJ
- **JDK 버전** : JDK 17
- **데이터베이스** : H2
- **빌드 툴** : Gradle 8.12.1
- **관리 툴** : GitHub

## 🔌 Dependencies
- **Spring Boot 3.4.2**
  - Spring Web
  - Spring JPA
  - Spring Redis
  - Spring Security
  - Spring Cache
 
 
- **데이터베이스**
  - H2 Database (`com.h2database:h2:2.2.220`)

  
- **웹 크롤링**
  - Jsoup (`org.jsoup:jsoup:1.15.3`)

  
- **보안 및 인증**
  - JWT (`io.jsonwebtoken:jjwt:0.9.1`)

  
- **유틸리티**
  - Apache Commons Collections (`org.apache.commons:commons-collections4:4.3`)
  - JAXB API (`javax.xml.bind:jaxb-api:2.3.1`) - `javax/xml/bind/DatatypeConverter` 관련 오류 해결을 위해 추가


- **개발 도구**
  - Lombok (`org.projectlombok:lombok`)
  - JUnit (`org.junit.platform:junit-platform-launcher`)

## 📂 프로젝트 구조
```
/dividend
├── src
│   ├── main
│   │   ├── java
│   │   │   └── com.example.dividend
│   │   ├── resources
│   │   │   ├── application.yml
│   │   │   └── logback-spring.xml
│   ├── test
│   │   └── java
│   │       └── com.example.dividend
├── build.gradle
├── settings.gradle
└── README.md
```

## ✨ 주요 기능
- 주식 배당금 데이터를 수집 및 저장
- JWT 기반 사용자 인증 및 보안
- Redis를 활용한 캐싱
- H2 데이터베이스를 통한 데이터 저장 및 관리
- RESTful API 제공

## 🚀 API 설명
✅ GET - finance/dividend/{companyName}
- 회사 이름을 input으로 받아서 해당 회사의 메타 정보와 배당금 정보를 반환
- 잘못된 회사명이 입력으로 들어온 경우 400 status 코드와 에러메시지 반환

✅ GET - company/autocomplete
- 자동완성 기능을 위한 API
- 검색하고자 하는 prefix 를 입력으로 받고, 해당 prefix 로 검색되는 회사명 리스트 중 10개 반환

✅ GET - company
- 서비스에서 관리하고 있는 모든 회사 목록을 반환
- 반환 결과는 Page 인터페이스 형태

✅ POST - company
- 새로운 회사 정보 추가
- 추가하고자 하는 회사의 ticker 를 입력으로 받아 해당 회사의 정보를 스크래핑하고 저장
- 이미 보유하고 있는 회사의 정보일 경우 400 status 코드와 적절한 에러 메시지 반환
- 존재하지 않는 회사 ticker 일 경우 400 status 코드와 적절한 에러 메시지 반환

✅ DELETE - company/{ticker}
- ticker 에 해당하는 회사 정보 삭제
- 삭제시 회사의 배당금 정보와 캐시도 모두 삭제되어야 함

✅ POST - auth/signup
- 회원가입 API
- 중복 ID 는 허용하지 않음
- 패스워드는 암호화된 형태로 저장되어야함

✅ POST - auth/signin
- 로그인 API
- 회원가입이 되어있고, 아이디/패스워드 정보가 옳은 경우 JWT 발급
