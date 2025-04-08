FROM openjdk:21-jdk-slim
WORKDIR /app
COPY target/hubspot-0.0.2-SNAPSHOT.jar app.jar
EXPOSE 8080
ENTRYPOINT ["java", "-jar", "app.jar"]