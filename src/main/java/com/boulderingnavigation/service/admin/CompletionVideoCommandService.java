package com.boulderingnavigation.service.admin;

import com.boulderingnavigation.domain.CompletionVideo;
import com.boulderingnavigation.domain.Problem;
import com.boulderingnavigation.dto.admin.CompletionVideoRequest;
import com.boulderingnavigation.dto.admin.CompletionVideoResponse;
import com.boulderingnavigation.repository.CompletionVideoRepository;
import com.boulderingnavigation.repository.ProblemRepository;
import jakarta.persistence.EntityNotFoundException;
import java.util.List;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
@RequiredArgsConstructor
@Transactional
public class CompletionVideoCommandService {

    private final CompletionVideoRepository completionVideoRepository;
    private final ProblemRepository problemRepository;

    @Transactional(readOnly = true)
    public List<CompletionVideoResponse> findAll() {
        return completionVideoRepository.findAll().stream().map(this::toResponse).toList();
    }

    public CompletionVideoResponse create(CompletionVideoRequest request) {
        Problem problem = getProblemOrThrow(request.problemId());
        CompletionVideo video = completionVideoRepository.save(
                CompletionVideo.builder().url(request.url()).platform(request.platform()).problem(problem).build());
        completionVideoRepository.flush();
        return toResponse(video);
    }

    public CompletionVideoResponse update(Long id, CompletionVideoRequest request) {
        CompletionVideo video = getOrThrow(id);
        Problem problem = getProblemOrThrow(request.problemId());
        video.update(request.url(), request.platform(), problem);
        completionVideoRepository.flush();
        return toResponse(video);
    }

    public void delete(Long id) {
        CompletionVideo video = getOrThrow(id);
        completionVideoRepository.delete(video);
        completionVideoRepository.flush();
    }

    private CompletionVideo getOrThrow(Long id) {
        return completionVideoRepository.findById(id)
                .orElseThrow(() -> new EntityNotFoundException("CompletionVideo not found: " + id));
    }

    private Problem getProblemOrThrow(Long problemId) {
        return problemRepository.findById(problemId)
                .orElseThrow(() -> new EntityNotFoundException("Problem not found: " + problemId));
    }

    private CompletionVideoResponse toResponse(CompletionVideo video) {
        return new CompletionVideoResponse(video.getId(), video.getUrl(), video.getPlatform().name(), video.getProblem().getId());
    }
}
