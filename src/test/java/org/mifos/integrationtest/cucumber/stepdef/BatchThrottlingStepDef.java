package org.mifos.integrationtest.cucumber.stepdef;

import static com.google.common.truth.Truth.assertThat;
import static org.mifos.integrationtest.common.Utils.getDefaultSpec;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.core.type.TypeReference;
import com.fasterxml.jackson.databind.ObjectMapper;
import io.cucumber.java.en.And;
import io.cucumber.java.en.Then;
import io.restassured.RestAssured;
import io.restassured.builder.ResponseSpecBuilder;
import io.restassured.specification.RequestSpecification;
import java.io.IOException;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;
import java.util.stream.Collectors;

import org.mifos.integrationtest.common.Batch;
import org.mifos.integrationtest.common.BatchDTO;
import org.mifos.integrationtest.common.BatchPage;
import org.mifos.integrationtest.common.SubBatchDetail;
import org.mifos.integrationtest.config.BulkProcessorConfig;
import org.mifos.integrationtest.config.OperationsAppConfig;
import org.springframework.beans.factory.annotation.Autowired;

public class BatchThrottlingStepDef extends BaseStepDef {

    private int throttleTimeInSeconds;

    private boolean throttleConditionMet = true;

    private List<SubBatchDetail> subBatchesDetailList;

    @Autowired
    private OperationsAppConfig operationsAppConfig;

    @And("the system has a configured throttle time of {int} seconds")
    public void theSystemHasAConfiguredThrottleTimeOfSeconds(int throttleTimeInSeconds) {
        this.throttleTimeInSeconds = throttleTimeInSeconds;
        assertThat(this.throttleTimeInSeconds).isGreaterThan(0);
    }

    @And("I fetch sub batch details from the response")
    public void iFetchSubBatchDetailsFromTheResponse() {
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

    @And("the difference between start time of the consecutive sub batches should be greater than or equal to throttle configuration")
    public void theDifferenceBetweenStartTimeOfTheConsecutiveSubBatchesShouldBeGreaterThanOrEqualToThrottleConfiguration() {
        List<Date> subBatchStartedAtList = subBatchesDetailList.stream().map(SubBatchDetail::getStartedAt).toList();
        for (int i = 0; i < subBatchesDetailList.size() - 1; i++) {
            Date currentBatchDate = subBatchStartedAtList.get(i);
            Date nextBatchDate = subBatchStartedAtList.get(i + 1);

            long differenceInSecs = findDifferenceBetweenDatesInSecs(currentBatchDate, nextBatchDate);
            throttleConditionMet = differenceInSecs < throttleTimeInSeconds;

            if (!throttleConditionMet) {
                break;
            }
        }
        assertThat(throttleConditionMet).isTrue();
    }

    private long findDifferenceBetweenDatesInSecs(Date currentBatchDate, Date nextBatchDate) {
        long currentBatchTimeInMillis = currentBatchDate.getTime();
        long nextBatchTimeInMillis = nextBatchDate.getTime();
        return (nextBatchTimeInMillis - currentBatchTimeInMillis) / 1000;
    }
}
