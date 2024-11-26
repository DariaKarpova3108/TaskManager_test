[![Maintainability](https://api.codeclimate.com/v1/badges/9769fc7adfc5bcc21b92/maintainability)](https://codeclimate.com/github/DariaKarpova3108/TaskManager_test/maintainability)
[![Test Coverage](https://api.codeclimate.com/v1/badges/9769fc7adfc5bcc21b92/test_coverage)](https://codeclimate.com/github/DariaKarpova3108/TaskManager_test/test_coverage)

# Task Management System

Task Management System — это API для управления задачами с поддержкой аутентификации и авторизации. Приложение реализовано на Java с использованием фреймворка Spring Boot.

## Функциональные возможности

- **Управление задачами**: создание, редактирование, удаление и просмотр задач.
- **Аутентификация и авторизация**:
    - JWT токены для доступа к API.
    - Роли пользователей: **администратор** и **пользователь**.
- **Управление статусом, приоритетом и исполнителями**.
- **Комментарии к задачам**.
- **Фильтрация, сортировка и пагинация** задач.
- **Генерация Swagger UI** для документации API.
- **Обработка ошибок и валидация входящих данных**.

---

## Стек технологий

- **Java 21**
- **Spring Boot**:
    - Spring Security (JWT аутентификация)
    - Spring Data JPA (работа с базой данных)
    - Springdoc OpenAPI (Swagger UI)
- **PostgreSQL** (в разработке), **H2 Database** (в тестировании)
- **MapStruct** для маппинга DTO
- **Docker** для развертывания

---

## Установка и запуск

```bash
1. Клонируйте репозиторий:
git clone git@github.com:DariaKarpova3108/TaskManager_test_task.git
cd TaskManager

2. Используется Gradle для управления зависимостями:
./gradlew build

3. Запуск в режиме разработки
./gradlew bootRun

4. Запуск в Docker
docker build -t task-manager .
docker run -p 7070:7070 --env-file .env task-manager
```
---

## В проекте настроена аутентификация
- **Используется JWT токен**
- **Для получения токена выполните запрос к /api/login с предоставлением email и password**
```
Дефолтные данные пользователя для входа в систему:
  - логин: admin@example.com
  - пароль: password
```
## Роли и права
- **Администратор:**
  - Управление всеми задачами: создание, редактирование, удаление, изменение статусов и приоритетов
  - Назначение исполнителей
- **Пользователь:**
  - Доступ только к своим задачам
  - Возможность изменять статус своих задач и оставлять комментарии
---
## Swagger UI
Для просмотра документации API перейдите по адресу
```
http://localhost:7070/swagger-ui.html
```
## Разработка
- **Для запуска тестов:**
```
./gradlew test
```
- **Генерация отчета покрытия тестами (JaCoCo):**
```
./gradlew jacocoTestReport
```