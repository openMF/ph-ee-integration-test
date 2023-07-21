package org.mifos.integrationtest.cucumber.stepdef;

import com.fasterxml.jackson.core.type.TypeReference;
import com.fasterxml.jackson.databind.ObjectMapper;
import io.cucumber.java.en.And;
import io.cucumber.java.en.Then;
import io.restassured.RestAssured;
import io.restassured.builder.ResponseSpecBuilder;
import io.restassured.specification.RequestSpecification;
import org.mifos.integrationtest.common.Batch;
import org.mifos.integrationtest.common.BatchPage;

import java.io.IOException;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;

import static com.google.common.truth.Truth.assertThat;
import static org.mifos.integrationtest.common.Utils.getDefaultSpec;

public class BatchThrottlingStepDef extends BaseStepDef {

    private int throttleTimeInSeconds;

    private boolean throttleConditionMet = true;

    private List<Date> batchStartingTimes;

    @And("the system has a configured throttle time of {int} seconds")
    public void theSystemHasAConfiguredThrottleTimeOfSeconds(int throttleTimeInSeconds) {
        this.throttleTimeInSeconds = throttleTimeInSeconds;
        assertThat(this.throttleTimeInSeconds).isGreaterThan(0);
    }

    @Then("the start time for the sub batches are retrieved")
    public void theStartTimeForTheSubBatchesAreRetrieved() {
        List<Batch> batchList = null;
        RequestSpecification requestSpec = getDefaultSpec();
        String response = RestAssured.given(requestSpec)
                .baseUri("http://localhost:8080")
                .queryParam("batchId", batchId)
                .expect()
                .spec(new ResponseSpecBuilder().expectStatusCode(200).build())
                .when()
                .get("/api/v1/batches")
                .andReturn().asString();

        ObjectMapper objectMapper = new ObjectMapper();
        try {
            BatchPage batchPage = objectMapper.readValue(response, new TypeReference<BatchPage>(){});
            batchList = batchPage.getContent();
        } catch (IOException e) {
            throw new RuntimeException(e);
        }

        batchStartingTimes = new ArrayList<>();
        for(Batch batch : batchList){
            batchStartingTimes.add(batch.getStartedAt());
        }
    }

    @And("the difference between start time of the consecutive sub batches should be greater than or equal to throttle configuration")
    public void theDifferenceBetweenStartTimeOfTheConsecutiveSubBatchesShouldBeGreaterThanOrEqualToThrottleConfiguration() {
        for(int i =0; i<batchStartingTimes.size()-1; i++){
            Date currentBatchDate = batchStartingTimes.get(i);
            Date nextBatchDate = batchStartingTimes.get(i+1);

            long differenceInSecs = findDifferenceBetweenDatesInSecs(currentBatchDate, nextBatchDate);
            throttleConditionMet = differenceInSecs < throttleTimeInSeconds;

            if(!throttleConditionMet){
                break;
            }
        }

        assertThat(throttleConditionMet).isTrue();
    }

    private long findDifferenceBetweenDatesInSecs(Date currentBatchDate, Date nextBatchDate) {
        long currentBatchTimeInMillis = currentBatchDate.getTime();
        long nextBatchTimeInMillis = nextBatchDate.getTime();
        return (nextBatchTimeInMillis - currentBatchTimeInMillis)/1000;
    }
}
