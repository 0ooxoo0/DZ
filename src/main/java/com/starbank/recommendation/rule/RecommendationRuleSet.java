package com.starbank.recommendation.rule;

import com.starbank.recommendation.dto.RecommendationDto;

import java.util.Optional;
import java.util.UUID;

/**
 * Набор правил одной рекомендации. Реализации помечены {@code @Component} — сервис получает их списком.
 */
public interface RecommendationRuleSet {

    /**
     * @return рекомендованный продукт, если пользователь удовлетворяет всем правилам набора, иначе {@link Optional#empty()}
     */
    Optional<RecommendationDto> check(UUID userId);
}
