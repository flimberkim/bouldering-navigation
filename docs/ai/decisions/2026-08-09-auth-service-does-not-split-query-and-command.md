# AuthCommandService/AuthQueryService는 일반적인 Query/Command 분리 기준을 따르지 않음

- 날짜: 2026-08-09
- 관련 커밋: 인증 기반(회원가입/로그인/JWT) 최초 구현

## 무엇을 결정했는가

`signup`과 `login`을 둘 다 `service/AuthCommandService.java`에 두었다. `login`은 DB에 쓰기 작업이 없어 [architecture/layering.md](../architecture/layering.md)의 "읽기 전용은 QueryService, 쓰기는 CommandService" 기준으로는 QueryService에 있어야 할 것처럼 보이지만, 그렇게 하지 않았다. `GET /api/auth/me`만 `service/AuthQueryService.java`에 별도로 둔다.

## 왜 (배경/문제)

`login`은 DB에 아무것도 쓰지 않지만 "조회"라고 부르기도 애매하다 — 비밀번호 검증에 실패하면 예외를 던지고, 성공하면 JWT를 발급하는 부수효과가 있는 동작이라 순수 조회(예: `ProblemQueryService.getDetail`)와 성격이 다르다. `signup`(진짜 쓰기)과 `login`(쓰기는 없지만 조회도 아님)을 억지로 서로 다른 클래스에 쪼개면 오히려 "인증"이라는 하나의 응집된 개념이 두 클래스로 흩어져 읽기 어려워진다.

## 고려했던 대안

- `AuthQueryService.login()`으로 두는 안: 트랜잭션 성격(`@Transactional(readOnly = true)`)만 보면 맞지만, "인증 실패/성공"이라는 판단 로직이 QueryService에 있는 게 이 프로젝트의 다른 QueryService들(단순 조회+DTO 변환만 하는)과 성격이 달라 어색함.
- `signup`/`login`을 각각 `AuthCommandService`/`AuthQueryService`로 쪼개는 안: 인증이라는 하나의 흐름이 두 파일로 나뉘어 다음에 이 코드를 보는 사람이 로그인 로직을 찾으려면 두 클래스를 모두 봐야 함.

## 영향받은 부분

- `service/AuthCommandService.java` — `signup(SignupRequest)`, `login(LoginRequest)` (메서드 레벨 `@Transactional(readOnly = true)`로 오버라이드).
- `service/AuthQueryService.java` — `me(Long userId)`만 존재.

## 관련 문서

- [architecture/layering.md](../architecture/layering.md)
