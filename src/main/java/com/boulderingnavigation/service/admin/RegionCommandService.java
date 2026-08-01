package com.boulderingnavigation.service.admin;

import com.boulderingnavigation.domain.Region;
import com.boulderingnavigation.dto.admin.RegionRequest;
import com.boulderingnavigation.dto.admin.RegionResponse;
import com.boulderingnavigation.repository.RegionRepository;
import jakarta.persistence.EntityNotFoundException;
import java.util.List;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
@RequiredArgsConstructor
@Transactional
public class RegionCommandService {

    private final RegionRepository regionRepository;

    @Transactional(readOnly = true)
    public List<RegionResponse> findAll() {
        return regionRepository.findAll().stream().map(this::toResponse).toList();
    }

    public RegionResponse create(RegionRequest request) {
        Region region = regionRepository.save(Region.builder().name(request.name()).build());
        regionRepository.flush();
        return toResponse(region);
    }

    public RegionResponse update(Long id, RegionRequest request) {
        Region region = getOrThrow(id);
        region.rename(request.name());
        regionRepository.flush();
        return toResponse(region);
    }

    public void delete(Long id) {
        Region region = getOrThrow(id);
        regionRepository.delete(region);
        regionRepository.flush();
    }

    private Region getOrThrow(Long id) {
        return regionRepository.findById(id)
                .orElseThrow(() -> new EntityNotFoundException("Region not found: " + id));
    }

    private RegionResponse toResponse(Region region) {
        return new RegionResponse(region.getId(), region.getName());
    }
}
