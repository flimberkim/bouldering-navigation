# bouldering-navigation

전국 자연 볼더링 문제를 찾아보고, SNS 완등 영상 링크와 방문 예정일 날씨를 함께 확인할 수 있는 서비스입니다.

## 기술 스택

- Java 21
- Spring Boot 4 (Web, Data JPA, Validation)
- Gradle (Kotlin DSL)
- MariaDB (운영), H2 (테스트)

## 로컬 실행

MariaDB가 필요합니다 (Docker 예시):

```
docker run -d --name bouldering-mariadb -p 3306:3306 \
  -e MARIADB_DATABASE=bouldering_navigation \
  -e MARIADB_ROOT_PASSWORD=changeme \
  mariadb:11
```

환경 변수로 접속 정보를 지정한 뒤 실행합니다.

```
./gradlew bootRun
```

| 변수 | 기본값 | 설명 |
| --- | --- | --- |
| DB_HOST | localhost | MariaDB 호스트 |
| DB_PORT | 3306 | MariaDB 포트 |
| DB_NAME | bouldering_navigation | 데이터베이스 이름 |
| DB_USERNAME | root | 사용자명 |
| DB_PASSWORD | (없음) | 비밀번호 |

## 테스트

```
./gradlew test
```
