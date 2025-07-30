FROM amazoncorretto:8-alpine-jdk
EXPOSE 8080
COPY target/core*.jar /app.jar
ENV PROFILE=dev
CMD ["usr/bin/java", "-jar", "-Dspring.profiles.active=${PROFILE}", "/app.jar"]
