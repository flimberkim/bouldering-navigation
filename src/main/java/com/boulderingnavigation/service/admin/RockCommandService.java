package com.boulderingnavigation.service.admin;

import com.boulderingnavigation.domain.Mountain;
import com.boulderingnavigation.domain.Rock;
import com.boulderingnavigation.dto.admin.RockRequest;
import com.boulderingnavigation.dto.admin.RockResponse;
import com.boulderingnavigation.repository.MountainRepository;
import com.boulderingnavigation.repository.RockRepository;
import jakarta.persistence.EntityNotFoundException;
import java.util.List;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
@RequiredArgsConstructor
@Transactional
public class RockCommandService {

    private final RockRepository rockRepository;
    private final MountainRepository mountainRepository;

    @Transactional(readOnly = true)
    public List<RockResponse> findAll() {
        return rockRepository.findAll().stream().map(this::toResponse).toList();
    }

    public RockResponse create(RockRequest request) {
        Mountain mountain = getMountainOrThrow(request.mountainId());
        Rock rock = rockRepository.save(Rock.builder().name(request.name()).mountain(mountain).build());
        rockRepository.flush();
        return toResponse(rock);
    }

    public RockResponse update(Long id, RockRequest request) {
        Rock rock = getOrThrow(id);
        Mountain mountain = getMountainOrThrow(request.mountainId());
        rock.update(request.name(), mountain);
        rockRepository.flush();
        return toResponse(rock);
    }

    public void delete(Long id) {
        Rock rock = getOrThrow(id);
        rockRepository.delete(rock);
        rockRepository.flush();
    }

    private Rock getOrThrow(Long id) {
        return rockRepository.findById(id)
                .orElseThrow(() -> new EntityNotFoundException("Rock not found: " + id));
    }

    private Mountain getMountainOrThrow(Long mountainId) {
        return mountainRepository.findById(mountainId)
                .orElseThrow(() -> new EntityNotFoundException("Mountain not found: " + mountainId));
    }

    private RockResponse toResponse(Rock rock) {
        return new RockResponse(rock.getId(), rock.getName(), rock.getMountain().getId(), rock.getMountain().getName());
    }
}
