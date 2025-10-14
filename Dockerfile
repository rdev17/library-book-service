FROM eclipse-temurin:17-jdk-alpine

LABEL authors="rano"

WORKDIR /app

COPY target/library-book-service-0.0.1-SNAPSHOT.jar app.jar

EXPOSE 8080

ENTRYPOINT ["java", "-jar", "app.jar"]