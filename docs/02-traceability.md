# Таблица трейсинга требований

Связь требований из [01-requirements.md](01-requirements.md) с задачами (issues), реализацией и проверкой.

| Требование | Issue | Ветка | Реализация | Проверка |
|---|---|---|---|---|
| US-01 | #1 Каркас проекта | `feature/project-setup` | `pom.xml`, `RecommendationServiceApplication` | `mvn clean verify` |
| US-02 | #3 Репозиторий и правила | `feature/recommendation-rules` | `rule/*RuleSet` | `RecommendationRuleSetTest` |
| US-03 | #4 REST-слой | `feature/rest-api` | `RecommendationController`, `RecommendationResponse` | `RecommendationControllerIntegrationTest.returnsEmptyRecommendationsWhenNothingMatched` |
| US-04 | #6 Тесты и документация | `feature/tests-and-docs` | `RecommendationService` + `RecommendationRuleSet` | `RecommendationServiceTest` |
| US-05 | #2 Подключение БД | `feature/project-setup` | `application.properties`, `RecommendationRepository` | Отсутствие DML/DDL в коде, NFR-07 |
| US-06 | #4 REST-слой | `feature/rest-api` | `RecommendationController.parseUserId` | `RecommendationControllerIntegrationTest.rejectsMalformedUserId` |
| FR-01 | #4 REST-слой | `feature/rest-api` | `RecommendationController.getRecommendations` | `RecommendationControllerIntegrationTest` |
| FR-02 | #4 REST-слой | `feature/rest-api` | `RecommendationDto`, `RecommendationResponse` | Сквозной тест, проверка тела ответа |
| FR-03 | #4 REST-слой | `feature/rest-api` | `@JsonProperty("user_id")` | Сквозной тест, проверка `user_id` в теле |
| FR-04 | #5 Сбор рекомендаций | `feature/recommendation-rules` | `RecommendationService.getRecommendations` | `RecommendationServiceTest.returnsEmptyListWhenNoRuleSetMatched` |
| FR-05 | #3 Репозиторий и правила | `feature/recommendation-rules` | `Invest500RuleSet` | `RecommendationRuleSetTest.invest500…` |
| FR-06 | #3 Репозиторий и правила | `feature/recommendation-rules` | `TopSavingRuleSet` | `RecommendationRuleSetTest.topSaving…` |
| FR-07 | #3 Репозиторий и правила | `feature/recommendation-rules` | `SimpleCreditRuleSet` | `RecommendationRuleSetTest.simpleCredit…` |
| FR-08 | #3 Репозиторий и правила | `feature/recommendation-rules` | `RecommendationRepository.HAS_PRODUCT_OF_TYPE` | `RecommendationRepositoryTest.findsProductOfType` |
| FR-09 | #3 Репозиторий и правила | `feature/recommendation-rules` | Условия в классах правил | `RecommendationRuleSetTest` (граничные значения) |
| FR-10 | #5 Сбор рекомендаций | `feature/recommendation-rules` | `List<RecommendationRuleSet>` в `RecommendationService` | `RecommendationServiceTest` |
| FR-11 | #4 REST-слой | `feature/rest-api` | `RecommendationController.parseUserId` | `RecommendationControllerIntegrationTest.rejectsMalformedUserId` |
| NFR-01 | #1 Каркас проекта | `feature/project-setup` | `pom.xml` | `mvn clean verify` |
| NFR-02 | #2 Подключение БД | `feature/project-setup` | `application.properties` | Проверка значений при приёмке |
| NFR-03 | #2 Подключение БД | `feature/project-setup` | `RecommendationRepository` | Отсутствие JPA-зависимостей в `pom.xml` |
| NFR-04 | #3 Репозиторий и правила | `feature/recommendation-rules` | Два параметризованных SQL-запроса | Ревью: SQL не дублируется |
| NFR-05 | #3 Репозиторий и правила | `feature/recommendation-rules` | `SUM`, `COALESCE` в SQL | `RecommendationRepositoryTest` |
| NFR-06 | #6 Тесты и документация | `feature/tests-and-docs` | Конструкторная инъекция | `RecommendationRuleSetTest` (Mockito) |
| NFR-07 | #2 Подключение БД | `feature/project-setup` | `spring.datasource.hikari.read-only=true` | Ревью: нет DML/DDL |
| NFR-08 | #5 Сбор рекомендаций | `feature/recommendation-rules` | `RecommendationRuleSet` + `@Component` | Ревью архитектуры, `docs/03-architecture.md` |
| NFR-09 | #4 REST-слой | `feature/rest-api` | `ResponseStatusException` | `RecommendationControllerIntegrationTest.rejectsMalformedUserId` |
| NFR-10 | #1 Каркас проекта | `feature/project-setup` | `pom.xml` | `mvn clean verify` |
