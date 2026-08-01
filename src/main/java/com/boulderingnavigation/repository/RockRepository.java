package com.boulderingnavigation.repository;

import com.boulderingnavigation.domain.Rock;
import java.util.List;
import org.springframework.data.jpa.repository.JpaRepository;

public interface RockRepository extends JpaRepository<Rock, Long> {

    List<Rock> findByMountainId(Long mountainId);
}
