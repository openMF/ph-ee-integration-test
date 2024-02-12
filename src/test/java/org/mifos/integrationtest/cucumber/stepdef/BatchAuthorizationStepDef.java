package org.mifos.integrationtest.cucumber.stepdef;

import static com.github.tomakehurst.wiremock.client.WireMock.equalTo;
import static com.github.tomakehurst.wiremock.client.WireMock.matchingJsonPath;
import static com.github.tomakehurst.wiremock.client.WireMock.postRequestedFor;
import static com.github.tomakehurst.wiremock.client.WireMock.urlEqualTo;
import static com.github.tomakehurst.wiremock.client.WireMock.verify;
import static com.google.common.truth.Truth.assertThat;
import static java.util.concurrent.TimeUnit.SECONDS;
import static org.awaitility.Awaitility.await;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import io.cucumber.java.en.Then;
import io.cucumber.java.en.When;
import io.restassured.RestAssured;
import io.restassured.builder.ResponseSpecBuilder;
import io.restassured.specification.RequestSpecification;
import org.mifos.integrationtest.common.AuthorizationRequest;
import org.mifos.integrationtest.common.BatchSummaryResponse;
import org.mifos.integrationtest.common.Utils;
import org.mifos.integrationtest.config.IdentityMapperConfig;
import org.mifos.integrationtest.config.MockPaymentSchemaConfig;
import org.springframework.beans.factory.annotation.Autowired;

public class BatchAuthorizationStepDef extends BaseStepDef {

    @Autowired
    MockPaymentSchemaConfig mockPaymentSchemaConfig;

    @Autowired
    IdentityMapperConfig identityMapperConfig;

    @Autowired
    ScenarioScopeState scenarioScopeState;

    private static AuthorizationRequest authorizationRequest;

    @Then("I should get batch status as {string}")
    public void iShouldGetBatchStatusAs(String status) {
        String response = scenarioScopeState.response;
        BatchSummaryResponse batchSummaryResponse;
        ObjectMapper objectMapper = new ObjectMapper();
        try {
            batchSummaryResponse = objectMapper.readValue(response, BatchSummaryResponse.class);
        } catch (JsonProcessingException e) {
            throw new RuntimeException(e);
        }
        assertThat(batchSummaryResponse.getStatus()).isEqualTo(status);
    }

    @Then("I should be able to verify that the {string} method to {string} endpoint received a request with authorization status")
    public void iShouldBeAbleToVerifyThatTheMethodToEndpointReceivedARequestWithAuthorizationStatus(String httpMethod, String endpoint) {
        await().atMost(awaitMost, SECONDS).pollInterval(pollInterval, SECONDS).untilAsserted(() -> {
            verify(postRequestedFor(urlEqualTo(endpoint)).withRequestBody(matchingJsonPath("$.status", equalTo("Y"))));
        });
    }

    @When("I call the Authorization API with batchId as {string} and expected status of {int} and stub {string}")
    public void iCallTheAuthorizationAPIWithBatchIdAsAndExpectedStatusOfAndStub(String batchId, int expectedStatus, String stub) {
        RequestSpecification requestSpec = Utils.getDefaultSpec();
        scenarioScopeState.response = RestAssured.given(requestSpec).header("Content-Type", "application/json")
                .header("X-CallbackURL", identityMapperConfig.callbackURL + stub).header("X-Client-Correlation-ID", "998877")
                .queryParam("command", "authorize").baseUri(mockPaymentSchemaConfig.mockPaymentSchemaContactPoint)
                .body(authorizationRequest).expect().spec(new ResponseSpecBuilder().expectStatusCode(expectedStatus).build()).when()
                .post(mockPaymentSchemaConfig.mockBatchAuthorizationEndpoint + batchId).andReturn().asString();

        logger.info("Authorization Response: {}", scenarioScopeState.response);
    }

    @When("I create an AuthorizationRequest for Batch Authorization with batch ID as {string}, payerIdentifier as {string}, currency as {string} and amount as {string}")
    public void iCreateAnAuthorizationRequestForBatchAuthorizationWithBatchIDAsPayerIdentifierAsCurrencyAsAndAmountAs(String batchId,
            String payerIdentifier, String currency, String amount) {
        authorizationRequest = new AuthorizationRequest(batchId, payerIdentifier, currency, amount);
    }
}
