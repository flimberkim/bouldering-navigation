package com.boulderingnavigation.service;

import com.boulderingnavigation.domain.Mountain;
import com.boulderingnavigation.domain.Problem;
import com.boulderingnavigation.domain.Rock;
import com.boulderingnavigation.dto.MountainSearchResponse;
import com.boulderingnavigation.dto.ProblemSummaryResponse;
import com.boulderingnavigation.dto.RockResponse;
import com.boulderingnavigation.repository.MountainRepository;
import com.boulderingnavigation.repository.ProblemRepository;
import com.boulderingnavigation.repository.RockRepository;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
@RequiredArgsConstructor
@Transactional(readOnly = true)
public class MountainQueryService {

    private final MountainRepository mountainRepository;
    private final RockRepository rockRepository;
    private final ProblemRepository problemRepository;

    /**
     * Matches by mountain name (full rocks/problems listing), or by problem
     * name/grade (listing filtered down to the matching rocks/problems only,
     * so callers can see which mountain and rock a matching problem is in).
     */
    public List<MountainSearchResponse> search(String query) {
        Map<Long, MountainSearchResponse> byMountainId = new LinkedHashMap<>();

        for (Mountain mountain : mountainRepository.findByNameContainingIgnoreCase(query)) {
            byMountainId.put(mountain.getId(), toSearchResponse(mountain));
        }

        List<Problem> matchingProblems =
                problemRepository.findByNameContainingIgnoreCaseOrGradeContainingIgnoreCase(query, query);

        Map<Mountain, Map<Rock, List<Problem>>> matchesByMountainThenRock = matchingProblems.stream()
                .collect(Collectors.groupingBy(
                        problem -> problem.getRock().getMountain(),
                        LinkedHashMap::new,
                        Collectors.groupingBy(Problem::getRock, LinkedHashMap::new, Collectors.toList())));

        for (Map.Entry<Mountain, Map<Rock, List<Problem>>> entry : matchesByMountainThenRock.entrySet()) {
            Mountain mountain = entry.getKey();
            if (byMountainId.containsKey(mountain.getId())) {
                continue;
            }
            byMountainId.put(mountain.getId(), toFilteredSearchResponse(mountain, entry.getValue()));
        }

        return List.copyOf(byMountainId.values());
    }

    private MountainSearchResponse toFilteredSearchResponse(Mountain mountain, Map<Rock, List<Problem>> problemsByRock) {
        List<RockResponse> rocks = problemsByRock.entrySet().stream()
                .map(entry -> new RockResponse(
                        entry.getKey().getId(),
                        entry.getKey().getName(),
                        entry.getValue().stream()
                                .map(problem -> new ProblemSummaryResponse(problem.getId(), problem.getName(), problem.getGrade()))
                                .toList()))
                .toList();
        return new MountainSearchResponse(mountain.getId(), mountain.getName(), mountain.getRegion().getName(), rocks);
    }

    private MountainSearchResponse toSearchResponse(Mountain mountain) {
        List<RockResponse> rocks = rockRepository.findByMountainId(mountain.getId()).stream()
                .map(this::toRockResponse)
                .toList();
        return new MountainSearchResponse(mountain.getId(), mountain.getName(), mountain.getRegion().getName(), rocks);
    }

    private RockResponse toRockResponse(Rock rock) {
        List<ProblemSummaryResponse> problems = problemRepository.findByRockId(rock.getId()).stream()
                .map(problem -> new ProblemSummaryResponse(problem.getId(), problem.getName(), problem.getGrade()))
                .toList();
        return new RockResponse(rock.getId(), rock.getName(), problems);
    }
}
