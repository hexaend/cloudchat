FROM gradle:8.14.2-jdk24 AS build

WORKDIR /home/gradle/project

COPY build.gradle.kts settings.gradle.kts gradle.properties ./

RUN --mount=type=cache,target=/home/gradle/.gradle/caches \
    --mount=type=cache,target=/home/gradle/.gradle/wrapper \
    gradle --no-daemon dependencies --refresh-dependencies || true

COPY auth-service/build.gradle.kts ./auth-service/build.gradle.kts

COPY auth-service/src ./auth-service/src

RUN --mount=type=cache,target=/home/gradle/.gradle/caches \
    --mount=type=cache,target=/home/gradle/.gradle/wrapper \
    gradle auth-service:bootJar --no-daemon --info

FROM eclipse-temurin:24-jre

WORKDIR /app

COPY --from=build /home/gradle/project/auth-service/build/libs/*.jar app.jar

EXPOSE 8081

ENTRYPOINT ["java", "-jar", "app.jar"]