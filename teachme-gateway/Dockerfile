FROM openjdk:8-jdk-alpine
MAINTAINER kiditz <kiditzbastara@gmail.com>

ADD ./target/teachme-gateway.jar /app/
CMD ["java", "-Xmx200m", "-jar", "/app/teachme-gateway.jar"]
EXPOSE 5000
