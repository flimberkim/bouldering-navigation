package com.boulderingnavigation.controller.admin;

import com.boulderingnavigation.dto.admin.RegionRequest;
import com.boulderingnavigation.dto.admin.RegionResponse;
import com.boulderingnavigation.service.admin.RegionCommandService;
import jakarta.validation.Valid;
import java.util.List;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/api/admin/regions")
@RequiredArgsConstructor
public class AdminRegionController {

    private final RegionCommandService regionCommandService;

    @GetMapping
    public List<RegionResponse> findAll() {
        return regionCommandService.findAll();
    }

    @PostMapping
    public ResponseEntity<RegionResponse> create(@RequestBody @Valid RegionRequest request) {
        return ResponseEntity.status(HttpStatus.CREATED).body(regionCommandService.create(request));
    }

    @PutMapping("/{id}")
    public RegionResponse update(@PathVariable Long id, @RequestBody @Valid RegionRequest request) {
        return regionCommandService.update(id, request);
    }

    @DeleteMapping("/{id}")
    public ResponseEntity<Void> delete(@PathVariable Long id) {
        regionCommandService.delete(id);
        return ResponseEntity.noContent().build();
    }
}
