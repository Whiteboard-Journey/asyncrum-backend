FROM openjdk:11-jdk
ARG JAR_FILE=build/libs/*.jar
COPY ${JAR_FILE} asyncrum-api-server.jar
ENTRYPOINT ["java","-Dspring.profiles.active=prod","-jar","/asyncrum-api-server.jar"]