# Используем базовый образ с JDK 21
FROM openjdk:21-jdk-slim

WORKDIR /app

# Копируем сгенерированный jar файл
COPY build/libs/notion-s3-0.0.1-SNAPSHOT.jar app.jar

# Устанавливаем права на файл, если нужно
RUN chmod +x /app/app.jar

# Запуск приложения
ENTRYPOINT ["java", "-jar", "app.jar", "--spring.profiles.active=prod"]
