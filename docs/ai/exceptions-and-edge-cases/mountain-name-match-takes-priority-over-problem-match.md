# 검색 시 산 이름 매치가 같은 산의 문제/등급 매치보다 우선한다

## 일반 규칙

`MountainQueryService.search`는 산 이름 매치와 문제 이름/등급 매치를 각각 조회한 뒤 하나의 목록으로 합친다. 일반적으로는 두 매치 결과를 단순히 합쳐야 할 것처럼 보인다.

## 이 상황에서 다르게 처리하는 이유

같은 산이 산 이름으로도 매치되고, 그 산 소속 문제가 등급/이름으로도 매치되는 경우가 있다 (예: "V4"로 검색했는데 마침 그 산 이름에도 "4"가 들어가는 경우, 또는 사용자가 산 이름을 검색했지만 그 산에 마침 매치되는 등급의 문제도 있는 경우). 이때 문제/등급 매치 쪽 로직으로 다시 처리하면 이미 산 이름 매치로 만들어둔 "전체 바위/문제 목록"이 "매치된 문제만 걸러진 목록"으로 덮어써져 정보가 줄어든다.

## 실제 처리 방식

`byMountainId` 맵에 산 이름 매치 결과를 먼저 채워 넣고, 문제/등급 매치를 순회할 때는 이미 맵에 있는 산(`byMountainId.containsKey(mountain.getId())`)이면 건너뛴다. 즉 **산 이름 매치가 항상 우선하고, 그 산은 전체 목록으로 표시된다.**

```java
public List<MountainSearchResponse> search(String query) {
    Map<Long, MountainSearchResponse> byMountainId = new LinkedHashMap<>();

    for (Mountain mountain : mountainRepository.findByNameContainingIgnoreCase(query)) {
        byMountainId.put(mountain.getId(), toSearchResponse(mountain));   // 전체 목록
    }

    // ... 문제/등급 매치 그룹핑 ...
    for (Map.Entry<Mountain, Map<Rock, List<Problem>>> entry : matchesByMountainThenRock.entrySet()) {
        Mountain mountain = entry.getKey();
        if (byMountainId.containsKey(mountain.getId())) {
            continue;   // 이미 산 이름으로 매치된 산은 필터링된 목록으로 덮어쓰지 않는다
        }
        byMountainId.put(mountain.getId(), toFilteredSearchResponse(mountain, entry.getValue()));
    }

    return List.copyOf(byMountainId.values());
}
```

## 관련 코드

`src/main/java/com/boulderingnavigation/service/MountainQueryService.java`의 `search` 메서드.

## 관련 문서

- [../patterns/existing-patterns.md](../patterns/existing-patterns.md)
- [../decisions/2026-08-08-broaden-mountain-search-to-problem-and-grade.md](../decisions/2026-08-08-broaden-mountain-search-to-problem-and-grade.md)
