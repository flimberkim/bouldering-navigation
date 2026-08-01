package com.boulderingnavigation.repository;

import com.boulderingnavigation.domain.Mountain;
import java.util.List;
import org.springframework.data.jpa.repository.JpaRepository;

public interface MountainRepository extends JpaRepository<Mountain, Long> {

    List<Mountain> findByNameContainingIgnoreCase(String name);
}
