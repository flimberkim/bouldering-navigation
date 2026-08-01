package com.boulderingnavigation.controller.admin;

import com.boulderingnavigation.dto.admin.ProblemRequest;
import com.boulderingnavigation.dto.admin.ProblemResponse;
import com.boulderingnavigation.service.admin.ProblemCommandService;
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
@RequestMapping("/api/admin/problems")
@RequiredArgsConstructor
public class AdminProblemController {

    private final ProblemCommandService problemCommandService;

    @GetMapping
    public List<ProblemResponse> findAll() {
        return problemCommandService.findAll();
    }

    @PostMapping
    public ResponseEntity<ProblemResponse> create(@RequestBody @Valid ProblemRequest request) {
        return ResponseEntity.status(HttpStatus.CREATED).body(problemCommandService.create(request));
    }

    @PutMapping("/{id}")
    public ProblemResponse update(@PathVariable Long id, @RequestBody @Valid ProblemRequest request) {
        return problemCommandService.update(id, request);
    }

    @DeleteMapping("/{id}")
    public ResponseEntity<Void> delete(@PathVariable Long id) {
        problemCommandService.delete(id);
        return ResponseEntity.noContent().build();
    }
}
