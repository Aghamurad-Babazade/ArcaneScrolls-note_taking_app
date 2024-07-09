FROM adoptopenjdk:11-jre-hotspot

WORKDIR /app

COPY target/ArcaneScrolls-0.0.1-SNAPSHOT.jar /app/ArcaneScrolls.jar

EXPOSE 8080

CMD ["java", "-jar", "/app/ArcaneScrolls.jar"]

