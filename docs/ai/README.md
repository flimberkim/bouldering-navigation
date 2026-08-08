# docs/ai — 프로젝트 개발 지식베이스

이 디렉터리는 AI 에이전트(그리고 사람 개발자)가 이 프로젝트에서 "어떻게 개발해야 하는가"를 참고하는 곳이다.

**핵심 목표**: 새로운 AI 에이전트가 이 프로젝트에 투입되더라도, 기존 개발자가 하던 방식과 최대한 비슷하게 판단하고 구현하도록 만드는 것.

그래서 여기에는 "무슨 작업을 했다"라는 작업 로그를 남기지 않는다. 대신 "이런 종류의 작업은 이렇게 판단하고 이렇게 만든다"는 **재사용 가능한 판단 기준**만 남긴다. 일회성 내용이나 단순 변경 이력은 git 커밋 로그가 이미 담당한다.

## 디렉터리 구조와 각 파일의 역할

```
docs/ai/
├── README.md                     ← 지금 이 파일. 전체 인덱스이자 운영 규칙
├── architecture/
│   └── layering.md               ← controller-service-repository-domain-dto 레이어링, Query/Command 분리
├── coding-rules/
│   └── naming.md                 ← 클래스/메서드/패키지/테스트 이름 규칙
├── testing/
│   └── backend-testing.md        ← 통합 테스트 작성 방식 (SpringBootTest + MockMvc + H2)
├── dto/
│   └── dto-conventions.md        ← DTO를 언제, 어떤 기준으로 만드는지
├── database/
│   └── query-and-transaction.md  ← 트랜잭션 경계, 조회 패턴, 지연 로딩 주의사항
├── api/
│   └── response-format.md        ← HTTP 상태 코드와 응답 바디 형식 규칙
├── exceptions/
│   └── exception-handling.md     ← GlobalExceptionHandler 중심의 예외 처리 방식
├── logging/
│   └── logging.md                ← 로깅 규칙 (현재는 미확립 — 잠정 원칙만 기록)
├── patterns/
│   └── existing-patterns.md      ← 새 기능을 만들 때 그대로 따라야 할 기존 구현 패턴
├── decisions/                    ← 의도적으로 일반 규칙과 다르게 결정한 사례 (파일당 1건)
│   ├── README.md                 ← 작성 형식과 파일명 규칙
│   └── YYYY-MM-DD-*.md
├── troubleshooting/               ← 실제로 겪은 문제와 원인/해결 (파일당 1건)
│   ├── README.md
│   └── YYYY-MM-DD-*.md
└── exceptions-and-edge-cases/     ← 일반 규칙이 적용되지 않는 비즈니스 로직 상황 (파일당 1건)
    ├── README.md
    └── *.md
```

각 폴더의 본문 문서(`architecture/layering.md` 등)는 "일반 규칙"을 다루고, `decisions/`, `troubleshooting/`, `exceptions-and-edge-cases/`는 "구체적인 사례"를 다룬다. 사례가 일반 규칙으로 굳어지면 본문 문서 쪽으로 승격시켜 정리한다.

## 문서 작성 원칙

모든 규칙 문서(`architecture/`, `coding-rules/`, `testing/`, `dto/`, `database/`, `api/`, `exceptions/`, `logging/`, `patterns/`)는 아래 원칙을 따른다.

1. 단순 설명이 아니라 **"이 프로젝트에서는 어떻게 해야 하는가"**를 명확한 지시문으로 쓴다. ("Spring에서는 이렇게 할 수 있다"가 아니라 "이 프로젝트에서는 이렇게 한다.")
2. 가능하면 실제 프로젝트 코드를 예제로 인용한다. 가상의 예제를 만들지 않는다.
3. **일반 규칙**과 **예외 규칙**을 구분해서 쓴다.
4. 왜 이 규칙을 쓰는지(배경, 트레이드오프)를 기록한다. 이유 없는 규칙은 다음 에이전트가 왜 지켜야 하는지 몰라 쉽게 깨진다.
5. 규칙과 다르게 구현된 사례가 있다면 **Anti-pattern**으로 명시한다.
6. 다시 참고할 만한 과거 사례는 **Case Study**로 남기거나, `decisions/` · `troubleshooting/` · `exceptions-and-edge-cases/`로 연결한다.
7. 새로운 규칙이 생기면 **기존 문서를 갱신**한다. 비슷한 주제의 문서를 중복 생성하지 않는다 — 먼저 이 README의 인덱스에서 이미 다루는 문서가 있는지 확인한다.
8. 다른 문서와 관련 있는 규칙은 Markdown 링크(`[제목](경로)`)로 연결한다.
9. 문서 상단에 **적용 범위**를 적어, 에이전트가 지금 작업이 이 문서와 관련 있는지 훑어보고 바로 판단할 수 있게 한다.

## 문서 기본 템플릿

새 규칙 문서를 만들 때는 이 템플릿을 그대로 복사해서 시작한다.

