FROM maven:3.8.2-jdk-11 as build
COPY . .
RUN mvn clean package

FROM openjdk:8-jdk-alpine
MAINTAINER org.gso
COPY --from=build target/backend-0.0.1.jar backend-0.0.1.jar
EXPOSE 8080
ENTRYPOINT ["java","-jar","/backend-0.0.1.jar"]