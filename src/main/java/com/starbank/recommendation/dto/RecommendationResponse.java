package com.starbank.recommendation.dto;

import com.fasterxml.jackson.annotation.JsonProperty;

import java.util.List;
import java.util.UUID;

/**
 * Тело ответа GET /recommendation/{user_id}. Имя поля {@code user_id} задано требованиями API.
 */
public record RecommendationResponse(
        @JsonProperty("user_id") UUID userId,
        List<RecommendationDto> recommendations) {
}
