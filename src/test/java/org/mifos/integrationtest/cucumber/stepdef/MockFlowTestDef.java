package org.mifos.integrationtest.cucumber.stepdef;

import static com.google.common.truth.Truth.assertThat;
import static java.util.concurrent.TimeUnit.SECONDS;
import static org.awaitility.Awaitility.await;

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
    ScenarioScopeState scenarioScopeState;

    @When("I call the outbound transfer endpoint with expected status {int}")
    public void iCallTheOutboundTransferEndpointWithExpectedStatus(int expectedStatus) {
        RequestSpecification requestSpec = Utils.getDefaultSpec(scenarioScopeState.tenant);
        logger.info("X-CorrelationId: {}", scenarioScopeState.clientCorrelationId);
        requestSpec.header(Utils.X_CORRELATIONID, scenarioScopeState.clientCorrelationId);
        requestSpec.header("X-Registering-Institution-ID", "SocialWelfare");
        scenarioScopeState.response = RestAssured.given(requestSpec).baseUri(channelConnectorConfig.channelConnectorContactPoint)
                .body(scenarioScopeState.inboundTransferMockReq).expect()
                .spec(new ResponseSpecBuilder().expectStatusCode(expectedStatus).build()).when()
                .post(channelConnectorConfig.transferEndpoint).andReturn().asString();

        logger.info("Inbound transfer Response: {}", scenarioScopeState.response);
        scenarioScopeState.transactionId = scenarioScopeState.response.split(":")[1].split(",")[0].split("\"")[1];
        logger.info("TransactionId: {}", scenarioScopeState.transactionId);
    }

    @And("I should have PayerFspId as not null")
    public void iShouldHavePayerFspIdAsNotNull() throws JSONException {
        assert scenarioScopeState.response.contains("payerDfspId");
        JSONObject jsonObject = new JSONObject(scenarioScopeState.response);
        JSONArray jsonArray = (JSONArray) jsonObject.get("content");
        JSONObject content = (JSONObject) jsonArray.get(0);
        String value = content.get("payerDfspId").toString();
        assert value != null;
    }

    @When("I call the get txn API with expected status of {int} and txnId")
    public void iCallTheGetTxnAPIWithExpectedStatusOfAndTxnId(int expectedStatus) {
        await().atMost(awaitMost, SECONDS).pollInterval(pollInterval, SECONDS).untilAsserted(() -> {
            RequestSpecification requestSpec = Utils.getDefaultSpec(scenarioScopeState.tenant);
            requestSpec.queryParam("transactionId", scenarioScopeState.transactionId);
            requestSpec.queryParam("size", "1");
            requestSpec.header("page", "0");
            if (authEnabled) {
                requestSpec.header("Authorization", "Bearer " + scenarioScopeState.accessToken);
            }

            scenarioScopeState.response = RestAssured.given(requestSpec).baseUri(operationsAppConfig.operationAppContactPoint).expect()
                    .spec(new ResponseSpecBuilder().expectStatusCode(expectedStatus).build()).when()
                    .get(operationsAppConfig.transfersEndpoint).andReturn().asString();

            logger.info("GetTxn Request Response: " + scenarioScopeState.response);
            assertThat(scenarioScopeState.response).containsMatch("startedAt");
            assertThat(scenarioScopeState.response).containsMatch("completedAt");
        });
    }

    @When("I call the get txn API with expected status of {int} and txnId with PayeeDFSPId check")
    public void iCallTheGetTxnAPIWithExpectedStatusOfAndTxnIdWithPayeeDFSPIdCheck(int expectedStatus) {
        await().pollDelay(15, SECONDS).atMost(awaitMost, SECONDS).pollInterval(pollInterval, SECONDS).untilAsserted(() -> {
            RequestSpecification requestSpec = Utils.getDefaultSpec(scenarioScopeState.tenant);
            requestSpec.queryParam("transactionId", scenarioScopeState.transactionId);
            requestSpec.queryParam("size", "1");
            requestSpec.header("page", "0");
            if (authEnabled) {
                requestSpec.header("Authorization", "Bearer " + scenarioScopeState.accessToken);
            }

            scenarioScopeState.response = RestAssured.given(requestSpec).baseUri(operationsAppConfig.operationAppContactPoint).expect()
                    .spec(new ResponseSpecBuilder().expectStatusCode(expectedStatus).build()).when()
                    .get(operationsAppConfig.transfersEndpoint).andReturn().asString();

            logger.info("GetTxn Request Response: " + scenarioScopeState.response);
            assertThat(scenarioScopeState.response).containsMatch("startedAt");
            assertThat(scenarioScopeState.response).containsMatch("completedAt");
            assert scenarioScopeState.response.contains("payeeDfspId");
            JSONObject jsonObject = new JSONObject(scenarioScopeState.response);
            JSONArray jsonArray = (JSONArray) jsonObject.get("content");
            JSONObject content = (JSONObject) jsonArray.get(0);
            String payeeDfspId = content.get("payeeDfspId").toString();
            assertThat(payeeDfspId).isNotEmpty();
            assertThat(payeeDfspId).isNotEqualTo("null");
            String payeeIdentifier = content.get("payeePartyId").toString();
            assertThat(payeeIdentifier).isEqualTo(scenarioScopeState.payerIdentifier);
        });
    }

    @And("I should have PayeeFspId as {string}")
    public void iShouldHavePayeeFspIdAs(String payeeDfspId) throws JSONException {
        assert scenarioScopeState.response.contains("payeeDfspId");
        JSONObject jsonObject = new JSONObject(scenarioScopeState.response);
        JSONArray jsonArray = (JSONArray) jsonObject.get("content");
        JSONObject content = (JSONObject) jsonArray.get(0);
        String value = content.get("payeeDfspId").toString();
        String payeeIdentifier = content.get("payeePartyId").toString();
        assertThat(value).isEqualTo(payeeDfspId);
        assertThat(payeeIdentifier).isEqualTo(scenarioScopeState.payerIdentifier);
    }

}
