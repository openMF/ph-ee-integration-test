FROM openjdk:17

RUN addgroup --system <group>
RUN adduser --system <user> --ingroup <group>
USER <user>:<group>

COPY . ph-ee-connector-integration-test
WORKDIR /ph-ee-connector-integration-test

#RUN ./gradlew cucumberCli
