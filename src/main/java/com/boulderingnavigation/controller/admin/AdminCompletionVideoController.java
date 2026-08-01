package com.boulderingnavigation.controller.admin;

import com.boulderingnavigation.dto.admin.CompletionVideoRequest;
import com.boulderingnavigation.dto.admin.CompletionVideoResponse;
import com.boulderingnavigation.service.admin.CompletionVideoCommandService;
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
@RequestMapping("/api/admin/videos")
@RequiredArgsConstructor
public class AdminCompletionVideoController {

    private final CompletionVideoCommandService completionVideoCommandService;

    @GetMapping
    public List<CompletionVideoResponse> findAll() {
        return completionVideoCommandService.findAll();
    }

    @PostMapping
    public ResponseEntity<CompletionVideoResponse> create(@RequestBody @Valid CompletionVideoRequest request) {
        return ResponseEntity.status(HttpStatus.CREATED).body(completionVideoCommandService.create(request));
    }

    @PutMapping("/{id}")
    public CompletionVideoResponse update(@PathVariable Long id, @RequestBody @Valid CompletionVideoRequest request) {
        return completionVideoCommandService.update(id, request);
    }

    @DeleteMapping("/{id}")
    public ResponseEntity<Void> delete(@PathVariable Long id) {
        completionVideoCommandService.delete(id);
        return ResponseEntity.noContent().build();
    }
}
