# TODO: add cache for gradle dependencies

FROM gradle:8.14.2-jdk24 AS build

WORKDIR /home/gradle/project

COPY build.gradle.kts .
COPY settings.gradle.kts .

COPY auth-service/build.gradle.kts ./auth-service/build.gradle.kts

COPY auth-service/src ./auth-service/src

RUN gradle --no-daemon clean build -x test auth-service:build

FROM eclipse-temurin:24-jre

RUN apt-get update && apt-get install -y curl && rm -rf /var/lib/apt/lists/*

WORKDIR /app

COPY --from=build /home/gradle/project/auth-service/build/libs/*.jar app.jar

EXPOSE 8081

CMD ["java", "-jar", "app.jar"]