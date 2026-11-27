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
 * Invest 500.
 * <p>
 * Правила: есть хотя бы один продукт типа DEBIT; нет продуктов типа INVEST;
 * сумма пополнений по продуктам типа SAVING больше 1000 ₽.
 */
@Component
public class Invest500RuleSet implements RecommendationRuleSet {

    public static final UUID PRODUCT_ID = UUID.fromString("147f6a0f-3b91-413b-ab99-87f081d60d5a");
    public static final String PRODUCT_NAME = "Invest 500";

    private static final BigDecimal MIN_SAVING_DEPOSIT = BigDecimal.valueOf(1000);

    private static final String PRODUCT_TEXT = """
            Откройте свой путь к успеху с индивидуальным инвестиционным счетом (ИИС) от нашего банка! \
            Воспользуйтесь налоговыми льготами и начните инвестировать с умом. Пополните счет до конца года \
            и получите выгоду в виде вычета на взнос в следующем налоговом периоде. Не упустите возможность \
            разнообразить свой портфель, снизить риски и следить за актуальными рыночными тенденциями. \
            Откройте ИИС сегодня и станьте ближе к финансовой независимости!""";

    private final RecommendationRepository repository;

    public Invest500RuleSet(RecommendationRepository repository) {
        this.repository = repository;
    }

    @Override
    public Optional<RecommendationDto> check(UUID userId) {
        boolean hasDebit = repository.hasProductOfType(userId, ProductType.DEBIT);
        boolean hasInvest = repository.hasProductOfType(userId, ProductType.INVEST);
        BigDecimal savingDeposits = repository.sumByProductTypeAndTransactionType(
                userId, ProductType.SAVING, TransactionType.DEPOSIT);

        if (hasDebit && !hasInvest && savingDeposits.compareTo(MIN_SAVING_DEPOSIT) > 0) {
            return Optional.of(new RecommendationDto(PRODUCT_NAME, PRODUCT_ID, PRODUCT_TEXT));
        }
        return Optional.empty();
    }
}
