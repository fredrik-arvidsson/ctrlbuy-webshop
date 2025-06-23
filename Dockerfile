# Railway Dockerfile för CtrlBuy Webshop
FROM openjdk:21-jdk-slim

# Installera nödvändiga verktyg
RUN apt-get update && apt-get install -y curl && rm -rf /var/lib/apt/lists/*

# Arbetskatalog
WORKDIR /app

# Kopiera Maven wrapper och pom.xml först (för bättre caching)
COPY .mvn/ .mvn/
COPY mvnw pom.xml ./

# Ladda ner dependencies (cachad layer)
RUN ./mvnw dependency:go-offline

# Kopiera källkod
COPY src ./src

# Bygg applikationen
RUN ./mvnw clean package -DskipTests

# Exponera port
EXPOSE 8080

# Sätt miljövariabler för Railway
ENV SPRING_PROFILES_ACTIVE=railway
ENV SPRING_DATASOURCE_URL=jdbc:h2:mem:railwaydb
ENV SPRING_DATASOURCE_DRIVER_CLASS_NAME=org.h2.Driver
ENV SPRING_JPA_DATABASE_PLATFORM=org.hibernate.dialect.H2Dialect
ENV SPRING_H2_CONSOLE_ENABLED=true

# Hälsokontroll
HEALTHCHECK --interval=30s --timeout=3s --start-period=60s --retries=3 \
  CMD curl -f http://localhost:8080/actuator/health || exit 1

# Starta applikationen - RÄTT JAR-NAMN!
CMD ["java", "-jar", "target/webshop-1.0-SNAPSHOT.jar"]