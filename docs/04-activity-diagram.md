# Диаграмма поведения (Activity Diagram)

Сценарий: получение рекомендаций для клиента.

```mermaid
flowchart TD
    Start([Сотрудник банка]) --> Request["GET /recommendation/{user_id}"]
    Request --> Parse{"user_id — корректный UUID?"}
    Parse -->|нет| BadRequest["400 Bad Request<br/>тело: описание ошибки"]
    BadRequest --> Stop1([Конец])

    Parse -->|да| Init["recommendations = пустой список"]
    Init --> Loop{"Есть необработанный<br/>RecommendationRuleSet?"}
    Loop -->|нет| Respond["200 OK<br/>{user_id, recommendations}"]
    Respond --> Stop2([Конец])

    Loop -->|да| Take["Взять следующий набор правил"]
    Take --> Check["Проверить правила набора<br/>через RecommendationRepository (SQL)"]

    Check --> Match{"Все правила<br/>выполнены?"}
    Match -->|нет| Loop
    Match -->|да| Add["Добавить продукт<br/>(name, id, text) в recommendations"]
    Add --> Loop
```

## Проверка правил одного набора

На примере «Простой кредит» (`SimpleCreditRuleSet.check`):

```mermaid
flowchart TD
    In([check user_id]) --> C1{"Есть операции<br/>по продуктам CREDIT?"}
    C1 -->|да| No["Optional.empty()"]
    C1 -->|нет| C2{"SUM(DEPOSIT по DEBIT)<br/>&gt; SUM(WITHDRAW по DEBIT)?"}
    C2 -->|нет| No
    C2 -->|да| C3{"SUM(WITHDRAW по DEBIT)<br/>&gt; 100 000 ₽?"}
    C3 -->|нет| No
    C3 -->|да| Yes["Optional.of(RecommendationDto)<br/>«Простой кредит»"]
```

Аналогично устроены `Invest500RuleSet` (`DEBIT есть` И `INVEST нет` И `SUM(SAVING, DEPOSIT) > 1000`)
и `TopSavingRuleSet` (`DEBIT есть` И (`SUM(DEBIT, DEPOSIT) >= 50 000` ИЛИ `SUM(SAVING, DEPOSIT) >= 50 000`)
И `SUM(DEBIT, DEPOSIT) > SUM(DEBIT, WITHDRAW)`).

## Последовательность вызовов

```mermaid
sequenceDiagram
    actor User as Сотрудник банка
    participant C as RecommendationController
    participant S as RecommendationService
    participant R as Invest500RuleSet
    participant Repo as RecommendationRepository
    participant DB as H2 (READ_ONLY)

    User->>C: GET /recommendation/{user_id}
    C->>S: getRecommendations(userId)
    loop по каждому RecommendationRuleSet
        S->>R: check(userId)
        R->>Repo: hasProductOfType(userId, DEBIT)
        Repo->>DB: SELECT COUNT(*) ... JOIN products
        DB-->>Repo: count
        Repo-->>R: true
        R->>Repo: sumByProductTypeAndTransactionType(userId, SAVING, DEPOSIT)
        Repo->>DB: SELECT COALESCE(SUM(amount), 0) ... JOIN products
        DB-->>Repo: sum
        Repo-->>R: 5000.00
        R-->>S: Optional.of(RecommendationDto)
    end
    S-->>C: List<RecommendationDto>
    C-->>User: 200 OK {user_id, recommendations}
```
