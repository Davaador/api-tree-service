# Build stage: Maven + JDK 21 ашиглаж build хийх
FROM maven:3.9.4-eclipse-temurin-21-alpine AS build
WORKDIR /app

# pom.xml болон src-гаа хуулна
COPY pom.xml .
COPY src ./src

# Build хийх (тестийг алгасна)
RUN mvn clean package -DskipTests

# Run stage: жижиг JDK image дээр ажиллуулна
FROM eclipse-temurin:21-jdk-alpine
ENV JAVA_OPTS="-Xmx512m -Xms256m"
WORKDIR /app

ENV API_FILE_UPLOAD_DIR=/uploads
RUN mkdir -p ${API_FILE_UPLOAD_DIR}

# Build stage-с jar файлыг хуулна
COPY --from=build /app/target/*.jar app.jar

EXPOSE 8080

ENTRYPOINT ["sh", "-c", "java $JAVA_OPTS -jar app.jar"]
