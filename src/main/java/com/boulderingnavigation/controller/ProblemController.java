package com.boulderingnavigation.controller;

import com.boulderingnavigation.dto.ProblemDetailResponse;
import com.boulderingnavigation.service.ProblemQueryService;
import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/api/problems")
@RequiredArgsConstructor
public class ProblemController {

    private final ProblemQueryService problemQueryService;

    @GetMapping("/{id}")
    public ProblemDetailResponse detail(@PathVariable Long id) {
        return problemQueryService.getDetail(id);
    }
}
