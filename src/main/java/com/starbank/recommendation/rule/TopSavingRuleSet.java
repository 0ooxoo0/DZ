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
 * Top Saving.
 * <p>
 * Правила: есть хотя бы один продукт типа DEBIT; сумма пополнений по DEBIT или по SAVING не меньше 50 000 ₽;
 * сумма пополнений по DEBIT больше суммы трат по DEBIT.
 */
@Component
public class TopSavingRuleSet implements RecommendationRuleSet {

    public static final UUID PRODUCT_ID = UUID.fromString("59efc529-2fff-41af-baff-90ccd7402925");
    public static final String PRODUCT_NAME = "Top Saving";

    private static final BigDecimal MIN_DEPOSIT = BigDecimal.valueOf(50_000);

    private static final String PRODUCT_TEXT = """
            Откройте свою собственную «Копилку» с нашим банком! «Копилка» — это уникальный банковский инструмент, \
            который поможет вам легко и удобно накапливать деньги на важные цели. Больше никаких забытых чеков \
            и потерянных квитанций — всё под контролем!

            Преимущества «Копилки»:

            Накопление средств на конкретные цели. Установите лимит и срок накопления, и банк будет автоматически \
            переводить определенную сумму на ваш счет.

            Прозрачность и контроль. Отслеживайте свои доходы и расходы, контролируйте процесс накопления \
            и корректируйте стратегию при необходимости.

            Безопасность и надежность. Ваши средства находятся под защитой банка, а доступ к ним возможен только \
            через мобильное приложение или интернет-банкинг.

            Начните использовать «Копилку» уже сегодня и станьте ближе к своим финансовым целям!""";

    private final RecommendationRepository repository;

    public TopSavingRuleSet(RecommendationRepository repository) {
        this.repository = repository;
    }

    @Override
    public Optional<RecommendationDto> check(UUID userId) {
        boolean hasDebit = repository.hasProductOfType(userId, ProductType.DEBIT);

        BigDecimal debitDeposits = repository.sumByProductTypeAndTransactionType(
                userId, ProductType.DEBIT, TransactionType.DEPOSIT);
        BigDecimal savingDeposits = repository.sumByProductTypeAndTransactionType(
                userId, ProductType.SAVING, TransactionType.DEPOSIT);

        boolean enoughDeposits = debitDeposits.compareTo(MIN_DEPOSIT) >= 0
                || savingDeposits.compareTo(MIN_DEPOSIT) >= 0;

        BigDecimal debitWithdrawals = repository.sumByProductTypeAndTransactionType(
                userId, ProductType.DEBIT, TransactionType.WITHDRAW);
        boolean depositsExceedWithdrawals = debitDeposits.compareTo(debitWithdrawals) > 0;

        if (hasDebit && enoughDeposits && depositsExceedWithdrawals) {
            return Optional.of(new RecommendationDto(PRODUCT_NAME, PRODUCT_ID, PRODUCT_TEXT));
        }
        return Optional.empty();
    }
}
