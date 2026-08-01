package com.boulderingnavigation.service.admin;

import com.boulderingnavigation.domain.Problem;
import com.boulderingnavigation.domain.Rock;
import com.boulderingnavigation.dto.admin.ProblemRequest;
import com.boulderingnavigation.dto.admin.ProblemResponse;
import com.boulderingnavigation.repository.ProblemRepository;
import com.boulderingnavigation.repository.RockRepository;
import jakarta.persistence.EntityNotFoundException;
import java.util.List;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
@RequiredArgsConstructor
@Transactional
public class ProblemCommandService {

    private final ProblemRepository problemRepository;
    private final RockRepository rockRepository;

    @Transactional(readOnly = true)
    public List<ProblemResponse> findAll() {
        return problemRepository.findAll().stream().map(this::toResponse).toList();
    }

    public ProblemResponse create(ProblemRequest request) {
        Rock rock = getRockOrThrow(request.rockId());
        Problem problem = problemRepository.save(
                Problem.builder().name(request.name()).grade(request.grade()).rock(rock).build());
        problemRepository.flush();
        return toResponse(problem);
    }

    public ProblemResponse update(Long id, ProblemRequest request) {
        Problem problem = getOrThrow(id);
        Rock rock = getRockOrThrow(request.rockId());
        problem.update(request.name(), request.grade(), rock);
        problemRepository.flush();
        return toResponse(problem);
    }

    public void delete(Long id) {
        Problem problem = getOrThrow(id);
        problemRepository.delete(problem);
        problemRepository.flush();
    }

    private Problem getOrThrow(Long id) {
        return problemRepository.findById(id)
                .orElseThrow(() -> new EntityNotFoundException("Problem not found: " + id));
    }

    private Rock getRockOrThrow(Long rockId) {
        return rockRepository.findById(rockId)
                .orElseThrow(() -> new EntityNotFoundException("Rock not found: " + rockId));
    }

    private ProblemResponse toResponse(Problem problem) {
        return new ProblemResponse(problem.getId(), problem.getName(), problem.getGrade(),
                problem.getRock().getId(), problem.getRock().getName());
    }
}