```markdown
# {제목}

> **적용 범위**: {이 문서가 다루는 코드 경로/상황을 한 줄로}

## 이 문서를 언제 참고해야 하는지

## 기본 원칙

## 구현 규칙

## 예제

## 예외 사항

## Anti-pattern

## 관련 사례 (Case Study)

## 관련 문서
```

`decisions/`, `troubleshooting/`, `exceptions-and-edge-cases/`는 각 폴더의 `README.md`에 별도의 (더 짧은) 사례 전용 템플릿이 있다.

## AI 에이전트가 작업 시작 전에 따를 규칙

1. 작업 범위와 관련된 `docs/ai/` 하위 문서를 위 인덱스에서 찾아 먼저 읽는다.
2. 같은 종류의 기능을 이미 구현한 기존 코드가 있는지 확인한다 — 특히 [`patterns/existing-patterns.md`](patterns/existing-patterns.md)에 정리된 패턴을 먼저 본다. 새로운 방식을 고안하기 전에 기존 코드를 복사해서 시작할 수 있는지 확인한다.
3. 문서 내용이 실제 코드와 다르면 코드를 신뢰하고, 문서를 코드에 맞게 고친 뒤 작업을 계속한다.

## AI 에이전트가 작업 완료 후 따를 규칙

작업이 끝나면 다음을 순서대로 확인한다. **해당 사항이 없으면 아무것도 기록하지 않는다** — 단순 작업 로그나 일회성 변경 내용은 여기 남기지 않는다.

1. 이번 작업에서 새롭게 발견되었거나, 처음으로 확립한 프로젝트 규칙이 있는가?
2. 기존 `docs/ai/` 문서와 충돌하는 내용이 있는가? (충돌한다면 코드 기준으로 문서를 바로 고친다.)
3. **재사용 가능한 판단 기준**이라면 → 관련된 기존 규칙 문서를 찾아 보완한다. 이미 다루는 문서가 있다면 새 문서를 만들지 않는다. 완전히 새로운 주제라면 위 원칙/템플릿에 따라 새 문서를 만들고 이 README의 인덱스에도 한 줄 추가한다.
4. **특정 상황에서만 적용되는 예외**라면 → [`decisions/`](decisions/README.md), [`troubleshooting/`](troubleshooting/README.md), [`exceptions-and-edge-cases/`](exceptions-and-edge-cases/README.md) 중 맞는 곳에 새 파일을 1건 추가한다. 기존 사례를 수정하는 게 아니라면 기존 사례 파일에 이어 붙이지 않고 새 파일로 만든다.

## 문서가 많아져도 찾기 쉽게 유지하는 방법

- **파일 하나 = 주제 하나.** 여러 주제를 한 파일에 몰아넣지 않는다. 지금은 카테고리마다 파일이 1개뿐이지만, 카테고리가 커지면(예: `testing/`에 `frontend-testing.md`가 추가되는 식) 그 카테고리 안에서 계속 분리한다.
- **사례 폴더(`decisions/`, `troubleshooting/`, `exceptions-and-edge-cases/`)는 항상 파일당 1건.** 하나의 로그 파일에 계속 이어 붙이지 않는다 — 그러면 결국 "거대한 파일 하나"가 되어 검색이 느려지고 git 충돌도 잦아진다. 파일명 자체가 검색 키워드가 되도록 설명적으로 짓는다.
- **카테고리 폴더에 파일이 10개를 넘어가면** 그 폴더에 자체 `README.md` 인덱스를 추가해 하위 목록을 정리한다 (지금 이 파일과 같은 역할을 그 폴더 안에서 한 번 더 하는 것).
- **파일명은 kebab-case로, 내용을 그대로 설명**하게 짓는다 (`2026-08-08-broaden-mountain-search-to-problem-and-grade.md`처럼). 파일명만 보고도 grep 없이 찾을 수 있어야 한다.
- 이 README의 상단 트리와 인덱스는 문서가 추가/이동될 때마다 함께 갱신한다 — 이 파일이 결국 "목차" 역할이므로 여기가 낡으면 전체 구조를 신뢰할 수 없게 된다.

## Cursor / Claude Code / Codex 같은 도구에서 운영하는 방식

- 실제 규칙 본문은 항상 `docs/ai/**`에만 둔다. 툴별 진입 파일(`CLAUDE.md`, 향후 추가될 수 있는 `AGENTS.md`, `.cursor/rules` 등)에는 규칙을 복붙하지 않고, 전부 이 README를 가리키는 **얇은 포인터**로만 유지한다. 그래야 규칙이 여러 곳에 흩어져 서로 어긋나는 일이 없다.
- 지금은 `CLAUDE.md`(저장소 루트)가 유일한 진입 파일이다. Cursor나 Codex 등 다른 도구를 도입하게 되면, 그 도구의 진입 파일도 같은 방식으로 "`docs/ai/README.md`를 먼저 읽어라"만 적고 내용을 복제하지 않는다.
- 이 지식베이스는 git으로 관리한다(gitignore 아님) — 프로젝트에 참여하는 사람과 AI 에이전트가 모두 동일한 컨텍스트를 공유해야 결과가 일관되기 때문이다.
