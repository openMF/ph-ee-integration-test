package org.mifos.integrationtest.cucumber.stepdef;

import static com.github.tomakehurst.wiremock.client.WireMock.getAllServeEvents;
import static com.google.common.truth.Truth.assertThat;
import static java.util.concurrent.TimeUnit.SECONDS;
import static org.awaitility.Awaitility.await;
import static org.mifos.integrationtest.common.Utils.HEADER_FILENAME;
import static org.mifos.integrationtest.common.Utils.HEADER_JWS_SIGNATURE;
import static org.mifos.integrationtest.common.Utils.HEADER_PROGRAM_ID;
import static org.mifos.integrationtest.common.Utils.HEADER_PURPOSE;
import static org.mifos.integrationtest.common.Utils.HEADER_REGISTERING_INSTITUTE_ID;
import static org.mifos.integrationtest.common.Utils.QUERY_PARAM_TYPE;

import com.github.tomakehurst.wiremock.stubbing.ServeEvent;
import io.cucumber.core.internal.com.fasterxml.jackson.core.JsonProcessingException;
import io.cucumber.core.internal.com.fasterxml.jackson.databind.JsonNode;
import io.cucumber.java.After;
import io.cucumber.java.en.And;
import io.cucumber.java.en.Given;
import io.cucumber.java.en.Then;
import io.cucumber.java.en.When;
import io.restassured.RestAssured;
import io.restassured.builder.ResponseSpecBuilder;
import io.restassured.http.Header;
import io.restassured.http.Headers;
import io.restassured.response.Response;
import io.restassured.specification.RequestSpecification;
import java.io.File;
import java.io.FileReader;
import java.io.IOException;
import java.io.Reader;
import java.math.BigDecimal;
import java.security.InvalidKeyException;
import java.security.NoSuchAlgorithmException;
import java.security.spec.InvalidKeySpecException;
import java.util.ArrayList;
import java.util.List;
import java.util.UUID;
import javax.crypto.BadPaddingException;
import javax.crypto.IllegalBlockSizeException;
import javax.crypto.NoSuchPaddingException;
import org.apache.commons.csv.CSVFormat;
import org.apache.commons.csv.CSVParser;
import org.apache.commons.csv.CSVRecord;
import org.apache.commons.lang3.StringUtils;
import org.apache.commons.validator.routines.UrlValidator;
import org.json.JSONException;
import org.json.JSONObject;
import org.mifos.connector.common.operations.dto.Transfer;
import org.mifos.connector.common.operations.type.TransferStatus;
import org.mifos.integrationtest.common.Utils;
import org.mifos.integrationtest.common.dto.BatchRequestDTO;
import org.mifos.integrationtest.common.dto.Party;
import org.mifos.integrationtest.common.dto.operationsapp.ActuatorResponse;
import org.mifos.integrationtest.common.dto.operationsapp.BatchAndSubBatchSummaryResponse;
import org.mifos.integrationtest.common.dto.operationsapp.BatchDTO;
import org.mifos.integrationtest.common.dto.operationsapp.BatchDetailResponse;
import org.mifos.integrationtest.common.dto.operationsapp.BatchTransactionResponse;
import org.mifos.integrationtest.common.dto.operationsapp.PaymentBatchDetail;
import org.mifos.integrationtest.common.dto.operationsapp.SubBatchSummary;
import org.mifos.integrationtest.config.BulkProcessorConfig;
import org.mifos.integrationtest.config.ChannelConnectorConfig;
import org.mifos.integrationtest.config.MockPaymentSchemaConfig;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.core.env.Environment;

public class BatchApiStepDef extends BaseStepDef {

    @Autowired
    BulkProcessorConfig bulkProcessorConfig;

    @Value("${callback_url}")
    public String callbackURL;
    @Autowired
    MockPaymentSchemaConfig mockPaymentSchemaConfig;

    @Autowired
    ChannelConnectorConfig channelConnectorConfig;

    @Autowired
    Environment environment;

    @Given("I have a batch id from previous scenario")
    public void setBatchId() {
        // todo fix this
        if (scenarioScopeState.batchId == null || scenarioScopeState.batchId.isEmpty()) {
            scenarioScopeState.batchId = UUID.randomUUID().toString();
        }
        assertThat(scenarioScopeState.batchId).isNotNull();
    }

    @Given("I have a batch with id {string}")
    public void setBatchId(String batchId) {
        scenarioScopeState.batchId = batchId;
        assertThat(scenarioScopeState.batchId).isNotEmpty();
    }

    @Given("I have the demo csv file {string}")
    public void setFilename(String filename) {
        scenarioScopeState.filename = filename;
        File f = new File(Utils.getAbsoluteFilePathToResource(scenarioScopeState.filename));
        assertThat(f.exists()).isTrue();
        assertThat(scenarioScopeState.filename).isNotEmpty();
    }

    @And("I make sure there is no file")
    public void removeTheFilename() {
        clearFilename();
        assertThat(scenarioScopeState.filename).isNull();
    }

    public void clearFilename() {
        scenarioScopeState.filename = null;
    }

    @And("I have the registeringInstituteId {string}")
    public void setRegisteringInstituteId(String registeringInstituteId) {
        scenarioScopeState.registeringInstituteId = registeringInstituteId;
        assertThat(scenarioScopeState.registeringInstituteId).isNotNull();
    }

    @And("I have the programId {string}")
    public void setProgramId(String programId) {
        scenarioScopeState.programId = programId;
        assertThat(scenarioScopeState.programId).isNotNull();
    }

