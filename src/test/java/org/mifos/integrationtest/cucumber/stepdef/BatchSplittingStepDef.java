package org.mifos.integrationtest.cucumber.stepdef;

import static com.google.common.truth.Truth.assertThat;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import io.cucumber.java.en.And;

import java.io.File;
import java.io.IOException;
import java.util.List;

import org.apache.commons.io.FileUtils;
import org.mifos.integrationtest.common.BatchDTO;
import org.mifos.integrationtest.common.SubBatchDetail;
import org.mifos.integrationtest.common.Utils;
import org.mifos.integrationtest.config.BulkProcessorConfig;
import org.mifos.integrationtest.config.OperationsAppConfig;
import org.springframework.beans.factory.annotation.Autowired;

public class BatchSplittingStepDef extends BaseStepDef {

    private int subBatchSize;

    private int totalTransactionCount;

    private List<SubBatchDetail> subBatchesDetailList;

    @And("the system has a configured sub batch size of {int} transactions")
    public void setSubBatchSize(int subBatchSize) {
        this.subBatchSize = subBatchSize;
    }

    @And("the transaction count in the batch is greater than sub batch size")
    public void theTransactionCountInTheBatchIsGreaterThanSubBatchSize() {
        File file = new File(Utils.getAbsoluteFilePathToResource(BaseStepDef.filename));
        String csvData;
        try {
            csvData = FileUtils.readFileToString(file, "UTF-8");
        } catch (IOException e) {
            throw new RuntimeException(e);
        }
        String[] split = csvData.split("\n");
        totalTransactionCount = split.length - 1;
        assertThat(totalTransactionCount).isGreaterThan(subBatchSize);
    }

    @And("I fetch batch ID from batch transaction API's response")
    public void iFetchBatchIDFromBatchTransactionAPISResponse() {
        assertThat(BaseStepDef.response).isNotEmpty();
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

    @And("the expected sub batch count is greater than 1")
    public void theExpectedSubBatchCountIsGreaterThanOne() {
        assertThat(subBatchesDetailList.size()).isGreaterThan(1);
    }

    private String fetchBatchId(String response) {
        String[] split = response.split(",");
        return split[0].substring(31);
    }
}
