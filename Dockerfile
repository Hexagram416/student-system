# Multi-stage build for Railway
FROM maven:3.9-eclipse-temurin-17-alpine AS build
WORKDIR /build
COPY student-system-server/pom.xml .
RUN mvn dependency:go-offline -q
COPY student-system-server/src ./src
RUN mvn clean package -DskipTests -q

FROM eclipse-temurin:17-jre-alpine
WORKDIR /app
COPY --from=build /build/target/student-system-server-1.0.0.jar app.jar
EXPOSE 8080

# H2 file database for zero-config deployment
# Switch to MySQL by setting DB_DRIVER, DB_URL, DB_USER, DB_PASS env vars
ENV PORT=8080

ENTRYPOINT ["java", "-jar", "app.jar", "--spring.profiles.active=prod"]
