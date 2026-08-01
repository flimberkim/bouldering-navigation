package com.boulderingnavigation.service.admin;

import com.boulderingnavigation.domain.Mountain;
import com.boulderingnavigation.domain.Region;
import com.boulderingnavigation.dto.admin.MountainRequest;
import com.boulderingnavigation.dto.admin.MountainResponse;
import com.boulderingnavigation.repository.MountainRepository;
import com.boulderingnavigation.repository.RegionRepository;
import jakarta.persistence.EntityNotFoundException;
import java.util.List;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
@RequiredArgsConstructor
@Transactional
public class MountainCommandService {

    private final MountainRepository mountainRepository;
    private final RegionRepository regionRepository;

    @Transactional(readOnly = true)
    public List<MountainResponse> findAll() {
        return mountainRepository.findAll().stream().map(this::toResponse).toList();
    }

    public MountainResponse create(MountainRequest request) {
        Region region = getRegionOrThrow(request.regionId());
        Mountain mountain = mountainRepository.save(Mountain.builder().name(request.name()).region(region).build());
        mountainRepository.flush();
        return toResponse(mountain);
    }

    public MountainResponse update(Long id, MountainRequest request) {
        Mountain mountain = getOrThrow(id);
        Region region = getRegionOrThrow(request.regionId());
        mountain.update(request.name(), region);
        mountainRepository.flush();
        return toResponse(mountain);
    }

    public void delete(Long id) {
        Mountain mountain = getOrThrow(id);
        mountainRepository.delete(mountain);
        mountainRepository.flush();
    }

    private Mountain getOrThrow(Long id) {
        return mountainRepository.findById(id)
                .orElseThrow(() -> new EntityNotFoundException("Mountain not found: " + id));
    }

    private Region getRegionOrThrow(Long regionId) {
        return regionRepository.findById(regionId)
                .orElseThrow(() -> new EntityNotFoundException("Region not found: " + regionId));
    }

    private MountainResponse toResponse(Mountain mountain) {
        return new MountainResponse(mountain.getId(), mountain.getName(), mountain.getRegion().getId(), mountain.getRegion().getName());
    }
}
