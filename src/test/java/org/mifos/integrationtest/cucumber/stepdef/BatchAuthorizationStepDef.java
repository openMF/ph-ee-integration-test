package org.mifos.integrationtest.cucumber.stepdef;

import static com.google.common.truth.Truth.assertThat;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import io.cucumber.java.en.Then;
import org.mifos.integrationtest.common.BatchDTO;

public class BatchAuthorizationStepDef extends BaseStepDef {

    @Then("I should get batch status as {string}")
    public void iShouldGetBatchStatusAs(String status) {
        String response = BaseStepDef.response;
        BatchDTO batchSummaryResponse;
        ObjectMapper objectMapper = new ObjectMapper();
        try {
            batchSummaryResponse = objectMapper.readValue(response, BatchDTO.class);
        } catch (JsonProcessingException e) {
            throw new RuntimeException(e);
        }
        assertThat(batchSummaryResponse.getStatus()).isEqualTo(status);
    }
}
