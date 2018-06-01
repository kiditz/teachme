FROM openjdk:8-jdk-alpine
MAINTAINER kiditz <kiditzbastara@gmail.com>

ADD ./target/api-gateway.jar /app/
CMD ["java", "-Xmx200m", "-jar", "/app/api-gateway.jar"]
EXPOSE 38289
