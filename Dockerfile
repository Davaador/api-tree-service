# Java 21 суурьтай image ашиглах
FROM eclipse-temurin:21-jdk-alpine
# JVM тохиргоо: RAM багасгах
ENV JAVA_OPTS="-Xmx512m -Xms256m"

WORKDIR /app

# JAR файлаа хуулна
COPY target/*.jar app.jar

EXPOSE 8080

# Апп эхлүүлэхэд JVM тохиргоо хэрэглэнэ
ENTRYPOINT ["sh", "-c", "java $JAVA_OPTS -jar app.jar"]