    @When("I call the batch summary API with expected status of {int} with total {int} txns")
    public void callBatchSummaryAPI(int expectedStatus, int totalTxns) {
        await().atMost(awaitMost+30, SECONDS).pollDelay(pollDelay, SECONDS).pollInterval(pollInterval, SECONDS).untilAsserted(() -> {
            RequestSpecification requestSpec = Utils.getDefaultSpec(scenarioScopeState.tenant);
            if (authEnabled) {
                requestSpec.header("Authorization", "Bearer " + scenarioScopeState.accessToken);
            }
            // requestSpec.queryParam("batchId", scenarioScopeDef.batchId);
            logger.info("Calling with batch id: {}", scenarioScopeState.batchId);

            scenarioScopeState.response = RestAssured.given(requestSpec).baseUri(operationsAppConfig.operationAppContactPoint).expect()
                    .spec(new ResponseSpecBuilder().expectStatusCode(expectedStatus).build()).when()
                    .get(operationsAppConfig.batchSummaryEndpoint + "/" + scenarioScopeState.batchId).andReturn().asString();

            logger.info("Batch Summary Response: " + scenarioScopeState.response);
            BatchDTO res = objectMapper.readValue(scenarioScopeState.response, BatchDTO.class);
            assertThat(res.getTotal()).isEqualTo(totalTxns);
            assertThat(res.getStatus()).isEqualTo("COMPLETED");
        });
    }

    @When("I call the batch summary API with expected status of {int} with total successfull {int} txns")
    public void callBatchSummaryAPIBulk(int expectedStatus, int totalTxns) {
        await().atMost(awaitMost, SECONDS).pollDelay(pollDelay, SECONDS).pollInterval(pollInterval, SECONDS).untilAsserted(() -> {
            RequestSpecification requestSpec = Utils.getDefaultSpec(scenarioScopeState.tenant);
            if (authEnabled) {
                requestSpec.header("Authorization", "Bearer " + scenarioScopeState.accessToken);
            }
            // requestSpec.queryParam("batchId", scenarioScopeDef.batchId);
            logger.info("Calling with batch id: {}", scenarioScopeState.batchId);

            scenarioScopeState.response = RestAssured.given(requestSpec).baseUri(operationsAppConfig.operationAppContactPoint).expect()
                    .spec(new ResponseSpecBuilder().expectStatusCode(expectedStatus).build()).when()
                    .get(operationsAppConfig.batchSummaryEndpoint + "/" + scenarioScopeState.batchId).andReturn().asString();

            logger.info("Batch Summary Response: " + scenarioScopeState.response);
            BatchDTO res = objectMapper.readValue(scenarioScopeState.response, BatchDTO.class);
            assertThat(res.getTotal()).isEqualTo(totalTxns);
        });
    }

    @When("I call the batch summary API for gsma with expected status of {int} with total {int} txns")
    public void callBatchSummaryAPIGSMA(int expectedStatus, int totalTxns) {
        await().atMost(awaitMost, SECONDS).pollDelay(pollDelay, SECONDS).pollInterval(pollInterval, SECONDS).untilAsserted(() -> {
            RequestSpecification requestSpec = Utils.getDefaultSpec(scenarioScopeState.tenant);
            if (authEnabled) {
                requestSpec.header("Authorization", "Bearer " + scenarioScopeState.accessToken);
            }
            // requestSpec.queryParam("batchId", scenarioScopeDef.batchId);
            logger.info("Calling with batch id: {}", scenarioScopeState.batchId);

            scenarioScopeState.response = RestAssured.given(requestSpec).baseUri(operationsAppConfig.operationAppContactPoint).expect()
                    .spec(new ResponseSpecBuilder().expectStatusCode(expectedStatus).build()).when()
                    .get(operationsAppConfig.batchSummaryEndpoint + "/" + scenarioScopeState.batchId).andReturn().asString();

            logger.info("Batch Summary Response: " + scenarioScopeState.response);
            BatchDTO res = objectMapper.readValue(scenarioScopeState.response, BatchDTO.class);
            assertThat(res.getTotal()).isEqualTo(totalTxns);
            assertThat(res.getTotal()).isEqualTo(res.getSuccessful());
        });
    }

    @Then("I am able to parse batch summary response")
    public void parseBatchSummaryResponse() {
        await().atMost(awaitMost, SECONDS).pollDelay(pollDelay, SECONDS).pollInterval(pollInterval, SECONDS).untilAsserted(() -> {
            BatchDTO batchDTO = null;
            assertThat(scenarioScopeState.response).isNotNull();
            assertThat(scenarioScopeState.response).isNotEmpty();
            try {
                batchDTO = objectMapper.readValue(scenarioScopeState.response, BatchDTO.class);
                scenarioScopeState.batchDTO = batchDTO;
            } catch (Exception e) {
                logger.error("Error parsing the batch summary response", e);
            }
            assertThat(scenarioScopeState.batchDTO).isNotNull();
        });
    }

    @And("Status of transaction is {string}")
    public void checkIfCreatedAtIsNotNull(String status) {
        assertThat(scenarioScopeState.batchDTO).isNotNull();
        assertThat(scenarioScopeState.batchDTO.getStatus()).isEqualTo(status);
    }

    @When("I call the batch details API with expected status of {int} with total {int} txns")
    public void callBatchDetailsAPI(int expectedStatus, int totalTxns) {
        await().atMost(awaitMost, SECONDS).pollDelay(pollDelay, SECONDS).pollInterval(pollInterval, SECONDS).untilAsserted(() -> {
            RequestSpecification requestSpec = Utils.getDefaultSpec(scenarioScopeState.tenant);
            if (authEnabled) {
                requestSpec.header("Authorization", "Bearer " + scenarioScopeState.accessToken);
            }
            requestSpec.queryParam("batchId", scenarioScopeState.batchId);
            logger.info("Calling with batch id : {}", scenarioScopeState.batchId);

            scenarioScopeState.response = RestAssured.given(requestSpec).baseUri(operationsAppConfig.operationAppContactPoint).expect()
                    .spec(new ResponseSpecBuilder().expectStatusCode(expectedStatus).build()).when()
                    .get(operationsAppConfig.batchDetailsEndpoint).andReturn().asString();

            logger.info("Batch Details Response: " + scenarioScopeState.response);
            BatchDetailResponse res = parseBatchDetailResponse(scenarioScopeState.response);
            assertThat(res.getContent().size()).isEqualTo(totalTxns);

        });
    }

