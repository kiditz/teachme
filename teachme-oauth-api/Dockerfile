FROM openjdk:8-jdk-alpine
MAINTAINER kiditz <kiditzbastara@gmail.com>

ADD ./target/teachme-oauth-api.jar /app/
CMD ["java", "-Xmx200m", "-jar", "/app/teachme-oauth-api.jar"]
EXPOSE 5001
