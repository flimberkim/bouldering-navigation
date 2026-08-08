# 네이밍 규칙

> **적용 범위**: `src/main/java/com/boulderingnavigation/**`, `src/test/java/com/boulderingnavigation/**` 전체.

## 이 문서를 언제 참고해야 하는지

새 클래스, 메서드, 패키지, 테스트를 추가할 때. 이름만 보고 역할과 계층을 알 수 있어야 한다는 게 기준이다.

## 기본 원칙

이름은 이 프로젝트에서 "어느 계층의 무엇을 하는 코드인지"를 그대로 드러내야 한다. `Manager`, `Helper`, `Util`처럼 계층이 드러나지 않는 이름은 쓰지 않는다.

## 구현 규칙

| 대상 | 규칙 | 예시 |
| --- | --- | --- |
| 조회 전용 서비스 | `{Domain}QueryService` | `ProblemQueryService`, `MountainQueryService` |
| 쓰기(CRUD) 서비스 | `{Domain}CommandService` (admin 패키지) | `RegionCommandService` |
| Admin 컨트롤러 | `Admin{Domain}Controller` | `AdminRegionController` |
| 공개 컨트롤러 | `{Domain}Controller` | `ProblemController` |
| 요청 DTO | `{Domain}Request` | `ProblemRequest` |
| 응답 DTO | `{Domain}Response` 또는 `{Domain}{목적}Response` | `RegionResponse`, `ProblemDetailResponse`, `ProblemSummaryResponse` |
| 패키지 | 계층별로 최상위 분리 | `domain`, `repository`, `service`, `service.admin`, `controller`, `controller.admin`, `dto`, `dto.admin`, `exception`, `config` |
| 테스트 메서드 | 한글, "조건_결과" 형태의 완전한 문장 | `산_이름으로_검색하면_바위와_문제_목록이_내려온다` |

테스트 메서드명을 한글 문장으로 쓰는 이유: 테스트 이름 자체가 그 엔드포인트/기능의 스펙 설명을 겸하도록 하기 위해서다. `test1`, `shouldReturnOk` 같은 이름은 그 역할을 하지 못한다.

## 예제

```java
public class ProblemQueryService { ... }          // 조회 전용
public class RegionCommandService { ... }          // admin 쓰기
public class AdminRegionController { ... }          // admin 컨트롤러
public record ProblemRequest(...) { ... }           // 요청 DTO
public record ProblemDetailResponse(...) { ... }    // 상세 조회 응답 DTO
```

```java
@Test
void 존재하지_않는_문제를_조회하면_404를_반환한다() throws Exception { ... }
```

## 예외 사항

응답 DTO가 단일 목적이고 이름이 모호할 여지가 없을 때는 `{목적}` 부분을 생략한다 (예: `RockResponse`, `VideoResponse` — "Rock 조회 응답"이라는 의미가 이름만으로 충분히 명확).

## Anti-pattern

- `ProblemService`, `ProblemManager`처럼 조회/쓰기 구분이나 계층이 드러나지 않는 이름.
- 테스트 메서드를 `test1()`, `testCreate()`처럼 내용이 드러나지 않게 짓는 것.

## 관련 사례 (Case Study)

없음.

## 관련 문서

- [architecture/layering.md](../architecture/layering.md)
- [dto/dto-conventions.md](../dto/dto-conventions.md)
- [testing/backend-testing.md](../testing/backend-testing.md)
