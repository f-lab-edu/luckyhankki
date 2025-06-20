# 1. 빌드 단계 (gradlew wrapper 사용)
FROM openjdk:17-jdk-slim AS builder
WORKDIR /app
# Gradle Wrapper 및 설정 파일 복사
COPY build.gradle settings.gradle ./
COPY gradlew ./
COPY gradle ./gradle
# gradlew 실행 권한 부여
RUN chmod +x ./gradlew
# 의존성 다운로드
RUN ./gradlew dependencies --no-daemon
# 전체 소스 복사
COPY . .
# 빌드 실행
RUN ./gradlew build --no-daemon

# 2. 실행 단계
FROM openjdk:17-jdk-slim
WORKDIR /app
# 빌드 단계에서 생성된 JAR 파일만 복사
COPY --from=builder /app/build/libs/*.jar app.jar
EXPOSE 8080
ENTRYPOINT ["java", "-jar", "app.jar"]