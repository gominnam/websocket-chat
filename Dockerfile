# 1. JAVA 17 image
FROM openjdk:17-jdk

# 2. 작업 디렉토리 설정
WORKDIR /app

# 3. 애플리케이션 코드 복사 (예: 빌드된 jar 파일)
COPY ./build/libs/websocket-chat.jar /app/websocket-chat.jar

# 4. 애플리케이션 실행
CMD ["java", "-jar", "/app/websocket-chat.jar", "--spring.profiles.active=prod"]
