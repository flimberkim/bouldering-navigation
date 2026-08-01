package com.boulderingnavigation.repository;

import com.boulderingnavigation.domain.Region;
import org.springframework.data.jpa.repository.JpaRepository;

public interface RegionRepository extends JpaRepository<Region, Long> {
}
