# Java 21 суурьтай image ашиглах
FROM eclipse-temurin:21-jdk-jammy

# Working directory
WORKDIR /app

# Build хийсэн JAR файлаа хуулна
COPY target/*.jar app.jar

# Expose Spring Boot-ын порт
EXPOSE 8080

# App-г асаах
ENTRYPOINT ["java", "-jar", "app.jar"]
