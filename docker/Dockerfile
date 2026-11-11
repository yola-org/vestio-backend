FROM eclipse-temurin:25-jre

ARG APP_VERSION=unknown
ENV APP_VERSION=${APP_VERSION}

WORKDIR /app
COPY build/libs/*.jar app.jar

USER 10001:10001
EXPOSE 8080
ENTRYPOINT ["java", "-jar", "/app/app.jar"]
