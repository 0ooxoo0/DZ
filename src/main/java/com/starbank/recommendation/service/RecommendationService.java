package com.starbank.recommendation.service;

import com.starbank.recommendation.dto.RecommendationDto;
import com.starbank.recommendation.rule.RecommendationRuleSet;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.Optional;
import java.util.UUID;

/**
 * Сервис рекомендаций: опрашивает все наборы правил и собирает непустые результаты.
 * Новый продукт добавляется в систему регистрацией ещё одного бина {@link RecommendationRuleSet} — код сервиса не меняется.
 */
@Service
public class RecommendationService {

    private final List<RecommendationRuleSet> ruleSets;

    public RecommendationService(List<RecommendationRuleSet> ruleSets) {
        this.ruleSets = ruleSets;
    }

    public List<RecommendationDto> getRecommendations(UUID userId) {
        return ruleSets.stream()
                .map(ruleSet -> ruleSet.check(userId))
                .flatMap(Optional::stream)
                .toList();
    }
}
