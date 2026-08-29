package com.ner.logistics.risk.client;

import com.ner.logistics.risk.config.RiskConfig;
import com.ner.logistics.risk.dto.MlPredictionRequest;
import com.ner.logistics.risk.dto.MlPredictionResponse;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Component;
import org.springframework.web.client.RestTemplate;

@Slf4j
@Component
@RequiredArgsConstructor
public class HttpMlPredictionClient implements MlPredictionClient {

    private final RiskConfig riskConfig;
    private final RestTemplate restTemplate;

    @Override
    public MlPredictionResponse predict(MlPredictionRequest request) {
        if (!riskConfig.isEnabled()) {
            return MlPredictionResponse.builder()
                    .available(false)
                    .message("ML prediction model is not currently available.")
                    .build();
        }

        try {
            String url = riskConfig.getBaseUrl() + "/api/predict";
            ResponseEntity<MlPredictionResponse> response = restTemplate.postForEntity(url, request, MlPredictionResponse.class);
            if (response.getStatusCode().is2xxSuccessful() && response.getBody() != null) {
                return response.getBody();
            }
        } catch (Exception e) {
            log.warn("ML Prediction Service unavailable at {}: {}", riskConfig.getBaseUrl(), e.getMessage());
        }

        return MlPredictionResponse.builder()
                .available(false)
                .message("ML prediction model is not currently available.")
                .build();
    }
}
