# --- Build stage ---

FROM maven:3.9-eclipse-temurin-17 AS build

WORKDIR /build

# Cache Maven dependencies separately from source.
COPY pom.xml .
RUN mvn -B -q dependency:go-offline

COPY src ./src

RUN mvn -B -q clean package -DskipTests

# --- Runtime stage ---

FROM eclipse-temurin:17-jre

WORKDIR /app

RUN groupadd --system spring && useradd --system --gid spring spring

USER spring

COPY --from=build /build/target/*.jar app.jar

EXPOSE 8080

ENTRYPOINT ["java", "-jar", "/app/app.jar"]