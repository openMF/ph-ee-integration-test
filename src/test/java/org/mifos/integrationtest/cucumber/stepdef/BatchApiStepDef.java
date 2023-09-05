package org.mifos.integrationtest.cucumber.stepdef;

import static com.google.common.truth.Truth.assertThat;
import static org.mifos.integrationtest.common.Utils.HEADER_FILENAME;
import static org.mifos.integrationtest.common.Utils.HEADER_JWS_SIGNATURE;
import static org.mifos.integrationtest.common.Utils.HEADER_PURPOSE;
import static org.mifos.integrationtest.common.Utils.QUERY_PARAM_TYPE;
import static org.mifos.integrationtest.common.Utils.HEADER_REGISTERING_INSTITUTE_ID;
import static org.mifos.integrationtest.common.Utils.HEADER_PROGRAM_ID;
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
import java.security.InvalidKeyException;
import java.security.NoSuchAlgorithmException;
import java.security.spec.InvalidKeySpecException;
import java.util.UUID;
import org.apache.commons.lang3.StringUtils;
import org.json.JSONException;
import org.json.JSONObject;
import org.mifos.integrationtest.common.Utils;
import org.mifos.integrationtest.common.dto.operationsapp.BatchDTO;
import org.mifos.integrationtest.common.dto.operationsapp.BatchTransactionResponse;
import org.mifos.integrationtest.config.BulkProcessorConfig;
import org.springframework.beans.factory.annotation.Autowired;
import javax.crypto.BadPaddingException;
import javax.crypto.IllegalBlockSizeException;
import javax.crypto.NoSuchPaddingException;

public class BatchApiStepDef extends BaseStepDef {

    @Autowired
    BulkProcessorConfig bulkProcessorConfig;

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
        BaseStepDef.response = RestAssured.given(requestSpec).baseUri(bulkProcessorConfig.bulkProcessorContactPoint)
                .expect()
                .spec(new ResponseSpecBuilder().expectStatusCode(expectedStatus).build())
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
        requestSpec.header(HEADER_PURPOSE, "Integration test");
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
    @And("I should have matching total txn count and successful txn count in response")
    public void iShouldHaveMatchingTotalTxnCountAndSuccessfulTxnCountInResponse() {
       assertThat(BaseStepDef.batchDTO).isNotNull();
        assertThat(BaseStepDef.batchDTO.getTotal()).isNotNull();
        assertThat(BaseStepDef.batchDTO.getSuccessful()).isNotNull();
        assertThat(BaseStepDef.batchDTO.getTotal()).isGreaterThan(0);
        assertThat(BaseStepDef.batchDTO.getSuccessful()).isGreaterThan(0);
        assertThat(BaseStepDef.batchDTO.getTotal()).isEqualTo(BaseStepDef.batchDTO.getSuccessful());

    }
}
