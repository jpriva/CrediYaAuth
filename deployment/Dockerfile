# BUILD STAGE -------------------------------------
FROM eclipse-temurin:17-jdk AS builder
WORKDIR /workspace/app
COPY build.gradle settings.gradle gradlew gradle.properties ./
COPY gradle ./gradle
COPY main.gradle .
RUN sed -i 's/\r$//' ./gradlew && \
     chmod +x ./gradlew && \
     ./gradlew dependencies
COPY applications ./applications
COPY domain ./domain
COPY infrastructure ./infrastructure

RUN ./gradlew build -x test


# RUNTIME STAGE -----------------------------------
FROM eclipse-temurin:17-jre-alpine
WORKDIR /app

RUN addgroup -S appgroup && adduser -S appuser -G appgroup

COPY --from=builder /workspace/app/applications/app-service/build/libs/*.jar app.jar

USER appuser
ENTRYPOINT [ "java", "-jar", "/app/app.jar" ]
