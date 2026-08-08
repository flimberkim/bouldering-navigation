# 레이어링 — Controller / Service / Repository / Domain / DTO

> **적용 범위**: `src/main/java/com/boulderingnavigation/**` 전체. 새 도메인이나 새 엔드포인트를 추가하는 모든 작업.

## 이 문서를 언제 참고해야 하는지

새 API 엔드포인트, 새 도메인 리소스, 또는 기존 리소스에 새 계층 코드를 추가하기 전에 읽는다.

## 기본 원칙

계층은 `controller → service → repository → domain → dto` 순으로 단방향 의존한다. 상위 계층이 하위 계층을 건너뛰지 않는다 (컨트롤러가 리포지토리를 직접 호출하지 않음).

조회와 쓰기를 서비스 클래스 단위로 분리한다: 읽기 전용 서비스는 `{Domain}QueryService`, 쓰기(생성/수정/삭제) 서비스는 `{Domain}CommandService`. 이렇게 나누는 이유는 트랜잭션 성격(읽기 전용 vs 쓰기)이 클래스 레벨 애노테이션으로 명확히 드러나고, 조회 로직이 쓰기 로직에 실수로 얽히지 않게 하기 위함이다.

관리자용 CRUD(admin)는 공개 조회 API와 패키지를 분리한다: `controller/admin`, `service/admin`, `dto/admin`.

## 구현 규칙

- **Controller** (`controller/`, `controller/admin/`): HTTP 매핑과 요청/응답 변환만 담당한다. 비즈니스 로직을 두지 않는다. `@RequiredArgsConstructor`로 서비스를 주입받는다.
- **Service** (`service/`, `service/admin/`): 트랜잭션 경계와 비즈니스 로직. 자세한 트랜잭션 규칙은 [database/query-and-transaction.md](../database/query-and-transaction.md) 참고.
- **Repository** (`repository/`): Spring Data JPA 인터페이스. 메서드 이름 파생 쿼리를 우선 사용한다.
- **Domain** (`domain/`): JPA 엔티티. 자기 상태를 바꾸는 메서드(`update`, `rename` 등)를 스스로 가진다 — 서비스가 세터로 필드를 직접 바꾸지 않는다.
- **Dto** (`dto/`, `dto/admin/`): 계층 간 데이터 전달 전용 Java record. 자세한 기준은 [dto/dto-conventions.md](../dto/dto-conventions.md) 참고.

## 예제

공개 조회 API 흐름 (`ProblemController` → `ProblemQueryService` → `ProblemRepository`/`CompletionVideoRepository`):

```java
// controller/ProblemController.java
@RestController
@RequestMapping("/api/problems")
@RequiredArgsConstructor
public class ProblemController {
    private final ProblemQueryService problemQueryService;

    @GetMapping("/{id}")
    public ProblemDetailResponse detail(@PathVariable Long id) {
        return problemQueryService.getDetail(id);
    }
}

// service/ProblemQueryService.java
@Service
@RequiredArgsConstructor
@Transactional(readOnly = true)
public class ProblemQueryService {
    private final ProblemRepository problemRepository;
    private final CompletionVideoRepository completionVideoRepository;

    public ProblemDetailResponse getDetail(Long problemId) {
        Problem problem = problemRepository.findById(problemId)
                .orElseThrow(() -> new EntityNotFoundException("Problem not found: " + problemId));
        // ... DTO로 변환해서 반환
    }
}
```

## 예외 사항

**Admin 리소스는 조회도 CommandService 안에 둔다.** 공개 API는 Query/Command를 클래스로 완전히 분리하지만, admin CRUD는 리소스 하나당 컨트롤러/서비스/DTO를 한 세트로 묶는 게 관리 화면 성격상 더 자연스럽기 때문에 `findAll()` 같은 조회 메서드도 `{Domain}CommandService` 안에 메서드 레벨 `@Transactional(readOnly = true)`로 둔다.

```java
// service/admin/RegionCommandService.java
@Service
@RequiredArgsConstructor
@Transactional
public class RegionCommandService {
    @Transactional(readOnly = true)
    public List<RegionResponse> findAll() { ... }

    public RegionResponse create(RegionRequest request) { ... }
}
```

## Anti-pattern

- 컨트롤러에서 리포지토리를 직접 호출하는 것 (서비스 계층 생략).
- 서비스에서 엔티티 필드를 세터로 직접 변경하는 것 — 대신 엔티티에 의미 있는 이름의 변경 메서드를 추가한다.

## 관련 사례 (Case Study)

없음 (아직 예외 사례가 쌓이면 [../decisions/](../decisions/README.md)에서 링크한다).

## 관련 문서

- [coding-rules/naming.md](../coding-rules/naming.md)
- [patterns/existing-patterns.md](../patterns/existing-patterns.md)
- [database/query-and-transaction.md](../database/query-and-transaction.md)
- [dto/dto-conventions.md](../dto/dto-conventions.md)
