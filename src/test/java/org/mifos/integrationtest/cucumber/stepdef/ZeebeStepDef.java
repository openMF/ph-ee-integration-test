package org.mifos.integrationtest.cucumber.stepdef;

import com.google.gson.JsonObject;
import com.google.gson.JsonParser;
import io.cucumber.java.en.And;
import io.cucumber.java.en.Then;
import io.cucumber.java.en.When;
import io.restassured.RestAssured;
import io.restassured.builder.MultiPartSpecBuilder;
import io.restassured.builder.ResponseSpecBuilder;
import io.restassured.specification.MultiPartSpecification;
import io.restassured.specification.RequestSpecification;
import org.apache.commons.io.IOUtils;
import org.apache.kafka.clients.consumer.ConsumerConfig;
import org.apache.kafka.clients.consumer.ConsumerRecord;
import org.apache.kafka.clients.consumer.ConsumerRecords;
import org.apache.kafka.clients.consumer.KafkaConsumer;
import org.apache.kafka.common.serialization.StringDeserializer;
import org.mifos.integrationtest.common.Utils;
import org.mifos.integrationtest.config.KafkaConfig;
import org.mifos.integrationtest.config.ZeebeOperationsConfig;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.kafka.annotation.KafkaListener;

import java.io.IOException;
import java.net.*;
import java.nio.charset.StandardCharsets;
import java.time.Duration;
import java.util.*;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.concurrent.TimeUnit;

import static com.google.common.truth.Truth.assertThat;

public class ZeebeStepDef extends BaseStepDef{
    @Autowired
    ZeebeOperationsConfig zeebeOperationsConfig;
    @Autowired
    KafkaConfig kafkaConfig;

    public static int startEventCount;
    public static int endEventCount;

    private static final String BPMN_FILE_URL = "https://raw.githubusercontent.com/arkadasfynarfin/ph-ee-env-labs/zeebe-upgrade/orchestration/feel/zeebetest.bpmn";

    Logger logger = LoggerFactory.getLogger(this.getClass());

    @When("I upload the BPMN file to zeebe")
    public void uploadBpmnFileToZeebe() throws MalformedURLException {
        String fileContent = getFileContent(BPMN_FILE_URL);
        BaseStepDef.response = uploadBPMNFile(fileContent);
        logger.info("BPMN file upload response: {}", BaseStepDef.response);
    }

    @And("I can start test workflow n times with message {string} and listen on kafka topic")
    public void iCanStartTestWorkflowNTimesWithMessage(String message) {
        logger.info("Test workflow started");
        String requestBody = String.format("{ \"message\": \"%s\" }", message);
        String endpoint= zeebeOperationsConfig.workflowEndpoint +"zeebetest";
        logger.info("Endpoint: {}", endpoint);
        logger.info("Request Body: {}", requestBody);
        ExecutorService apiExecutorService = Executors.newCachedThreadPool();
        KafkaConsumer<String, String> consumer = createKafkaConsumer();
        consumer.subscribe(Collections.singletonList(kafkaConfig.kafkaTopic));
        Set<String> startProcessInstanceKeySet = new HashSet<>();
        Set<String> endProcessInstanceKeySet = new HashSet<>();
        List<JsonObject> recordValues = new ArrayList<>();


        for (int i=0; i<zeebeOperationsConfig.noOfWorkflows;i++) {
            final int workflowNumber = i;
            apiExecutorService.execute(()->{
                BaseStepDef.response = sendWorkflowRequest(endpoint, requestBody);
                logger.info("Workflow Response {}: {}", workflowNumber, BaseStepDef.response);

            });
            ConsumerRecords<String, String> records = consumer.poll(Duration.ofMillis(100));
            logger.info("No. of records received: {}", records.count());

            if(!records.isEmpty()){
                for(ConsumerRecord<String, String> record: records){
                    logger.info("Key: {} ===== Value: {}", record.key(), record.value());
                    JsonObject payload = JsonParser.parseString(record.value()).getAsJsonObject();
                    JsonObject value = payload.get("value").getAsJsonObject();
                    recordValues.add(value);
                }
            }
        }

        for(int i=0; i<50; i++){
            logger.info("Additional consumer polls");
            ConsumerRecords<String, String> records = consumer.poll(Duration.ofMillis(100));
            logger.info("No. of records received: {}", records.count());

            if(!records.isEmpty()){
                for(ConsumerRecord<String, String> record: records){
                    logger.info("Key: {} ===== Value: {}", record.key(), record.value());
                    JsonObject payload = JsonParser.parseString(record.value()).getAsJsonObject();
                    JsonObject value = payload.get("value").getAsJsonObject();
                    recordValues.add(value);
                }
            }
        }

        apiExecutorService.shutdown();
        try {
            apiExecutorService.awaitTermination(Integer.MAX_VALUE, TimeUnit.MICROSECONDS);
        } catch (InterruptedException e) {
            e.printStackTrace();
        }

        for(JsonObject recordValue: recordValues){
            String bpmnElementType = recordValue.get("bpmnElementType") == null ? "" : recordValue.get("bpmnElementType").getAsString();
            String bpmnProcessId = recordValue.get("bpmnProcessId").getAsString();
            String processInstanceKey = recordValue.get("processInstanceKey").getAsString();

            if(bpmnElementType.equals("START_EVENT")){
                if(!startProcessInstanceKeySet.contains(processInstanceKey) && bpmnProcessId.equals("zeebetest")){
                    startEventCount++;
                    startProcessInstanceKeySet.add(processInstanceKey);
                }
            }

            if(bpmnElementType.equals("END_EVENT")){
                if(!endProcessInstanceKeySet.contains(processInstanceKey) && bpmnProcessId.equals("zeebetest")){
                    endEventCount++;
                    endProcessInstanceKeySet.add(processInstanceKey);
                }
            }
        }

        logger.info("Test workflow ended");
    }

