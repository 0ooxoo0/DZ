package com.starbank.recommendation.service;

import com.starbank.recommendation.dto.RecommendationDto;
import com.starbank.recommendation.rule.RecommendationRuleSet;
import org.junit.jupiter.api.Test;

import java.util.List;
import java.util.Optional;
import java.util.UUID;

import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.when;

class RecommendationServiceTest {

    private static final UUID USER_ID = UUID.fromString("cd515076-5d8a-44be-930e-8d4fcb79f42d");

    private final RecommendationRuleSet firstRuleSet = mock(RecommendationRuleSet.class);
    private final RecommendationRuleSet secondRuleSet = mock(RecommendationRuleSet.class);

    @Test
    void returnsRecommendationsOfRuleSetsThatMatched() {
        RecommendationDto expected = new RecommendationDto("Invest 500", UUID.randomUUID(), "text");
        when(firstRuleSet.check(USER_ID)).thenReturn(Optional.empty());
        when(secondRuleSet.check(USER_ID)).thenReturn(Optional.of(expected));

        List<RecommendationDto> recommendations =
                new RecommendationService(List.of(firstRuleSet, secondRuleSet)).getRecommendations(USER_ID);

        assertThat(recommendations).containsExactly(expected);
    }

    @Test
    void returnsEmptyListWhenNoRuleSetMatched() {
        when(firstRuleSet.check(USER_ID)).thenReturn(Optional.empty());
        when(secondRuleSet.check(USER_ID)).thenReturn(Optional.empty());

        List<RecommendationDto> recommendations =
                new RecommendationService(List.of(firstRuleSet, secondRuleSet)).getRecommendations(USER_ID);

        assertThat(recommendations).isEmpty();
    }
}
