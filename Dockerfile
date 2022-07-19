# JDK 11 이미지 사용
FROM openjdk:11-jdk

# JAR_FILE 변수에 값을 저장
ARG JAR_FILE=build/libs/*.jar

# 변수에 저장된 것을 컨테이너 실행시 이름을 asyncrum-api-server.jar 파일로 변경하여 컨테이너에 저장
COPY ${JAR_FILE} asyncrum-api-server.jar

# 빌드된 이미지가 run될 때 실행 명령어
ENTRYPOINT ["java","-Dspring.profiles.active=prod","-jar","/asyncrum-api-server.jar"]