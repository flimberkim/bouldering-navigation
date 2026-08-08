# DTO 작성 기준

> **적용 범위**: `src/main/java/com/boulderingnavigation/dto/**` (공개 API), `dto/admin/**` (admin API).

## 이 문서를 언제 참고해야 하는지

새 엔드포인트의 요청/응답 형태가 필요할 때, 또는 기존 DTO를 다른 용도로 재사용해도 될지 판단할 때.

## 기본 원칙

DTO는 계층 간 데이터 전달 전용이며 로직을 갖지 않는다. 항상 Java `record`로 만든다 — 불변이고, 필드 목록만으로 형태가 드러나기 때문이다.

## 구현 규칙

- **요청(Request) DTO**: 필드에 `jakarta.validation` 애노테이션(`@NotBlank`, `@NotNull` 등)을 직접 붙인다. 컨트롤러 파라미터에 `@Valid`를 붙여 검증을 트리거한다.
- **응답(Response) DTO**: 검증 애노테이션을 붙이지 않는다. 연관 엔티티를 그대로 내려주지 않고, 클라이언트가 필요한 값만 평탄화해서 담는다 (`rockName`, `mountainName`, `regionName`처럼 — 엔티티 참조가 아니라 문자열/원시값).
- **용도별로 별도 DTO를 만든다.** 같은 도메인이라도 목록용과 상세용은 다른 record로 분리한다 (`ProblemSummaryResponse` vs `ProblemDetailResponse`). 하나의 DTO에 optional 필드를 잔뜩 채워 여러 용도로 돌려쓰지 않는다.
- **패키지 위치**: admin CRUD용 DTO는 `dto/admin`, 공개 조회용 DTO는 `dto` 바로 아래.

## 예제

```java
// dto/admin/ProblemRequest.java — 요청 DTO, 검증 애노테이션 필수
public record ProblemRequest(
        @NotBlank String name,
        @NotBlank String grade,
        @NotNull Long rockId
) {}

// dto/ProblemDetailResponse.java — 상세 조회 전용 응답 DTO, 연관 엔티티를 평탄화
public record ProblemDetailResponse(
        Long problemId,
        String name,
        String grade,
        String rockName,
        String mountainName,
        String regionName,
        List<VideoResponse> videos
) {}

// dto/ProblemSummaryResponse.java — 목록용은 상세용과 별도 record
```

## 예외 사항

없음.

## Anti-pattern

- JPA 엔티티를 그대로 컨트롤러 응답으로 반환하는 것 (지연 로딩 프록시 직렬화 문제, 내부 구조 노출).
- DTO에 setter나 비즈니스 로직 메서드를 추가하는 것 — DTO는 데이터만 옮긴다.
- 목록용과 상세용을 하나의 DTO로 합치고 안 쓰는 필드는 `null`로 채우는 것.

## 관련 사례 (Case Study)

없음.

## 관련 문서

- [architecture/layering.md](../architecture/layering.md)
- [api/response-format.md](../api/response-format.md)
- [coding-rules/naming.md](../coding-rules/naming.md)