    @When("I call the batch transactions endpoint with expected status of {int} without payload")
    public void iCallTheBatchTransactionsEndpointWithExpectedStatusOfWithoutPayload(int expectedStatus) {
        RequestSpecification requestSpec = Utils.getDefaultSpec(scenarioScopeState.tenant, scenarioScopeState.clientCorrelationId);
        requestSpec.header(HEADER_PURPOSE, "Integration test");
        requestSpec.header(HEADER_FILENAME, "");
        requestSpec.header(QUERY_PARAM_TYPE, "CSV");
        if (StringUtils.isNotBlank(scenarioScopeState.filename)) {
            requestSpec.header(HEADER_FILENAME, scenarioScopeState.filename);
        }
        if (scenarioScopeState.signature != null && !scenarioScopeState.signature.isEmpty()) {
            requestSpec.header(HEADER_JWS_SIGNATURE, scenarioScopeState.signature);
        }
        scenarioScopeState.response = RestAssured.given(requestSpec).baseUri(bulkProcessorConfig.bulkProcessorContactPoint).expect().when()
                .post(bulkProcessorConfig.bulkTransactionEndpoint).andReturn().asString();

        logger.info("Batch Transactions without payload Response: " + scenarioScopeState.response);
    }

    @And("I should get batchId in response")
    public void iShouldGetBatchIdInResponse() throws JSONException {
        JSONObject jsonObject = new JSONObject(scenarioScopeState.response);
        String pollingPath = (String) jsonObject.get("PollingPath");
        String[] response = pollingPath.split("/");
        logger.info("Batch Id: {}", response[response.length - 1]);
        scenarioScopeState.batchId = response[response.length - 1];

    }

    @When("I should call callbackUrl api")
    public void iShouldCallCallbackUrlApi() throws NoSuchPaddingException, IllegalBlockSizeException, IOException, NoSuchAlgorithmException,
            BadPaddingException, InvalidKeySpecException, InvalidKeyException {
        RequestSpecification requestSpec = Utils.getDefaultSpec(scenarioScopeState.tenant, scenarioScopeState.clientCorrelationId);
        String callbackReq = new String("The Batch Aggregation API was complete");
        logger.info(callbackReq);
        String jwsSignature = generateSignature(scenarioScopeState.clientCorrelationId, scenarioScopeState.tenant, callbackReq, false);

        requestSpec.header(HEADER_JWS_SIGNATURE, jwsSignature);

        scenarioScopeState.statusCode = RestAssured.given(requestSpec).body(callbackReq).post(bulkProcessorConfig.getCallbackUrl())
                .andReturn().getStatusCode();
    }

    @And("I have callbackUrl as {string}")
    public void iHaveCallbackUrlAs(String callBackUrl) {
        assertThat(callBackUrl).isNotEmpty();
        bulkProcessorConfig.setCallbackUrl(callBackUrl);
        scenarioScopeState.callbackUrl = callBackUrl;
    }

    @Then("I should get expected status of {int}")
    public void iShouldGetExpectedStatusOf(int expectedStatus) throws NoSuchPaddingException, IllegalBlockSizeException, IOException,
            NoSuchAlgorithmException, BadPaddingException, InvalidKeySpecException, InvalidKeyException {
        assertThat(scenarioScopeState.statusCode).isNotNull();
        assertThat(scenarioScopeState.statusCode).isEqualTo(expectedStatus);
        if (expectedStatus != 200) {
            bulkProcessorConfig.setRetryCount(bulkProcessorConfig.getRetryCount() - 1);
            iShouldCallCallbackUrlApi();
        }

    }

    @And("I have retry count as {int}")
    public void iHaveRetryCountAs(int retryCount) {
        assertThat(retryCount).isNotNull();
        bulkProcessorConfig.setRetryCount(retryCount);
    }

    @Then("I should get non empty response with failure and success percentage")
    public void iShouldGetNonEmptyResponseWithFailureAndSuccessPercentage() {
        assertThat(scenarioScopeState.response).isNotNull();
        assertThat(scenarioScopeState.response.contains("failPercentage"));
        assertThat(scenarioScopeState.response.contains("successPercentage"));
    }

    @When("I call the batch transactions endpoint with expected status of {int}")
    public void callBatchTransactionsEndpoint(int expectedStatus) {
        await().atMost(awaitMost, SECONDS).pollDelay(pollDelay, SECONDS).pollInterval(pollInterval, SECONDS).untilAsserted(() -> {
            RequestSpecification requestSpec = Utils.getDefaultSpec(scenarioScopeState.tenant, scenarioScopeState.clientCorrelationId);
            requestSpec.header(HEADER_PURPOSE, "Integartion test");
            requestSpec.header(HEADER_FILENAME, scenarioScopeState.filename);
            requestSpec.header(HEADER_REGISTERING_INSTITUTE_ID, "SocialWelfare");
            requestSpec.queryParam(QUERY_PARAM_TYPE, "CSV");
            requestSpec.header(QUERY_PARAM_TYPE, "CSV");
            if (scenarioScopeState.signature != null && !scenarioScopeState.signature.isEmpty()) {
                requestSpec.header(HEADER_JWS_SIGNATURE, scenarioScopeState.signature);
            }
            if (StringUtils.isNotBlank(scenarioScopeState.registeringInstituteId) && StringUtils.isNotBlank(scenarioScopeState.programId)) {
                requestSpec.header(HEADER_REGISTERING_INSTITUTE_ID, scenarioScopeState.registeringInstituteId);
                requestSpec.header(HEADER_PROGRAM_ID, scenarioScopeState.programId);
            }

            File f = new File(Utils.getAbsoluteFilePathToResource(scenarioScopeState.filename));
            Response resp = RestAssured.given(requestSpec).baseUri(bulkProcessorConfig.bulkProcessorContactPoint)
                    .contentType("multipart/form-data").multiPart("data", f).expect()
                    .spec(new ResponseSpecBuilder().expectStatusCode(expectedStatus).build()).when()
                    .post(bulkProcessorConfig.bulkTransactionEndpoint).then().extract().response();

            scenarioScopeState.response = resp.andReturn().asString();
            scenarioScopeState.restResponseObject = resp;

            Headers allHeaders = resp.getHeaders();
            for (Header header : allHeaders) {
                logger.debug("{}", header.getName());
                logger.debug("{}", header.getValue());
            }
            logger.info("Batch Transactions Response: {}", scenarioScopeState.response);
        });
    }

