package org.mifos.integrationtest.cucumber.stepdef;

import static com.google.common.truth.Truth.assertThat;
import static org.mifos.integrationtest.common.Utils.CONTENT_TYPE;
import static org.mifos.integrationtest.common.Utils.CONTENT_TYPE_VALUE;
import static org.mifos.integrationtest.common.Utils.X_CORRELATIONID;

import com.google.gson.Gson;
import io.cucumber.core.internal.com.fasterxml.jackson.core.JsonProcessingException;
import io.cucumber.java.en.And;
import io.cucumber.java.en.Given;
import io.cucumber.java.en.When;
import io.restassured.RestAssured;
import io.restassured.builder.ResponseSpecBuilder;
import io.restassured.specification.RequestSpecification;
import java.util.UUID;
import org.json.JSONException;
import org.json.JSONObject;
import org.mifos.integrationtest.common.CollectionHelper;
import org.mifos.integrationtest.common.Utils;
import org.mifos.integrationtest.common.dto.CollectionResponse;
import org.mifos.integrationtest.config.GsmaConfig;
import org.springframework.beans.factory.annotation.Autowired;

public class IdempotencyStepDef extends BaseStepDef {

    @Autowired
    GSMATransferDef gsmaTransferDef;

    @Autowired
    GsmaConfig gsmaConfig;

    @Autowired
    GSMATransferStepDef gsmaTransferStepDef;

    @When("I call collection api with client correlation id expected status {int}")
    public void iCallCollectionApiWithClientCorrelationIdExpectedStatus(int expectedStatus) throws JSONException {
        RequestSpecification requestSpec = Utils.getDefaultSpec(scenarioScopeDef.tenant);
        requestSpec.header(Utils.X_CORRELATIONID, scenarioScopeDef.clientCorrelationId);
        logger.info("X-CorrelationId: {}", scenarioScopeDef.clientCorrelationId);
        JSONObject collectionRequestBody = CollectionHelper.getCollectionRequestBody("1", "254708374149", "24450523");
        String json = RestAssured.given(requestSpec).baseUri(channelConnectorConfig.channelConnectorContactPoint)
                .body(collectionRequestBody.toString()).expect().spec(new ResponseSpecBuilder().expectStatusCode(expectedStatus).build())
                .when().post(channelConnectorConfig.collectionEndpoint).andReturn().asString();
        CollectionResponse response = (new Gson()).fromJson(json, CollectionResponse.class);
        assertThat(response.getTransactionId()).isNotEmpty();
    }

    @When("I call collection api to fail with client correlation id expected status {int}")
    public void iCallCollectionApiWithClientCorrelationIdErrorExpectedStatus(int expectedStatus) throws JSONException {
        RequestSpecification requestSpec = Utils.getDefaultSpec(scenarioScopeDef.tenant);
        requestSpec.header(Utils.X_CORRELATIONID, scenarioScopeDef.clientCorrelationId);
        JSONObject collectionRequestBody = CollectionHelper.getCollectionRequestBody("1", "254708374149", "24450523");
        scenarioScopeDef.response = RestAssured.given(requestSpec).baseUri(channelConnectorConfig.channelConnectorContactPoint)
                .body(collectionRequestBody.toString()).expect().spec(new ResponseSpecBuilder().expectStatusCode(expectedStatus).build())
                .when().post(channelConnectorConfig.collectionEndpoint).andReturn().asString();
        // CollectionResponse response = (new Gson()).fromJson(json, CollectionResponse.class);
        assertThat(scenarioScopeDef.response).isNotEmpty();
    }

    @Given("I have same clientCorrelationId")
    public void iHaveSameClientCorrelationId() {
        if (scenarioScopeDef.clientCorrelationId == null || scenarioScopeDef.clientCorrelationId.isEmpty()) {
            scenarioScopeDef.clientCorrelationId = "123";
        }
        assertThat(scenarioScopeDef.clientCorrelationId).isNotNull();
    }

    @And("I should have error as Transaction already Exists")
    public void iShouldHaveErrorAsTransactionAlreadyExists() throws Exception {
        assertThat(scenarioScopeDef.response).contains("Transaction already exists");
    }

