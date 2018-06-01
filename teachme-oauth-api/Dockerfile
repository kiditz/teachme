FROM openjdk:8-jdk-alpine
MAINTAINER kiditz <kiditzbastara@gmail.com>

ADD ./target/slerp-io-oauth-api.jar /app/
CMD ["java", "-Xmx200m", "-jar", "/app/slerp-io-oauth-api.jar"]
EXPOSE 3001
