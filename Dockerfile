FROM amazoncorretto:17-alpine
COPY webshop.jar app.jar
EXPOSE 5000
ENV SERVER_PORT=5000
ENTRYPOINT ["java", "-jar", "/app.jar"]
