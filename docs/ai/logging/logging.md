# 로깅 규칙

> **적용 범위**: 백엔드 전체 (`src/main/java/com/boulderingnavigation/**`).

## 이 문서를 언제 참고해야 하는지

로깅 코드를 새로 추가하려 할 때.

## 현재 상태

이 프로젝트에는 **아직 로깅 코드가 전혀 없다** (`Logger`, `slf4j`, `log.info/warn/error/debug` 전체 검색 결과 0건, 2026-08-09 기준). 아래는 실제 사용 사례가 없는 **잠정 원칙**이며, 처음으로 로깅이 추가되면 이 문서를 실제 코드 예제로 갱신해야 한다.

## 기본 원칙 (잠정)

Spring Boot 기본 로깅(Logback + Slf4j)을 그대로 사용한다. 별도 로깅 프레임워크나 구조화 로깅 라이브러리를 임의로 도입하지 않는다.

## 구현 규칙 (잠정)

- 로거 선언은 `private static final Logger log = LoggerFactory.getLogger(...)`를 직접 쓰기보다, Lombok `@Slf4j`를 우선 검토한다 — 이 프로젝트가 이미 Lombok을 전역적으로 쓰고 있기 때문이다 (`@Getter`, `@Builder`, `@RequiredArgsConstructor` 등, [architecture/layering.md](../architecture/layering.md) 참고).
- 어느 레벨에서 로깅할지, 어떤 정보를 남길지는 아직 정해진 규칙이 없다. 처음 로깅을 추가하는 작업에서 이 부분을 정하고 이 문서에 반영한다.

## 예제

없음 (아직 실제 코드 없음).

## 예외 사항

없음.

## Anti-pattern

아직 없음.

## 관련 사례 (Case Study)

없음 — 로깅 관련 결정이 처음 생기면 [../decisions/](../decisions/README.md)에 기록하고 여기서 링크한다.

## 관련 문서

없음.
