package org.mifos.integrationtest.cucumber.stepdef;

import static com.google.common.truth.Truth.assertThat;

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

public class MockFlowTestDef extends BaseStepDef {

    @Value("${operations-app.auth.enabled}")
    public Boolean authEnabled;

    @Autowired
    OperationsAppConfig operationsAppConfig;

    @Autowired
    ScenarioScopeDef scenarioScopeDef;

    @When("I call the outbound transfer endpoint with expected status {int}")
    public void iCallTheOutboundTransferEndpointWithExpectedStatus(int expectedStatus) {
        RequestSpecification requestSpec = Utils.getDefaultSpec(scenarioScopeDef.tenant);
        logger.info("X-CorrelationId: {}", scenarioScopeDef.clientCorrelationId);
        requestSpec.header(Utils.X_CORRELATIONID, scenarioScopeDef.clientCorrelationId);
        requestSpec.header("X-Registering-Institution-ID", "SocialWelfare");
        scenarioScopeDef.response = RestAssured.given(requestSpec).baseUri(channelConnectorConfig.channelConnectorContactPoint)
                .body(scenarioScopeDef.inboundTransferMockReq).expect()
                .spec(new ResponseSpecBuilder().expectStatusCode(expectedStatus).build()).when()
                .post(channelConnectorConfig.transferEndpoint).andReturn().asString();

        logger.info("Inbound transfer Response: {}", scenarioScopeDef.response);
        scenarioScopeDef.transactionId = scenarioScopeDef.response.split(":")[1].split(",")[0].split("\"")[1];
        logger.info("TransactionId: {}", scenarioScopeDef.transactionId);
    }

    @And("I should have PayerFspId as not null")
    public void iShouldHavePayerFspIdAsNotNull() throws JSONException {
        assert scenarioScopeDef.response.contains("payerDfspId");
        JSONObject jsonObject = new JSONObject(scenarioScopeDef.response);
        JSONArray jsonArray = (JSONArray) jsonObject.get("content");
        JSONObject content = (JSONObject) jsonArray.get(0);
        String value = content.get("payerDfspId").toString();
        assert value != null;
    }

    @When("I call the get txn API with expected status of {int} and txnId")
    public void iCallTheGetTxnAPIWithExpectedStatusOfAndTxnId(int expectedStatus) {
        RequestSpecification requestSpec = Utils.getDefaultSpec(scenarioScopeDef.tenant);
        requestSpec.queryParam("transactionId", scenarioScopeDef.transactionId);
        requestSpec.queryParam("size", "1");
        requestSpec.header("page", "0");
        if (authEnabled) {
            requestSpec.header("Authorization", "Bearer " + scenarioScopeDef.accessToken);
        }

        scenarioScopeDef.response = RestAssured.given(requestSpec).baseUri(operationsAppConfig.operationAppContactPoint).expect()
                .spec(new ResponseSpecBuilder().expectStatusCode(expectedStatus).build()).when().get(operationsAppConfig.transfersEndpoint)
                .andReturn().asString();

        logger.info("GetTxn Request Response: " + scenarioScopeDef.response);
    }

    @And("I should have PayeeFspId as {string}")
    public void iShouldHavePayeeFspIdAs(String payeeDfspId) throws JSONException {
        assert scenarioScopeDef.response.contains("payeeDfspId");
        JSONObject jsonObject = new JSONObject(scenarioScopeDef.response);
        JSONArray jsonArray = (JSONArray) jsonObject.get("content");
        JSONObject content = (JSONObject) jsonArray.get(0);
        String value = content.get("payeeDfspId").toString();
        String payeeIdentifier = content.get("payeePartyId").toString();
        assertThat(value).isEqualTo(payeeDfspId);
        assertThat(payeeIdentifier).isEqualTo(scenarioScopeDef.payerIdentifier);
    }

}
