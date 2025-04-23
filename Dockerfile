FROM openjdk:11.0.6-jre-slim-buster

COPY build/libs/springboot-0.0.1-SNAPSHOT.jar springboot-app.jar

CMD ["java", "-jar", "springboot-app.jar" ]
