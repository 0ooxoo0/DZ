package com.starbank.recommendation.repository;

import com.starbank.recommendation.domain.ProductType;
import com.starbank.recommendation.domain.TransactionType;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.jdbc.JdbcTest;
import org.springframework.jdbc.core.JdbcTemplate;

import java.math.BigDecimal;
import java.util.UUID;

import static org.assertj.core.api.Assertions.assertThat;

/**
 * Проверяет SQL-запросы на тестовом наборе данных (src/test/resources/data.sql).
 */
@JdbcTest
class RecommendationRepositoryTest {

    private static final UUID USER_WITH_DEBIT_AND_SAVING = UUID.fromString("cd515076-5d8a-44be-930e-8d4fcb79f42d");
    private static final UUID USER_WITHOUT_PRODUCTS = UUID.fromString("00000000-0000-0000-0000-000000000004");

    @Autowired
    private JdbcTemplate jdbcTemplate;

    private RecommendationRepository repository;

    @BeforeEach
    void setUp() {
        repository = new RecommendationRepository(jdbcTemplate);
    }

    @Test
    void findsProductOfType() {
        assertThat(repository.hasProductOfType(USER_WITH_DEBIT_AND_SAVING, ProductType.DEBIT)).isTrue();
        assertThat(repository.hasProductOfType(USER_WITH_DEBIT_AND_SAVING, ProductType.CREDIT)).isFalse();
    }

    @Test
    void returnsFalseForUserWithoutTransactions() {
        assertThat(repository.hasProductOfType(USER_WITHOUT_PRODUCTS, ProductType.DEBIT)).isFalse();
    }

    @Test
    void sumsTransactionsByProductTypeAndTransactionType() {
        BigDecimal savingDeposits = repository.sumByProductTypeAndTransactionType(
                USER_WITH_DEBIT_AND_SAVING, ProductType.SAVING, TransactionType.DEPOSIT);
        BigDecimal debitDeposits = repository.sumByProductTypeAndTransactionType(
                USER_WITH_DEBIT_AND_SAVING, ProductType.DEBIT, TransactionType.DEPOSIT);

        assertThat(savingDeposits).isEqualByComparingTo("5000.00");
        assertThat(debitDeposits).isEqualByComparingTo("1000.00");
    }

    @Test
    void returnsZeroWhenThereAreNoMatchingTransactions() {
        BigDecimal creditWithdrawals = repository.sumByProductTypeAndTransactionType(
                USER_WITH_DEBIT_AND_SAVING, ProductType.CREDIT, TransactionType.WITHDRAW);

        assertThat(creditWithdrawals).isEqualByComparingTo(BigDecimal.ZERO);
    }
}
