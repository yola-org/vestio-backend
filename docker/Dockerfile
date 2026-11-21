FROM eclipse-temurin:25-jre

ARG APP_VERSION=unknown
ENV APP_VERSION=${APP_VERSION}
ENV TZ=UTC
ENV JAVA_TOOL_OPTIONS="-Duser.timezone=UTC"

WORKDIR /app
COPY build/libs/*.jar app.jar

USER 10001:10001
EXPOSE 8080
ENTRYPOINT ["java", "-jar", "/app/app.jar"]
