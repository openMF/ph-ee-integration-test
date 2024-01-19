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
        if (BaseStepDef.batchId == null || BaseStepDef.batchId.isEmpty()) {
            BaseStepDef.batchId = UUID.randomUUID().toString();
        }
        assertThat(BaseStepDef.batchId).isNotNull();
    }

    @Given("I have a batch with id {string}")
    public void setBatchId(String batchId) {
        BaseStepDef.batchId = batchId;
        assertThat(BaseStepDef.batchId).isNotEmpty();
    }

    @Given("I have the demo csv file {string}")
    public void setFilename(String filename) {
        BaseStepDef.filename = filename;
        File f = new File(Utils.getAbsoluteFilePathToResource(BaseStepDef.filename));
        assertThat(f.exists()).isTrue();
        assertThat(BaseStepDef.filename).isNotEmpty();
    }

    @And("I make sure there is no file")
    public void removeTheFilename() {
        clearFilename();
        assertThat(BaseStepDef.filename).isNull();
    }

    public void clearFilename() {
        BaseStepDef.filename = null;
    }

    @And("I have the registeringInstituteId {string}")
    public void setRegisteringInstituteId(String registeringInstituteId) {
        BaseStepDef.registeringInstituteId = registeringInstituteId;
        assertThat(BaseStepDef.registeringInstituteId).isNotNull();
    }

    @And("I have the programId {string}")
    public void setProgramId(String programId) {
        BaseStepDef.programId = programId;
        assertThat(BaseStepDef.programId).isNotNull();
    }

    @When("I call the batch summary API with expected status of {int}")
    public void callBatchSummaryAPI(int expectedStatus) {
        RequestSpecification requestSpec = Utils.getDefaultSpec(BaseStepDef.tenant);
        if (authEnabled) {
            requestSpec.header("Authorization", "Bearer " + BaseStepDef.accessToken);
        }
        // requestSpec.queryParam("batchId", BaseStepDef.batchId);
        logger.info("Calling with batch id: {}", BaseStepDef.batchId);

        BaseStepDef.response = RestAssured.given(requestSpec).baseUri(operationsAppConfig.operationAppContactPoint).expect()
                .spec(new ResponseSpecBuilder().expectStatusCode(expectedStatus).build()).when()
                .get(operationsAppConfig.batchSummaryEndpoint + "/" + BaseStepDef.batchId).andReturn().asString();

        logger.info("Batch Summary Response: " + BaseStepDef.response);
    }

    @Then("I am able to parse batch summary response")
    public void parseBatchSummaryResponse() {
        BatchDTO batchDTO = null;
        assertThat(BaseStepDef.response).isNotNull();
        assertThat(BaseStepDef.response).isNotEmpty();
        try {
            batchDTO = objectMapper.readValue(BaseStepDef.response, BatchDTO.class);
            BaseStepDef.batchDTO = batchDTO;
        } catch (Exception e) {
            logger.error("Error parsing the batch summary response", e);
        }
        assertThat(BaseStepDef.batchDTO).isNotNull();
    }

    @And("Status of transaction is {string}")
    public void checkIfCreatedAtIsNotNull(String status) {
        assertThat(BaseStepDef.batchDTO).isNotNull();
        assertThat(BaseStepDef.batchDTO.getStatus()).isEqualTo(status);
    }

    @When("I call the batch details API with expected status of {int}")
    public void callBatchDetailsAPI(int expectedStatus) {
        RequestSpecification requestSpec = Utils.getDefaultSpec(BaseStepDef.tenant);
        if (authEnabled) {
            requestSpec.header("Authorization", "Bearer " + BaseStepDef.accessToken);
        }
        requestSpec.queryParam("batchId", BaseStepDef.batchId);

        BaseStepDef.response = RestAssured.given(requestSpec).baseUri(operationsAppConfig.operationAppContactPoint).expect()
                .spec(new ResponseSpecBuilder().expectStatusCode(expectedStatus).build()).when()
                .get(operationsAppConfig.batchDetailsEndpoint).andReturn().asString();

        logger.info("Batch Details Response: " + BaseStepDef.response);
    }

    @When("I call the batch transactions endpoint with expected status of {int} without payload")
    public void iCallTheBatchTransactionsEndpointWithExpectedStatusOfWithoutPayload(int expectedStatus) {
        RequestSpecification requestSpec = Utils.getDefaultSpec(BaseStepDef.tenant, BaseStepDef.clientCorrelationId);
        requestSpec.header(HEADER_PURPOSE, "Integration test");
        requestSpec.header(HEADER_FILENAME, "");
        requestSpec.header(QUERY_PARAM_TYPE, "CSV");
        if (StringUtils.isNotBlank(BaseStepDef.filename)) {
            requestSpec.header(HEADER_FILENAME, BaseStepDef.filename);
        }
        if (BaseStepDef.signature != null && !BaseStepDef.signature.isEmpty()) {
            requestSpec.header(HEADER_JWS_SIGNATURE, BaseStepDef.signature);
        }
        BaseStepDef.response = RestAssured.given(requestSpec).baseUri(bulkProcessorConfig.bulkProcessorContactPoint).expect().when()
                .post(bulkProcessorConfig.bulkTransactionEndpoint).andReturn().asString();

        logger.info("Batch Transactions without payload Response: " + BaseStepDef.response);
    }

    @And("I should get batchId in response")
    public void iShouldGetBatchIdInResponse() throws JSONException {
        JSONObject jsonObject = new JSONObject(BaseStepDef.response);
        String pollingPath = (String) jsonObject.get("PollingPath");
        String[] response = pollingPath.split("/");
        logger.info("Batch Id: {}", response[response.length - 1]);
        BaseStepDef.batchId = response[response.length - 1];

    }

    @When("I should call callbackUrl api")
    public void iShouldCallCallbackUrlApi() throws NoSuchPaddingException, IllegalBlockSizeException, IOException, NoSuchAlgorithmException,
            BadPaddingException, InvalidKeySpecException, InvalidKeyException {
        RequestSpecification requestSpec = Utils.getDefaultSpec(BaseStepDef.tenant, BaseStepDef.clientCorrelationId);
        String callbackReq = new String("The Batch Aggregation API was complete");
        logger.info(callbackReq);
        String jwsSignature = generateSignature(BaseStepDef.clientCorrelationId, BaseStepDef.tenant, callbackReq, false);

        requestSpec.header(HEADER_JWS_SIGNATURE, jwsSignature);

        BaseStepDef.statusCode = RestAssured.given(requestSpec).body(callbackReq).post(bulkProcessorConfig.getCallbackUrl()).andReturn()
                .getStatusCode();
    }

    @And("I have callbackUrl as {string}")
    public void iHaveCallbackUrlAs(String callBackUrl) {
        assertThat(callBackUrl).isNotEmpty();
        bulkProcessorConfig.setCallbackUrl(callBackUrl);
        BaseStepDef.callbackUrl = callBackUrl;
    }

    @Then("I should get expected status of {int}")
    public void iShouldGetExpectedStatusOf(int expectedStatus) throws NoSuchPaddingException, IllegalBlockSizeException, IOException,
            NoSuchAlgorithmException, BadPaddingException, InvalidKeySpecException, InvalidKeyException {
        assertThat(BaseStepDef.statusCode).isNotNull();
        assertThat(BaseStepDef.statusCode).isEqualTo(expectedStatus);
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
        assertThat(BaseStepDef.response).isNotNull();
        assertThat(BaseStepDef.response.contains("failPercentage"));
        assertThat(BaseStepDef.response.contains("successPercentage"));
    }

    @When("I call the batch transactions endpoint with expected status of {int}")
    public void callBatchTransactionsEndpoint(int expectedStatus) {
        RequestSpecification requestSpec = Utils.getDefaultSpec(BaseStepDef.tenant, BaseStepDef.clientCorrelationId);
        requestSpec.header(HEADER_PURPOSE, "Integartion test");
        requestSpec.header(HEADER_FILENAME, BaseStepDef.filename);
        requestSpec.header(HEADER_REGISTERING_INSTITUTE_ID, "SocialWelfare");
        requestSpec.queryParam(QUERY_PARAM_TYPE, "CSV");
        requestSpec.header(QUERY_PARAM_TYPE, "CSV");
        if (BaseStepDef.signature != null && !BaseStepDef.signature.isEmpty()) {
            requestSpec.header(HEADER_JWS_SIGNATURE, BaseStepDef.signature);
        }
        if (StringUtils.isNotBlank(BaseStepDef.registeringInstituteId) && StringUtils.isNotBlank(BaseStepDef.programId)) {
            requestSpec.header(HEADER_REGISTERING_INSTITUTE_ID, BaseStepDef.registeringInstituteId);
            requestSpec.header(HEADER_PROGRAM_ID, BaseStepDef.programId);
        }

        File f = new File(Utils.getAbsoluteFilePathToResource(BaseStepDef.filename));
        Response resp = RestAssured.given(requestSpec).baseUri(bulkProcessorConfig.bulkProcessorContactPoint)
                .contentType("multipart/form-data").multiPart("data", f).expect()
                .spec(new ResponseSpecBuilder().expectStatusCode(expectedStatus).build()).when()
                .post(bulkProcessorConfig.bulkTransactionEndpoint).then().extract().response();

        BaseStepDef.response = resp.andReturn().asString();
        BaseStepDef.restResponseObject = resp;

        Headers allHeaders = resp.getHeaders();
        for (Header header : allHeaders) {
            logger.debug("{}", header.getName());
            logger.debug("{}", header.getValue());
        }
        logger.info("Batch Transactions Response: " + BaseStepDef.response);
    }

    @And("I should have {string} and {string} in response")
    public void iShouldHaveAndInResponse(String pollingpath, String suggestedcallback) {
        assertThat(BaseStepDef.response).contains(pollingpath);
        assertThat(BaseStepDef.response).contains(suggestedcallback);

    }

    @And("I have callbackUrl as simulated url")
    public void iHaveCallbackUrlAsSimulatedUrl() {
        bulkProcessorConfig.setCallbackUrl(bulkProcessorConfig.simulateEndpoint);
        BaseStepDef.callbackUrl = bulkProcessorConfig.getCallbackUrl();
    }

    @And("I fetch batch ID from batch transaction API's response")
    public void iFetchBatchIDFromBatchTransactionAPISResponse() {
        assertThat(BaseStepDef.batchTransactionResponse).isNotNull();
        BaseStepDef.batchId = fetchBatchId(BaseStepDef.batchTransactionResponse);
        logger.info("batchId: {}", batchId);
        assertThat(batchId).isNotEmpty();
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
        assertThat(BaseStepDef.response).isNotEmpty();
        BatchTransactionResponse response = null;
        try {
            response = objectMapper.readValue(BaseStepDef.response, BatchTransactionResponse.class);
            BaseStepDef.batchTransactionResponse = response;
        } catch (Exception e) {
            logger.error("Error parsing the batch transaction response", e);
        }
        assertThat(BaseStepDef.batchTransactionResponse).isNotNull();
    }

    @And("I should have matching total txn count and successful txn count in response")
    public void iShouldHaveMatchingTotalTxnCountAndSuccessfulTxnCountInResponse() {
        assertThat(BaseStepDef.batchDTO).isNotNull();
        assertThat(BaseStepDef.batchDTO.getTotal()).isNotNull();
        assertThat(BaseStepDef.batchDTO.getSuccessful()).isNotNull();
        assertThat(BaseStepDef.batchDTO.getTotal()).isGreaterThan(0);
        assertThat(BaseStepDef.batchDTO.getSuccessful()).isGreaterThan(0);
        assertThat(BaseStepDef.batchDTO.getTotal()).isEqualTo(BaseStepDef.batchDTO.getSuccessful());

    }

    @When("I can assert the approved count as {int} and approved amount as {int}")
    public void iCanAssertTheApprovedCountAsAndApprovedAmountAs(int count, int amount) {
        BigDecimal approvedCount = BaseStepDef.batchDTO.getApprovedCount();
        BigDecimal approvedAmount = BaseStepDef.batchDTO.getApprovedAmount();
        assertThat(approvedCount).isEqualTo(new BigDecimal(count));
        assertThat(approvedAmount).isEqualTo(new BigDecimal(amount));
    }

    @When("I call the batch transactions raw endpoint with expected status of {int}")
    public void callBatchTransactionsRawEndpoint(int expectedStatus) {
        RequestSpecification requestSpec = Utils.getDefaultSpec(BaseStepDef.tenant, BaseStepDef.clientCorrelationId);
        requestSpec.header(HEADER_PURPOSE, "Integartion test");
        requestSpec.header(HEADER_FILENAME, "");
        requestSpec.header(QUERY_PARAM_TYPE, "RAW");
        if (BaseStepDef.signature != null && !BaseStepDef.signature.isEmpty()) {
            requestSpec.header(HEADER_JWS_SIGNATURE, BaseStepDef.signature);
        }
        if (StringUtils.isNotBlank(BaseStepDef.registeringInstituteId) && StringUtils.isNotBlank(BaseStepDef.programId)) {
            requestSpec.header(HEADER_REGISTERING_INSTITUTE_ID, BaseStepDef.registeringInstituteId);
            requestSpec.header(HEADER_PROGRAM_ID, BaseStepDef.programId);
        }

        File f = new File(Utils.getAbsoluteFilePathToResource(BaseStepDef.filename));
        Response resp = RestAssured.given(requestSpec).baseUri(bulkProcessorConfig.bulkProcessorContactPoint)
                .contentType("application/json").body(BaseStepDef.batchRawRequest).expect()
                // .spec(new ResponseSpecBuilder().expectStatusCode(expectedStatus).build())
                .when().post(bulkProcessorConfig.bulkTransactionEndpoint).then().extract().response();

        BaseStepDef.response = resp.andReturn().asString();
        BaseStepDef.restResponseObject = resp;

        Headers allHeaders = resp.getHeaders();
        for (Header header : allHeaders) {
            logger.info(" : {}", header.getName());
            logger.info("{}", header.getValue());
        }
        logger.info("Batch Transactions Response: " + BaseStepDef.response);
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
        BaseStepDef.batchRequestDTO = batchRequestDTO;

        List<BatchRequestDTO> batchRequestDTOS = new ArrayList<>();
        batchRequestDTOS.add(batchRequestDTO);
        BaseStepDef.batchRawRequest = objectMapper.writeValueAsString(batchRequestDTOS);
        assertThat(BaseStepDef.batchRawRequest).isNotEmpty();
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
        logger.info(BaseStepDef.response);
        BatchDetailResponse batchDetailResponse = parseBatchDetailResponse(BaseStepDef.response);
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
        BatchDetailResponse batchDetailResponse = parseBatchDetailResponse(BaseStepDef.response);
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
        RequestSpecification requestSpec = Utils.getDefaultSpec(BaseStepDef.tenant);
        logger.info("Calling with batch id: {}", BaseStepDef.batchId);

        BaseStepDef.response = RestAssured.given(requestSpec).baseUri(operationsAppConfig.operationAppContactPoint).expect()
                .spec(new ResponseSpecBuilder().expectStatusCode(expectedStatus).build()).when()
                .get(operationsAppConfig.batchAggregateEndpoint + BaseStepDef.batchId).andReturn().asString();
        logger.info("Batch Aggregate Response: " + BaseStepDef.response);
    }

    public void batchTearDown() {
        BaseStepDef.filename = null;
        BaseStepDef.batchId = null;
        BaseStepDef.response = null;
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
        BaseStepDef.batchId = batchID;
    }

    @And("I call the batch summary API for sub batch summary with expected status of {int}")
    public void iCallTheBatchSummaryAPIForSubBatchSummaryWithExpectedStatusOf(int expectedStatus) {
        RequestSpecification requestSpec = Utils.getDefaultSpec(BaseStepDef.tenant);
        if (authEnabled) {
            requestSpec.header("Authorization", "Bearer " + BaseStepDef.accessToken);
        }
        // requestSpec.queryParam("batchId", BaseStepDef.batchId);
        logger.info("Calling with batch id: {}", BaseStepDef.batchId);

        BaseStepDef.response = RestAssured.given(requestSpec).baseUri(operationsAppConfig.operationAppContactPoint).expect()
                .spec(new ResponseSpecBuilder().expectStatusCode(expectedStatus).build()).when()
                .get(operationsAppConfig.batchSummaryEndpoint + BaseStepDef.batchId).andReturn().asString();

        logger.info("Batch Summary Response: " + BaseStepDef.response);
    }

    @Then("I am able to parse sub batch summary response")
    public void iAmAbleToParseSubBatchSummaryResponse() {
        batchAndSubBatchSummaryResponse = null;
        assertThat(BaseStepDef.response).isNotNull();
        assertThat(BaseStepDef.response).isNotEmpty();
        try {
            BaseStepDef.batchAndSubBatchSummaryResponse = objectMapper.readValue(BaseStepDef.response,
                    BatchAndSubBatchSummaryResponse.class);
        } catch (Exception e) {
            logger.error("Error parsing the batch summary response", e);
        }
        assertThat(BaseStepDef.batchAndSubBatchSummaryResponse).isNotNull();
    }

    @And("I call the sub batch summary API for sub batch summary with expected status of {int}")
    public void iCallTheSubBatchSummaryAPIForSubBatchSummaryWithExpectedStatusOf(int expectedStatus) {
        RequestSpecification requestSpec = Utils.getDefaultSpec(BaseStepDef.tenant);
        requestSpec.header("X-Correlation-ID", BaseStepDef.clientCorrelationId);
        if (authEnabled) {
            requestSpec.header("Authorization", "Bearer " + BaseStepDef.accessToken);
        }
        // requestSpec.queryParam("batchId", BaseStepDef.batchId);
        logger.info("Calling with batch id: {}", BaseStepDef.clientCorrelationId);
        logger.info("Calling with batch id: {}",
                operationsAppConfig.operationAppContactPoint + operationsAppConfig.batchesEndpoint + "/" + BaseStepDef.batchId);

        BaseStepDef.response = RestAssured.given(requestSpec).baseUri(operationsAppConfig.operationAppContactPoint).expect()
                .spec(new ResponseSpecBuilder().expectStatusCode(expectedStatus).build()).when()
                .get(operationsAppConfig.batchesEndpoint + "/" + BaseStepDef.batchId).andReturn().asString();

        logger.info("Sub batch Summary Response: " + BaseStepDef.response);
    }

    @And("I should assert total txn count and successful txn count in response")
    public void iShouldAssertTotalTxnCountAndSuccessfulTxnCountInResponse() {
        assertThat(BaseStepDef.batchAndSubBatchSummaryResponse).isNotNull();
        assertThat(BaseStepDef.batchAndSubBatchSummaryResponse.getTotal()).isNotNull();
        assertThat(BaseStepDef.batchAndSubBatchSummaryResponse.getSuccessful()).isNotNull();
        assertThat(BaseStepDef.batchAndSubBatchSummaryResponse.getTotal()).isGreaterThan(0);
        assertThat(BaseStepDef.batchAndSubBatchSummaryResponse.getSuccessful()).isGreaterThan(0);
        assertThat(BaseStepDef.batchAndSubBatchSummaryResponse.getTotal())
                .isEqualTo(BaseStepDef.batchAndSubBatchSummaryResponse.getSuccessful());
    }

    @And("Total transaction in batch should add up to total transaction in each sub batch")
    public void matchTotalSubBatchTxnAndBatchTxnCount() {
        assertThat(BaseStepDef.batchAndSubBatchSummaryResponse).isNotNull();
        assertThat(Integer.parseInt(BaseStepDef.batchAndSubBatchSummaryResponse.getTotalSubBatches())).isGreaterThan(1);
        long batchTotal = BaseStepDef.batchAndSubBatchSummaryResponse.getTotal();
        long subBatchTotal = 0L;
        for (SubBatchSummary subBatchSummary : BaseStepDef.batchAndSubBatchSummaryResponse.getSubBatchSummaryList()) {
            subBatchTotal += subBatchSummary.getTotal();
        }
        assertThat(batchTotal).isEqualTo(subBatchTotal);

    }

    @And("I call the payment batch detail API with expected status of {int}")
    public void iCallThePaymentBatchDetailAPIWithExpectedStatusOf(int expectedStatus) {
        RequestSpecification requestSpec = Utils.getDefaultSpec(BaseStepDef.tenant);
        requestSpec.header("X-Correlation-ID", BaseStepDef.clientCorrelationId);
        requestSpec.queryParam("associations", "all");
        if (authEnabled) {
            requestSpec.header("Authorization", "Bearer " + BaseStepDef.accessToken);
        }
        // requestSpec.queryParam("batchId", BaseStepDef.batchId);
        logger.info("Calling with batch id: {}", BaseStepDef.clientCorrelationId);
        logger.info("Calling with batch id: {}",
                operationsAppConfig.operationAppContactPoint + operationsAppConfig.batchesEndpoint + "/" + BaseStepDef.batchId);

        BaseStepDef.response = RestAssured.given(requestSpec).baseUri(operationsAppConfig.operationAppContactPoint).expect()
                .spec(new ResponseSpecBuilder().expectStatusCode(expectedStatus).build()).when()
                .get(operationsAppConfig.batchesEndpoint + "/" + BaseStepDef.batchId).andReturn().asString();

        logger.info("Batch Payment Detail Response: " + BaseStepDef.response);
    }

    @Then("I am able to parse payment batch detail response")
    public void iAmAbleToParsePaymentBatchDetailResponse() {
        paymentBatchDetail = null;
        assertThat(BaseStepDef.response).isNotNull();
        assertThat(BaseStepDef.response).isNotEmpty();
        try {
            BaseStepDef.paymentBatchDetail = objectMapper.readValue(BaseStepDef.response, PaymentBatchDetail.class);
        } catch (Exception e) {
            logger.error("Error parsing the payment batch detail response", e);
        }
        assertThat(BaseStepDef.paymentBatchDetail).isNotNull();
    }

    @And("I should assert total txn count and successful txn count in payment batch detail response")
    public void iShouldAssertTotalTxnCountAndSuccessfulTxnCountInPaymentBatchDetailResponse() {
        assertThat(BaseStepDef.paymentBatchDetail).isNotNull();
        assertThat(BaseStepDef.paymentBatchDetail.getSubBatchList().size()).isEqualTo(3);
        assertThat(BaseStepDef.paymentBatchDetail.getInstructionList().size()).isEqualTo(12);
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
        RequestSpecification requestSpec = Utils.getDefaultSpec(BaseStepDef.tenant, BaseStepDef.clientCorrelationId);
        requestSpec.header(HEADER_PURPOSE, "Integration test");
        requestSpec.header(HEADER_FILENAME, BaseStepDef.filename);
        requestSpec.queryParam(QUERY_PARAM_TYPE, "CSV");
        requestSpec.header(QUERY_PARAM_TYPE, "CSV");
        requestSpec.header("X-CallbackURL", callbackURL + callback);
        if (BaseStepDef.signature != null && !BaseStepDef.signature.isEmpty()) {
            requestSpec.header(HEADER_JWS_SIGNATURE, BaseStepDef.signature);
        }
        if (StringUtils.isNotBlank(BaseStepDef.registeringInstituteId) && StringUtils.isNotBlank(BaseStepDef.programId)) {
            requestSpec.header(HEADER_REGISTERING_INSTITUTE_ID, BaseStepDef.registeringInstituteId);
            requestSpec.header(HEADER_PROGRAM_ID, BaseStepDef.programId);
        }

        File f = new File(Utils.getAbsoluteFilePathToResource(BaseStepDef.filename));
        Response resp = RestAssured.given(requestSpec).baseUri(bulkProcessorConfig.bulkProcessorContactPoint)
                .contentType("multipart/form-data").multiPart("data", f).expect()
                .spec(new ResponseSpecBuilder().expectStatusCode(expectedStatus).build()).when()
                .post(bulkProcessorConfig.bulkTransactionEndpoint).then().extract().response();

        BaseStepDef.response = resp.andReturn().asString();
        BaseStepDef.restResponseObject = resp;

        Headers allHeaders = resp.getHeaders();
        for (Header header : allHeaders) {
            logger.info("{}", header.getName());
            logger.info(header.getValue());
        }
        logger.info("Batch Transactions Response: " + BaseStepDef.response);
    }

    @And("I should assert total txn count and successful txn count in payment batch detail response for batch account lookup")
    public void iShouldAssertTotalTxnCountAndSuccessfulTxnCountInPaymentBatchDetailResponseForBatchAccountLookup() {
        assertThat(BaseStepDef.paymentBatchDetail).isNotNull();
        assertThat(BaseStepDef.paymentBatchDetail.getInstructionList().size()).isEqualTo(3);
    }

    @And("I am able to parse actuator response")
    public void iAmAbleToParseActuatorResponse() {
        ActuatorResponse actuatorResponse = null;
        assertThat(BaseStepDef.response).isNotNull();
        assertThat(BaseStepDef.response).isNotEmpty();
        try {
            actuatorResponse = objectMapper.readValue(BaseStepDef.response, ActuatorResponse.class);
            BaseStepDef.actuatorResponse = actuatorResponse;
        } catch (Exception e) {
            logger.error("Error parsing the actuator response", e);
        }
        assertThat(BaseStepDef.actuatorResponse).isNotNull();
    }

    @And("Status of service is {string}")
    public void statusOfServiceIs(String status) {
        assertThat(BaseStepDef.actuatorResponse).isNotNull();
        assertThat(BaseStepDef.actuatorResponse.getStatus()).isEqualTo(status);
    }

    @When("I call the actuator API with Contactpoint {string} and endpoint {string}")
    public void iCallTheActuatorAPIWithContactpointAndEndpoint(String config, String endpoint) {
        RequestSpecification requestSpec = Utils.getDefaultSpec();

        Response resp = RestAssured.given(requestSpec).baseUri(environment.getProperty(config)).expect()
                .spec(new ResponseSpecBuilder().build()).when().get(endpoint).then().extract().response();

        BaseStepDef.response = resp.andReturn().asString();
        BaseStepDef.restResponseObject = resp;

        logger.info("Actuator Response: " + BaseStepDef.response);
    }
}
