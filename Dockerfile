# Multi-stage build för Railway med Amazon Corretto
FROM amazoncorretto:21-alpine AS build

# Set working directory
WORKDIR /app

# Copy pom.xml first for better caching
COPY pom.xml .

# Download dependencies (cached if pom.xml unchanged)
RUN apk add --no-cache maven && mvn dependency:go-offline -B

# Copy source code
COPY src ./src

# Build application (skip tests for faster builds)
RUN mvn clean package -DskipTests -B

# Runtime stage
FROM amazoncorretto:21-alpine

# Set working directory
WORKDIR /app

# Copy the built jar from build stage
COPY --from=build /app/target/webshop-1.0-SNAPSHOT.jar app.jar

# Expose port (Railway uses PORT environment variable)
EXPOSE 8080

# Environment for Railway
ENV SPRING_PROFILES_ACTIVE=prod

# Run the application
CMD ["java", "-jar", "app.jar"]