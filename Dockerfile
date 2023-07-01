# Build
FROM maven:3.9.2-eclipse-temurin-17 AS build
COPY src /home/app/src
COPY pom.xml /home/app
RUN mvn -f /home/app/pom.xml clean package -Dmaven.test.skip=true

# Package
FROM openjdk:17
COPY --from=build /home/app/target/circle8-backend-0.0.1.jar /usr/local/lib/circle8-backend.jar
EXPOSE 8080
ENTRYPOINT ["java", "-jar", "/usr/local/lib/circle8-backend.jar"]
