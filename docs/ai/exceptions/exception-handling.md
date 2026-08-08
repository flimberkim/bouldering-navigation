# 예외 처리 방식

> **적용 범위**: `src/main/java/com/boulderingnavigation/exception/GlobalExceptionHandler.java`, 그리고 이를 트리거하는 모든 서비스 코드.

## 이 문서를 언제 참고해야 하는지

새로운 실패 케이스(404/400/409 등)를 처리해야 하거나, 서비스에서 어떤 예외를 던져야 할지 판단할 때.

## 기본 원칙

예외 처리는 컨트롤러나 서비스에 흩어놓지 않고 `GlobalExceptionHandler` 한 곳에 중앙화한다. 서비스는 표준 예외를 던지기만 하고, "그 예외가 어떤 HTTP 상태 코드/메시지가 되는지"는 핸들러가 전담한다. 이렇게 하는 이유는 같은 종류의 실패(예: 리소스 없음)가 컨트롤러마다 다른 상태 코드로 응답하는 걸 막기 위해서다.

## 구현 규칙

- **리소스 없음** → 서비스에서 `jakarta.persistence.EntityNotFoundException`을 던진다 → 핸들러가 404 + 예외 메시지 그대로 반환.
- **검증 실패** → 컨트롤러 파라미터의 `@Valid`가 던지는 `MethodArgumentNotValidException`을 핸들러가 잡아 `{필드명: 에러메시지}` 맵으로 변환 → 400.
- **DB 제약 위반** → `DataIntegrityViolationException`을 핸들러가 잡는다. SQLState `23505`(unique 위반)면 "이미 존재하는 이름입니다.", 그 외(FK 등 하위 데이터 존재)면 "하위 데이터가 존재하여 처리할 수 없습니다." → 둘 다 409.
- 새로운 예외 케이스가 필요하면 `GlobalExceptionHandler`에 `@ExceptionHandler` 메서드를 추가한다. 컨트롤러나 서비스에 try/catch를 넣지 않는다.
- DB 제약 위반을 핸들러가 잡으려면 쓰기 작업 직후 `repository.flush()`를 호출해야 한다 — 자세한 이유는 [database/query-and-transaction.md](../database/query-and-transaction.md) 참고.

## 예제

```java
@RestControllerAdvice
public class GlobalExceptionHandler {

    private static final String UNIQUE_VIOLATION_SQLSTATE = "23505";

    @ExceptionHandler(EntityNotFoundException.class)
    public ResponseEntity<String> handleEntityNotFound(EntityNotFoundException e) {
        return ResponseEntity.status(HttpStatus.NOT_FOUND).body(e.getMessage());
    }

    @ExceptionHandler(DataIntegrityViolationException.class)
    public ResponseEntity<String> handleDataIntegrityViolation(DataIntegrityViolationException e) {
        if (isUniqueViolation(e)) {
            return ResponseEntity.status(HttpStatus.CONFLICT).body("이미 존재하는 이름입니다.");
        }
        return ResponseEntity.status(HttpStatus.CONFLICT).body("하위 데이터가 존재하여 처리할 수 없습니다.");
    }

    @ExceptionHandler(MethodArgumentNotValidException.class)
    public ResponseEntity<Map<String, String>> handleValidation(MethodArgumentNotValidException e) {
        Map<String, String> errors = new LinkedHashMap<>();
        e.getBindingResult().getFieldErrors()
                .forEach(fieldError -> errors.put(fieldError.getField(), fieldError.getDefaultMessage()));
        return ResponseEntity.status(HttpStatus.BAD_REQUEST).body(errors);
    }
}
```

## 예외 사항

없음.

## Anti-pattern

- 컨트롤러나 서비스 메서드 안에서 try/catch로 직접 `ResponseEntity`를 만들어 반환하는 것.
- 리소스 없음을 `Optional.orElse(null)`로 그냥 넘기고 컨트롤러에서 null 체크하는 것 — 항상 `EntityNotFoundException`을 던져 핸들러가 일관되게 처리하게 한다.

## 관련 사례 (Case Study)

없음.

## 관련 문서

- [api/response-format.md](../api/response-format.md)
- [database/query-and-transaction.md](../database/query-and-transaction.md)
