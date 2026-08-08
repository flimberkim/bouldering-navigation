# API 응답 형식

> **적용 범위**: `src/main/java/com/boulderingnavigation/controller/**`, `controller/admin/**`.

## 이 문서를 언제 참고해야 하는지

새 엔드포인트를 추가할 때, 상태 코드나 응답 바디 형태를 정할 때.

## 기본 원칙

응답은 감싸는 봉투(wrapper) 없이 DTO(또는 DTO 리스트)를 그대로 반환한다. 성공/실패 여부는 오직 HTTP 상태 코드로 표현한다 — 응답 바디 안에 `success` 같은 필드를 따로 두지 않는다.

## 구현 규칙

| 동작 | 상태 코드 | 바디 |
| --- | --- | --- |
| 조회 (GET) | 200 | DTO 또는 `List<DTO>` 그대로 |
| 생성 (POST) | 201 Created | 생성된 리소스의 Response DTO |
| 수정 (PUT) | 200 | 수정된 리소스의 Response DTO |
| 삭제 (DELETE) | 204 No Content | 없음 |
| 검증 실패 | 400 | `{필드명: 에러메시지}` 맵 |
| 리소스 없음 | 404 | 에러 메시지 문자열 (JSON 봉투 없이 그대로) |
| 제약 충돌 (unique, 하위 데이터 존재 등) | 409 | 사람이 읽을 수 있는 한글 메시지 문자열 |

## 예제

```java
@RestController
@RequestMapping("/api/admin/regions")
@RequiredArgsConstructor
public class AdminRegionController {

    @GetMapping
    public List<RegionResponse> findAll() {                 // 200 + 리스트 그대로
        return regionCommandService.findAll();
    }

    @PostMapping
    public ResponseEntity<RegionResponse> create(@RequestBody @Valid RegionRequest request) {
        return ResponseEntity.status(HttpStatus.CREATED)     // 201
                .body(regionCommandService.create(request));
    }

    @PutMapping("/{id}")
    public RegionResponse update(@PathVariable Long id, @RequestBody @Valid RegionRequest request) {
        return regionCommandService.update(id, request);     // 200
    }

    @DeleteMapping("/{id}")
    public ResponseEntity<Void> delete(@PathVariable Long id) {
        regionCommandService.delete(id);
        return ResponseEntity.noContent().build();            // 204
    }
}
```

실패 응답은 컨트롤러가 아니라 [`GlobalExceptionHandler`](../exceptions/exception-handling.md)가 만든다 — 자세한 매핑 규칙은 그 문서를 참고.

## 예외 사항

없음.

## Anti-pattern

- `{ "success": true, "data": ... }`, `{ "code": 0, "result": ... }`처럼 커스텀 봉투를 씌우는 것 — 이 프로젝트는 상태 코드만으로 성공/실패를 표현하므로 쓰지 않는다.
- 404/409 에러를 JSON 객체(`{"error": "..."}`)로 감싸는 것 — 이 프로젝트는 문자열 그대로 반환한다.

## 관련 사례 (Case Study)

없음.

## 관련 문서

- [exceptions/exception-handling.md](../exceptions/exception-handling.md)
- [dto/dto-conventions.md](../dto/dto-conventions.md)
