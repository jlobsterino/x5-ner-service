# X5 NER Service

Проект для распознавания именованных сущностей в товарах с использованием ML-модели (spaCy) и Spring Boot бэкенда.

## Требования

- [Docker Desktop](https://www.docker.com/products/docker-desktop/) для Windows
- [Git](https://git-scm.com/download/win)

## Быстрый старт

1. **Клонируйте репозиторий:**
git clone https://github.com/jlobsterino/x5-ner-service.git
cd x5-ner-service
2. **Убедитесь, что Docker Desktop запущен**

3. **Запустите проект:**
docker compose up --build
Первый запуск займёт несколько минут (скачивание образов и сборка).

4. **Откройте в браузере:**

- Backend API: http://localhost:8080
- ML Service API Docs: http://localhost:8000/docs
- База данных PostgreSQL: localhost:5432

## Остановка проекта

Нажмите `Ctrl+C` в терминале, затем:
docker compose down
## Структура проекта

.
├── ml_project/ # ML-сервис на FastAPI + spaCy
│ ├── Dockerfile
│ ├── main.py
│ ├── requirements.txt
│ └── my_ner_model_new/ # Обученная NER модель
│
├── x5-ner-service/ # Backend на Spring Boot
│ ├── Dockerfile
│ ├── docker-compose.yml
│ ├── pom.xml
│ ├── src/
│ └── README.md
│
└── .gitignore
undefined

## Сервисы
- **backend** (порт 8080) — Spring Boot REST API
- **ml-service** (порт 8000) — FastAPI сервис для NER-модели
- **postgres** (порт 5432) — База данных PostgreSQL

## Проблемы?

Если контейнеры не запускаются:
1. Убедитесь, что Docker Desktop запущен
2. Проверьте, что порты 8080, 8000 и 5432 свободны
3. Попробуйте перезапустить Docker Desktop