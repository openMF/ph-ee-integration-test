package org.mifos.integrationtest.cucumber.stepdef;

import static com.google.common.truth.Truth.assertThat;

import com.google.gson.Gson;
import io.cucumber.java.en.And;
import io.cucumber.java.en.When;
import io.restassured.RestAssured;
import io.restassured.builder.ResponseSpecBuilder;
import io.restassured.specification.RequestSpecification;
import org.json.JSONException;
import org.json.JSONObject;
import org.mifos.integrationtest.common.CollectionHelper;
import org.mifos.integrationtest.common.Utils;
import org.mifos.integrationtest.common.dto.CollectionResponse;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;

public class GetTxnApiDef extends BaseStepDef {

    @Autowired
    ScenarioScopeDef scenarioScopeDef;
    private String clientCorrelationId = "123456789";

    @Value("${operations-app.auth.enabled}")
    public Boolean authEnabled;

    @When("I call the get txn API with expected status of {int}")
    public void callTxnReqApi(int expectedStatus) {
        RequestSpecification requestSpec = Utils.getDefaultSpec(scenarioScopeDef.tenant);
        if (authEnabled) {
            requestSpec.header("Authorization", "Bearer " + scenarioScopeDef.accessToken);
        }

        BaseStepDef.response = RestAssured.given(requestSpec).baseUri(operationsAppConfig.operationAppContactPoint).expect()
                .spec(new ResponseSpecBuilder().expectStatusCode(expectedStatus).build()).when()
                .get(operationsAppConfig.transactionRequestsEndpoint).andReturn().asString();

        logger.info("GetTxn Request Response: " + scenarioScopeDef.response);
    }

    @And("I should have clientCorrelationId in response")
    public void checkClientCorrelationId() {
        assertThat(scenarioScopeDef.response).containsMatch("clientCorrelationId");
    }

    @When("I call the get txn API with date {string} and {string} expected status of {int}")
    public void callTxnReqApiwithParams(String startDate, String endDate, int expectedStatus) {
        RequestSpecification requestSpec = Utils.getDefaultSpec(scenarioScopeDef.tenant);
        if (authEnabled) {
            requestSpec.header("Authorization", "Bearer " + scenarioScopeDef.accessToken);
        }
        requestSpec.queryParam("startFrom", startDate);
        requestSpec.queryParam("startTo", endDate);
        scenarioScopeDef.response = RestAssured.given(requestSpec).baseUri(operationsAppConfig.operationAppContactPoint).expect()
                .spec(new ResponseSpecBuilder().expectStatusCode(expectedStatus).build()).when()
                .get(operationsAppConfig.transactionRequestsEndpoint).andReturn().asString();

        logger.info("GetTxn Request Response: " + scenarioScopeDef.response);

    }

    @And("I should have startedAt and completedAt in response")
    public void checkDate() {
        assertThat(scenarioScopeDef.response).containsMatch("startedAt");
        assertThat(scenarioScopeDef.response).containsMatch("completedAt");
    }

    @And("I call collection api with expected status {int}")
    public void iCallCollectionApiWithExpectedStatus(int expectedStatus) throws JSONException {
        RequestSpecification requestSpec = Utils.getDefaultSpec(scenarioScopeDef.tenant);
        requestSpec.header(Utils.X_CORRELATIONID, clientCorrelationId);
        JSONObject collectionRequestBody = CollectionHelper.getCollectionRequestBody("1", "254708374149", "24450523");
        logger.info(String.valueOf(collectionRequestBody));
        String json = RestAssured.given(requestSpec).baseUri(channelConnectorConfig.channelConnectorContactPoint)
                .body(collectionRequestBody.toString()).expect().spec(new ResponseSpecBuilder().expectStatusCode(expectedStatus).build())
                .when().post("/channel/collection").andReturn().asString();
        CollectionResponse response = (new Gson()).fromJson(json, CollectionResponse.class);
        assertThat(response.getTransactionId()).isNotEmpty();
    }
}
