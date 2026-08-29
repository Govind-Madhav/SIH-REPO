package com.ner.logistics.decision;

import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.List;

@RestController
@RequestMapping("/api/decisions")
@RequiredArgsConstructor
public class DecisionController {

    private final LogisticsDecisionService logisticsDecisionService;

    @GetMapping("/recommendations")
    public ResponseEntity<List<DecisionRecommendationDto>> getRecommendations() {
        return ResponseEntity.ok(logisticsDecisionService.getRecommendations());
    }
}
