package com.boulderingnavigation.repository;

import com.boulderingnavigation.domain.Problem;
import java.util.List;
import org.springframework.data.jpa.repository.JpaRepository;

public interface ProblemRepository extends JpaRepository<Problem, Long> {

    List<Problem> findByRockId(Long rockId);

    List<Problem> findByNameContainingIgnoreCaseOrGradeContainingIgnoreCase(String name, String grade);
}