    @And("I should have {string} and {string} in response")
    public void iShouldHaveAndInResponse(String pollingpath, String suggestedcallback) {
        assertThat(scenarioScopeState.response).contains(pollingpath);
        assertThat(scenarioScopeState.response).contains(suggestedcallback);

    }

    @And("I have callbackUrl as simulated url")
    public void iHaveCallbackUrlAsSimulatedUrl() {
        bulkProcessorConfig.setCallbackUrl(bulkProcessorConfig.simulateEndpoint);
        scenarioScopeState.callbackUrl = bulkProcessorConfig.getCallbackUrl();
    }

    @And("I fetch batch ID from batch transaction API's response")
    public void iFetchBatchIDFromBatchTransactionAPISResponse() {

        assertThat(scenarioScopeState.batchTransactionResponse).isNotNull();
        scenarioScopeState.batchId = fetchBatchId(scenarioScopeState.batchTransactionResponse);
        logger.info("batchId: {}", scenarioScopeState.batchId);
        assertThat(scenarioScopeState.batchId).isNotEmpty();
    }

    @Then("I check for result file URL validity")
    public void iCheckForResultFileURLValidity() {
        assertThat(isValidURL(scenarioScopeState.batchAndSubBatchSummaryResponse.getFile())).isTrue();
    }

    boolean isValidURL(String url) {
        UrlValidator validator = new UrlValidator(UrlValidator.ALLOW_LOCAL_URLS);
        return validator.isValid(url);
    }

    @And("I am able to parse batch transactions response")
    public void parseBatchTransactionsResponseStep() {
        parseBatchTransactionsResponse();
    }

    private String fetchBatchId(BatchTransactionResponse batchTransactionResponse) {
        String pollingPath = batchTransactionResponse.getPollingPath().replace("\"", "");
        String[] pollingPathSplitResult = pollingPath.split("/");
        return pollingPathSplitResult[pollingPathSplitResult.length - 1];
    }

    private void parseBatchTransactionsResponse() {
        assertThat(scenarioScopeState.response).isNotEmpty();
        BatchTransactionResponse response = null;
        try {
            response = objectMapper.readValue(scenarioScopeState.response, BatchTransactionResponse.class);
            scenarioScopeState.batchTransactionResponse = response;
        } catch (Exception e) {
            logger.error("Error parsing the batch transaction response", e);
        }
        assertThat(scenarioScopeState.batchTransactionResponse).isNotNull();
    }

    @And("I should have matching total txn count and successful txn count in response")
    public void iShouldHaveMatchingTotalTxnCountAndSuccessfulTxnCountInResponse() {
        await().atMost(awaitMost, SECONDS).untilAsserted(() -> {
            assertThat(scenarioScopeState.batchDTO).isNotNull();
            assertThat(scenarioScopeState.batchDTO.getTotal()).isNotNull();
            assertThat(scenarioScopeState.batchDTO.getSuccessful()).isNotNull();
            assertThat(scenarioScopeState.batchDTO.getTotal()).isGreaterThan(0);
            assertThat(scenarioScopeState.batchDTO.getSuccessful()).isGreaterThan(0);
            assertThat(scenarioScopeState.batchDTO.getTotal()).isEqualTo(scenarioScopeState.batchDTO.getSuccessful());

        });
    }

    @And("My total txns {} and successful txn count in response should Match")
    public void iShouldHaveMatchingTotalTxnCountAndSuccessfulTxnCount(int totalTxnsCount) {
        await().atMost(awaitMost, SECONDS).untilAsserted(() -> {
            assertThat(scenarioScopeState.batchDTO).isNotNull();
            assertThat(scenarioScopeState.batchDTO.getTotal()).isNotNull();
            assertThat(scenarioScopeState.batchDTO.getSuccessful()).isNotNull();
            assertThat(scenarioScopeState.batchDTO.getTotal()).isGreaterThan(0);
            assertThat(scenarioScopeState.batchDTO.getSuccessful()).isGreaterThan(0);
            assertThat(totalTxnsCount).isEqualTo(scenarioScopeState.batchDTO.getSuccessful());

        });
    }

    @When("I can assert the approved count as {int} and approved amount as {int}")
    public void iCanAssertTheApprovedCountAsAndApprovedAmountAs(int count, int amount) {
        BigDecimal approvedCount = scenarioScopeState.batchDTO.getApprovedCount();
        BigDecimal approvedAmount = scenarioScopeState.batchDTO.getApprovedAmount();
        assertThat(approvedCount).isEqualTo(new BigDecimal(count));
        assertThat(approvedAmount).isEqualTo(new BigDecimal(amount));
    }

    @When("I call the batch transactions raw endpoint with expected status of {int}")
    public void callBatchTransactionsRawEndpoint(int expectedStatus) {
        RequestSpecification requestSpec = Utils.getDefaultSpec(scenarioScopeState.tenant, scenarioScopeState.clientCorrelationId);
        requestSpec.header(HEADER_PURPOSE, "Integartion test");
        requestSpec.header(HEADER_FILENAME, "");
        requestSpec.header(QUERY_PARAM_TYPE, "RAW");
        if (scenarioScopeState.signature != null && !scenarioScopeState.signature.isEmpty()) {
            requestSpec.header(HEADER_JWS_SIGNATURE, scenarioScopeState.signature);
        }
        if (StringUtils.isNotBlank(scenarioScopeState.registeringInstituteId) && StringUtils.isNotBlank(scenarioScopeState.programId)) {
            requestSpec.header(HEADER_REGISTERING_INSTITUTE_ID, scenarioScopeState.registeringInstituteId);
            requestSpec.header(HEADER_PROGRAM_ID, scenarioScopeState.programId);
        }

        File f = new File(Utils.getAbsoluteFilePathToResource(scenarioScopeState.filename));
        Response resp = RestAssured.given(requestSpec).baseUri(bulkProcessorConfig.bulkProcessorContactPoint)
                .contentType("application/json").body(scenarioScopeState.batchRawRequest).expect()
                // .spec(new ResponseSpecBuilder().expectStatusCode(expectedStatus).build())
                .when().post(bulkProcessorConfig.bulkTransactionEndpoint).then().extract().response();

        scenarioScopeState.response = resp.andReturn().asString();
        scenarioScopeState.restResponseObject = resp;

        Headers allHeaders = resp.getHeaders();
        for (Header header : allHeaders) {
            logger.info(" : {}", header.getName());
            logger.info("{}", header.getValue());
        }
        logger.info("Batch Transactions Response: " + scenarioScopeState.response);
    }

