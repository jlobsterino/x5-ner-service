FROM python:3.9-slim

WORKDIR /app

# Устанавливаем системные зависимости
RUN apt-get update && apt-get install -y \
    gcc \
    g++ \
    curl \
    && rm -rf /var/lib/apt/lists/*

# Копируем requirements.txt
COPY requirements.txt .

# Устанавливаем Python пакеты
RUN pip install --no-cache-dir -r requirements.txt

# Копируем код
COPY main.py .

# Копируем модель
COPY my_ner_model_new ./my_ner_model_new

# Открываем порт
EXPOSE 8000

# Запуск
CMD ["python", "main.py"]
