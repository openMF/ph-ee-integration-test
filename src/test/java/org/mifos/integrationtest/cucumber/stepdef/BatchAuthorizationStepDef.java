package org.mifos.integrationtest.cucumber.stepdef;

import static com.google.common.truth.Truth.assertThat;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import io.cucumber.java.en.Then;
import org.mifos.integrationtest.common.BatchSummaryResponse;

public class BatchAuthorizationStepDef extends BaseStepDef {

    @Then("I should get batch status as {string}")
    public void iShouldGetBatchStatusAs(String status) {
        String response = BaseStepDef.response;
        BatchSummaryResponse batchSummaryResponse;
        ObjectMapper objectMapper = new ObjectMapper();
        try {
            batchSummaryResponse = objectMapper.readValue(response, BatchSummaryResponse.class);
        } catch (JsonProcessingException e) {
            throw new RuntimeException(e);
        }
        assertThat(batchSummaryResponse.getStatus()).isEqualTo(status);
    }
}
