FROM openjdk:17

WORKDIR /app

COPY target/ArcaneScrolls-0.1.0-Arcane.jar app.jar

ENTRYPOINT ["java", "-jar", "app.jar"]