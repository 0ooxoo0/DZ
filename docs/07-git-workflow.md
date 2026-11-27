# Организация работы над проектом

## 1. Роли

Работа выполнена одним человеком с совмещением ролей, рекомендованных в FAQ курса:

| Роль | Кто | Зона ответственности |
|---|---|---|
| Тимлид | Dmitriy | декомпозиция ТЗ, приоритеты, ревью собственного кода перед слиянием |
| Разработчик | Dmitriy | реализация сервиса, схема БД-запросов, рефакторинг |
| Тестировщик | Dmitriy | JUnit + Mockito тесты, сквозной тест REST API |
| Документатор | Dmitriy | вики проекта, OpenAPI, диаграммы |

## 2. Ветки

Ветки создаются под задачи, а не под людей. Рабочая ветка — `develop`, релизная — `main`.

```
main
 └── develop
      ├── feature/project-setup          каркас Spring Boot и подключение БД
      ├── feature/recommendation-rules   репозиторий, правила, сервис
      ├── feature/rest-api               REST-контроллер и DTO ответа
      └── feature/tests-and-docs         тесты и документация
```

Порядок работы с веткой:

```bash
git switch develop
git switch -c feature/recommendation-rules
# ... работа, коммиты по шагам ...
git switch develop
git merge --no-ff feature/recommendation-rules -m "Merge feature/recommendation-rules into develop"
git branch -d feature/recommendation-rules
```

После стабилизации `develop` сливается в `main`:

```bash
git switch main
git merge --no-ff develop -m "Merge develop into main"
```

## 3. Задачи (issues)

| Issue | Заголовок | Ветка | Результат |
|---|---|---|---|
| #1 | Каркас проекта Spring Boot, сборка Maven | `feature/project-setup` | `pom.xml`, главный класс приложения |
| #2 | Подключение выданной БД H2 в режиме READ_ONLY | `feature/project-setup` | `application.properties` |
| #3 | Репозиторий на JdbcTemplate и три набора правил | `feature/recommendation-rules` | `RecommendationRepository`, `*RuleSet` |
| #4 | REST-метод `GET /recommendation/{user_id}` | `feature/rest-api` | контроллер, DTO ответа |
| #5 | Сбор рекомендаций из всех наборов правил | `feature/recommendation-rules` | `RecommendationService` |
| #6 | Модульные и сквозные тесты, документация | `feature/tests-and-docs` | `src/test`, `docs/` |

## 4. Порядок работы над задачей

1. Создать issue с описанием и критериями приёмки.
2. Создать ветку от `develop` с именем `feature/<краткое-имя>`.
3. Коммитить изменения небольшими шагами; сообщение — в повелительном наклонении:
   `добавить репозиторий рекомендаций`, `исправить граничное условие Top Saving`.
4. Прогнать `mvn clean verify`.
5. Слить ветку в `develop` (при командной работе — через Pull Request с ревью).
6. Удалить ветку, закрыть issue.

## 5. Соответствие требованиям задания

| Требование задания | Как выполнено |
|---|---|
| Показать динамику работы с задачами | 6 задач в таблице выше, каждая — отдельная ветка |
| Показать работу с GIT и ветками | `main` + `develop` + 4 feature-ветки, слияния `--no-ff` |
| Публичный репозиторий | https://github.com/0ooxoo0/DZ |
| Ответственный за сдачу работы | Dmitriy (d.sherin@list.ru) |
