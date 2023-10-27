package org.mifos.integrationtest.cucumber.stepdef;

import static com.google.common.truth.Truth.assertThat;

import io.cucumber.core.internal.com.fasterxml.jackson.core.JsonProcessingException;
import io.cucumber.java.en.Given;
import org.mifos.connector.common.channel.dto.TransactionChannelRequestDTO;

public class ValidateTxnDef extends BaseStepDef {

    public static TransactionChannelRequestDTO mockTransactionChannelRequestDTO = null;

    @Given("I can mock TransactionChannelRequestDTO with wrong msisdn")
    public void iCanMockTransactionChannelRequestDTOWithWrongMsisdn() throws JsonProcessingException {
        if (mockTransactionChannelRequestDTO != null) {
            assertThat(mockTransactionChannelRequestDTO).isNotNull();
            return;
        }
        StringBuilder jsonBuilder = new StringBuilder();
        jsonBuilder.append("{").append("\"payer\": {").append("\"partyIdInfo\": {").append("\"partyIdType\": \"MSISDN\",")
                .append("\"partyIdentifier\": \"277101019bbv\"").append("}},").append("\"payee\": {").append("\"partyIdInfo\": {")
                .append("\"partyIdType\": \"MSISDN\",").append("\"partyIdentifier\": \"27710102999\"").append("}},").append("\"amount\": {")
                .append("\"amount\": 230,").append("\"currency\": \"TZS\"").append("}}");
        String json = jsonBuilder.toString();
        mockTransactionChannelRequestDTO = objectMapper.readValue(json, TransactionChannelRequestDTO.class);
        assertThat(mockTransactionChannelRequestDTO).isNotNull();
        BaseStepDef.inboundTransferMockReq = mockTransactionChannelRequestDTO;
    }

}
