package com.ner.logistics.risk.service;

import com.ner.logistics.risk.client.MlPredictionClient;
import com.ner.logistics.risk.dto.MlPredictionRequest;
import com.ner.logistics.risk.dto.MlPredictionResponse;
import com.ner.logistics.risk.dto.PredictiveRiskRequest;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.util.Map;

@Service
@RequiredArgsConstructor
public class MlPredictionService {

    private final RiskFeatureService featureService;
    private final MlPredictionClient mlPredictionClient;

    public MlPredictionResponse getPrediction(PredictiveRiskRequest request) {
        Map<String, Object> featureVector = featureService.collectFeatureVector(
                request.getLatitude(),
                request.getLongitude(),
                30.0
        );

        MlPredictionRequest clientRequest = MlPredictionRequest.builder()
                .latitude(request.getLatitude())
                .longitude(request.getLongitude())
                .predictionWindowHours(request.getPredictionWindowHours() != null ? request.getPredictionWindowHours() : 2)
                .features(featureVector)
                .build();

        return mlPredictionClient.predict(clientRequest);
    }
}
