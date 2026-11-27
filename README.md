# Recommendation Service — банк «Стар»

Сервис рекомендаций банковских продуктов: по идентификатору клиента возвращает список продуктов,
правила которых он удовлетворяет.

## Стек

| Компонент | Версия / выбор |
|---|---|
| Java | 17 |
| Spring Boot | 3.2.3 |
| Сборка | Maven |
| Доступ к данным | Spring JDBC + `JdbcTemplate` (JPA/Hibernate не используются) |
| БД | H2, файловая, `READ_ONLY` |
| Тесты | JUnit 5, Mockito, AssertJ, `TestRestTemplate` |

## API

```
GET /recommendation/{user_id}
```

`200 OK`, `Content-Type: application/json`:

```json
{
  "user_id": "cd515076-5d8a-44be-930e-8d4fcb79f42d",
  "recommendations": [
    {"name": "Invest 500", "id": "147f6a0f-3b91-413b-ab99-87f081d60d5a", "text": "..."}
  ]
}
```

Если ни одно правило не выполнено, возвращается тот же объект с пустым массивом `recommendations`.

## Конфигурация БД

`src/main/resources/application.properties`:

```properties
spring.datasource.url=jdbc:h2:file:./transaction
spring.datasource.driver-class-name=org.h2.Driver
spring.datasource.username=sa
spring.datasource.password=
spring.datasource.hikari.read-only=true
```

Файл `transaction.mv.db` кладётся в рабочий каталог запуска. База только для чтения: приложение
не создаёт и не изменяет её таблицы.

## Запуск

```bash
mvn spring-boot:run
```

или

```bash
mvn clean package
java -jar target/recommendation-service-0.1.0-SNAPSHOT.jar
```

## Тесты

```bash
mvn test
```

Тесты используют in-memory H2 со схемой и данными из `src/test/resources` и не требуют
файла `transaction.mv.db`: 15 тестов, включая сквозной вызов REST-метода через `TestRestTemplate`.

## Проверка работы

Для запуска без выданного файла БД есть тестовая схема и данные:

```bash
java -jar target/recommendation-service-0.1.0-SNAPSHOT.jar \
  --spring.datasource.url=jdbc:h2:file:./runtime/transaction \
  --spring.sql.init.mode=always \
  --spring.sql.init.schema-locations=file:./src/test/resources/schema.sql \
  --spring.sql.init.data-locations=file:./src/test/resources/data.sql

curl http://localhost:8080/recommendation/cd515076-5d8a-44be-930e-8d4fcb79f42d
```

Проверено на пользователях из ТЗ: `cd515076-…-8d4fcb79f42d` → «Invest 500»,
`d4a4d619-…-76c49409546b` → «Top Saving», `1f9b149c-…-16bea229b71a` → «Простой кредит»,
несуществующий пользователь → `{"user_id":"…","recommendations":[]}`,
некорректный UUID → `400 Bad Request`.

## Структура проекта

```
src/main/java/com/starbank/recommendation
├── controller   REST-контроллер
├── service      RecommendationService — опрос всех наборов правил
├── rule         интерфейс RecommendationRuleSet и три реализации (@Component)
├── repository   RecommendationRepository — SQL через JdbcTemplate
├── domain       перечисления ProductType, TransactionType
└── dto          RecommendationDto, RecommendationResponse
```

## Документация

| Документ | Содержание |
|---|---|
| [docs/00-project-brief.md](docs/00-project-brief.md) | краткое содержание проекта |
| [docs/01-requirements.md](docs/01-requirements.md) | формализованное ТЗ: user story и NFR |
| [docs/02-traceability.md](docs/02-traceability.md) | таблица трейсинга требований |
| [docs/03-architecture.md](docs/03-architecture.md) | диаграмма компонентов и классов |
| [docs/04-activity-diagram.md](docs/04-activity-diagram.md) | диаграмма поведения |
| [docs/05-api.yaml](docs/05-api.yaml) | спецификация REST API (OpenAPI 3) |
| [docs/06-deployment.md](docs/06-deployment.md) | требования по развёртыванию, диаграмма размещения |
| [docs/07-git-workflow.md](docs/07-git-workflow.md) | ветки, задачи, порядок работы |

## Автор

Dmitriy — d.sherin@list.ru, [github.com/0ooxoo0/DZ](https://github.com/0ooxoo0/DZ)
