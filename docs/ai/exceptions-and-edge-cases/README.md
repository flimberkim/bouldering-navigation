# exceptions-and-edge-cases/ — 일반 규칙이 적용되지 않는 비즈니스 로직 상황

> **적용 범위**: 일반적인 코딩 규칙(레이어링, DTO, API 형식 등)의 문제가 아니라, **비즈니스 로직 자체**가 특정 상황에서 직관과 다르게 동작하도록 만들어진 경우를 기록하는 곳.

## `decisions/`, `troubleshooting/`과의 차이

- `decisions/`: "왜 이렇게 구현하기로 했는가" — 구현 방식/기술 선택에 대한 결정.
- `troubleshooting/`: "무엇이 고장났고 어떻게 고쳤는가" — 버그와 그 해결.
- `exceptions-and-edge-cases/`: "겉보기엔 이상해 보일 수 있지만 의도된 동작" — 다음에 이 로직을 건드릴 에이전트가 "버그인가?"하고 오해해서 고치지 않도록 미리 설명해두는 곳.

## 파일명 규칙

`짧은-상황-설명-kebab-case.md` (날짜 접두어 없음 — 날짜보다 "어떤 상황인지"가 검색 키워드이기 때문).

## 항목 템플릿

```markdown
# {상황}

## 일반 규칙

## 이 상황에서 다르게 처리하는 이유

## 실제 처리 방식

## 관련 코드

## 관련 문서
```

## 기존 사례

- [mountain-name-match-takes-priority-over-problem-match.md](mountain-name-match-takes-priority-over-problem-match.md) — 검색 시 산 이름 매치와 문제/등급 매치가 같은 산에서 겹치면 산 이름 매치 쪽(전체 목록)이 우선시되고 필터링된 매치는 버려진다.
