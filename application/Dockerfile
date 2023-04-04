#
# Build stage
#
FROM maven:3.9.0-eclipse-temurin-17-alpine AS build

WORKDIR /app

COPY pom.xml ./
COPY src ./src

CMD ["./mvnw", "spring-boot:run"]

#
# Package stage
#
FROM eclipse-temurin:17.0.6_10-jre-alpine
COPY --from=build /home/app/target/application-0.0.1-SNAPSHOT.jar /usr/local/lib/application.jar
EXPOSE 8087
ENTRYPOINT ["java","-jar","/usr/local/lib1111/application.jar"]