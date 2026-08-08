# 백엔드 테스트 작성 방식

> **적용 범위**: `src/test/java/com/boulderingnavigation/**`.

## 이 문서를 언제 참고해야 하는지

새 엔드포인트나 서비스 로직을 추가한 뒤 테스트를 작성할 때.

## 기본 원칙

실제 스프링 컨텍스트 + MockMvc + 실제 DB(H2)를 띄운 **통합 테스트**가 기본이다. Mockito로 서비스/리포지토리를 목킹해서 컨트롤러만 떼어 단위 테스트하지 않는다 — 엔티티 연관관계(산-바위-문제-영상)가 실제로 맞물려 동작하는지가 이 프로젝트에서 가장 중요한 검증 대상이기 때문이다.

## 구현 규칙

- 클래스 레벨에 `@SpringBootTest` + `@AutoConfigureMockMvc`를 붙인다.
- 조회만 검증하는 테스트는 `@Transactional`도 함께 붙여, 각 테스트가 끝나면 자동 롤백되게 한다 (`MountainAndProblemQueryTest`).
- 생성/수정/삭제를 검증해서 커밋된 상태를 확인해야 하는 테스트는 `@Transactional`을 붙이지 않고, 대신 `@AfterEach`에서 리포지토리로 직접 정리한다 (`AdminCrudTest`).
- `@BeforeEach`에서 리포지토리로 실제 연관관계를 가진 엔티티를 시딩한다. 순서는 상위 → 하위 (`Region → Mountain → Rock → Problem → CompletionVideo`), 각 엔티티는 `.builder()...build()`로 만들고 바로 `repository.save(...)`한다.
- 검증은 `MockMvc` + `jsonPath`로 한다. 상태 코드와 필드 값을 함께 검증한다.
- 테스트 메서드명은 한글 문장으로 짓는다 — 자세한 규칙은 [coding-rules/naming.md](../coding-rules/naming.md).
- 테스트 DB는 H2를 MariaDB 호환 모드로 띄운다 (`src/test/resources/application.yml`: `jdbc:h2:mem:...;MODE=MariaDB`, `ddl-auto: create-drop`).
- **`src/test/resources/application.yml`은 `src/main/resources/application.yml`과 병합되지 않고 완전히 대체한다.** Spring Boot가 클래스패스에서 `application.yml`을 하나만 로드하기 때문에, 테스트 클래스패스에서는 `src/test/resources`쪽이 우선한다. 즉 `jwt.secret`처럼 main의 `application.yml`에만 추가한 새 설정 값은 테스트에서 플레이스홀더를 못 찾아 `PlaceholderResolutionException`으로 컨텍스트 로딩이 실패한다 — **datasource 외의 설정을 main `application.yml`에 추가할 때마다 같은 키를 `src/test/resources/application.yml`에도 동일하게(같은 env-var-with-default 형태로) 추가해야 한다.**
- `api-spec.yaml` 계약 검증은 `ApiSpecContractTest` 하나가 전담한다 — 새 엔드포인트를 추가할 때마다 별도의 계약 테스트를 만들지 않는다. 대신 `api-spec.yaml`을 실제 라우트와 맞게 갱신하기만 하면 이 테스트가 자동으로 검증한다.

## 예제

```java
@SpringBootTest
@AutoConfigureMockMvc
@Transactional
class MountainAndProblemQueryTest {

    @Autowired private MockMvc mockMvc;
    @Autowired private RegionRepository regionRepository;
    // ...

    private Problem seededProblem;

    @BeforeEach
    void setUp() {
        Region region = regionRepository.save(Region.builder().name("경기도").build());
        Mountain mountain = mountainRepository.save(Mountain.builder().name("수락산").region(region).build());
        Rock rock = rockRepository.save(Rock.builder().name("치마바위").mountain(mountain).build());
        seededProblem = problemRepository.save(Problem.builder().name("좌측 크랙").grade("V4").rock(rock).build());
    }

    @Test
    void 산_이름으로_검색하면_바위와_문제_목록이_내려온다() throws Exception {
        mockMvc.perform(get("/api/mountains").param("q", "수락"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$[0].name").value("수락산"));
    }
}
```

CRUD 테스트는 롤백 대신 명시적 정리:

```java
@SpringBootTest
@AutoConfigureMockMvc
class AdminCrudTest {

    @AfterEach
    void tearDown() {
        mountainRepository.deleteAll();
        regionRepository.deleteAll();
    }

    @Test
    void 지역을_등록_수정_삭제할_수_있다() throws Exception {
        // POST → 201, PUT → 200, DELETE → 204 를 한 흐름으로 검증
    }
}
```

## 예외 사항

`ApiSpecContractTest`는 위 패턴을 따르지 않는다. MockMvc로 요청을 보내는 대신, `RequestMappingHandlerMapping`을 직접 읽어 실제 등록된 라우트와 `api-spec.yaml`을 문자열 집합으로 비교하는 별도 목적의 테스트다.

## Anti-pattern

- 서비스/리포지토리를 Mockito로 목킹해서 컨트롤러만 단위 테스트하는 것.
- 테스트 메서드명을 영어 요약이나 `test1` 식으로 짓는 것 ([coding-rules/naming.md](../coding-rules/naming.md) 위반).
- 새 엔드포인트마다 별도의 api-spec 대조 테스트를 새로 만드는 것 — `ApiSpecContractTest`가 이미 전체를 커버한다.

## 관련 사례 (Case Study)

없음.

## 관련 문서

- [coding-rules/naming.md](../coding-rules/naming.md)
- [database/query-and-transaction.md](../database/query-and-transaction.md)
- [api/response-format.md](../api/response-format.md)
