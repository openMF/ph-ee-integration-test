package org.mifos.integrationtest.cucumber.stepdef;

import static com.google.common.truth.Truth.assertThat;

import io.cucumber.java.en.And;
import io.cucumber.java.en.When;
import io.restassured.RestAssured;
import io.restassured.builder.ResponseSpecBuilder;
import io.restassured.specification.RequestSpecification;
import org.mifos.integrationtest.common.Utils;
import org.mifos.integrationtest.config.ChannelConnectorConfig;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;

public class ChannelClientIdDef extends BaseStepDef {

    @Value("${operations-app.auth.enabled}")
    public Boolean authEnabled;

    @Autowired
    ChannelConnectorConfig channelConnectorConfig;

    private String clientCorrelationId = "123456789";

    @And("I have request type as {string}")
    public void iHaveRequestTypeAs(String requestType) {
        scenarioScopeState.requestType = requestType;
        channelConnectorConfig.setRequestType(requestType);
        assertThat(scenarioScopeState.requestType).isNotEmpty();
    }

    @And("I should have clientRefId in response")
    public void iShouldHaveClientRefIdInResponse() {
        assertThat(scenarioScopeState.response).containsMatch("clientRefId");
    }

    @When("I call the transfer API with expected status of {int}")
    public void iCallTheTransferAPIWithExpectedStatusOf(int expectedStatus) {
        RequestSpecification requestSpec = Utils.getDefaultSpec(scenarioScopeState.tenant);
        if (authEnabled) {
            requestSpec.header("Authorization", "Bearer " + scenarioScopeState.accessToken);
        }

        scenarioScopeState.response = RestAssured.given(requestSpec).baseUri(operationsAppConfig.operationAppContactPoint).expect()
                .spec(new ResponseSpecBuilder().expectStatusCode(expectedStatus).build()).when().get(operationsAppConfig.transfersEndpoint)
                .andReturn().asString();

        logger.info("Inbound transfer Response: {}", scenarioScopeState.response);
    }

    @When("I call the txn State with client correlation id as {string} expected status of {int}")
    public void iCallTheTxnStateWithClientCorrelationIdAsExpectedStatusOf(String XClientCorrelationId, int expectedStatus) {
        RequestSpecification requestSpec = Utils.getDefaultSpec(scenarioScopeState.tenant);
        if (authEnabled) {
            requestSpec.header("Authorization", "Bearer " + scenarioScopeState.accessToken);
        }
        requestSpec.header(Utils.REQUEST_TYPE_PARAM_NAME, channelConnectorConfig.getRequestType());
        scenarioScopeState.response = RestAssured.given(requestSpec).baseUri(channelConnectorConfig.channelConnectorContactPoint).expect()
                .spec(new ResponseSpecBuilder().expectStatusCode(expectedStatus).build()).when()
                .get("/channel/txnState/" + XClientCorrelationId).andReturn().asString();

        logger.info("Txn Req response: {}", scenarioScopeState.response);
    }

    @When("I call the transfer API with size {int} and page {int} expecting expected status of {int}")
    public void iCallTheTransferAPIWithSizeAndPageExpectingExpectedStatusOf(int size, int page, int expectedStatus) {
        RequestSpecification requestSpec = Utils.getDefaultSpec(scenarioScopeState.tenant);
        if (authEnabled) {
            requestSpec.header("Authorization", "Bearer " + scenarioScopeState.accessToken);
        }

        String endpoint = String.format("%s?size=%d&page=%d", operationsAppConfig.transfersEndpoint, size, page);
        logger.info("Calling endpoint: {}", endpoint);

        scenarioScopeState.response = RestAssured.given(requestSpec)
                .baseUri(operationsAppConfig.operationAppContactPoint)
                .expect()
                .spec(new ResponseSpecBuilder().expectStatusCode(expectedStatus).build())
                .when()
                .get(endpoint)
                .andReturn()
                .asString();

        logger.info("Inbound transfer with size and page Response: {}", scenarioScopeState.response);
    }
}
