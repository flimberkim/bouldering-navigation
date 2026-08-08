# 기존 코드에서 사용하는 패턴

> **적용 범위**: 백엔드 전체. 특히 기존 리소스와 같은 모양의 새 기능을 추가할 때.

## 이 문서를 언제 참고해야 하는지

**새 기능을 구현하기 전에 가장 먼저 읽는 문서.** 같은 종류의 기능(admin CRUD 리소스 추가, 검색 기능 추가 등)이 이미 있다면, 새로운 방식을 고안하지 않고 아래 패턴을 그대로 복사해서 시작한다.

## 기본 원칙

같은 종류의 기능은 프로젝트 안에서 항상 같은 모양이어야 한다. 이미 5개 리소스(Region, Mountain, Rock, Problem, CompletionVideo)가 같은 CRUD 패턴을 따르고 있으므로, 6번째 리소스도 그 패턴을 따른다.

## 구현 규칙 (패턴 목록)

### 1. Admin CRUD 4종 세트

새 관리용 리소스를 추가할 때는 항상 다음 4개 파일을 함께 만든다:

- `dto/admin/{Domain}Request.java` + `dto/admin/{Domain}Response.java`
- `service/admin/{Domain}CommandService.java`
- `controller/admin/Admin{Domain}Controller.java`

Region/Mountain/Rock/Problem/CompletionVideo 전부 이 세트를 그대로 따른다. 자세한 내부 구조는 [architecture/layering.md](../architecture/layering.md)의 예외 사항 참고.

### 2. 엔티티 생성/변경

```java
@Entity
@Table(name = "problem")
@Getter
@NoArgsConstructor(access = AccessLevel.PROTECTED)
public class Problem {
    @Id @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Builder
    public Problem(String name, String grade, Rock rock) { ... }

    public void update(String name, String grade, Rock rock) { ... }   // setter 대신 의미 있는 메서드
}
```

세터를 두지 않고, 의미 있는 이름의 변경 메서드(`update(...)`, `rename(...)`)를 엔티티 스스로 가진다.

### 3. 존재 확인 후 사용

```java
private Region getOrThrow(Long id) {
    return regionRepository.findById(id)
            .orElseThrow(() -> new EntityNotFoundException("Region not found: " + id));
}
```

리소스 조회 후 사용하는 모든 Command 서비스 메서드(수정, 삭제, 하위 리소스 등록 시 상위 리소스 확인 등)가 이 헬퍼 패턴을 따른다.

### 4. 여러 조건을 하나의 쿼리 파라미터로 받는 검색

```java
public List<MountainSearchResponse> search(String query) {
    Map<Long, MountainSearchResponse> byMountainId = new LinkedHashMap<>();
    // 1) 산 이름으로 먼저 매치
    for (Mountain mountain : mountainRepository.findByNameContainingIgnoreCase(query)) {
        byMountainId.put(mountain.getId(), toSearchResponse(mountain));
    }
    // 2) 문제 이름/등급으로 매치된 것을 추가 (이미 있는 산은 건너뜀)
    // ...
    return List.copyOf(byMountainId.values());
}
```

리포지토리 파생 쿼리 여러 개를 서비스에서 조합하고, `LinkedHashMap`으로 순서를 지키며 중복을 제거한다. JPQL 한 방으로 억지로 합치지 않는다. 이 패턴이 어떻게 만들어졌는지는 [../decisions/2026-08-08-broaden-mountain-search-to-problem-and-grade.md](../decisions/2026-08-08-broaden-mountain-search-to-problem-and-grade.md) 참고.

## 예제

위 4개 패턴의 실제 코드는 각각 `Region`/`RegionCommandService`/`AdminRegionController`, `Problem` 엔티티, `RegionCommandService.getOrThrow`, `MountainQueryService.search`를 참고.

## 예외 사항

Admin 리소스는 조회도 CommandService 안에 둔다 (공개 API만 Query/Command를 클래스로 완전히 분리) — [architecture/layering.md](../architecture/layering.md)의 예외 사항 참고.

## Anti-pattern

- 리소스마다 제각각 다른 모양으로 CRUD를 구현하는 것 (예: 어떤 리소스는 세터를 쓰고 어떤 리소스는 `update()` 메서드를 쓰는 것).
- 존재 확인 로직을 매번 새로 작성하는 것 — `getOrThrow` 헬퍼 패턴을 재사용한다.

## 관련 사례 (Case Study)

- [../decisions/2026-08-08-broaden-mountain-search-to-problem-and-grade.md](../decisions/2026-08-08-broaden-mountain-search-to-problem-and-grade.md)

## 관련 문서

- [architecture/layering.md](../architecture/layering.md)
- [database/query-and-transaction.md](../database/query-and-transaction.md)
- [exceptions/exception-handling.md](../exceptions/exception-handling.md)
