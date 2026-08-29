package com.ner.logistics.risk.service;

import org.springframework.stereotype.Component;

@Component
public class RiskLevelResolver {

    public String resolveFromProbability(double probability) {
        if (probability >= 0.81) {
            return "CRITICAL";
        } else if (probability >= 0.61) {
            return "HIGH";
        } else if (probability >= 0.31) {
            return "MEDIUM";
        } else {
            return "LOW";
        }
    }

    public String resolveFromScore(int score) {
        if (score >= 81) {
            return "CRITICAL";
        } else if (score >= 61) {
            return "HIGH";
        } else if (score >= 31) {
            return "MEDIUM";
        } else {
            return "LOW";
        }
    }
}
