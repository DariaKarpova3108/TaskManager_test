FROM eclipse-temurin:21-jdk

ARG GRADLE_VERSION=8.7

RUN apt-get update && apt-get install -yq make unzip

WORKDIR /app

COPY . .

RUN ./gradlew installDist

CMD ["java", "-jar", "app-0.0.1-SNAPSHOT.jar"]

EXPOSE 7070
