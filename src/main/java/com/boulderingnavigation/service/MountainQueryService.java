package com.boulderingnavigation.service;

import com.boulderingnavigation.domain.Mountain;
import com.boulderingnavigation.domain.Rock;
import com.boulderingnavigation.dto.MountainSearchResponse;
import com.boulderingnavigation.dto.ProblemSummaryResponse;
import com.boulderingnavigation.dto.RockResponse;
import com.boulderingnavigation.repository.MountainRepository;
import com.boulderingnavigation.repository.ProblemRepository;
import com.boulderingnavigation.repository.RockRepository;
import java.util.List;
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

    public List<MountainSearchResponse> searchByName(String name) {
        return mountainRepository.findByNameContainingIgnoreCase(name).stream()
                .map(this::toSearchResponse)
                .toList();
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
