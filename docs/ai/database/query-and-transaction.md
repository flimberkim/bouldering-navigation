# DB 조회 및 트랜잭션 작성 기준

> **적용 범위**: `src/main/java/com/boulderingnavigation/service/**`, `repository/**`.

## 이 문서를 언제 참고해야 하는지

새 조회/쓰기 로직을 서비스에 추가할 때, 또는 지연 로딩(N+1, LazyInitializationException) 문제가 의심될 때.

## 기본 원칙

트랜잭션 경계는 서비스 레이어에서 선언적으로 (`@Transactional`) 관리한다. 리포지토리는 Spring Data JPA 파생 쿼리(메서드 이름 기반)를 우선 사용하고, JPQL/QueryDSL은 파생 쿼리로 표현이 안 될 때만 검토한다 (현재 이 프로젝트에는 아직 그런 사례가 없다).

## 구현 규칙

- **조회 전용 서비스**: 클래스 레벨에 `@Transactional(readOnly = true)`.
- **쓰기 서비스**: 클래스 레벨은 `@Transactional`(쓰기 기본), 그 안에 순수 조회 메서드가 있으면 메서드 레벨로 `@Transactional(readOnly = true)`를 오버라이드한다.
- **존재 확인 패턴**: `repository.findById(id).orElseThrow(() -> new EntityNotFoundException("{Domain} not found: " + id))`. 반복되면 서비스 안에 private 헬퍼(`getOrThrow`)로 뽑는다.
- **쓰기 후 flush**: Command 서비스는 저장/삭제 직후 `repository.flush()`를 명시적으로 호출한다. 그래야 unique 제약 등 DB 무결성 위반이 같은 트랜잭션 안에서 즉시 드러나고, [`GlobalExceptionHandler`](../exceptions/exception-handling.md)가 `DataIntegrityViolationException`으로 잡을 수 있다. flush를 생략하면 위반이 트랜잭션 커밋 시점까지 미뤄져 핸들러가 못 잡을 수 있다.
- **지연 로딩과 `open-in-view: false`**: `application.yml`에 `spring.jpa.open-in-view: false`가 설정되어 있다. 즉 트랜잭션이 끝난 뒤(컨트롤러/직렬화 시점)에는 지연 로딩(`FetchType.LAZY`)이 동작하지 않는다. 연관 엔티티 접근과 DTO 변환은 반드시 서비스 메서드 안, 트랜잭션이 열려 있는 동안 끝낸다.
- **여러 조건을 조합하는 검색**: 억지로 JPQL 한 방으로 만들지 않는다. 리포지토리 파생 쿼리 여러 개를 서비스에서 조합하고, 순서를 지켜야 하면 `LinkedHashMap`으로 결과를 모은다.

## 예제

```java
// 조회 전용 서비스
@Service
@RequiredArgsConstructor
@Transactional(readOnly = true)
public class ProblemQueryService {
    public ProblemDetailResponse getDetail(Long problemId) {
        Problem problem = problemRepository.findById(problemId)
                .orElseThrow(() -> new EntityNotFoundException("Problem not found: " + problemId));
        Rock rock = problem.getRock();          // 트랜잭션 안이므로 지연 로딩 가능
        // ... DTO 변환도 여기서 끝낸다
    }
}

// 쓰기 서비스: 클래스는 쓰기 기본, 조회 메서드만 readOnly 오버라이드 + flush
@Service
@RequiredArgsConstructor
@Transactional
public class RegionCommandService {
    @Transactional(readOnly = true)
    public List<RegionResponse> findAll() { ... }

    public RegionResponse create(RegionRequest request) {
        Region region = regionRepository.save(Region.builder().name(request.name()).build());
        regionRepository.flush();   // unique 제약 위반을 여기서 바로 드러냄
        return toResponse(region);
    }

    private Region getOrThrow(Long id) {
        return regionRepository.findById(id)
                .orElseThrow(() -> new EntityNotFoundException("Region not found: " + id));
    }
}
```

여러 조건을 조합하는 검색 (`MountainQueryService.search`): 산 이름 매치와 문제/등급 매치를 각각 리포지토리로 조회한 뒤, `LinkedHashMap<Long, MountainSearchResponse>`로 순서를 지키며 병합한다. 두 매치가 같은 산에서 겹치는 경우의 처리 방식은 [exceptions-and-edge-cases/mountain-name-match-takes-priority-over-problem-match.md](../exceptions-and-edge-cases/mountain-name-match-takes-priority-over-problem-match.md) 참고.

## 예외 사항

없음 (여러 조건 검색을 애플리케이션 레벨에서 조합하는 것 자체가 이미 "기본 원칙"에 포함됨).

## Anti-pattern

- 컨트롤러나 DTO 변환 로직에서 지연 로딩을 트리거하는 것 (`open-in-view: false`이므로 `LazyInitializationException`이 난다).
- `@Transactional` 없이 여러 저장/삭제를 순차 실행하는 것.
- 쓰기 후 `flush()` 없이 unique 제약 위반이 나중에 발견되길 기대하는 것.

## 관련 사례 (Case Study)

없음.

## 관련 문서

- [exceptions/exception-handling.md](../exceptions/exception-handling.md)
- [architecture/layering.md](../architecture/layering.md)
- [patterns/existing-patterns.md](../patterns/existing-patterns.md)
