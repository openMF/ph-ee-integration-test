package org.mifos.integrationtest.cucumber.stepdef;

import static com.google.common.truth.Truth.assertThat;

import io.cucumber.core.internal.com.fasterxml.jackson.core.JsonProcessingException;
import io.cucumber.java.en.And;
import io.cucumber.java.en.Given;
import io.cucumber.java.en.When;
import io.restassured.RestAssured;
import io.restassured.builder.ResponseSpecBuilder;
import io.restassured.specification.RequestSpecification;
import io.restassured.specification.ResponseSpecification;
import org.json.JSONException;
import org.json.JSONObject;
import org.mifos.connector.common.channel.dto.TransactionChannelRequestDTO;
import org.mifos.integrationtest.common.Utils;
import static org.hamcrest.Matchers.*;

public class InboundStepDef extends BaseStepDef {

    public static TransactionChannelRequestDTO mockTransactionChannelRequestDTO = null;

    @Given("I can mock TransactionChannelRequestDTO")
    public void mockTransactionChannelRequestDTO() throws JsonProcessingException {
        if (mockTransactionChannelRequestDTO != null) {
            assertThat(mockTransactionChannelRequestDTO).isNotNull();
            return;
        }
        StringBuilder jsonBuilder = new StringBuilder();
        jsonBuilder.append("{").append("\"payer\": {").append("\"partyIdInfo\": {").append("\"partyIdType\": \"MSISDN\",")
                .append("\"partyIdentifier\": \"27710101999\"").append("}},").append("\"payee\": {").append("\"partyIdInfo\": {")
                .append("\"partyIdType\": \"MSISDN\",").append("\"partyIdentifier\": \"27710102999\"").append("}},").append("\"amount\": {")
                .append("\"amount\": 230,").append("\"currency\": \"TZS\"").append("}}");
        String json = jsonBuilder.toString();
        mockTransactionChannelRequestDTO = objectMapper.readValue(json, TransactionChannelRequestDTO.class);
        assertThat(mockTransactionChannelRequestDTO).isNotNull();
        BaseStepDef.inboundTransferMockReq = mockTransactionChannelRequestDTO;
    }

    @When("I call the inbound transfer endpoint with expected status of {int}")
    public void callBatchSummaryAPILegacy(int expectedStatus) {
        callBatchSummaryAPI(expectedStatus);
    }

    @When("I call the inbound transfer endpoint with expected status of {int} and no authentication")
    public void callBatchSummaryAPINoAuth(int expectedStatus) {
        callBatchSummaryAPI(expectedStatus);
    }

    @And("I call the inbound transfer endpoint with authentication")
    public void callBatchSummaryAPIWithAuth() {
        RequestSpecification requestSpec = Utils.getDefaultSpec(BaseStepDef.tenant);
        requestSpec.header("Authorization", "Bearer " +
                keycloakTokenResponse.getAccessToken());

        // since after authentication channel can return anything apart from 400 and 401
        ResponseSpecification responseSpecBuilder = new ResponseSpecBuilder()
                .expectStatusCode(anyOf(not(anyOf(is(400), is(401))))).build();

        BaseStepDef.response = RestAssured.given(requestSpec)
                .baseUri(channelConnectorConfig.channelConnectorContactPoint)
                .body(mockTransactionChannelRequestDTO)
                .expect()
                .spec(responseSpecBuilder)
                .when()
                .post(channelConnectorConfig.transferEndpoint)
                .andReturn().asString();
    }

    public void callBatchSummaryAPI(int expectedStatus) {
        RequestSpecification requestSpec = Utils.getDefaultSpec(BaseStepDef.tenant);
        BaseStepDef.response = RestAssured.given(requestSpec)
                .baseUri(channelConnectorConfig.channelConnectorContactPoint)
                .body(mockTransactionChannelRequestDTO)
                .expect()
                .spec(new ResponseSpecBuilder().expectStatusCode(expectedStatus).build())
                .when()
                .post(channelConnectorConfig.transferEndpoint)
                .andReturn().asString();
        logger.info("Inbound transfer Response: {}", BaseStepDef.response);
    }

    @And("I should be able to parse transactionId")
    public void parseTransactionId() {
        String transactionId;
        try {
            JSONObject jsonObject = new JSONObject(BaseStepDef.response);
            transactionId = jsonObject.getString("transactionId");
        } catch (JSONException e) {
            logger.error("Error parsing the transaction id", e);
            assertThat(false).isTrue();
            return;
        }
        assertThat(transactionId).isNotNull();
        assertThat(transactionId).isNotEmpty();
    }

    @Given("I can mock TransactionChannelRequestDTO for account lookup")
    public void iCanMockTransactionChannelRequestDTOForAccountLookup() throws JsonProcessingException {
        StringBuilder jsonBuilder = new StringBuilder();
        jsonBuilder.append("{").append("\"payer\": {").append("\"partyIdInfo\": {").append("\"partyIdType\": \"MSISDN\",")
                .append("\"partyIdentifier\": \"27710101999\"").append("}").append("},").append("\"payee\": {").append("\"partyIdInfo\": {")
                .append("\"partyIdType\": \"MSISDN\",").append("\"partyIdentifier\": ").append("\"").append(beneficiaryPayeeIdentity)
                .append("\"") // Replace with the variable here
                .append("}").append("},").append("\"amount\": {").append("\"amount\": 2240,").append("\"currency\": \"TZS\"").append("}")
                .append("}");

        String json = jsonBuilder.toString();
        mockTransactionChannelRequestDTO = objectMapper.readValue(json, TransactionChannelRequestDTO.class);
        assertThat(mockTransactionChannelRequestDTO).isNotNull();
        BaseStepDef.inboundTransferMockReq = mockTransactionChannelRequestDTO;
    }

}
