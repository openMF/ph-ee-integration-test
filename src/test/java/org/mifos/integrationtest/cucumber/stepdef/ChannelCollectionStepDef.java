package org.mifos.integrationtest.cucumber.stepdef;

import static com.google.common.truth.Truth.assertThat;
import static java.util.concurrent.TimeUnit.SECONDS;
import static org.awaitility.Awaitility.await;

import com.google.gson.Gson;
import io.cucumber.java.en.And;
import io.cucumber.java.en.Then;
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
import org.mifos.integrationtest.common.dto.operationsapp.GetTransactionRequestResponse;
import org.springframework.beans.factory.annotation.Autowired;

public class ChannelCollectionStepDef extends BaseStepDef {

    @Autowired
    ScenarioScopeState scenarioScopeState;

    @And("I have the request body with payer ams identifier keys as {string} and {string}")
    public void iHaveRequestBody(String key1, String key2) throws JSONException {
        JSONObject collectionRequestBody = CollectionHelper.getCollectionRequestBody(key1, key2);
        scenarioScopeState.requestBody = collectionRequestBody;
        logger.info(String.valueOf(scenarioScopeState.requestBody));
    }

    @When("I call the channel collection API with client correlation id and expected status of {int}")
    public void iCallChannelCollectionAPI(int expectedStatus) {
        RequestSpecification requestSpec = Utils.getDefaultSpec(scenarioScopeState.tenant);
        requestSpec.header(Utils.X_CORRELATIONID, UUID.randomUUID());
        scenarioScopeState.response = RestAssured.given(requestSpec).baseUri(channelConnectorConfig.channelConnectorContactPoint)
                // BaseStepDef.response = RestAssured.given(requestSpec).baseUri("https://localhost:8443")
                .body(scenarioScopeState.requestBody.toString()).expect()
                .spec(new ResponseSpecBuilder().expectStatusCode(expectedStatus).build()).when()
                .post(channelConnectorConfig.collectionEndpoint).andReturn().asString();
    }

    @Then("I should get transaction id in response")
    public void iGetTransactionIdInResponse() {
        CollectionResponse response = (new Gson()).fromJson(scenarioScopeState.response, CollectionResponse.class);
        scenarioScopeState.transactionId = response.getTransactionId();
        assertThat(response.getTransactionId()).isNotNull();
    }

    @Then("I should get transactionId with null value in response")
    public void iGetErrorInResponse() {
        CollectionResponse response = (new Gson()).fromJson(scenarioScopeState.response, CollectionResponse.class);
        assertThat(response.getTransactionId()).isNull();
    }

    @When("I call the get txn API in ops app with transactionId as parameter")
    public void iCallTheTxnAPIWithTransactionId() throws InterruptedException {
        RequestSpecification requestSpec = Utils.getDefaultSpec(scenarioScopeState.tenant);
        if (authEnabled) {
            requestSpec.header("Authorization", "Bearer " + scenarioScopeState.accessToken);
        }
        requestSpec.queryParam("transactionId", scenarioScopeState.transactionId);

        await().atMost(awaitMost, SECONDS).pollInterval(pollInterval, SECONDS).untilAsserted(() -> {
            scenarioScopeState.response = RestAssured.given(requestSpec).baseUri(operationsAppConfig.operationAppContactPoint).expect()
                    .spec(new ResponseSpecBuilder().expectStatusCode(200).build()).when()
                    .get(operationsAppConfig.transactionRequestsEndpoint).andReturn().asString();

            logger.info("GetTxn Request Response: " + scenarioScopeState.response);
        });
    }

    @Then("I should get transaction state as completed and externalId not null")
    public void assertValues() {
        GetTransactionRequestResponse transactionRequestResponse = (new Gson()).fromJson(scenarioScopeState.response,
                GetTransactionRequestResponse.class);
        assertThat(transactionRequestResponse.getContent().size()).isEqualTo(1);
        assertThat(transactionRequestResponse.getContent().get(0).getState()).isAnyOf("ACCEPTED", "FAILED");
        assertThat(transactionRequestResponse.getContent().get(0).getExternalId()).isNotNull();
    }

}
