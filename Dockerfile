FROM openjdk:8-jdk-alpine
MAINTAINER org.gso
COPY target/backend-0.0.1.jar backend-0.0.1.jar
ENTRYPOINT ["java","-jar","/backend-0.0.1.jar"]