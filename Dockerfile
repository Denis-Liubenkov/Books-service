FROM openjdk:17-jdk-slim

ARG JAR_FILE=books-service-0.0.1-SNAPSHOT.jar

WORKDIR /app

COPY build/libs/${JAR_FILE} /app/Books-service.jar

ENTRYPOINT ["java", "-jar", "/app/Books-service.jar"]
