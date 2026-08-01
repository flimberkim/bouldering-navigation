package com.boulderingnavigation.service;

import com.boulderingnavigation.domain.Problem;
import com.boulderingnavigation.domain.Rock;
import com.boulderingnavigation.dto.ProblemDetailResponse;
import com.boulderingnavigation.dto.VideoResponse;
import com.boulderingnavigation.repository.CompletionVideoRepository;
import com.boulderingnavigation.repository.ProblemRepository;
import jakarta.persistence.EntityNotFoundException;
import java.util.List;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
@RequiredArgsConstructor
@Transactional(readOnly = true)
public class ProblemQueryService {

    private final ProblemRepository problemRepository;
    private final CompletionVideoRepository completionVideoRepository;

    public ProblemDetailResponse getDetail(Long problemId) {
        Problem problem = problemRepository.findById(problemId)
                .orElseThrow(() -> new EntityNotFoundException("Problem not found: " + problemId));
        Rock rock = problem.getRock();

        List<VideoResponse> videos = completionVideoRepository.findByProblemId(problemId).stream()
                .map(video -> new VideoResponse(video.getId(), video.getUrl(), video.getPlatform().name()))
                .toList();

        return new ProblemDetailResponse(
                problem.getId(),
                problem.getName(),
                problem.getGrade(),
                rock.getName(),
                rock.getMountain().getName(),
                rock.getMountain().getRegion().getName(),
                videos
        );
    }
}
