package com.starbank.recommendation.rule;

import com.starbank.recommendation.domain.ProductType;
import com.starbank.recommendation.domain.TransactionType;
import com.starbank.recommendation.dto.RecommendationDto;
import com.starbank.recommendation.repository.RecommendationRepository;
import org.junit.jupiter.api.Test;

import java.math.BigDecimal;
import java.util.Optional;
import java.util.UUID;

import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.when;

/**
 * Граничные значения правил рекомендаций. Запросы к БД замоканы: проверяется только логика сравнений.
 */
class RecommendationRuleSetTest {

    private static final UUID USER_ID = UUID.randomUUID();

    private final RecommendationRepository repository = mock(RecommendationRepository.class);

    @Test
    void invest500RequiresDebitWithoutInvestAndSavingDepositsAbove1000() {
        when(repository.hasProductOfType(USER_ID, ProductType.DEBIT)).thenReturn(true);
        when(repository.hasProductOfType(USER_ID, ProductType.INVEST)).thenReturn(false);
        when(repository.sumByProductTypeAndTransactionType(USER_ID, ProductType.SAVING, TransactionType.DEPOSIT))
                .thenReturn(BigDecimal.valueOf(1000));
        assertThat(new Invest500RuleSet(repository).check(USER_ID)).isEmpty();

        when(repository.sumByProductTypeAndTransactionType(USER_ID, ProductType.SAVING, TransactionType.DEPOSIT))
                .thenReturn(BigDecimal.valueOf(1000.01));
        Optional<RecommendationDto> recommendation = new Invest500RuleSet(repository).check(USER_ID);
        assertThat(recommendation).isPresent();
        assertThat(recommendation.orElseThrow().id()).isEqualTo(Invest500RuleSet.PRODUCT_ID);
    }

    @Test
    void invest500IsNotRecommendedWhenUserAlreadyHasInvestProduct() {
        when(repository.hasProductOfType(USER_ID, ProductType.DEBIT)).thenReturn(true);
        when(repository.hasProductOfType(USER_ID, ProductType.INVEST)).thenReturn(true);
        when(repository.sumByProductTypeAndTransactionType(any(), any(), any()))
                .thenReturn(BigDecimal.valueOf(1_000_000));

        assertThat(new Invest500RuleSet(repository).check(USER_ID)).isEmpty();
    }

    @Test
    void topSavingRequiresDepositsAtLeast50000AndAboveWithdrawals() {
        when(repository.hasProductOfType(USER_ID, ProductType.DEBIT)).thenReturn(true);
        when(repository.sumByProductTypeAndTransactionType(USER_ID, ProductType.DEBIT, TransactionType.DEPOSIT))
                .thenReturn(BigDecimal.valueOf(49_999));
        when(repository.sumByProductTypeAndTransactionType(USER_ID, ProductType.SAVING, TransactionType.DEPOSIT))
                .thenReturn(BigDecimal.valueOf(49_999));
        when(repository.sumByProductTypeAndTransactionType(USER_ID, ProductType.DEBIT, TransactionType.WITHDRAW))
                .thenReturn(BigDecimal.valueOf(10_000));
        assertThat(new TopSavingRuleSet(repository).check(USER_ID)).isEmpty();

        when(repository.sumByProductTypeAndTransactionType(USER_ID, ProductType.DEBIT, TransactionType.DEPOSIT))
                .thenReturn(BigDecimal.valueOf(50_000));
        when(repository.sumByProductTypeAndTransactionType(USER_ID, ProductType.DEBIT, TransactionType.WITHDRAW))
                .thenReturn(BigDecimal.valueOf(50_000));
        assertThat(new TopSavingRuleSet(repository).check(USER_ID)).isEmpty();

        when(repository.sumByProductTypeAndTransactionType(USER_ID, ProductType.DEBIT, TransactionType.WITHDRAW))
                .thenReturn(BigDecimal.valueOf(49_999.99));
        assertThat(new TopSavingRuleSet(repository).check(USER_ID)).isPresent();
    }

    @Test
    void topSavingAcceptsSavingDepositsAsAlternativeToDebitDeposits() {
        when(repository.hasProductOfType(USER_ID, ProductType.DEBIT)).thenReturn(true);
        when(repository.sumByProductTypeAndTransactionType(USER_ID, ProductType.DEBIT, TransactionType.DEPOSIT))
                .thenReturn(BigDecimal.valueOf(1_000));
        when(repository.sumByProductTypeAndTransactionType(USER_ID, ProductType.SAVING, TransactionType.DEPOSIT))
                .thenReturn(BigDecimal.valueOf(50_000));
        when(repository.sumByProductTypeAndTransactionType(USER_ID, ProductType.DEBIT, TransactionType.WITHDRAW))
                .thenReturn(BigDecimal.valueOf(999));

        assertThat(new TopSavingRuleSet(repository).check(USER_ID)).isPresent();
    }

    @Test
    void simpleCreditRequiresNoCreditAndWithdrawalsAbove100000() {
        when(repository.hasProductOfType(USER_ID, ProductType.CREDIT)).thenReturn(false);
        when(repository.sumByProductTypeAndTransactionType(USER_ID, ProductType.DEBIT, TransactionType.DEPOSIT))
                .thenReturn(BigDecimal.valueOf(100_001));
        when(repository.sumByProductTypeAndTransactionType(USER_ID, ProductType.DEBIT, TransactionType.WITHDRAW))
                .thenReturn(BigDecimal.valueOf(100_000));
        assertThat(new SimpleCreditRuleSet(repository).check(USER_ID)).isEmpty();

        when(repository.sumByProductTypeAndTransactionType(USER_ID, ProductType.DEBIT, TransactionType.WITHDRAW))
                .thenReturn(BigDecimal.valueOf(100_000.01));
        assertThat(new SimpleCreditRuleSet(repository).check(USER_ID)).isPresent();
    }

    @Test
    void simpleCreditIsNotRecommendedWhenUserAlreadyHasCreditProduct() {
        when(repository.hasProductOfType(USER_ID, ProductType.CREDIT)).thenReturn(true);
        when(repository.sumByProductTypeAndTransactionType(USER_ID, ProductType.DEBIT, TransactionType.DEPOSIT))
                .thenReturn(BigDecimal.valueOf(500_000));
        when(repository.sumByProductTypeAndTransactionType(USER_ID, ProductType.DEBIT, TransactionType.WITHDRAW))
                .thenReturn(BigDecimal.valueOf(200_000));

        assertThat(new SimpleCreditRuleSet(repository).check(USER_ID)).isEmpty();
    }
}
