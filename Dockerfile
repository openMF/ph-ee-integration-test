FROM eclipse-temurin:17-jdk
COPY . ph-ee-connector-integration-test
WORKDIR /ph-ee-connector-integration-test

#RUN ./gradlew cucumberCli
