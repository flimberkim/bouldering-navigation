package com.boulderingnavigation.controller.admin;

import com.boulderingnavigation.dto.admin.RockRequest;
import com.boulderingnavigation.dto.admin.RockResponse;
import com.boulderingnavigation.service.admin.RockCommandService;
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
@RequestMapping("/api/admin/rocks")
@RequiredArgsConstructor
public class AdminRockController {

    private final RockCommandService rockCommandService;

    @GetMapping
    public List<RockResponse> findAll() {
        return rockCommandService.findAll();
    }

    @PostMapping
    public ResponseEntity<RockResponse> create(@RequestBody @Valid RockRequest request) {
        return ResponseEntity.status(HttpStatus.CREATED).body(rockCommandService.create(request));
    }

    @PutMapping("/{id}")
    public RockResponse update(@PathVariable Long id, @RequestBody @Valid RockRequest request) {
        return rockCommandService.update(id, request);
    }

    @DeleteMapping("/{id}")
    public ResponseEntity<Void> delete(@PathVariable Long id) {
        rockCommandService.delete(id);
        return ResponseEntity.noContent().build();
    }
}
