# 검색 쿼리 파라미터를 산 이름 전용에서 문제 이름/등급까지 포함하도록 확장

- 날짜: 2026-08-08
- 관련 커밋: `bc9a560` Search by problem name or grade, not just mountain name

## 무엇을 결정했는가

`GET /api/mountains`의 검색 쿼리 파라미터 이름을 `name`에서 `q`로 바꾸고, 산 이름뿐 아니라 문제 이름/등급으로도 매치되게 확장했다. 산 이름으로 매치되면 그 산의 전체 바위/문제 목록을 내려주고, 문제 이름/등급으로만 매치되면 매치된 바위/문제만 걸러서 내려준다 (어느 산·바위에 속하는지는 함께 표시).

## 왜 (배경/문제)

사용자가 "V4"나 특정 문제 이름으로도 찾고 싶어할 것이라는 요구를 반영. 파라미터 이름을 `name → q`로 바꾼 건 이제 산 이름만 검색하는 게 아니라는 걸 API 사용자에게 이름으로도 드러내기 위함.

## 고려했던 대안

커밋에 명시된 대안은 없음 — 파라미터 이름 확장과 필터링된 응답 구조가 함께 결정되었다.

## 영향받은 부분

- `api-spec.yaml` (쿼리 파라미터 이름/설명)
- `MountainQueryService.search` — 산 매치 + 문제/등급 매치를 조합하는 로직 추가 ([patterns/existing-patterns.md](../patterns/existing-patterns.md)의 "여러 조건을 하나의 쿼리 파라미터로 받는 검색" 패턴이 여기서 나옴)
- `ProblemRepository` — `findByNameContainingIgnoreCaseOrGradeContainingIgnoreCase` 파생 쿼리 추가
- `frontend/src/api/mountains.ts`, `SearchBar.tsx`, `SearchPage.tsx` — 파라미터 이름과 검색 안내 문구 변경
- 산 이름 매치와 문제/등급 매치가 같은 산에서 겹칠 때의 처리 방식은 [../exceptions-and-edge-cases/mountain-name-match-takes-priority-over-problem-match.md](../exceptions-and-edge-cases/mountain-name-match-takes-priority-over-problem-match.md) 참고.

## 관련 문서

- [patterns/existing-patterns.md](../patterns/existing-patterns.md)
- [database/query-and-transaction.md](../database/query-and-transaction.md)
