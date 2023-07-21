package org.mifos.integrationtest.cucumber.stepdef;

import static com.google.common.truth.Truth.assertThat;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import io.cucumber.java.en.And;
import java.util.List;
import org.mifos.integrationtest.common.BatchDTO;
import org.mifos.integrationtest.common.SubBatchDetail;
import org.mifos.integrationtest.config.BulkProcessorConfig;
import org.mifos.integrationtest.config.OperationsAppConfig;
import org.springframework.beans.factory.annotation.Autowired;

public class BatchSplittingStepDef extends BaseStepDef {

    private int subBatchSize;

    private List<SubBatchDetail> subBatchesDetailList;

    @Autowired
    private BulkProcessorConfig bulkProcessorConfig;

    @Autowired
    private OperationsAppConfig operationsAppConfig;

    @And("the system has a configured sub batch size of {int} transactions")
    public void setSubBatchSize(int subBatchSize) {
        this.subBatchSize = subBatchSize;
    }

    @And("I fetch batch ID from batch transaction API's response")
    public void iFetchBatchIDFromBatchTransactionAPISResponse() {
        BaseStepDef.batchId = fetchBatchId(BaseStepDef.response);
        logger.info("batchId: {}", batchId);
        assertThat(batchId).isNotEmpty();
    }

    @And("I fetch sub batch details from batch summary API response")
    public void iFetchSubBatchDetailsFromBatchSummaryAPIResponse() {
        String response = BaseStepDef.response;
        BatchDTO batchSummaryResponse;
        ObjectMapper objectMapper = new ObjectMapper();

        try {
            batchSummaryResponse = objectMapper.readValue(response, BatchDTO.class);
        } catch (JsonProcessingException e) {
            throw new RuntimeException(e);
        }
        subBatchesDetailList = batchSummaryResponse.getSubBatchesDetail();
        assertThat(subBatchesDetailList).isNotNull();
    }

    @And("the expected sub batch count is equal to actual sub batch count")
    public void theExpectedSubBatchCountIsEqualToActualSubBatchCount() {
        assertThat(subBatchesDetailList.size()).isGreaterThan(1);
    }

    private String fetchBatchId(String response) {
        String[] split = response.split(",");
        return split[0].substring(31);
    }
}
