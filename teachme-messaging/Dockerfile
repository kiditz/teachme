FROM openjdk:8-jdk-alpine
MAINTAINER kiditz <kiditzbastara@gmail.com>
RUN apk --no-cache --update-cache add tzdata
RUN date
ENV TZ=Asia/Jakarta
ADD ./target/teachme-messaging.jar /app/
CMD ["java", "-Xmx200m", "-jar", "/app/teachme-messaging.jar"]
EXPOSE 3004
