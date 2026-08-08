# CLAUDE.md

이 프로젝트에서 작업하는 모든 AI 에이전트가 항상 먼저 읽는 진입점입니다. 실제 지식베이스는 [`docs/ai/README.md`](docs/ai/README.md)에 주제별로 정리되어 있습니다.

## 프로젝트 개요

전국 자연 볼더링 문제를 찾아보고, SNS 완등 영상 링크와 방문 예정일 날씨를 함께 확인할 수 있는 서비스.

- 백엔드: `src/main/java/com/boulderingnavigation/**` — Java 21, Spring Boot 4, Gradle(Kotlin DSL), MariaDB(운영)/H2(테스트)
- 프론트엔드: `frontend/` — React + TypeScript + Vite
- API 계약: 루트의 `api-spec.yaml` — 백엔드/프론트엔드가 공유하는 단일 소스. 백엔드만 수정하며, 코드 변경과 같은 커밋에서 함께 수정한다. `ApiSpecContractTest`가 스펙과 실제 라우트 불일치 시 빌드를 실패시킨다.

## 핵심 목표

새로운 AI 에이전트가 이 프로젝트에 투입되더라도, 기존에 쌓인 개발 방식과 판단 기준을 참고해서 기존 개발자가 하던 것과 최대한 비슷한 방식으로 일관된 결과를 만드는 것. 이를 위해 지식은 "무슨 작업을 했다"가 아니라 "이런 상황에서는 이렇게 판단한다"의 형태로 `docs/ai/`에 쌓는다.

## 작업 시작 전

1. 작업 범위에 해당하는 `docs/ai/` 하위 문서를 먼저 읽는다 — 전체 목록과 각 문서가 다루는 범위는 [`docs/ai/README.md`](docs/ai/README.md)의 인덱스를 참고한다.
2. 새 기능을 구현하기 전에는 같은 종류의 기존 코드(예: 다른 admin 리소스의 Controller/CommandService/DTO 세트)를 먼저 찾아 패턴을 확인한다 — `docs/ai/patterns/`에 정리되어 있다.
3. 문서와 실제 코드가 어긋나 있으면 코드를 신뢰하고, 문서를 코드에 맞게 고친 뒤 작업을 계속한다.

## 작업 완료 후

다음을 확인하고 필요한 경우에만 문서를 갱신한다. 단순 작업 로그나 일회성 내용은 기록하지 않는다.

1. 이번 작업에서 새롭게 발견되었거나 확립된 프로젝트 규칙이 있는가?
2. 기존 `docs/ai/` 문서와 충돌하는 내용이 있는가? (있다면 어느 쪽이 맞는지 판단하고 문서를 코드에 맞게 고친다)
3. 재사용 가능한 판단 기준이라면 → 관련된 기존 문서를 찾아 보완한다. 이미 다루는 문서가 있으면 새 문서를 만들지 않는다.
4. 특정 상황에서만 적용되는 예외이거나 다시 참고할 만한 사례라면 → `docs/ai/decisions/`, `docs/ai/troubleshooting/`, `docs/ai/exceptions-and-edge-cases/` 중 맞는 곳에 새 파일로 1건씩 기록한다 (형식은 각 디렉터리의 `README.md` 참고).

## 공통 규칙

- 백엔드 에이전트는 `frontend/`를 건드리지 않고, 프론트엔드 에이전트는 `src/main/java`, `src/test/java`, `api-spec.yaml`을 건드리지 않는다 (`.claude/agents/backend.md`, `.claude/agents/frontend.md` 참고).
- 백엔드 변경 후에는 `./gradlew test`, 프론트엔드 변경 후에는 `npm run build` / `npm run lint`(있다면)를 통과시킨다.
- `docs/ai/`는 git으로 관리한다 (gitignore 아님) — 같은 저장소에서 작업하는 다른 사람/다른 AI 에이전트도 동일한 컨텍스트를 참고해야 하기 때문이다.
