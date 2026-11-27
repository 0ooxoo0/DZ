package com.starbank.recommendation.repository;

import com.starbank.recommendation.domain.ProductType;
import com.starbank.recommendation.domain.TransactionType;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.stereotype.Repository;

import java.math.BigDecimal;
import java.util.UUID;

/**
 * Доступ к выданной банком БД. Используется только {@link JdbcTemplate} — сущностей и JPA нет.
 * <p>
 * Все правила рекомендаций сводятся к двум запросам, которые различаются только параметрами:
 * наличие у пользователя продукта заданного типа и сумма операций заданного вида по продуктам заданного типа.
 */
@Repository
public class RecommendationRepository {

    /** Пользователь является клиентом продукта, если по нему есть хотя бы одна операция. */
    private static final String HAS_PRODUCT_OF_TYPE = """
            SELECT COUNT(*)
            FROM transactions t
                     JOIN products p ON p.id = t.product_id
            WHERE t.user_id = ?
              AND p.type = ?
            """;

    /** Сумма операций пользователя одного вида по всем его продуктам одного типа. */
    private static final String SUM_BY_PRODUCT_TYPE_AND_TRANSACTION_TYPE = """
            SELECT COALESCE(SUM(t.amount), 0)
            FROM transactions t
                     JOIN products p ON p.id = t.product_id
            WHERE t.user_id = ?
              AND p.type = ?
              AND t.type = ?
            """;

    private final JdbcTemplate jdbcTemplate;

    public RecommendationRepository(JdbcTemplate jdbcTemplate) {
        this.jdbcTemplate = jdbcTemplate;
    }

    public boolean hasProductOfType(UUID userId, ProductType productType) {
        Long count = jdbcTemplate.queryForObject(
                HAS_PRODUCT_OF_TYPE, Long.class, userId, productType.name());
        return count != null && count > 0;
    }

    public BigDecimal sumByProductTypeAndTransactionType(UUID userId,
                                                         ProductType productType,
                                                         TransactionType transactionType) {
        BigDecimal sum = jdbcTemplate.queryForObject(
                SUM_BY_PRODUCT_TYPE_AND_TRANSACTION_TYPE, BigDecimal.class,
                userId, productType.name(), transactionType.name());
        return sum != null ? sum : BigDecimal.ZERO;
    }
}
