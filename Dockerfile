# AWS Dockerfile för CtrlBuy Webshop - FUNGERANDE VERSION
FROM openjdk:21-jdk-slim

# Installera nödvändiga verktyg
RUN apt-get update && apt-get install -y curl && rm -rf /var/lib/apt/lists/*

# Arbetskatalog
WORKDIR /app

# Kopiera Maven wrapper och pom.xml först (för bättre caching)
COPY .mvn/ .mvn/
COPY mvnw pom.xml ./

# Gör mvnw executable
RUN chmod +x mvnw

# Ladda ner dependencies (cachad layer)
RUN ./mvnw dependency:go-offline -q

# Kopiera källkod
COPY src ./src

# Bygg applikationen (med längre timeout)
RUN ./mvnw clean package -DskipTests -Dmaven.test.skip=true -q

# Exponera port
EXPOSE 8080

# Sätt miljövariabler för AWS - ENDAST PROFIL!
ENV SPRING_PROFILES_ACTIVE=aws

# Hälsokontroll
HEALTHCHECK --interval=30s --timeout=3s --start-period=60s --retries=3 \
  CMD curl -f http://localhost:8080/actuator/health || exit 1

# Starta applikationen
CMD ["java", "-jar", "target/webshop-1.0-SNAPSHOT.jar"]