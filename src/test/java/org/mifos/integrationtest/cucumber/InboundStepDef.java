package org.mifos.integrationtest.cucumber;

import io.cucumber.core.internal.com.fasterxml.jackson.core.JsonProcessingException;
import io.cucumber.java.en.And;
import io.cucumber.java.en.Given;
import io.cucumber.java.en.When;
import io.restassured.RestAssured;
import io.restassured.builder.ResponseSpecBuilder;
import io.restassured.specification.RequestSpecification;
import org.json.JSONException;
import org.json.JSONObject;
import org.mifos.connector.common.channel.dto.TransactionChannelRequestDTO;
import org.mifos.integrationtest.common.Utils;

import static com.google.common.truth.Truth.assertThat;

public class InboundStepDef extends BaseStepDef {

    public static TransactionChannelRequestDTO mockTransactionChannelRequestDTO = null;

    @Given("I can mock TransactionChannelRequestDTO")
    public void mockTransactionChannelRequestDTO() throws JsonProcessingException {
        if (mockTransactionChannelRequestDTO != null) {
            assertThat(mockTransactionChannelRequestDTO).isNotNull();
            return;
        }
        StringBuilder jsonBuilder = new StringBuilder();
        jsonBuilder.append("{")
                .append("\"payer\": {")
                .append("\"partyIdInfo\": {")
                .append("\"partyIdType\": \"MSISDN\",")
                .append("\"partyIdentifier\": \"27710101999\"")
                .append("}},")
                .append("\"payee\": {")
                .append("\"partyIdInfo\": {")
                .append("\"partyIdType\": \"MSISDN\",")
                .append("\"partyIdentifier\": \"27710102999\"")
                .append("}},")
                .append("\"amount\": {")
                .append("\"amount\": 230,")
                .append("\"currency\": \"TZS\"")
                .append("}}");
        String json = jsonBuilder.toString();
        mockTransactionChannelRequestDTO = objectMapper.readValue(json, TransactionChannelRequestDTO.class);
        assertThat(mockTransactionChannelRequestDTO).isNotNull();
    }

    @When("I call the inbound transfer endpoint with expected status of {int}")
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
            e.printStackTrace();
            assertThat(false).isTrue();
            return;
        }
        assertThat(transactionId).isNotNull();
        assertThat(transactionId).isNotEmpty();
    }

}
