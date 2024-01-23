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
        scenarioScopeDef.requestType = requestType;
        channelConnectorConfig.setRequestType(requestType);
        assertThat(scenarioScopeDef.requestType).isNotEmpty();
    }

    @And("I should have clientRefId in response")
    public void iShouldHaveClientRefIdInResponse() {
        assertThat(scenarioScopeDef.response).containsMatch("clientRefId");
    }

    @When("I call the transfer API with expected status of {int}")
    public void iCallTheTransferAPIWithExpectedStatusOf(int expectedStatus) {
        RequestSpecification requestSpec = Utils.getDefaultSpec(scenarioScopeDef.tenant);
        if (authEnabled) {
            requestSpec.header("Authorization", "Bearer " + scenarioScopeDef.accessToken);
        }

        scenarioScopeDef.response = RestAssured.given(requestSpec).baseUri(operationsAppConfig.operationAppContactPoint).expect()
                .spec(new ResponseSpecBuilder().expectStatusCode(expectedStatus).build()).when().get(operationsAppConfig.transfersEndpoint)
                .andReturn().asString();

        logger.info("Inbound transfer Response: {}", scenarioScopeDef.response);
    }

    @When("I call the txn State with client correlation id as {int} expected status of {int}")
    public void iCallTheTxnStateWithClientCorrelationIdAsExpectedStatusOf(int XClientCorrelationId, int expectedStatus) {
        RequestSpecification requestSpec = Utils.getDefaultSpec(scenarioScopeDef.tenant);
        if (authEnabled) {
            requestSpec.header("Authorization", "Bearer " + scenarioScopeDef.accessToken);
        }
        requestSpec.header(Utils.REQUEST_TYPE_PARAM_NAME, channelConnectorConfig.getRequestType());
        scenarioScopeDef.response = RestAssured.given(requestSpec).baseUri(channelConnectorConfig.channelConnectorContactPoint).expect()
                .spec(new ResponseSpecBuilder().expectStatusCode(expectedStatus).build()).when()
                .get("/channel/txnState/" + XClientCorrelationId).andReturn().asString();

        logger.info("Txn Req response: {}", scenarioScopeDef.response);
    }
}
