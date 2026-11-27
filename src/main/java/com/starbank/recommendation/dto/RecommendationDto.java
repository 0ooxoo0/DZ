package com.starbank.recommendation.dto;

import java.util.UUID;

/**
 * Продукт, рекомендуемый пользователю.
 *
 * @param name имя продукта
 * @param id   идентификатор продукта
 * @param text текстовое описание продукта
 */
public record RecommendationDto(String name, UUID id, String text) {
}
