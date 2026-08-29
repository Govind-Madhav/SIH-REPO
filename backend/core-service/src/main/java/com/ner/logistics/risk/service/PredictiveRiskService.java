package com.ner.logistics.risk.service;

import com.ner.logistics.risk.dto.*;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.messaging.simp.SimpMessagingTemplate;
import org.springframework.stereotype.Service;

import java.util.Map;

@Slf4j
@Service
@RequiredArgsConstructor
public class PredictiveRiskService {

    private final RiskEngineService riskEngineService;
    private final MlPredictionService mlPredictionService;
    private final SimpMessagingTemplate messagingTemplate;

    public HybridRiskIntelligenceResponse getHybridIntelligence(Double lat, Double lng) {
        RiskEvaluationRequest evalReq = RiskEvaluationRequest.builder()
                .latitude(lat)
                .longitude(lng)
                .rainfallMm24h(35.0)
                .build();

        RiskEvaluationResponse currentEval = riskEngineService.evaluateRealTimeRisk(evalReq);

        PredictiveRiskRequest predReq = PredictiveRiskRequest.builder()
                .latitude(lat)
                .longitude(lng)
                .predictionWindowHours(2)
                .build();

        MlPredictionResponse mlResponse = mlPredictionService.getPrediction(predReq);

        // STOMP WebSocket alert if predicted risk is CRITICAL or probability >= 0.80
        if (mlResponse.isAvailable() && (
                "CRITICAL".equalsIgnoreCase(mlResponse.getPredictedRiskLevel()) ||
                (mlResponse.getDisruptionProbability() != null && mlResponse.getDisruptionProbability() >= 0.80)
        )) {
            Map<String, Object> websocketAlert = Map.of(
                    "alertType", "PREDICTIVE_RISK_ALERT",
                    "riskLevel", mlResponse.getPredictedRiskLevel(),
                    "probability", mlResponse.getDisruptionProbability(),
                    "location", Map.of("latitude", lat, "longitude", lng),
                    "message", "High probability of logistics disruption within the next 2 hours."
            );
            messagingTemplate.convertAndSend("/topic/risk-alerts", websocketAlert);
            log.info("🚨 WebSocket Alert broadcasted for high ML disruption probability: {}", mlResponse.getDisruptionProbability());
        }

        return HybridRiskIntelligenceResponse.builder()
                .location(Map.of("latitude", lat, "longitude", lng))
                .currentAssessment(currentEval)
                .futurePrediction(mlResponse)
                .build();
    }
}
