package com.boulderingnavigation.repository;

import com.boulderingnavigation.domain.CompletionVideo;
import java.util.List;
import org.springframework.data.jpa.repository.JpaRepository;

public interface CompletionVideoRepository extends JpaRepository<CompletionVideo, Long> {

    List<CompletionVideo> findByProblemId(Long problemId);
}
