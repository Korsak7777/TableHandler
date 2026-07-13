# Цель проекта

Проект для изучения подхода codeAsPrompt в работе с LLM агентами

## About codeAsPrompt

Подход подразумевает использование существующей кодовой базы в качестве составной части промта ([примера](https://github.com/f/prompts.chat/blob/main/src/content/book/ru/02-anatomy-of-effective-prompt.mdx#6-%D0%BF%D1%80%D0%B8%D0%BC%D0%B5%D1%80%D1%8B-few-shot-learning)). 

## Docker

### Задаем значение переменных

```bash
 $env:REGISTRY_URL="localhost:5001"
 $env:IMAGE_NAME="tablehandler"
 $env:IMAGE_TAG="1.0.1"
 $env:APP_PORT="8081"
```

### Сборка и отправка в репозиторий

```bash
docker compose build --push

 $env:REGISTRY_URL="localhost:5001"
 $env:IMAGE_NAME="table_handler"
 $env:IMAGE_TAG="1.0.1"
 $env:APP_PORT="8081"
```

### Запуск

```bash
docker compose up -d --no-build

 $env:REGISTRY_URL="localhost:5001"
 $env:IMAGE_NAME="table_handler"
 $env:IMAGE_TAG="1.0.1"
 $env:APP_PORT="8081"
```

Запускает контейнер из репозитория `localhost:5000`. Приложение будет доступно на порту, указанном в `APP_PORT` (по умолчанию `8081`).

### Остановка

```bash
docker compose down
```

