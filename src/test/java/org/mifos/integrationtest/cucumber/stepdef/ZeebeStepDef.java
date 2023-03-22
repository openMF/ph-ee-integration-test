package org.mifos.integrationtest.cucumber.stepdef;

import com.google.gson.JsonObject;
import com.google.gson.JsonParser;
import io.cucumber.java.en.And;
import io.cucumber.java.en.When;
import io.restassured.RestAssured;
import io.restassured.builder.ResponseSpecBuilder;
import io.restassured.specification.RequestSpecification;
import org.apache.kafka.clients.consumer.ConsumerRecord;
import org.apache.kafka.clients.consumer.ConsumerRecords;
import org.apache.kafka.clients.consumer.KafkaConsumer;
import org.apache.kafka.common.serialization.StringDeserializer;
import org.json.JSONException;
import org.mifos.integrationtest.common.Utils;
import org.mifos.integrationtest.config.KafkaConfig;
import org.mifos.integrationtest.config.ZeebeOperationsConfig;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import java.net.InetAddress;
import java.net.UnknownHostException;
import java.time.Duration;
import java.util.*;

import static com.google.common.truth.Truth.assertThat;

public class ZeebeStepDef extends BaseStepDef{
    @Autowired
    ZeebeOperationsConfig zeebeOperationsConfig;
    @Autowired
    KafkaConfig kafkaConfig;

    public static int recordCount;
    Logger logger = LoggerFactory.getLogger(this.getClass());



    @When("I can start a test workflow n times and verify the output")
    public void iCanStartATestWorkflowTimesAndVerifyTheOutput() {
        RequestSpecification requestSpec = Utils.getDefaultSpec(BaseStepDef.tenant);
        logger.info("Started");
        StringBuilder jsonBuilder = new StringBuilder();
        jsonBuilder.append("{");
        jsonBuilder.append("\"message\": \"hello\",");
        jsonBuilder.append("}");

        String endpoint= zeebeOperationsConfig.workflowEndpoint +"zeebe-test";
        for (int i=0; i<=zeebeOperationsConfig.noOfWorkflows;i++) {

            logger.info("Endpoint: {}", endpoint);
            BaseStepDef.response = RestAssured.given(requestSpec)
                    .baseUri(zeebeOperationsConfig.zeebeOperationContactPoint)
                    .body(jsonBuilder.toString())
                    .expect()
                    .spec(new ResponseSpecBuilder().expectStatusCode(200).build())
                    .when()
                    .post(endpoint)
                    .andReturn().asString();


            logger.info("Workflow Response: {}", BaseStepDef.response);

        }
    }

    @And("I listen on zeebe-export topic")
    public void listen() throws JSONException, UnknownHostException {
        if(zeebeOperationsConfig.zeebeTest) {
            logger.info("kafka broker: {}", kafkaConfig.kafkaBroker);
            Properties props = new Properties();
            props.put("bootstrap.servers", kafkaConfig.kafkaBroker);
            props.put("client.id", InetAddress.getLocalHost().getHostName());
            props.put("group.id", InetAddress.getLocalHost().getHostName());
            props.put("key.deserializer", org.apache.kafka.common.serialization.StringDeserializer.class.getName());
            props.put("value.deserializer", StringDeserializer.class.getName());

            KafkaConsumer<String, String> consumer = new KafkaConsumer<>(props);
            consumer.subscribe(Collections.singletonList(kafkaConfig.kafkaTopic));
            while (recordCount <= zeebeOperationsConfig.noOfWorkflows) {
                ConsumerRecords<String, String> records = consumer.poll(Duration.ofMillis(100));
                for (ConsumerRecord<String, String> record : records) {
                    JsonObject payload = JsonParser.parseString(record.value()).getAsJsonObject();
                    JsonObject value = payload.get("value").getAsJsonObject();
                    String bpmnElementType = value.get("bpmnElementType").isJsonNull() ?"": value.get("bpmnElementType").getAsString();
                    String bpmnProcessId =value.get("bpmnProcessId").isJsonNull() ?"": value.get("bpmnProcessId").getAsString();
                    System.out.printf("offset = %d, key = %s, value = %s%n", record.offset(), record.key(), record.value());
                    logger.info("value {}", record.value());
                    if(bpmnElementType.matches("START_EVENT") && bpmnProcessId.matches("zeebe-test"))
                        recordCount++;
                }
            }
            consumer.close();
        }
    }

    @When("I can upload the bpmn to zeebe")
    public void iCanUploadTheBpmnToZeebe() {
        RequestSpecification requestSpec = Utils.getDefaultSpec(BaseStepDef.tenant);
        BaseStepDef.response = RestAssured.given(requestSpec)
                .baseUri(zeebeOperationsConfig.zeebeOperationContactPoint)
                .multiPart("file","https://raw.githubusercontent.com/apurbraj/ph-ee-env-labs/notificationbpmn/orchestration/feel/notification.bpmn")
                .expect()
                .spec(new ResponseSpecBuilder().expectStatusCode(200).build())
                .when()
                .post(zeebeOperationsConfig.uploadBpmnEndpoint)
                .andReturn().asString();


        logger.info("Workflow Response: {}", BaseStepDef.response);
    }

    @And("The number of workflows started should be equal to number of message consumed on kafka topic")
    public void theNumberOfWorkflowsStartedShouldBeEqualToNumberOfMessageConsumedOnKafkaTopic() {
        logger.info("No of workflows started: {}", zeebeOperationsConfig.noOfWorkflows);
        logger.info("No of records consume: {}", recordCount);
        assertThat(zeebeOperationsConfig.noOfWorkflows).isEqualTo(recordCount);
    }
}
