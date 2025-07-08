# Använd Amazon Corretto 17 med Maven pre-installerat
FROM amazoncorretto:17-alpine

# Installera Maven
RUN apk add --no-cache maven

# Skapa en app-användare för säkerhet
RUN addgroup -g 1001 -S app && \
    adduser -S app -u 1001 -G app

# Sätt working directory
WORKDIR /app

# Kopiera allt och bygg (enklare approach)
COPY . .

# Bygg applikationen med system Maven
RUN mvn clean package -DskipTests

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