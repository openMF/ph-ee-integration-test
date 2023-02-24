FROM openjdk:17
COPY . ph-ee-connector-integration-test
WORKDIR /ph-ee-connector-integration-test

RUN ./gradlew cucumberCli