package com.boulderingnavigation.controller.admin;

import com.boulderingnavigation.dto.admin.MountainRequest;
import com.boulderingnavigation.dto.admin.MountainResponse;
import com.boulderingnavigation.service.admin.MountainCommandService;
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
@RequestMapping("/api/admin/mountains")
@RequiredArgsConstructor
public class AdminMountainController {

    private final MountainCommandService mountainCommandService;

    @GetMapping
    public List<MountainResponse> findAll() {
        return mountainCommandService.findAll();
    }

    @PostMapping
    public ResponseEntity<MountainResponse> create(@RequestBody @Valid MountainRequest request) {
        return ResponseEntity.status(HttpStatus.CREATED).body(mountainCommandService.create(request));
    }

    @PutMapping("/{id}")
    public MountainResponse update(@PathVariable Long id, @RequestBody @Valid MountainRequest request) {
        return mountainCommandService.update(id, request);
    }

    @DeleteMapping("/{id}")
    public ResponseEntity<Void> delete(@PathVariable Long id) {
        mountainCommandService.delete(id);
        return ResponseEntity.noContent().build();
    }
}