    @And("I can mock the Batch Transaction Request DTO without payer info")
    public void mockBatchTransactionRequestDTOWithoutPayer() throws JsonProcessingException {
        BatchRequestDTO batchRequestDTO = mockBatchTransactionRequestDTO("mojaloop");
        batchRequestDTO = setCreditPartyInMockBatchTransactionRequestDTO(batchRequestDTO);
        batchRequestDTO = setDebitPartyInMockBatchTransactionRequestDTO(batchRequestDTO);
        assertThat(batchRequestDTO).isNotNull();
        assertThat(batchRequestDTO.getCurrency()).isNotEmpty();
        assertThat(batchRequestDTO.getAmount()).isNotEmpty();
        assertThat(batchRequestDTO.getPaymentMode()).isNotEmpty();
        assertThat(batchRequestDTO.getCreditParty()).isNotEmpty();
        scenarioScopeState.batchRequestDTO = batchRequestDTO;

        List<BatchRequestDTO> batchRequestDTOS = new ArrayList<>();
        batchRequestDTOS.add(batchRequestDTO);
        scenarioScopeState.batchRawRequest = objectMapper.writeValueAsString(batchRequestDTOS);
        assertThat(scenarioScopeState.batchRawRequest).isNotEmpty();
    }

    public BatchDetailResponse parseBatchDetailResponse(String jsonString) {
        BatchDetailResponse batchDetailResponse = null;
        try {
            batchDetailResponse = objectMapper.readValue(jsonString, BatchDetailResponse.class);
        } catch (Exception e) {
            logger.error("Error parsing the batch detail response", e);
        }
        return batchDetailResponse;
    }

    @Then("I should get transactions with note set as {string}")
    public void iShouldGetTransactionsWithNoteSetAs(String duplicateTransactionNote) {
        logger.info(scenarioScopeState.response);
        BatchDetailResponse batchDetailResponse = parseBatchDetailResponse(scenarioScopeState.response);
        int duplicateRecordCount = 0;
        assertThat(batchDetailResponse).isNotNull();
        List<Transfer> transfers = batchDetailResponse.getContent();

        for (Transfer transfer : transfers) {
            if (transfer.getErrorInformation() == null) {
                continue;
            }
            if (transfer.getErrorInformation().toLowerCase().contains(duplicateTransactionNote.toLowerCase())) {
                duplicateRecordCount++;
            }
        }
        assertThat(duplicateRecordCount).isGreaterThan(0);
    }

    @And("All the duplicate transaction should have status as Failed")
    public void duplicateTransactionStatusShouldBeFailed() {
        BatchDetailResponse batchDetailResponse = parseBatchDetailResponse(scenarioScopeState.response);
        assertThat(batchDetailResponse).isNotNull();
        List<Transfer> transfers = batchDetailResponse.getContent();

        for (Transfer transfer : transfers) {
            if (transfer.getErrorInformation() == null) {
                continue;
            }
            if (transfer.getErrorInformation().toLowerCase().contains("duplicate")) {
                assertThat(transfer.getStatus().equals(TransferStatus.FAILED));
            }
        }
    }

    @After("@batch-teardown")
    public void operationsBatchTestTearDown() {
        logger.info("Running @ops-batch-teardown");
        batchTearDown();
    }

    @When("I call the batch aggregate API with expected status of {int} with total {int} txns")
    public void iCallTheBatchAggregateAPIWithExpectedStatusOf(int expectedStatus, int totalTxns) {
        await().atMost(awaitMost, SECONDS).pollDelay(5, SECONDS).pollInterval(pollInterval, SECONDS).untilAsserted(() -> {

            RequestSpecification requestSpec = Utils.getDefaultSpec(scenarioScopeState.tenant);
            logger.info("Calling with batch id: {}", scenarioScopeState.batchId);

            scenarioScopeState.response = RestAssured.given(requestSpec).baseUri(operationsAppConfig.operationAppContactPoint).expect()
                    .spec(new ResponseSpecBuilder().expectStatusCode(expectedStatus).build()).when()
                    .get(operationsAppConfig.batchAggregateEndpoint + scenarioScopeState.batchId).andReturn().asString();
            logger.info("Batch Aggregate Response: " + scenarioScopeState.response);
            BatchDTO res = objectMapper.readValue(scenarioScopeState.response, BatchDTO.class);
            assertThat(res.getTotal()).isEqualTo(totalTxns);
        });
    }

    public void batchTearDown() {
        scenarioScopeState.filename = null;
        scenarioScopeState.batchId = null;
        scenarioScopeState.response = null;
    }

    public BatchRequestDTO mockBatchTransactionRequestDTO(String paymentMode) {
        BatchRequestDTO batchRequestDTO = new BatchRequestDTO();
        batchRequestDTO.setAmount("100");
        batchRequestDTO.setCurrency("USD");
        batchRequestDTO.setPaymentMode(paymentMode);
        batchRequestDTO.setDescriptionText("Integration test");
        batchRequestDTO.setRequestId(UUID.randomUUID().toString());
        return batchRequestDTO;
    }

