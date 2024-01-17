package org.mifos.integrationtest.cucumber.stepdef;

import io.cucumber.java.en.And;
import io.cucumber.java.en.When;
import io.restassured.RestAssured;
import io.restassured.builder.ResponseSpecBuilder;
import io.restassured.specification.RequestSpecification;
import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;
import org.mifos.integrationtest.common.Utils;
import org.mifos.integrationtest.config.OperationsAppConfig;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;

import static com.google.common.truth.Truth.assertThat;

public class MockFlowTestDef extends BaseStepDef {

    @Value("${operations-app.auth.enabled}")
    public Boolean authEnabled;

    @Autowired
    OperationsAppConfig operationsAppConfig;

    @When("I call the outbound transfer endpoint with expected status {int}")
    public void iCallTheOutboundTransferEndpointWithExpectedStatus(int expectedStatus) {
        RequestSpecification requestSpec = Utils.getDefaultSpec(BaseStepDef.tenant);
        logger.info("X-CorrelationId: {}", BaseStepDef.clientCorrelationId);
        requestSpec.header(Utils.X_CORRELATIONID, BaseStepDef.clientCorrelationId);
        requestSpec.header("X-Registering-Institution-ID", "SocialWelfare");
        BaseStepDef.response = RestAssured.given(requestSpec).baseUri(channelConnectorConfig.channelConnectorContactPoint)
                .body(BaseStepDef.inboundTransferMockReq).expect().spec(new ResponseSpecBuilder().expectStatusCode(expectedStatus).build())
                .when().post(channelConnectorConfig.transferEndpoint).andReturn().asString();

        logger.info("Inbound transfer Response: {}", BaseStepDef.response);
        BaseStepDef.transactionId = BaseStepDef.response.split(":")[1].split(",")[0].split("\"")[1];
        logger.info("TransactionId: {}", BaseStepDef.transactionId);
    }

    @And("I should have PayerFspId as not null")
    public void iShouldHavePayerFspIdAsNotNull() throws JSONException {
        assert BaseStepDef.response.contains("payerDfspId");
        JSONObject jsonObject = new JSONObject(BaseStepDef.response);
        JSONArray jsonArray = (JSONArray) jsonObject.get("content");
        JSONObject content = (JSONObject) jsonArray.get(0);
        String value = content.get("payerDfspId").toString();
        assert value != null;
    }

    @When("I call the get txn API with expected status of {int} and txnId")
    public void iCallTheGetTxnAPIWithExpectedStatusOfAndTxnId(int expectedStatus) {
        RequestSpecification requestSpec = Utils.getDefaultSpec(BaseStepDef.tenant);
        requestSpec.queryParam("transactionId", BaseStepDef.transactionId);
        requestSpec.queryParam("size", "1");
        requestSpec.header("page", "0");
        if (authEnabled) {
            requestSpec.header("Authorization", "Bearer " + BaseStepDef.accessToken);
        }

        BaseStepDef.response = RestAssured.given(requestSpec).baseUri(operationsAppConfig.operationAppContactPoint).expect()
                .spec(new ResponseSpecBuilder().expectStatusCode(expectedStatus).build()).when().get(operationsAppConfig.transfersEndpoint)
                .andReturn().asString();

        logger.info("GetTxn Request Response: " + BaseStepDef.response);
    }

    @And("I should have PayeeFspId as {string}")
    public void iShouldHavePayeeFspIdAs(String payeeDfspId) throws JSONException {
        assert BaseStepDef.response.contains("payeeDfspId");
        JSONObject jsonObject = new JSONObject(BaseStepDef.response);
        JSONArray jsonArray = (JSONArray) jsonObject.get("content");
        JSONObject content = (JSONObject) jsonArray.get(0);
        String value = content.get("payeeDfspId").toString();
        String payeeIdentifier = content.get("payeePartyId").toString();
        assertThat(value).isEqualTo(payeeDfspId);
        assertThat(payeeIdentifier).isEqualTo(BaseStepDef.payerIdentifier);
    }

}
