package com.starbank.recommendation.controller;

import com.starbank.recommendation.dto.RecommendationResponse;
import com.starbank.recommendation.service.RecommendationService;
import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.server.ResponseStatusException;

import java.util.UUID;

/**
 * REST API сервиса рекомендаций.
 */
@RestController
public class RecommendationController {

    private final RecommendationService recommendationService;

    public RecommendationController(RecommendationService recommendationService) {
        this.recommendationService = recommendationService;
    }

    /**
     * @param userId идентификатор пользователя в формате UUID
     * @return 200 OK с рекомендациями пользователя; пустой список, если рекомендаций нет
     */
    @GetMapping("/recommendation/{user_id}")
    public RecommendationResponse getRecommendations(@PathVariable("user_id") String userId) {
        UUID id = parseUserId(userId);
        return new RecommendationResponse(id, recommendationService.getRecommendations(id));
    }

    private UUID parseUserId(String userId) {
        try {
            return UUID.fromString(userId);
        } catch (IllegalArgumentException e) {
            throw new ResponseStatusException(HttpStatus.BAD_REQUEST,
                    "user_id must be a valid UUID, got: " + userId);
        }
    }
}
