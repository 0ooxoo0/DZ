package com.starbank.recommendation.rule;

import com.starbank.recommendation.domain.ProductType;
import com.starbank.recommendation.domain.TransactionType;
import com.starbank.recommendation.dto.RecommendationDto;
import com.starbank.recommendation.repository.RecommendationRepository;
import org.springframework.stereotype.Component;

import java.math.BigDecimal;
import java.util.Optional;
import java.util.UUID;

/**
 * Простой кредит.
 * <p>
 * Правила: нет продуктов типа CREDIT; сумма пополнений по DEBIT больше суммы трат по DEBIT;
 * сумма трат по DEBIT больше 100 000 ₽.
 */
@Component
public class SimpleCreditRuleSet implements RecommendationRuleSet {

    public static final UUID PRODUCT_ID = UUID.fromString("ab138afb-f3ba-4a93-b74f-0fcee86d447f");
    public static final String PRODUCT_NAME = "Простой кредит";

    private static final BigDecimal MIN_WITHDRAWALS = BigDecimal.valueOf(100_000);

    private static final String PRODUCT_TEXT = """
            Откройте мир выгодных кредитов с нами!

            Ищете способ быстро и без лишних хлопот получить нужную сумму? Тогда наш выгодный кредит — именно то, \
            что вам нужно! Мы предлагаем низкие процентные ставки, гибкие условия и индивидуальный подход \
            к каждому клиенту.

            Почему выбирают нас:

            Быстрое рассмотрение заявки. Мы ценим ваше время, поэтому процесс рассмотрения заявки занимает \
            всего несколько часов.

            Удобное оформление. Подать заявку на кредит можно онлайн на нашем сайте или в мобильном приложении.

            Широкий выбор кредитных продуктов. Мы предлагаем кредиты на различные цели: покупку недвижимости, \
            автомобиля, образование, лечение и многое другое.

            Не упустите возможность воспользоваться выгодными условиями кредитования от нашей компании!""";

    private final RecommendationRepository repository;

    public SimpleCreditRuleSet(RecommendationRepository repository) {
        this.repository = repository;
    }

    @Override
    public Optional<RecommendationDto> check(UUID userId) {
        boolean hasCredit = repository.hasProductOfType(userId, ProductType.CREDIT);

        BigDecimal debitDeposits = repository.sumByProductTypeAndTransactionType(
                userId, ProductType.DEBIT, TransactionType.DEPOSIT);
        BigDecimal debitWithdrawals = repository.sumByProductTypeAndTransactionType(
                userId, ProductType.DEBIT, TransactionType.WITHDRAW);

        boolean depositsExceedWithdrawals = debitDeposits.compareTo(debitWithdrawals) > 0;
        boolean withdrawalsAboveLimit = debitWithdrawals.compareTo(MIN_WITHDRAWALS) > 0;

        if (!hasCredit && depositsExceedWithdrawals && withdrawalsAboveLimit) {
            return Optional.of(new RecommendationDto(PRODUCT_NAME, PRODUCT_ID, PRODUCT_TEXT));
        }
        return Optional.empty();
    }
}
