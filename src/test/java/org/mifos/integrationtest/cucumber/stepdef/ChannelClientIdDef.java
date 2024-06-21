package org.mifos.integrationtest.cucumber.stepdef;

import static com.google.common.truth.Truth.assertThat;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import io.cucumber.java.en.And;
import io.cucumber.java.en.When;
import io.restassured.RestAssured;
import io.restassured.builder.ResponseSpecBuilder;
import io.restassured.specification.RequestSpecification;
import java.time.LocalDate;
import java.time.format.DateTimeFormatter;
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

        scenarioScopeState.response = RestAssured.given(requestSpec).baseUri(operationsAppConfig.operationAppContactPoint).expect()
                .spec(new ResponseSpecBuilder().expectStatusCode(expectedStatus).build()).when().get(endpoint).andReturn().asString();

        logger.info("Inbound transfer with size and page Response: {}", scenarioScopeState.response);
    }

    @When("I call the transfer API with specific date range expecting expected status of {int}")
    public void iCallTheTransferAPIWithSpecificDateRangeExpectingExpectedStatusOf(int expectedStatus) {
        RequestSpecification requestSpec = Utils.getDefaultSpec(scenarioScopeState.tenant);
        LocalDate endDate = LocalDate.now();
        LocalDate startDate = endDate.minusDays(5);
        DateTimeFormatter formatter = DateTimeFormatter.ISO_LOCAL_DATE;

        String startfrom = startDate.format(formatter);
        String startto = endDate.format(formatter);

        String endpoint = String.format("%s?startfrom=%s&startto=%s", operationsAppConfig.transfersEndpoint, startfrom, startto);
        String fullUrl = operationsAppConfig.operationAppContactPoint + endpoint;

        logger.info("Calling endpoint: {}", fullUrl);

        scenarioScopeState.response = RestAssured.given(requestSpec).baseUri(operationsAppConfig.operationAppContactPoint).expect()
                .spec(new ResponseSpecBuilder().expectStatusCode(expectedStatus).build()).when().get(endpoint).andReturn().asString();

        logger.info("Inbound transfer with date range Response: {}", scenarioScopeState.response);
    }

    @And("I should have page and size in response")
    public void iShouldHavePageAndSizeInResponse() throws JsonProcessingException {
        String response = scenarioScopeState.response;

        // Parse the JSON response
        ObjectMapper objectMapper = new ObjectMapper();
        JsonNode jsonNode = objectMapper.readTree(response);

        // Check that 'size' and 'number' are present
        assertThat(jsonNode.has("size")).isTrue();
        assertThat(jsonNode.has("number")).isTrue();

        // Check the exact values
        int expectedSize = 4;
        int expectedNumber = 2;
        assertThat(jsonNode.get("size").asInt()).isEqualTo(expectedSize);
        assertThat(jsonNode.get("number").asInt()).isEqualTo(expectedNumber);
    }

    @When("I call the transfer API with currency {string} and amount {int} expecting expected status of {int}")
    public void iCallTheTransferAPIWithCurrencyAndAmountExpectingExpectedStatusOf(String currency, int amount, int expectedStatus) {
        RequestSpecification requestSpec = Utils.getDefaultSpec(scenarioScopeState.tenant);
        if (authEnabled) {
            requestSpec.header("Authorization", "Bearer " + scenarioScopeState.accessToken);
        }

        String endpoint = String.format("%s?currency=%s&amount=%d", operationsAppConfig.transfersEndpoint, currency, amount);
        logger.info("Calling get transfer endpoint: {}", endpoint);

        scenarioScopeState.response = RestAssured.given(requestSpec).baseUri(operationsAppConfig.operationAppContactPoint).expect()
                .spec(new ResponseSpecBuilder().expectStatusCode(expectedStatus).build()).when().get(endpoint).andReturn().asString();

        logger.info("Inbound transfer with currency and amount Response: {}", scenarioScopeState.response);
    }

    @And("I should have currency and amount in response")
    public void iShouldHaveCurrencyAndAmountInResponse() throws JsonProcessingException {
        String response = scenarioScopeState.response;

        // Parse the JSON response
        ObjectMapper objectMapper = new ObjectMapper();
        JsonNode jsonNode = objectMapper.readTree(response);

        assertThat(jsonNode.has("content")).isTrue();
        assertThat(jsonNode.get("content").isArray()).isTrue();
        assertThat(jsonNode.get("content").size()).isGreaterThan(0);

        // Extract the first object in the 'content' array
        JsonNode firstContentItem = jsonNode.get("content").get(0);

        // Check that 'currency' and 'amount' are present in the first content item
        assertThat(firstContentItem.has("currency")).isTrue();
        assertThat(firstContentItem.has("amount")).isTrue();

        // Retrieve and validate the currency
        JsonNode currencyNode = firstContentItem.get("currency");
        assertThat(currencyNode).isNotNull();
        String currency = currencyNode.asText();
        String expectedCurrency = "USD";
        assertThat(currency).isEqualTo(expectedCurrency);

        // Retrieve and validate the amount
        JsonNode amountNode = firstContentItem.get("amount");
        assertThat(amountNode).isNotNull();
        String amountAsString = amountNode.asText();
        int amount = Integer.parseInt(amountAsString);
        int expectedAmount = 1;
        assertThat(amount).isEqualTo(expectedAmount);
    }
}
