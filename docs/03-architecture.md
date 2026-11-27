# Архитектура

## 1. Диаграмма компонентов

```mermaid
flowchart LR
    Client["Сотрудник банка<br/>(клиент REST API)"]

    subgraph App["recommendation-service (Spring Boot)"]
        Controller["RecommendationController<br/>&lt;&lt;RestController&gt;&gt;"]
        Service["RecommendationService<br/>&lt;&lt;Service&gt;&gt;"]
        Rules["RecommendationRuleSet<br/>&lt;&lt;interface&gt;&gt;<br/>Invest500RuleSet, TopSavingRuleSet,<br/>SimpleCreditRuleSet &lt;&lt;Component&gt;&gt;"]
        Repo["RecommendationRepository<br/>&lt;&lt;Repository&gt;&gt;"]
        Jdbc["JdbcTemplate"]
    end

    DB[("H2<br/>transaction.mv.db<br/>READ_ONLY")]

    Client -->|"GET /recommendation/{user_id}"| Controller
    Controller --> Service
    Service -->|"List&lt;RecommendationRuleSet&gt;"| Rules
    Rules --> Repo
    Repo --> Jdbc
    Jdbc -->|"SQL: JOIN transactions, products"| DB
```

Компоненты и их ответственность:

| Компонент | Ответственность |
|---|---|
| `RecommendationController` | Приём HTTP-запроса, разбор `user_id`, формирование тела ответа |
| `RecommendationService` | Опрос всех наборов правил, сбор непустых результатов |
| `RecommendationRuleSet` | Контракт набора правил одной рекомендации |
| `Invest500RuleSet`, `TopSavingRuleSet`, `SimpleCreditRuleSet` | Проверка правил конкретного продукта |
| `RecommendationRepository` | Единственное место с SQL, доступ через `JdbcTemplate` |
| `RecommendationDto`, `RecommendationResponse` | Транспортные объекты уровня API |

## 2. Диаграмма классов

```mermaid
classDiagram
    class RecommendationController {
        -RecommendationService recommendationService
        +getRecommendations(String userId) RecommendationResponse
        -parseUserId(String userId) UUID
    }

    class RecommendationService {
        -List~RecommendationRuleSet~ ruleSets
        +getRecommendations(UUID userId) List~RecommendationDto~
    }

    class RecommendationRuleSet {
        <<interface>>
        +check(UUID userId) Optional~RecommendationDto~
    }

    class Invest500RuleSet {
        +UUID PRODUCT_ID
        +String PRODUCT_NAME
        +check(UUID userId) Optional~RecommendationDto~
    }

    class TopSavingRuleSet {
        +UUID PRODUCT_ID
        +String PRODUCT_NAME
        +check(UUID userId) Optional~RecommendationDto~
    }

    class SimpleCreditRuleSet {
        +UUID PRODUCT_ID
        +String PRODUCT_NAME
        +check(UUID userId) Optional~RecommendationDto~
    }

    class RecommendationRepository {
        -JdbcTemplate jdbcTemplate
        +hasProductOfType(UUID userId, ProductType type) boolean
        +sumByProductTypeAndTransactionType(UUID, ProductType, TransactionType) BigDecimal
    }

    class ProductType {
        <<enumeration>>
        DEBIT
        CREDIT
        INVEST
        SAVING
    }

    class TransactionType {
        <<enumeration>>
        DEPOSIT
        WITHDRAW
    }

    class RecommendationDto {
        <<record>>
        +String name
        +UUID id
        +String text
    }

    class RecommendationResponse {
        <<record>>
        +UUID userId
        +List~RecommendationDto~ recommendations
    }

    RecommendationController --> RecommendationService
    RecommendationController ..> RecommendationResponse
    RecommendationService --> "0..*" RecommendationRuleSet
    RecommendationRuleSet <|.. Invest500RuleSet
    RecommendationRuleSet <|.. TopSavingRuleSet
    RecommendationRuleSet <|.. SimpleCreditRuleSet
    Invest500RuleSet --> RecommendationRepository
    TopSavingRuleSet --> RecommendationRepository
    SimpleCreditRuleSet --> RecommendationRepository
    RecommendationRepository ..> ProductType
    RecommendationRepository ..> TransactionType
    RecommendationService ..> RecommendationDto
```

## 3. Схема данных (выданная БД)

```mermaid
erDiagram
    USERS ||--o{ TRANSACTIONS : "совершает"
    PRODUCTS ||--o{ TRANSACTIONS : "указан в"

    USERS {
        UUID id PK
        VARCHAR username
        VARCHAR first_name
        VARCHAR last_name
    }
    PRODUCTS {
        UUID id PK
        VARCHAR name
        VARCHAR type "DEBIT | CREDIT | INVEST | SAVING"
    }
    TRANSACTIONS {
        UUID id PK
        UUID user_id FK
        UUID product_id FK
        VARCHAR type "DEPOSIT | WITHDRAW"
        DECIMAL amount
    }
```

Приложение читает эти таблицы и никогда их не изменяет.

## 4. Выделение общих SQL-запросов

Все правила трёх продуктов сводятся к двум вопросам:

1. Пользуется ли клиент продуктом типа `T`? → `HAS_PRODUCT_OF_TYPE`
   (`JOIN transactions/products`, `WHERE user_id = ? AND p.type = ?`, `COUNT(*) > 0`).
2. Какова сумма операций вида `X` по продуктам типа `T`? → `SUM_BY_PRODUCT_TYPE_AND_TRANSACTION_TYPE`
   (`JOIN transactions/products`, `WHERE user_id = ? AND p.type = ? AND t.type = ?`, `SUM(t.amount)`).

Оба запроса параметризованы и переиспользуются всеми правилами:

| Правило | Обращения к репозиторию |
|---|---|
| Invest 500 | `hasProductOfType(DEBIT)`, `hasProductOfType(INVEST)`, `sum(SAVING, DEPOSIT)` |
| Top Saving | `hasProductOfType(DEBIT)`, `sum(DEBIT, DEPOSIT)`, `sum(SAVING, DEPOSIT)`, `sum(DEBIT, WITHDRAW)` |
| Простой кредит | `hasProductOfType(CREDIT)`, `sum(DEBIT, DEPOSIT)`, `sum(DEBIT, WITHDRAW)` |

Сравнение с порогами (`> 1000`, `>= 50 000`, `> 100 000`) и логические связки выполняются
в Java: SQL остаётся простым и читаемым, а логика правил покрывается модульными тестами без БД.

## 5. Расширение

Новый продукт добавляется одним классом:

```java
@Component
public class NewProductRuleSet implements RecommendationRuleSet {
    @Override
    public Optional<RecommendationDto> check(UUID userId) { ... }
}
```

`RecommendationService` подхватывает его автоматически через `List<RecommendationRuleSet>`.
Изменения в контроллере, сервисе и существующих правилах не требуются.
