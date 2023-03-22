FROM openjdk:17
WORKDIR /app
COPY . .

CMD ./gradlew cucumberCli