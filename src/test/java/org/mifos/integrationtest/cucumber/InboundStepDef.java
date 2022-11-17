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
        String json = "{\n" +
                "    \"payer\": {\n" +
                "        \"partyIdInfo\": {\n" +
                "            \"partyIdType\": \"MSISDN\",\n" +
                "            \"partyIdentifier\": \"27710101999\"\n" +
                "        }\n" +
                "    },\n" +
                "    \"payee\": {\n" +
                "        \"partyIdInfo\": {\n" +
                "            \"partyIdType\": \"MSISDN\",\n" +
                "            \"partyIdentifier\": \"27710102999\"\n" +
                "        }\n" +
                "    },\n" +
                "    \"amount\": {\n" +
                "        \"amount\": 230,\n" +
                "        \"currency\": \"TZS\"\n" +
                "    }\n" +
                "}";
        mockTransactionChannelRequestDTO = objectMapper.readValue(json, TransactionChannelRequestDTO.class);
        assertThat(mockTransactionChannelRequestDTO).isNotNull();
    }

    @When("I call the inbound transfer endpoint with expected status of {int}")
    public void callBatchSummaryAPI(int expectedStatus) {
        RequestSpecification requestSpec = Utils.getDefaultSpec(BaseStepDef.tenant);

        BaseStepDef.response = RestAssured.given(requestSpec)
                .baseUri(channelConnectorConfig.channelConnectorContactPoint)
                .body(mockTransactionChannelRequestDTO) // todo check if this is correct?
                .expect()
                .spec(new ResponseSpecBuilder().expectStatusCode(expectedStatus).build())
                .when()
                .post(channelConnectorConfig.transferEndpoint)
                .andReturn().asString();

        logger.info("Inbound transfer Response: " + BaseStepDef.response);
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
