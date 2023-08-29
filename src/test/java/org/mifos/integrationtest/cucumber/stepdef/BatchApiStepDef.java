package org.mifos.integrationtest.cucumber.stepdef;

import static com.google.common.truth.Truth.assertThat;
import static org.mifos.integrationtest.common.Utils.HEADER_FILENAME;
import static org.mifos.integrationtest.common.Utils.HEADER_JWS_SIGNATURE;
import static org.mifos.integrationtest.common.Utils.HEADER_PURPOSE;
import static org.mifos.integrationtest.common.Utils.QUERY_PARAM_TYPE;
import static org.mifos.integrationtest.common.Utils.HEADER_REGISTERING_INSTITUTE_ID;
import static org.mifos.integrationtest.common.Utils.HEADER_PROGRAM_ID;

import io.cucumber.core.internal.com.fasterxml.jackson.core.JsonProcessingException;
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
import java.security.InvalidKeyException;
import java.security.NoSuchAlgorithmException;
import java.security.spec.InvalidKeySpecException;
import java.util.Date;
import java.util.StringJoiner;
import java.util.UUID;
import org.apache.commons.lang3.StringUtils;
import org.json.JSONException;
import org.json.JSONObject;
import org.mifos.integrationtest.common.Utils;
import org.mifos.integrationtest.common.dto.operationsapp.BatchDTO;
import org.mifos.integrationtest.common.dto.operationsapp.BatchTransactionResponse;
import org.mifos.integrationtest.common.dto.operationsapp.TransferResponse;
import org.mifos.integrationtest.config.BulkProcessorConfig;
import org.springframework.beans.factory.annotation.Autowired;
import javax.crypto.BadPaddingException;
import javax.crypto.IllegalBlockSizeException;
import javax.crypto.NoSuchPaddingException;

public class BatchApiStepDef extends BaseStepDef {

    @Autowired
    BulkProcessorConfig bulkProcessorConfig;

    private String firstSubBatchFirstTxn;

    private String secondSubBatchFirstTxn;

    private long differenceInSeconds;

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