    public BatchRequestDTO setCreditPartyInMockBatchTransactionRequestDTO(BatchRequestDTO batchRequestDTO) {
        List<Party> creditParties = new ArrayList<>();
        creditParties.add(new Party("accountNumber", "003001003873110196"));
        batchRequestDTO.setCreditParty(creditParties);
        return batchRequestDTO;
    }

    public BatchRequestDTO setDebitPartyInMockBatchTransactionRequestDTO(BatchRequestDTO batchRequestDTO) {
        List<Party> debitParties = new ArrayList<>();
        debitParties.add(new Party("accountNumber", "003001003879112168"));
        batchRequestDTO.setDebitParty(debitParties);
        return batchRequestDTO;
    }

    private String fetchBatchId(String response) {
        String[] split = response.split(",");
        return split[0].substring(31);
    }

    @Given("I have a batch id {string}")
    public void iHaveABatchId(String batchID) {
        scenarioScopeState.batchId = batchID;
    }

    @And("I call the batch summary API for sub batch summary with expected status of {int}")
    public void iCallTheBatchSummaryAPIForSubBatchSummaryWithExpectedStatusOf(int expectedStatus) {
        RequestSpecification requestSpec = Utils.getDefaultSpec(scenarioScopeState.tenant);
        if (authEnabled) {
            requestSpec.header("Authorization", "Bearer " + scenarioScopeState.accessToken);
        }
        // requestSpec.queryParam("batchId", scenarioScopeDef.batchId);
        logger.info("Calling with batch id: {}", scenarioScopeState.batchId);

        scenarioScopeState.response = RestAssured.given(requestSpec).baseUri(operationsAppConfig.operationAppContactPoint).expect()
                .spec(new ResponseSpecBuilder().expectStatusCode(expectedStatus).build()).when()
                .get(operationsAppConfig.batchSummaryEndpoint + scenarioScopeState.batchId).andReturn().asString();

        logger.info("Batch Summary Response: " + scenarioScopeState.response);
    }

    @Then("I am able to parse sub batch summary response")
    public void iAmAbleToParseSubBatchSummaryResponse() {
        scenarioScopeState.batchAndSubBatchSummaryResponse = null;
        assertThat(scenarioScopeState.response).isNotNull();
        assertThat(scenarioScopeState.response).isNotEmpty();
        try {
            scenarioScopeState.batchAndSubBatchSummaryResponse = objectMapper.readValue(scenarioScopeState.response,
                    BatchAndSubBatchSummaryResponse.class);
        } catch (Exception e) {
            logger.error("Error parsing the batch summary response", e);
        }
        assertThat(scenarioScopeState.batchAndSubBatchSummaryResponse).isNotNull();
    }

    @And("I call the sub batch summary API for sub batch summary with expected status of {int} and total count {int}")
    public void iCallTheSubBatchSummaryAPIForSubBatchSummaryWithExpectedStatusOf(int expectedStatus, int totalCount) {
        await().atMost(awaitMost + 15, SECONDS).pollDelay(pollDelay, SECONDS).pollInterval(pollInterval, SECONDS).untilAsserted(() -> {
            RequestSpecification requestSpec = Utils.getDefaultSpec(scenarioScopeState.tenant);
            requestSpec.header("X-Correlation-ID", scenarioScopeState.clientCorrelationId);
            if (authEnabled) {
                requestSpec.header("Authorization", "Bearer " + scenarioScopeState.accessToken);
            }
            // requestSpec.queryParam("batchId", scenarioScopeDef.batchId);
            logger.info("Calling with batch id: {}", scenarioScopeState.clientCorrelationId);
            logger.info("Calling with batch id: {}",
                    operationsAppConfig.operationAppContactPoint + operationsAppConfig.batchesEndpoint + "/" + scenarioScopeState.batchId);

            scenarioScopeState.response = RestAssured.given(requestSpec).baseUri(operationsAppConfig.operationAppContactPoint).expect()
                    .spec(new ResponseSpecBuilder().expectStatusCode(expectedStatus).build()).when()
                    .get(operationsAppConfig.batchesEndpoint + "/" + scenarioScopeState.batchId).andReturn().asString();

            logger.info("Sub batch Summary Response: " + scenarioScopeState.response);

            BatchAndSubBatchSummaryResponse res = objectMapper.readValue(scenarioScopeState.response,
                    BatchAndSubBatchSummaryResponse.class);
            assertThat(totalCount).isEqualTo(res.getSuccessful());
        });
    }

    @And("I call the sub batch summary API for result file url with expected status of {int}")
    public void iCallTheSubBatchSummaryAPIForResutlFileURLWithExpectedStatusOf(int expectedStatus) {
        await().atMost(awaitMost, SECONDS).pollDelay(pollDelay, SECONDS).pollInterval(pollInterval, SECONDS).untilAsserted(() -> {
            RequestSpecification requestSpec = Utils.getDefaultSpec(scenarioScopeState.tenant);
            requestSpec.header("X-Correlation-ID", scenarioScopeState.clientCorrelationId);
            if (authEnabled) {
                requestSpec.header("Authorization", "Bearer " + scenarioScopeState.accessToken);
            }
            // requestSpec.queryParam("batchId", scenarioScopeDef.batchId);
            logger.info("Calling with batch id: {}", scenarioScopeState.clientCorrelationId);
            logger.info("Calling with batch id: {}",
                    operationsAppConfig.operationAppContactPoint + operationsAppConfig.batchesEndpoint + "/" + scenarioScopeState.batchId);

            scenarioScopeState.response = RestAssured.given(requestSpec).baseUri(operationsAppConfig.operationAppContactPoint).expect()
                    .spec(new ResponseSpecBuilder().expectStatusCode(expectedStatus).build()).when()
                    .get(operationsAppConfig.batchesEndpoint + "/" + scenarioScopeState.batchId).andReturn().asString();

            logger.info("Sub batch Summary Response: " + scenarioScopeState.response);

            BatchAndSubBatchSummaryResponse res = objectMapper.readValue(scenarioScopeState.response,
                    BatchAndSubBatchSummaryResponse.class);
            assertThat(res.getFile()).isNotNull();
        });
    }

