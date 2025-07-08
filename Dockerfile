# Använd Amazon Corretto 17 (samma som Railway buildpack)
FROM amazoncorretto:17-alpine

# Skapa en app-användare för säkerhet
RUN addgroup -g 1001 -S app && \
    adduser -S app -u 1001 -G app

# Sätt working directory
WORKDIR /app

# Kopiera Maven wrapper och pom.xml först (för caching)
COPY .mvn/ .mvn
COPY mvnw pom.xml ./

# Ladda ner dependencies (detta cachas om pom.xml inte ändras)
RUN ./mvnw dependency:go-offline

# Kopiera källkoden
COPY src ./src

# Bygg applikationen
RUN ./mvnw clean package -DskipTests

# Kopiera den byggda JAR-filen
RUN cp target/*.jar app.jar

# Byt till app-användaren
USER app

# Exponera port (Railway hanterar PORT environment variable automatiskt)
EXPOSE 8080

# Hälsokontroll
HEALTHCHECK --interval=30s --timeout=3s --start-period=60s --retries=3 \
  CMD curl -f http://localhost:8080/actuator/health || exit 1

# Starta applikationen med optimerade JVM-inställningar
ENTRYPOINT ["java", \
  "-Djava.security.egd=file:/dev/./urandom", \
  "-Dspring.profiles.active=production", \
  "-jar", \
  "/app/app.jar"]