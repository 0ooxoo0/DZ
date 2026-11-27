package com.starbank.recommendation.controller;

import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.test.web.client.TestRestTemplate;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;

import static org.assertj.core.api.Assertions.assertThat;

/**
 * Сквозная проверка REST API на in-memory БД с тестовыми данными.
 */
@SpringBootTest(webEnvironment = SpringBootTest.WebEnvironment.RANDOM_PORT)
class RecommendationControllerIntegrationTest {

    private static final String INVEST_500_ID = "147f6a0f-3b91-413b-ab99-87f081d60d5a";

    @Autowired
    private TestRestTemplate restTemplate;

    @Test
    void returnsRecommendationsForUserWithMatchingRules() {
        ResponseEntity<String> response = restTemplate.getForEntity(
                "/recommendation/{user_id}", String.class, "cd515076-5d8a-44be-930e-8d4fcb79f42d");

        assertThat(response.getStatusCode()).isEqualTo(HttpStatus.OK);
        assertThat(response.getHeaders().getContentType()).hasToString("application/json");
        assertThat(response.getBody())
                .contains("\"user_id\":\"cd515076-5d8a-44be-930e-8d4fcb79f42d\"")
                .contains(INVEST_500_ID)
                .contains("Invest 500");
    }

    @Test
    void returnsEmptyRecommendationsWhenNothingMatched() {
        ResponseEntity<String> response = restTemplate.getForEntity(
                "/recommendation/{user_id}", String.class, "00000000-0000-0000-0000-000000000004");

        assertThat(response.getStatusCode()).isEqualTo(HttpStatus.OK);
        assertThat(response.getBody())
                .contains("\"user_id\":\"00000000-0000-0000-0000-000000000004\"")
                .contains("\"recommendations\":[]");
    }

    @Test
    void rejectsMalformedUserId() {
        ResponseEntity<String> response = restTemplate.getForEntity(
                "/recommendation/{user_id}", String.class, "not-a-uuid");

        assertThat(response.getStatusCode()).isEqualTo(HttpStatus.BAD_REQUEST);
    }
}