    @When("I call gsma transaction api with client correlation id expected status {int}")
    public void sendRequestToGSMAEndpointSavings(int status) throws JsonProcessingException {
        gsmaTransferStepDef.setTenantLoan("goriila");
        gsmaTransferStepDef.callCreatePayerClientEndpoint();
        gsmaTransferStepDef.callCreateSavingsProductEndpoint();
        gsmaTransferStepDef.callApproveSavingsEndpoint("approve");
        gsmaTransferStepDef.callSavingsActivateEndpoint("activate");
        gsmaTransferStepDef.callDepositAccountEndpoint("deposit", 11);
        gsmaTransferStepDef.setHeaders("mifos", "gorilla", 11);
        RequestSpecification requestSpec = Utils.getDefaultSpec();
        requestSpec.header(X_CORRELATIONID, scenarioScopeDef.clientCorrelationId);
        requestSpec.header(CONTENT_TYPE, CONTENT_TYPE_VALUE);

        gsmaTransferDef.gsmaTransferBody = gsmaTransferDef.setGsmaTransactionBody("S");

        gsmaTransferDef.gsmaTransactionResponse = RestAssured.given(requestSpec).baseUri(gsmaConfig.channelConnectorBaseUrl)
                .body(gsmaTransferDef.gsmaTransferBody).expect().spec(new ResponseSpecBuilder().expectStatusCode(status).build()).when()
                .post(gsmaConfig.gsmaEndpoint).andReturn().asString();

        logger.info("GSMA Transaction Response: " + gsmaTransferDef.gsmaTransactionResponse);
        assertThat(gsmaTransferDef.gsmaTransactionResponse).isNotEmpty();
    }

    @When("I call gsma transaction to fail with client correlation id expected status {int}")
    public void iCallGsmaTransactionToFailWithClientCorrelationIdExpectedStatus(int status) throws JsonProcessingException {
        RequestSpecification requestSpec = Utils.getDefaultSpec();
        requestSpec.header("amsName", gsmaTransferDef.amsName);
        requestSpec.header("accountHoldingInstitutionId", gsmaTransferDef.acccountHoldingInstitutionId);
        requestSpec.header(X_CORRELATIONID, scenarioScopeDef.clientCorrelationId);
        requestSpec.header(CONTENT_TYPE, CONTENT_TYPE_VALUE);

        gsmaTransferDef.gsmaTransferBody = gsmaTransferDef.setGsmaTransactionBody("S");

        gsmaTransferDef.gsmaTransactionResponse = RestAssured.given(requestSpec).baseUri(gsmaConfig.channelConnectorBaseUrl)
                .body(gsmaTransferDef.gsmaTransferBody).expect().spec(new ResponseSpecBuilder().expectStatusCode(status).build()).when()
                .post(gsmaConfig.gsmaEndpoint).andReturn().asString();

        logger.info("GSMA Transaction Response: " + gsmaTransferDef.gsmaTransactionResponse);
    }

    @When("I call inbound transfer api with client correlation id expected status {int}")
    public void iCallInboundTransferApiWithClientCorrelationIdExpectedStatus(int expectedStatus) {
        RequestSpecification requestSpec = Utils.getDefaultSpec(scenarioScopeDef.tenant);
        requestSpec.header(Utils.X_CORRELATIONID, scenarioScopeDef.clientCorrelationId);
        scenarioScopeDef.response = RestAssured.given(requestSpec).baseUri(channelConnectorConfig.channelConnectorContactPoint)
                .body(scenarioScopeDef.inboundTransferMockReq).expect()
                .spec(new ResponseSpecBuilder().expectStatusCode(expectedStatus).build()).when()
                .post(channelConnectorConfig.transferEndpoint).andReturn().asString();

        logger.info("Inbound transfer Response: {}", scenarioScopeDef.response);
    }

    @When("I call Inbound transaction Req api with client correlation id expected status {int}")
    public void iCallInboundTransferReqApiWithClientCorrelationIdExpectedStatus(int expectedStatus) {
        RequestSpecification requestSpec = Utils.getDefaultSpec(scenarioScopeDef.tenant);
        requestSpec.header(Utils.X_CORRELATIONID, scenarioScopeDef.clientCorrelationId);
        scenarioScopeDef.response = RestAssured.given(requestSpec).baseUri(channelConnectorConfig.channelConnectorContactPoint)
                .body(scenarioScopeDef.inboundTransferMockReq).expect()
                .spec(new ResponseSpecBuilder().expectStatusCode(expectedStatus).build()).when()
                .post(channelConnectorConfig.transferReqEndpoint).andReturn().asString();

        logger.info("Inbound transfer Response: {}", scenarioScopeDef.response);
    }

    @Given("I create a new clientCorrelationId")
    public void iCreateANewClientCorrelationId() {
        scenarioScopeDef.clientCorrelationId = UUID.randomUUID().toString();
        assertThat(scenarioScopeDef.clientCorrelationId).isNotNull();
    }
}
