package com.boulderingnavigation.controller;

import com.boulderingnavigation.dto.MountainSearchResponse;
import com.boulderingnavigation.service.MountainQueryService;
import java.util.List;
import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/api/mountains")
@RequiredArgsConstructor
public class MountainController {

    private final MountainQueryService mountainQueryService;

    @GetMapping
    public List<MountainSearchResponse> search(@RequestParam String q) {
        return mountainQueryService.search(q);
    }
}
