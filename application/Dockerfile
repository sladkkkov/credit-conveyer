
FROM eclipse-temurin:17-alpine
WORKDIR application
COPY target/application-0.0.1-SNAPSHOT.jar application.jar

ENTRYPOINT ["java","-jar","application.jar"]