FROM maven:3.9-eclipse-temurin-21 AS builder
WORKDIR /app

# Copy pom.xml first (caches dependencies if pom.xml hasn't changed)
COPY pom.xml .
RUN mvn dependency:go-offline -B

# Copy source and build
COPY src ./src
RUN mvn clean package -DskipTests -B

# Stage 2: Run
FROM eclipse-temurin:21-jre-alpine
WORKDIR /app

COPY --from=builder /app/target/*.jar app.jar

ENTRYPOINT ["java", "-jar", "app.jar"]