    @And("The number of workflows started should be equal to number of message consumed on kafka topic")
    public void verifyNumberOfWorkflowsStartedEqualsNumberOfMessagesConsumed() {
        logger.info("No of workflows started: {}", zeebeOperationsConfig.noOfWorkflows);
        logger.info("No of records consumed: {}", startEventCount);
        logger.info("No of records exported: {}", endEventCount);
        assertThat(startEventCount).isEqualTo(zeebeOperationsConfig.noOfWorkflows);
        assertThat(startEventCount).isEqualTo(zeebeOperationsConfig.noOfWorkflows);
    }

    private String getFileContent(String fileUrl) {
        try {
            return IOUtils.toString(URI.create(fileUrl), StandardCharsets.UTF_8);
        } catch (IOException e) {
            throw new RuntimeException(e);
        }
    }

    private String uploadBPMNFile(String fileContent) throws MalformedURLException {
        RequestSpecification requestSpec = Utils.getDefaultSpec();
        return RestAssured.given(requestSpec)
                .baseUri(zeebeOperationsConfig.zeebeOperationContactPoint)
                .multiPart(getMultiPart(fileContent))
                .expect()
                .spec(new ResponseSpecBuilder().expectStatusCode(200).build())
                .when()
                .post(zeebeOperationsConfig.uploadBpmnEndpoint)
                .andReturn().asString();
    }

    private MultiPartSpecification getMultiPart(String fileContent) {
        return new MultiPartSpecBuilder(fileContent.getBytes()).
                fileName("zeebe-test.bpmn").
                controlName("file").
                mimeType("text/plain").
                build();
    }

    private String sendWorkflowRequest(String endpoint, String requestBody){
        RequestSpecification requestSpec = Utils.getDefaultSpec();
        return RestAssured.given(requestSpec)
                .baseUri(zeebeOperationsConfig.zeebeOperationContactPoint)
                .body(requestBody)
                .expect()
                .spec(new ResponseSpecBuilder().expectStatusCode(200).build())
                .when()
                .post(endpoint)
                .andReturn().asString();
    }

    private KafkaConsumer<String, String> createKafkaConsumer() {
//        logger.info("inside create kafka consumer");
//        Properties properties = new Properties();
//        properties.put("bootstrap.servers", kafkaConfig.kafkaBroker);
//        properties.put("client.id", InetAddress.getLocalHost().getHostName());
//        properties.put("group.id", InetAddress.getLocalHost().getHostName());
//        properties.put("key.deserializer", StringDeserializer.class.getName());
//        properties.put("value.deserializer", StringDeserializer.class.getName());
//        logger.info("properties initialzed");
//        KafkaConsumer<String, String> consumer = new KafkaConsumer<>(properties);
//        logger.info("consumer created");
//        consumer.subscribe(Collections.singletonList(kafkaConfig.kafkaTopic));
//        return consumer;
        String hostname = null;
        try {
            hostname = InetAddress.getLocalHost().getHostName();
        } catch (UnknownHostException e) {
            logger.error("failed to resolve local hostname, picking random clientId");
            hostname = UUID.randomUUID().toString();
        }
        Map<String, Object> properties = new HashMap<>();
        properties.put(ConsumerConfig.BOOTSTRAP_SERVERS_CONFIG, kafkaConfig.kafkaBroker);
        properties.put(ConsumerConfig.CLIENT_ID_CONFIG, hostname);
        properties.put(ConsumerConfig.GROUP_ID_CONFIG, "group-1");
        properties.put(ConsumerConfig.KEY_DESERIALIZER_CLASS_CONFIG, StringDeserializer.class.getName());
        properties.put(ConsumerConfig.VALUE_DESERIALIZER_CLASS_CONFIG, StringDeserializer.class.getName());
        return new KafkaConsumer<>(properties);
    }

    private void processKafkaRecords(ConsumerRecords<String, String> records){
        for (ConsumerRecord<String, String> record : records) {
            JsonObject payload = JsonParser.parseString(record.value()).getAsJsonObject();
            JsonObject value = payload.get("value").getAsJsonObject();
            String bpmnElementType = value.get("bpmnElementType").isJsonNull() ?"": value.get("bpmnElementType").getAsString();
            String bpmnProcessId =value.get("bpmnProcessId").isJsonNull() ?"": value.get("bpmnProcessId").getAsString();
            System.out.printf("offset = %d, key = %s, value = %s%n", record.offset(), record.key(), record.value());
            logger.info("value {}", record.value());

            if(bpmnElementType.matches("START_EVENT") && bpmnProcessId.matches("zeebetest"))
                startEventCount++;

            if(bpmnElementType.matches("END_EVENT") && bpmnProcessId.matches("zeebetest"))
                endEventCount++;
        }
    }
}