    @And("I should assert total txn count and successful txn count in response")
    public void iShouldAssertTotalTxnCountAndSuccessfulTxnCountInResponse() {
        await().atMost(awaitMost, SECONDS).untilAsserted(() -> {
            assertThat(scenarioScopeState.batchAndSubBatchSummaryResponse.getTotal()).isGreaterThan(0);
            assertThat(scenarioScopeState.batchAndSubBatchSummaryResponse.getSuccessful()).isGreaterThan(0);
            assertThat(scenarioScopeState.batchAndSubBatchSummaryResponse.getTotal())
                    .isEqualTo(scenarioScopeState.batchAndSubBatchSummaryResponse.getSuccessful());
        });
    }

    @And("Total transaction in batch should add up to total transaction in each sub batch")
    public void matchTotalSubBatchTxnAndBatchTxnCount() {
        assertThat(scenarioScopeState.batchAndSubBatchSummaryResponse).isNotNull();
        assertThat(Integer.parseInt(String.valueOf(scenarioScopeState.batchAndSubBatchSummaryResponse.getTotalSubBatches())))
                .isGreaterThan(1);
        long batchTotal = scenarioScopeState.batchAndSubBatchSummaryResponse.getTotal();
        long subBatchTotal = 0L;
        for (SubBatchSummary subBatchSummary : scenarioScopeState.batchAndSubBatchSummaryResponse.getSubBatchSummaryList()) {
            subBatchTotal += subBatchSummary.getTotal();
        }
        assertThat(batchTotal).isEqualTo(subBatchTotal);
    }

    @And("I call the payment batch detail API with expected status of {int} with total {int} txns")
    public void iCallThePaymentBatchDetailAPIWithExpectedStatusOf(int expectedStatus, int totaltxns) {
        await().atMost(awaitMost, SECONDS).pollDelay(pollDelay, SECONDS).pollInterval(pollInterval, SECONDS).untilAsserted(() -> {
            RequestSpecification requestSpec = Utils.getDefaultSpec(scenarioScopeState.tenant);
            requestSpec.header("X-Correlation-ID", scenarioScopeState.clientCorrelationId);
            requestSpec.queryParam("associations", "all");
            if (authEnabled) {
                requestSpec.header("Authorization", "Bearer " + scenarioScopeState.accessToken);
            }
            // requestSpec.queryParam("batchId", scenarioScopeDef.batchId);
            logger.info("Calling with batch id: {}", scenarioScopeState.batchId);
            logger.info("Calling with URL: {}",
                    operationsAppConfig.operationAppContactPoint + operationsAppConfig.batchesEndpoint + "/" + scenarioScopeState.batchId);

            scenarioScopeState.response = RestAssured.given(requestSpec).baseUri(operationsAppConfig.operationAppContactPoint).expect()
                    .spec(new ResponseSpecBuilder().expectStatusCode(expectedStatus).build()).when()
                    .get(operationsAppConfig.batchesEndpoint + "/" + scenarioScopeState.batchId).andReturn().asString();

            logger.info("Batch Payment Detail Response: " + scenarioScopeState.response);
            PaymentBatchDetail res = objectMapper.readValue(scenarioScopeState.response, PaymentBatchDetail.class);
            assertThat(res.getInstructionList().size()).isEqualTo(totaltxns);
            assertThat(res.getSuccessful()).isEqualTo(totaltxns);
        });
    }

    @Then("I am able to parse payment batch detail response")
    public void iAmAbleToParsePaymentBatchDetailResponse() {
        scenarioScopeState.paymentBatchDetail = null;
        assertThat(scenarioScopeState.response).isNotNull();
        assertThat(scenarioScopeState.response).isNotEmpty();
        try {
            scenarioScopeState.paymentBatchDetail = objectMapper.readValue(scenarioScopeState.response, PaymentBatchDetail.class);
        } catch (Exception e) {
            logger.error("Error parsing the payment batch detail response", e);
        }
        assertThat(scenarioScopeState.paymentBatchDetail).isNotNull();
    }

    @And("I should assert total txn count and successful txn count in payment batch detail response")
    public void iShouldAssertTotalTxnCountAndSuccessfulTxnCountInPaymentBatchDetailResponse() {
        assertThat(scenarioScopeState.paymentBatchDetail).isNotNull();
        assertThat(scenarioScopeState.paymentBatchDetail.getSubBatchList().size()).isEqualTo(3);
        assertThat(scenarioScopeState.paymentBatchDetail.getInstructionList().size()).isEqualTo(12);
        assertThat(scenarioScopeState.paymentBatchDetail.getTotal()).isEqualTo(scenarioScopeState.paymentBatchDetail.getSuccessful());
        assertThat(scenarioScopeState.paymentBatchDetail.getStatus().equals("COMPLETED"));
        assertThat(scenarioScopeState.paymentBatchDetail.getCompletedAt()).isNotNull();
    }

    @Then("I should be able to extract response body from callback for batch")
    public void iShouldBeAbleToExtractResponseBodyFromCallbackForBatch() {
        boolean flag = false;
        List<ServeEvent> allServeEvents = getAllServeEvents();
        for (int i = allServeEvents.size() - 1; i >= 0; i--) {
            ServeEvent request = allServeEvents.get(i);
            if (!(request.getRequest().getBodyAsString()).isEmpty()) {
                JsonNode rootNode = null;
                flag = true;
                try {
                    rootNode = objectMapper.readTree(request.getRequest().getBody());
                    logger.info("Rootnode value:" + rootNode);
                    assertThat(rootNode).isNotNull();
                } catch (IOException e) {
                    throw new RuntimeException(e);
                }

            }
            assertThat(flag).isTrue();
        }
    }

