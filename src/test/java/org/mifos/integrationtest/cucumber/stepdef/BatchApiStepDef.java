package org.mifos.integrationtest.cucumber.stepdef;

import static com.github.tomakehurst.wiremock.client.WireMock.getAllServeEvents;
import static com.google.common.truth.Truth.assertThat;
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
import java.io.IOException;
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
import org.apache.commons.lang3.StringUtils;
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
        if (scenarioScopeDef.batchId == null || scenarioScopeDef.batchId.isEmpty()) {
            scenarioScopeDef.batchId = UUID.randomUUID().toString();
        }
        assertThat(scenarioScopeDef.batchId).isNotNull();
    }

    @Given("I have a batch with id {string}")
    public void setBatchId(String batchId) {
        scenarioScopeDef.batchId = batchId;
        assertThat(scenarioScopeDef.batchId).isNotEmpty();
    }

    @Given("I have the demo csv file {string}")
    public void setFilename(String filename) {
        scenarioScopeDef.filename = filename;
        File f = new File(Utils.getAbsoluteFilePathToResource(scenarioScopeDef.filename));
        assertThat(f.exists()).isTrue();
        assertThat(scenarioScopeDef.filename).isNotEmpty();
    }

    @And("I make sure there is no file")
    public void removeTheFilename() {
        clearFilename();
        assertThat(scenarioScopeDef.filename).isNull();
    }

    public void clearFilename() {
        scenarioScopeDef.filename = null;
    }

    @And("I have the registeringInstituteId {string}")
    public void setRegisteringInstituteId(String registeringInstituteId) {
        scenarioScopeDef.registeringInstituteId = registeringInstituteId;
        assertThat(scenarioScopeDef.registeringInstituteId).isNotNull();
    }

    @And("I have the programId {string}")
    public void setProgramId(String programId) {
        scenarioScopeDef.programId = programId;
        assertThat(scenarioScopeDef.programId).isNotNull();
    }

    @When("I call the batch summary API with expected status of {int}")
    public void callBatchSummaryAPI(int expectedStatus) {
        RequestSpecification requestSpec = Utils.getDefaultSpec(scenarioScopeDef.tenant);
        if (authEnabled) {
            requestSpec.header("Authorization", "Bearer " + scenarioScopeDef.accessToken);
        }
        // requestSpec.queryParam("batchId", scenarioScopeDef.batchId);
        logger.info("Calling with batch id: {}", scenarioScopeDef.batchId);

        scenarioScopeDef.response = RestAssured.given(requestSpec).baseUri(operationsAppConfig.operationAppContactPoint).expect()
                .spec(new ResponseSpecBuilder().expectStatusCode(expectedStatus).build()).when()
                .get(operationsAppConfig.batchSummaryEndpoint + "/" + scenarioScopeDef.batchId).andReturn().asString();

        logger.info("Batch Summary Response: " + scenarioScopeDef.response);
    }

    @Then("I am able to parse batch summary response")
    public void parseBatchSummaryResponse() {
        BatchDTO batchDTO = null;
        assertThat(scenarioScopeDef.response).isNotNull();
        assertThat(scenarioScopeDef.response).isNotEmpty();
        try {
            batchDTO = objectMapper.readValue(scenarioScopeDef.response, BatchDTO.class);
            scenarioScopeDef.batchDTO = batchDTO;
        } catch (Exception e) {
            logger.error("Error parsing the batch summary response", e);
        }
        assertThat(scenarioScopeDef.batchDTO).isNotNull();
    }

    @And("Status of transaction is {string}")
    public void checkIfCreatedAtIsNotNull(String status) {
        assertThat(scenarioScopeDef.batchDTO).isNotNull();
        assertThat(scenarioScopeDef.batchDTO.getStatus()).isEqualTo(status);
    }

    @When("I call the batch details API with expected status of {int}")
    public void callBatchDetailsAPI(int expectedStatus) {
        RequestSpecification requestSpec = Utils.getDefaultSpec(scenarioScopeDef.tenant);
        if (authEnabled) {
            requestSpec.header("Authorization", "Bearer " + scenarioScopeDef.accessToken);
        }
        requestSpec.queryParam("batchId", scenarioScopeDef.batchId);

        scenarioScopeDef.response = RestAssured.given(requestSpec).baseUri(operationsAppConfig.operationAppContactPoint).expect()
                .spec(new ResponseSpecBuilder().expectStatusCode(expectedStatus).build()).when()
                .get(operationsAppConfig.batchDetailsEndpoint).andReturn().asString();

        logger.info("Batch Details Response: " + scenarioScopeDef.response);
    }

    @When("I call the batch transactions endpoint with expected status of {int} without payload")
    public void iCallTheBatchTransactionsEndpointWithExpectedStatusOfWithoutPayload(int expectedStatus) {
        RequestSpecification requestSpec = Utils.getDefaultSpec(scenarioScopeDef.tenant, scenarioScopeDef.clientCorrelationId);
        requestSpec.header(HEADER_PURPOSE, "Integration test");
        requestSpec.header(HEADER_FILENAME, "");
        requestSpec.header(QUERY_PARAM_TYPE, "CSV");
        if (StringUtils.isNotBlank(scenarioScopeDef.filename)) {
            requestSpec.header(HEADER_FILENAME, scenarioScopeDef.filename);
        }
        if (scenarioScopeDef.signature != null && !scenarioScopeDef.signature.isEmpty()) {
            requestSpec.header(HEADER_JWS_SIGNATURE, scenarioScopeDef.signature);
        }
        scenarioScopeDef.response = RestAssured.given(requestSpec).baseUri(bulkProcessorConfig.bulkProcessorContactPoint).expect().when()
                .post(bulkProcessorConfig.bulkTransactionEndpoint).andReturn().asString();

        logger.info("Batch Transactions without payload Response: " + scenarioScopeDef.response);
    }

    @And("I should get batchId in response")
    public void iShouldGetBatchIdInResponse() throws JSONException {
        JSONObject jsonObject = new JSONObject(scenarioScopeDef.response);
        String pollingPath = (String) jsonObject.get("PollingPath");
        String[] response = pollingPath.split("/");
        logger.info("Batch Id: {}", response[response.length - 1]);
        scenarioScopeDef.batchId = response[response.length - 1];

    }

    @When("I should call callbackUrl api")
    public void iShouldCallCallbackUrlApi() throws NoSuchPaddingException, IllegalBlockSizeException, IOException, NoSuchAlgorithmException,
            BadPaddingException, InvalidKeySpecException, InvalidKeyException {
        RequestSpecification requestSpec = Utils.getDefaultSpec(scenarioScopeDef.tenant, scenarioScopeDef.clientCorrelationId);
        String callbackReq = new String("The Batch Aggregation API was complete");
        logger.info(callbackReq);
        String jwsSignature = generateSignature(scenarioScopeDef.clientCorrelationId, scenarioScopeDef.tenant, callbackReq, false);

        requestSpec.header(HEADER_JWS_SIGNATURE, jwsSignature);

        scenarioScopeDef.statusCode = RestAssured.given(requestSpec).body(callbackReq).post(bulkProcessorConfig.getCallbackUrl())
                .andReturn().getStatusCode();
    }

    @And("I have callbackUrl as {string}")
    public void iHaveCallbackUrlAs(String callBackUrl) {
        assertThat(callBackUrl).isNotEmpty();
        bulkProcessorConfig.setCallbackUrl(callBackUrl);
        scenarioScopeDef.callbackUrl = callBackUrl;
    }

    @Then("I should get expected status of {int}")
    public void iShouldGetExpectedStatusOf(int expectedStatus) throws NoSuchPaddingException, IllegalBlockSizeException, IOException,
            NoSuchAlgorithmException, BadPaddingException, InvalidKeySpecException, InvalidKeyException {
        assertThat(scenarioScopeDef.statusCode).isNotNull();
        assertThat(scenarioScopeDef.statusCode).isEqualTo(expectedStatus);
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
        assertThat(scenarioScopeDef.response).isNotNull();
        assertThat(scenarioScopeDef.response.contains("failPercentage"));
        assertThat(scenarioScopeDef.response.contains("successPercentage"));
    }

    @When("I call the batch transactions endpoint with expected status of {int}")
    public void callBatchTransactionsEndpoint(int expectedStatus) {
        RequestSpecification requestSpec = Utils.getDefaultSpec(scenarioScopeDef.tenant, scenarioScopeDef.clientCorrelationId);
        requestSpec.header(HEADER_PURPOSE, "Integartion test");
        requestSpec.header(HEADER_FILENAME, scenarioScopeDef.filename);
        requestSpec.header(HEADER_REGISTERING_INSTITUTE_ID, "SocialWelfare");
        requestSpec.queryParam(QUERY_PARAM_TYPE, "CSV");
        requestSpec.header(QUERY_PARAM_TYPE, "CSV");
        if (scenarioScopeDef.signature != null && !scenarioScopeDef.signature.isEmpty()) {
            requestSpec.header(HEADER_JWS_SIGNATURE, scenarioScopeDef.signature);
        }
        if (StringUtils.isNotBlank(scenarioScopeDef.registeringInstituteId) && StringUtils.isNotBlank(scenarioScopeDef.programId)) {
            requestSpec.header(HEADER_REGISTERING_INSTITUTE_ID, scenarioScopeDef.registeringInstituteId);
            requestSpec.header(HEADER_PROGRAM_ID, scenarioScopeDef.programId);
        }

        File f = new File(Utils.getAbsoluteFilePathToResource(scenarioScopeDef.filename));
        Response resp = RestAssured.given(requestSpec).baseUri(bulkProcessorConfig.bulkProcessorContactPoint)
                .contentType("multipart/form-data").multiPart("data", f).expect()
                .spec(new ResponseSpecBuilder().expectStatusCode(expectedStatus).build()).when()
                .post(bulkProcessorConfig.bulkTransactionEndpoint).then().extract().response();

        scenarioScopeDef.response = resp.andReturn().asString();
        scenarioScopeDef.restResponseObject = resp;

        Headers allHeaders = resp.getHeaders();
        for (Header header : allHeaders) {
            logger.debug("{}", header.getName());
            logger.debug("{}", header.getValue());
        }
        logger.info("Batch Transactions Response: " + scenarioScopeDef.response);
    }

    @And("I should have {string} and {string} in response")
    public void iShouldHaveAndInResponse(String pollingpath, String suggestedcallback) {
        assertThat(scenarioScopeDef.response).contains(pollingpath);
        assertThat(scenarioScopeDef.response).contains(suggestedcallback);

    }

    @And("I have callbackUrl as simulated url")
    public void iHaveCallbackUrlAsSimulatedUrl() {
        bulkProcessorConfig.setCallbackUrl(bulkProcessorConfig.simulateEndpoint);
        scenarioScopeDef.callbackUrl = bulkProcessorConfig.getCallbackUrl();
    }

    @And("I fetch batch ID from batch transaction API's response")
    public void iFetchBatchIDFromBatchTransactionAPISResponse() {
        assertThat(scenarioScopeDef.batchTransactionResponse).isNotNull();
        scenarioScopeDef.batchId = fetchBatchId(scenarioScopeDef.batchTransactionResponse);
        logger.info("batchId: {}", scenarioScopeDef.batchId);
        assertThat(scenarioScopeDef.batchId).isNotEmpty();
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
        assertThat(scenarioScopeDef.response).isNotEmpty();
        BatchTransactionResponse response = null;
        try {
            response = objectMapper.readValue(scenarioScopeDef.response, BatchTransactionResponse.class);
            scenarioScopeDef.batchTransactionResponse = response;
        } catch (Exception e) {
            logger.error("Error parsing the batch transaction response", e);
        }
        assertThat(scenarioScopeDef.batchTransactionResponse).isNotNull();
    }

    @And("I should have matching total txn count and successful txn count in response")
    public void iShouldHaveMatchingTotalTxnCountAndSuccessfulTxnCountInResponse() {
        assertThat(scenarioScopeDef.batchDTO).isNotNull();
        assertThat(scenarioScopeDef.batchDTO.getTotal()).isNotNull();
        assertThat(scenarioScopeDef.batchDTO.getSuccessful()).isNotNull();
        assertThat(scenarioScopeDef.batchDTO.getTotal()).isGreaterThan(0);
        assertThat(scenarioScopeDef.batchDTO.getSuccessful()).isGreaterThan(0);
        assertThat(scenarioScopeDef.batchDTO.getTotal()).isEqualTo(scenarioScopeDef.batchDTO.getSuccessful());

    }

    @When("I can assert the approved count as {int} and approved amount as {int}")
    public void iCanAssertTheApprovedCountAsAndApprovedAmountAs(int count, int amount) {
        BigDecimal approvedCount = scenarioScopeDef.batchDTO.getApprovedCount();
        BigDecimal approvedAmount = scenarioScopeDef.batchDTO.getApprovedAmount();
        assertThat(approvedCount).isEqualTo(new BigDecimal(count));
        assertThat(approvedAmount).isEqualTo(new BigDecimal(amount));
    }

    @When("I call the batch transactions raw endpoint with expected status of {int}")
    public void callBatchTransactionsRawEndpoint(int expectedStatus) {
        RequestSpecification requestSpec = Utils.getDefaultSpec(scenarioScopeDef.tenant, scenarioScopeDef.clientCorrelationId);
        requestSpec.header(HEADER_PURPOSE, "Integartion test");
        requestSpec.header(HEADER_FILENAME, "");
        requestSpec.header(QUERY_PARAM_TYPE, "RAW");
        if (scenarioScopeDef.signature != null && !scenarioScopeDef.signature.isEmpty()) {
            requestSpec.header(HEADER_JWS_SIGNATURE, scenarioScopeDef.signature);
        }
        if (StringUtils.isNotBlank(scenarioScopeDef.registeringInstituteId) && StringUtils.isNotBlank(scenarioScopeDef.programId)) {
            requestSpec.header(HEADER_REGISTERING_INSTITUTE_ID, scenarioScopeDef.registeringInstituteId);
            requestSpec.header(HEADER_PROGRAM_ID, scenarioScopeDef.programId);
        }

        File f = new File(Utils.getAbsoluteFilePathToResource(scenarioScopeDef.filename));
        Response resp = RestAssured.given(requestSpec).baseUri(bulkProcessorConfig.bulkProcessorContactPoint)
                .contentType("application/json").body(scenarioScopeDef.batchRawRequest).expect()
                // .spec(new ResponseSpecBuilder().expectStatusCode(expectedStatus).build())
                .when().post(bulkProcessorConfig.bulkTransactionEndpoint).then().extract().response();

        scenarioScopeDef.response = resp.andReturn().asString();
        scenarioScopeDef.restResponseObject = resp;

        Headers allHeaders = resp.getHeaders();
        for (Header header : allHeaders) {
            logger.info(" : {}", header.getName());
            logger.info("{}", header.getValue());
        }
        logger.info("Batch Transactions Response: " + scenarioScopeDef.response);
    }

    @And("I can mock the Batch Transaction Request DTO without payer info")
    public void mockBatchTransactionRequestDTOWithoutPayer() throws JsonProcessingException {
        BatchRequestDTO batchRequestDTO = mockBatchTransactionRequestDTO();
        batchRequestDTO = setCreditPartyInMockBatchTransactionRequestDTO(batchRequestDTO);
        batchRequestDTO = setDebitPartyInMockBatchTransactionRequestDTO(batchRequestDTO);
        assertThat(batchRequestDTO).isNotNull();
        assertThat(batchRequestDTO.getCurrency()).isNotEmpty();
        assertThat(batchRequestDTO.getAmount()).isNotEmpty();
        assertThat(batchRequestDTO.getSubType()).isNotEmpty();
        assertThat(batchRequestDTO.getCreditParty()).isNotEmpty();
        scenarioScopeDef.batchRequestDTO = batchRequestDTO;

        List<BatchRequestDTO> batchRequestDTOS = new ArrayList<>();
        batchRequestDTOS.add(batchRequestDTO);
        scenarioScopeDef.batchRawRequest = objectMapper.writeValueAsString(batchRequestDTOS);
        assertThat(scenarioScopeDef.batchRawRequest).isNotEmpty();
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
        logger.info(scenarioScopeDef.response);
        BatchDetailResponse batchDetailResponse = parseBatchDetailResponse(scenarioScopeDef.response);
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
        BatchDetailResponse batchDetailResponse = parseBatchDetailResponse(scenarioScopeDef.response);
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

    @When("I call the batch aggregate API with expected status of {int}")
    public void iCallTheBatchAggregateAPIWithExpectedStatusOf(int expectedStatus) {
        RequestSpecification requestSpec = Utils.getDefaultSpec(scenarioScopeDef.tenant);
        logger.info("Calling with batch id: {}", scenarioScopeDef.batchId);

        scenarioScopeDef.response = RestAssured.given(requestSpec).baseUri(operationsAppConfig.operationAppContactPoint).expect()
                .spec(new ResponseSpecBuilder().expectStatusCode(expectedStatus).build()).when()
                .get(operationsAppConfig.batchAggregateEndpoint + scenarioScopeDef.batchId).andReturn().asString();
        logger.info("Batch Aggregate Response: " + scenarioScopeDef.response);
    }

    public void batchTearDown() {
        scenarioScopeDef.filename = null;
        scenarioScopeDef.batchId = null;
        scenarioScopeDef.response = null;
    }

    public BatchRequestDTO mockBatchTransactionRequestDTO() {
        BatchRequestDTO batchRequestDTO = new BatchRequestDTO();
        batchRequestDTO.setAmount("100");
        batchRequestDTO.setCurrency("USD");
        batchRequestDTO.setSubType("mojaloop");
        batchRequestDTO.setDescriptionText("Integration test");
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
        scenarioScopeDef.batchId = batchID;
    }

    @And("I call the batch summary API for sub batch summary with expected status of {int}")
    public void iCallTheBatchSummaryAPIForSubBatchSummaryWithExpectedStatusOf(int expectedStatus) {
        RequestSpecification requestSpec = Utils.getDefaultSpec(scenarioScopeDef.tenant);
        if (authEnabled) {
            requestSpec.header("Authorization", "Bearer " + scenarioScopeDef.accessToken);
        }
        // requestSpec.queryParam("batchId", scenarioScopeDef.batchId);
        logger.info("Calling with batch id: {}", scenarioScopeDef.batchId);

        scenarioScopeDef.response = RestAssured.given(requestSpec).baseUri(operationsAppConfig.operationAppContactPoint).expect()
                .spec(new ResponseSpecBuilder().expectStatusCode(expectedStatus).build()).when()
                .get(operationsAppConfig.batchSummaryEndpoint + scenarioScopeDef.batchId).andReturn().asString();

        logger.info("Batch Summary Response: " + scenarioScopeDef.response);
    }

    @Then("I am able to parse sub batch summary response")
    public void iAmAbleToParseSubBatchSummaryResponse() {
        scenarioScopeDef.batchAndSubBatchSummaryResponse = null;
        assertThat(scenarioScopeDef.response).isNotNull();
        assertThat(scenarioScopeDef.response).isNotEmpty();
        try {
            scenarioScopeDef.batchAndSubBatchSummaryResponse = objectMapper.readValue(scenarioScopeDef.response,
                    BatchAndSubBatchSummaryResponse.class);
        } catch (Exception e) {
            logger.error("Error parsing the batch summary response", e);
        }
        assertThat(scenarioScopeDef.batchAndSubBatchSummaryResponse).isNotNull();
    }

    @And("I call the sub batch summary API for sub batch summary with expected status of {int}")
    public void iCallTheSubBatchSummaryAPIForSubBatchSummaryWithExpectedStatusOf(int expectedStatus) {
        RequestSpecification requestSpec = Utils.getDefaultSpec(scenarioScopeDef.tenant);
        requestSpec.header("X-Correlation-ID", scenarioScopeDef.clientCorrelationId);
        if (authEnabled) {
            requestSpec.header("Authorization", "Bearer " + scenarioScopeDef.accessToken);
        }
        // requestSpec.queryParam("batchId", scenarioScopeDef.batchId);
        logger.info("Calling with batch id: {}", scenarioScopeDef.clientCorrelationId);
        logger.info("Calling with batch id: {}",
                operationsAppConfig.operationAppContactPoint + operationsAppConfig.batchesEndpoint + "/" + scenarioScopeDef.batchId);

        scenarioScopeDef.response = RestAssured.given(requestSpec).baseUri(operationsAppConfig.operationAppContactPoint).expect()
                .spec(new ResponseSpecBuilder().expectStatusCode(expectedStatus).build()).when()
                .get(operationsAppConfig.batchesEndpoint + "/" + scenarioScopeDef.batchId).andReturn().asString();

        logger.info("Sub batch Summary Response: " + scenarioScopeDef.response);
    }

    @And("I should assert total txn count and successful txn count in response")
    public void iShouldAssertTotalTxnCountAndSuccessfulTxnCountInResponse() {
        assertThat(scenarioScopeDef.batchAndSubBatchSummaryResponse).isNotNull();
        assertThat(scenarioScopeDef.batchAndSubBatchSummaryResponse.getTotal()).isNotNull();
        assertThat(scenarioScopeDef.batchAndSubBatchSummaryResponse.getSuccessful()).isNotNull();
        assertThat(scenarioScopeDef.batchAndSubBatchSummaryResponse.getTotal()).isGreaterThan(0);
        assertThat(scenarioScopeDef.batchAndSubBatchSummaryResponse.getSuccessful()).isGreaterThan(0);
        assertThat(scenarioScopeDef.batchAndSubBatchSummaryResponse.getTotal())
                .isEqualTo(scenarioScopeDef.batchAndSubBatchSummaryResponse.getSuccessful());
    }

    @And("Total transaction in batch should add up to total transaction in each sub batch")
    public void matchTotalSubBatchTxnAndBatchTxnCount() {
        assertThat(scenarioScopeDef.batchAndSubBatchSummaryResponse).isNotNull();
        assertThat(Integer.parseInt(scenarioScopeDef.batchAndSubBatchSummaryResponse.getTotalSubBatches())).isGreaterThan(1);
        long batchTotal = scenarioScopeDef.batchAndSubBatchSummaryResponse.getTotal();
        long subBatchTotal = 0L;
        for (SubBatchSummary subBatchSummary : scenarioScopeDef.batchAndSubBatchSummaryResponse.getSubBatchSummaryList()) {
            subBatchTotal += subBatchSummary.getTotal();
        }
        assertThat(batchTotal).isEqualTo(subBatchTotal);

    }

    @And("I call the payment batch detail API with expected status of {int}")
    public void iCallThePaymentBatchDetailAPIWithExpectedStatusOf(int expectedStatus) {
        RequestSpecification requestSpec = Utils.getDefaultSpec(scenarioScopeDef.tenant);
        requestSpec.header("X-Correlation-ID", scenarioScopeDef.clientCorrelationId);
        requestSpec.queryParam("associations", "all");
        if (authEnabled) {
            requestSpec.header("Authorization", "Bearer " + scenarioScopeDef.accessToken);
        }
        // requestSpec.queryParam("batchId", scenarioScopeDef.batchId);
        logger.info("Calling with batch id: {}", scenarioScopeDef.clientCorrelationId);
        logger.info("Calling with batch id: {}",
                operationsAppConfig.operationAppContactPoint + operationsAppConfig.batchesEndpoint + "/" + scenarioScopeDef.batchId);

        scenarioScopeDef.response = RestAssured.given(requestSpec).baseUri(operationsAppConfig.operationAppContactPoint).expect()
                .spec(new ResponseSpecBuilder().expectStatusCode(expectedStatus).build()).when()
                .get(operationsAppConfig.batchesEndpoint + "/" + scenarioScopeDef.batchId).andReturn().asString();

        logger.info("Batch Payment Detail Response: " + scenarioScopeDef.response);
    }

    @Then("I am able to parse payment batch detail response")
    public void iAmAbleToParsePaymentBatchDetailResponse() {
        scenarioScopeDef.paymentBatchDetail = null;
        assertThat(scenarioScopeDef.response).isNotNull();
        assertThat(scenarioScopeDef.response).isNotEmpty();
        try {
            scenarioScopeDef.paymentBatchDetail = objectMapper.readValue(scenarioScopeDef.response, PaymentBatchDetail.class);
        } catch (Exception e) {
            logger.error("Error parsing the payment batch detail response", e);
        }
        assertThat(scenarioScopeDef.paymentBatchDetail).isNotNull();
    }

    @And("I should assert total txn count and successful txn count in payment batch detail response")
    public void iShouldAssertTotalTxnCountAndSuccessfulTxnCountInPaymentBatchDetailResponse() {
        assertThat(scenarioScopeDef.paymentBatchDetail).isNotNull();
        assertThat(scenarioScopeDef.paymentBatchDetail.getSubBatchList().size()).isEqualTo(3);
        assertThat(scenarioScopeDef.paymentBatchDetail.getInstructionList().size()).isEqualTo(12);
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
        RequestSpecification requestSpec = Utils.getDefaultSpec(scenarioScopeDef.tenant, scenarioScopeDef.clientCorrelationId);
        requestSpec.header(HEADER_PURPOSE, "Integration test");
        requestSpec.header(HEADER_FILENAME, scenarioScopeDef.filename);
        requestSpec.queryParam(QUERY_PARAM_TYPE, "CSV");
        requestSpec.header(QUERY_PARAM_TYPE, "CSV");
        requestSpec.header("X-CallbackURL", callbackURL + callback);
        if (scenarioScopeDef.signature != null && !scenarioScopeDef.signature.isEmpty()) {
            requestSpec.header(HEADER_JWS_SIGNATURE, scenarioScopeDef.signature);
        }
        if (StringUtils.isNotBlank(scenarioScopeDef.registeringInstituteId) && StringUtils.isNotBlank(scenarioScopeDef.programId)) {
            requestSpec.header(HEADER_REGISTERING_INSTITUTE_ID, scenarioScopeDef.registeringInstituteId);
            requestSpec.header(HEADER_PROGRAM_ID, scenarioScopeDef.programId);
        }

        File f = new File(Utils.getAbsoluteFilePathToResource(scenarioScopeDef.filename));
        Response resp = RestAssured.given(requestSpec).baseUri(bulkProcessorConfig.bulkProcessorContactPoint)
                .contentType("multipart/form-data").multiPart("data", f).expect()
                .spec(new ResponseSpecBuilder().expectStatusCode(expectedStatus).build()).when()
                .post(bulkProcessorConfig.bulkTransactionEndpoint).then().extract().response();

        scenarioScopeDef.response = resp.andReturn().asString();
        scenarioScopeDef.restResponseObject = resp;

        Headers allHeaders = resp.getHeaders();
        for (Header header : allHeaders) {
            logger.info("{}", header.getName());
            logger.info(header.getValue());
        }
        logger.info("Batch Transactions Response: " + scenarioScopeDef.response);
    }

    @And("I should assert total txn count and successful txn count in payment batch detail response for batch account lookup")
    public void iShouldAssertTotalTxnCountAndSuccessfulTxnCountInPaymentBatchDetailResponseForBatchAccountLookup() {
        assertThat(scenarioScopeDef.paymentBatchDetail).isNotNull();
        assertThat(scenarioScopeDef.paymentBatchDetail.getInstructionList().size()).isEqualTo(3);
    }

    @And("I am able to parse actuator response")
    public void iAmAbleToParseActuatorResponse() {
        ActuatorResponse actuatorResponse = null;
        assertThat(scenarioScopeDef.response).isNotNull();
        assertThat(scenarioScopeDef.response).isNotEmpty();
        try {
            actuatorResponse = objectMapper.readValue(scenarioScopeDef.response, ActuatorResponse.class);
            scenarioScopeDef.actuatorResponse = actuatorResponse;
        } catch (Exception e) {
            logger.error("Error parsing the actuator response", e);
        }
        assertThat(scenarioScopeDef.actuatorResponse).isNotNull();
    }

    @And("Status of service is {string}")
    public void statusOfServiceIs(String status) {
        assertThat(scenarioScopeDef.actuatorResponse).isNotNull();
        assertThat(scenarioScopeDef.actuatorResponse.getStatus()).isEqualTo(status);
    }

    @When("I call the actuator API with Contactpoint {string} and endpoint {string}")
    public void iCallTheActuatorAPIWithContactpointAndEndpoint(String config, String endpoint) {
        RequestSpecification requestSpec = Utils.getDefaultSpec();

        Response resp = RestAssured.given(requestSpec).baseUri(environment.getProperty(config)).expect()
                .spec(new ResponseSpecBuilder().build()).when().get(endpoint).then().extract().response();

        scenarioScopeDef.response = resp.andReturn().asString();
        scenarioScopeDef.restResponseObject = resp;

        logger.info("Actuator Response: " + scenarioScopeDef.response);
    }
}