    @And("I have tenant as {string}")
    public void setTenant(String tenant) {
        BaseStepDef.tenant = tenant;
        assertThat(BaseStepDef.tenant).isNotEmpty();
        BaseStepDef.clientCorrelationId = UUID.randomUUID().toString();
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
                .get(operationsAppConfig.batchSummaryEndpoint + "?batchId=" + BaseStepDef.batchId).andReturn().asString();

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
        RequestSpecification requestSpec = Utils.getDefaultSpec(BaseStepDef.tenant);
        requestSpec.header("X-CorrelationID", BaseStepDef.clientCorrelationId);
        requestSpec.queryParam("type", "CSV");
        if (StringUtils.isNotBlank(BaseStepDef.filename)) {
            requestSpec.header(HEADER_FILENAME, BaseStepDef.filename);
        }
        if (BaseStepDef.signature != null && !BaseStepDef.signature.isEmpty()) {
            requestSpec.header(HEADER_JWS_SIGNATURE, BaseStepDef.signature);
        }
        BaseStepDef.response = RestAssured.given(requestSpec).baseUri(bulkProcessorConfig.bulkProcessorContactPoint).expect()
               .when()
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
    public void iShouldCallCallbackUrlApi() throws  NoSuchPaddingException, IllegalBlockSizeException, IOException,
            NoSuchAlgorithmException, BadPaddingException, InvalidKeySpecException, InvalidKeyException {
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
    public void iShouldGetExpectedStatusOf(int expectedStatus) throws  NoSuchPaddingException,
            IllegalBlockSizeException, IOException, NoSuchAlgorithmException, BadPaddingException,
            InvalidKeySpecException, InvalidKeyException {
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
        RequestSpecification requestSpec = Utils.getDefaultSpec(BaseStepDef.tenant,BaseStepDef.clientCorrelationId);
        requestSpec.header(HEADER_PURPOSE, "Integartion test");
        requestSpec.header(HEADER_FILENAME, BaseStepDef.filename);
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
                .spec(new ResponseSpecBuilder().expectStatusCode(expectedStatus).build())
                .when()
                .post(bulkProcessorConfig.bulkTransactionEndpoint).then().extract().response();

        BaseStepDef.response = resp.andReturn().asString();
        BaseStepDef.restResponseObject = resp;

        Headers allHeaders = resp.getHeaders();
        for (Header header : allHeaders) {
            System.out.print(header.getName() + " : ");
            System.out.println(header.getValue());
        }
        logger.info("Batch Transactions Response: " + BaseStepDef.response);
    }

    @Then("I should get non empty response")
    public void nonEmptyResponseCheck() {
        assertThat(BaseStepDef.response).isNotNull();
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
        String pollingPath = batchTransactionResponse.getPollingPath()
                .replace("\"", "");
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

    @Given("The system has a configured throttle time of {int} seconds and sub-batch size of {int}")
    public void theSystemHasAConfiguredThrottleTimeOfSecondsAndSubBatchSizeOf(int throttleTime, int subBatchSize) {
        BaseStepDef.throttleTime = throttleTime;
        BaseStepDef.subBatchSize = subBatchSize;
    }


    @And("I fetch requestId of first transactions for consecutive sub-batches based on sub-batch size")
    public void iFetchRequestIdOfFirstTransactionsForConsecutiveSubBatchesBasedOnSubBatchSize() {
        logger.info(filename);
        String fileContent = getFileContent(filename);
        String[] firstTxnFromFirstAndSecondSubBatch = getFirstTxnFromFirstAndSecondSubBatch(fileContent, subBatchSize);
        firstSubBatchFirstTxn = firstTxnFromFirstAndSecondSubBatch[0];
        secondSubBatchFirstTxn = firstTxnFromFirstAndSecondSubBatch[1];
        logger.info("first sub batch txn: " + firstSubBatchFirstTxn);
        logger.info("second sub batch txn: " + secondSubBatchFirstTxn);
    }

    @And("The difference between completedAt for requestIds is greater than or equal to throttleTime")
    public void theDifferenceBetweenCompletedAtForRequestIdsIsGreaterThanOrEqualToThrottleTime() {
        assertThat(differenceInSeconds).isGreaterThan((long) throttleTime);
    }

    @Then("I call the transfer API for requestIds fetched with expected status of {int}")
    public void iCallTheTransferAPIForRequestIdsFetchedWithExpectedStatusOf(int expectedStatus) {
        Date firstSubBatchFirstTxnStartedAtTimestamp =
                makeTransferApiCallAndFetchStartedAtTimestamp(firstSubBatchFirstTxn, expectedStatus);
        Date secondSubBatchFirstTxnStartedAtTimestamp =
                makeTransferApiCallAndFetchStartedAtTimestamp(secondSubBatchFirstTxn, expectedStatus);

        assertThat(firstSubBatchFirstTxnStartedAtTimestamp).isNotNull();
        assertThat(secondSubBatchFirstTxnStartedAtTimestamp).isNotNull();

        long firstSubBatchTxnStartedAtInMillis = firstSubBatchFirstTxnStartedAtTimestamp.getTime();
        long secondSubBatchTxnStartedAtInMillis = secondSubBatchFirstTxnStartedAtTimestamp.getTime();
        long differenceInMillis = secondSubBatchTxnStartedAtInMillis - firstSubBatchTxnStartedAtInMillis;
        differenceInSeconds = differenceInMillis / 1000;
    }

    private String getFileContent(String filePath) {
        File file = new File(Utils.getAbsoluteFilePathToResource(BaseStepDef.filename));
        Reader reader;
        CSVFormat csvFormat;
        CSVParser csvParser = null;
        try {
            reader = new FileReader(file);
            csvFormat = CSVFormat.DEFAULT.withDelimiter(',');
            csvParser = new CSVParser(reader, csvFormat);
        } catch (IOException e) {
            throw new RuntimeException(e);
        }
        StringJoiner stringJoiner = new StringJoiner("\n");

        for (CSVRecord csvRecord : csvParser) {
            stringJoiner.add(csvRecord.toString());
        }
        return stringJoiner.toString();
    }

    private String[] getFirstTxnFromFirstAndSecondSubBatch(String fileContent, int batchSize){
        String[] csvRecords = fileContent.split("\n");

        String firstBatchFirstTxnRecord = csvRecords[1];
        String secondBatchFirstTxnRecord = csvRecords[1+batchSize];

        String[] firstBatchFirstTxnRecordValues = firstBatchFirstTxnRecord.split(",");
        String[] secondBatchFirstTxnRecordValues = secondBatchFirstTxnRecord.split(",");

        return new String[]{firstBatchFirstTxnRecordValues[4].trim(), secondBatchFirstTxnRecordValues[4].trim()};
    }

    private Date makeTransferApiCallAndFetchStartedAtTimestamp(String clientCorrelationId, int expectedStatus){
        RequestSpecification requestSpec = Utils.getDefaultSpec(BaseStepDef.tenant);
        String urlPath = operationsAppConfig.transfersEndpoint + "&clientCorrelationId=" + clientCorrelationId;
        BaseStepDef.response = RestAssured.given(requestSpec).baseUri(operationsAppConfig.operationAppContactPoint)
                .expect().spec(new ResponseSpecBuilder().expectStatusCode(expectedStatus).build()).when()
                .get(urlPath).andReturn().asString();
        logger.info(BaseStepDef.response);
        return fetchStartAtTimestamp(BaseStepDef.response);
    }

    private Date fetchStartAtTimestamp(String response) {
        TransferResponse transferResponse = null;
        try {
            transferResponse = objectMapper.readValue(BaseStepDef.response, TransferResponse.class);
        } catch (JsonProcessingException e) {
            throw new RuntimeException(e);
        }

        return transferResponse.getStartedAt();
    }
}