    @When("I call the batch transactions endpoint with expected status of {int} and callbackurl as {string}")
    public void iCallTheBatchTransactionsEndpointWithExpectedStatusOfAndCallbackurlAs(int expectedStatus, String callback) {
        RequestSpecification requestSpec = Utils.getDefaultSpec(scenarioScopeState.tenant, scenarioScopeState.clientCorrelationId);
        requestSpec.header(HEADER_PURPOSE, "Integration test");
        requestSpec.header(HEADER_FILENAME, scenarioScopeState.filename);
        requestSpec.queryParam(QUERY_PARAM_TYPE, "CSV");
        requestSpec.header(QUERY_PARAM_TYPE, "CSV");
        requestSpec.header("X-CallbackURL", callbackURL + callback);
        if (scenarioScopeState.signature != null && !scenarioScopeState.signature.isEmpty()) {
            requestSpec.header(HEADER_JWS_SIGNATURE, scenarioScopeState.signature);
        }
        if (StringUtils.isNotBlank(scenarioScopeState.registeringInstituteId) && StringUtils.isNotBlank(scenarioScopeState.programId)) {
            requestSpec.header(HEADER_REGISTERING_INSTITUTE_ID, scenarioScopeState.registeringInstituteId);
            requestSpec.header(HEADER_PROGRAM_ID, scenarioScopeState.programId);
        }

        File f = new File(Utils.getAbsoluteFilePathToResource(scenarioScopeState.filename));
        Response resp = RestAssured.given(requestSpec).baseUri(bulkProcessorConfig.bulkProcessorContactPoint)
                .contentType("multipart/form-data").multiPart("data", f).expect()
                .spec(new ResponseSpecBuilder().expectStatusCode(expectedStatus).build()).when()
                .post(bulkProcessorConfig.bulkTransactionEndpoint).then().extract().response();

        scenarioScopeState.response = resp.andReturn().asString();
        scenarioScopeState.restResponseObject = resp;

        Headers allHeaders = resp.getHeaders();
        for (Header header : allHeaders) {
            logger.info("{}", header.getName());
            logger.info(header.getValue());
        }
        logger.info("Batch Transactions Response: " + scenarioScopeState.response);
    }

    @And("I should assert total txn count and successful txn count in payment batch detail response for batch account lookup")
    public void iShouldAssertTotalTxnCountAndSuccessfulTxnCountInPaymentBatchDetailResponseForBatchAccountLookup() {
        assertThat(scenarioScopeState.paymentBatchDetail).isNotNull();
        assertThat(scenarioScopeState.paymentBatchDetail.getInstructionList().size()).isEqualTo(3);
    }

    @And("I am able to parse actuator response")
    public void iAmAbleToParseActuatorResponse() {
        ActuatorResponse actuatorResponse = null;
        assertThat(scenarioScopeState.response).isNotNull();
        assertThat(scenarioScopeState.response).isNotEmpty();
        try {
            actuatorResponse = objectMapper.readValue(scenarioScopeState.response, ActuatorResponse.class);
            scenarioScopeState.actuatorResponse = actuatorResponse;
        } catch (Exception e) {
            logger.error("Error parsing the actuator response", e);
        }
        assertThat(scenarioScopeState.actuatorResponse).isNotNull();
    }

    @And("Status of service is {string}")
    public void statusOfServiceIs(String status) {
        assertThat(scenarioScopeState.actuatorResponse).isNotNull();
        assertThat(scenarioScopeState.actuatorResponse.getStatus()).isEqualTo(status);
    }

    @When("I call the actuator API with Contactpoint {string} and endpoint {string}")
    public void iCallTheActuatorAPIWithContactpointAndEndpoint(String config, String endpoint) {
        RequestSpecification requestSpec = Utils.getDefaultSpec();

        Response resp = RestAssured.given(requestSpec).baseUri(environment.getProperty(config)).expect()
                .spec(new ResponseSpecBuilder().build()).when().get(endpoint).then().extract().response();

        scenarioScopeState.response = resp.andReturn().asString();
        scenarioScopeState.restResponseObject = resp;

        logger.info("Actuator Response: " + scenarioScopeState.response);
    }

    @And("I can mock the Batch Transaction Request DTO without closed loop")
    public void iCanMockTheBatchTransactionRequestDTOWithoutClosedLoop() throws JsonProcessingException {
        BatchRequestDTO batchRequestDTO = mockBatchTransactionRequestDTO("closedloop");
        batchRequestDTO = setCreditPartyInMockBatchTransactionRequestDTO(batchRequestDTO);
        batchRequestDTO = setDebitPartyInMockBatchTransactionRequestDTO(batchRequestDTO);
        assertThat(batchRequestDTO).isNotNull();
        assertThat(batchRequestDTO.getCurrency()).isNotEmpty();
        assertThat(batchRequestDTO.getAmount()).isNotEmpty();
        assertThat(batchRequestDTO.getPaymentMode()).isNotEmpty();
        assertThat(batchRequestDTO.getCreditParty()).isNotEmpty();
        BaseStepDef.batchRequestDTO = batchRequestDTO;

        List<BatchRequestDTO> batchRequestDTOS = new ArrayList<>();
        batchRequestDTOS.add(batchRequestDTO);
        BaseStepDef.batchRawRequest = objectMapper.writeValueAsString(batchRequestDTOS);
        assertThat(BaseStepDef.batchRawRequest).isNotEmpty();
    }

    @And("I create a list of payee identifiers from csv file")
    public void iCreateAListOfPayeeIdentifiersFromCsvFile() {
        String csvFile = Utils.getAbsoluteFilePathToResource(scenarioScopeState.filename);
        scenarioScopeState.payeeIdentifiers = new ArrayList<>();
        try (Reader reader = new FileReader(csvFile);
                CSVParser csvParser = new CSVParser(reader, CSVFormat.DEFAULT.withFirstRecordAsHeader())) {
            for (CSVRecord csvRecord : csvParser) {
                String payeeIdentifier = csvRecord.get("payee_identifier");
                scenarioScopeState.payeeIdentifiers.add(payeeIdentifier);
            }
        } catch (Exception e) {
            logger.error(e.getMessage());
        }
    }
}
