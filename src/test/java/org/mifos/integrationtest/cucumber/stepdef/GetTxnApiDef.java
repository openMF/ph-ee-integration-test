package org.mifos.integrationtest.cucumber.stepdef;

import static com.google.common.truth.Truth.assertThat;
import static java.util.concurrent.TimeUnit.SECONDS;
import static org.awaitility.Awaitility.await;

import com.google.gson.Gson;
import io.cucumber.java.en.And;
import io.cucumber.java.en.When;
import io.restassured.RestAssured;
import io.restassured.builder.ResponseSpecBuilder;
import io.restassured.specification.RequestSpecification;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import org.json.JSONException;
import org.json.JSONObject;
import org.mifos.integrationtest.common.CollectionHelper;
import org.mifos.integrationtest.common.Utils;
import org.mifos.integrationtest.common.dto.CollectionResponse;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;

public class GetTxnApiDef extends BaseStepDef {

    @Autowired
    ScenarioScopeState scenarioScopeState;
    private String clientCorrelationId = "123456789";

    @Value("${operations-app.auth.enabled}")
    public Boolean authEnabled;

    @When("I call the get txn API with expected status of {int}")
    public void callTxnReqApi(int expectedStatus) {
        await().atMost(awaitMost, SECONDS).pollDelay(pollDelay, SECONDS).pollInterval(pollInterval, SECONDS).untilAsserted(() -> {
            RequestSpecification requestSpec = Utils.getDefaultSpec(scenarioScopeState.tenant);
            if (authEnabled) {
                requestSpec.header("Authorization", "Bearer " + scenarioScopeState.accessToken);
            }
            requestSpec.queryParam("transactionId", scenarioScopeState.response);
            scenarioScopeState.response = RestAssured.given(requestSpec).baseUri(operationsAppConfig.operationAppContactPoint).expect()
                    .spec(new ResponseSpecBuilder().expectStatusCode(expectedStatus).build()).when()
                    .get(operationsAppConfig.transactionRequestsEndpoint).andReturn().asString();
            assertThat(scenarioScopeState.response).isNotNull();
            logger.info("GetTxn Request Response: " + scenarioScopeState.response);
        });
    }

    @And("I should have clientCorrelationId in response")
    public void checkClientCorrelationId() {
        assertThat(scenarioScopeState.response).containsMatch("clientCorrelationId");
    }

    @When("I call the get txn API with date today minus {int} day and {string} with expected status of {int}")
    public void callTxnReqApiwithParams(int daydiff, String endDate, int expectedStatus) {
        DateTimeFormatter formatter = DateTimeFormatter.ofPattern("yyyy-MM-dd hh:mm:ss");
        if ("current date".equals(endDate)) {
            endDate = formatter.format(LocalDateTime.now().plusDays(1));
        }
        String startDate = formatter.format(LocalDateTime.now().minusDays(daydiff));
        RequestSpecification requestSpec = Utils.getDefaultSpec(scenarioScopeState.tenant);
        if (authEnabled) {
            requestSpec.header("Authorization", "Bearer " + scenarioScopeState.accessToken);
        }
        requestSpec.queryParam("startFrom", startDate);
        requestSpec.queryParam("startTo", endDate);
        scenarioScopeState.response = RestAssured.given(requestSpec).baseUri(operationsAppConfig.operationAppContactPoint).expect()
                .spec(new ResponseSpecBuilder().expectStatusCode(expectedStatus).build()).when()
                .get(operationsAppConfig.transactionRequestsEndpoint).andReturn().asString();

        logger.info("GetTxn Request Response: " + scenarioScopeState.response);

    }

    @And("I should have startedAt and completedAt in response")
    public void checkDate() {
        assertThat(scenarioScopeState.response).containsMatch("startedAt");
        assertThat(scenarioScopeState.response).containsMatch("completedAt");
    }

    @And("I call collection api with expected status {int}")
    public void iCallCollectionApiWithExpectedStatus(int expectedStatus) throws JSONException {
        RequestSpecification requestSpec = Utils.getDefaultSpec(scenarioScopeState.tenant);
        requestSpec.header(Utils.X_CORRELATIONID, clientCorrelationId);
        JSONObject collectionRequestBody = CollectionHelper.getCollectionRequestBody("1", "254708374149", "24450523");
        logger.info(String.valueOf(collectionRequestBody));
        String json = RestAssured.given(requestSpec).baseUri(channelConnectorConfig.channelConnectorContactPoint)
                .body(collectionRequestBody.toString()).expect().spec(new ResponseSpecBuilder().expectStatusCode(expectedStatus).build())
                .when().post("/channel/collection").andReturn().asString();
        CollectionResponse response = (new Gson()).fromJson(json, CollectionResponse.class);
        scenarioScopeState.response = response.getTransactionId();
        assertThat(response.getTransactionId()).isNotEmpty();
    }
}
