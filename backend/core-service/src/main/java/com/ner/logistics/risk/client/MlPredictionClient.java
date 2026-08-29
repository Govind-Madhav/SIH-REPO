package com.ner.logistics.risk.client;

import com.ner.logistics.risk.dto.MlPredictionRequest;
import com.ner.logistics.risk.dto.MlPredictionResponse;

public interface MlPredictionClient {
    MlPredictionResponse predict(MlPredictionRequest request);
}
