package org.mifos.integrationtest.cucumber.stepdef;

import io.cucumber.java.After;
import io.cucumber.java.Before;
import io.cucumber.java.en.And;
import io.cucumber.java.en.Given;
import io.cucumber.java.en.Then;
import io.cucumber.java.en.When;
import io.restassured.RestAssured;
import io.restassured.builder.ResponseSpecBuilder;
import io.restassured.specification.RequestSpecification;
import lombok.extern.slf4j.Slf4j;
import org.mifos.integrationtest.common.Utils;
import org.mifos.integrationtest.common.dto.operationsapp.BatchPaginatedResponse;

import java.util.Map;

import static com.google.common.truth.Truth.assertThat;

@Slf4j
public class OperationsStepDef extends BaseStepDef {

    // todo remove once @gov-223 testing is done
    @Given("I am happy")
    public void happy() {
        assertThat(1).isEqualTo(1);
    }

    // todo remove once @gov-223 testing is done
    @When("I get chocolate")
    public void whenHappy() {
        assertThat(1).isEqualTo(1);
    }

    // todo remove once @gov-223 testing is done
    @Then("I get more happy")
    public void moreHappy() {
        assertThat(1).isEqualTo(1);
    }

    @Before("@ops-batch-setup")
    public void operationsBatchTestSetup() {
        batchDbSetup();
        log.info("Running @ops-batch-setup");
    }

    @After("@ops-batch-teardown")
    public void operationsBatchTestTearDown() {
        batchDbTearDown();
        log.info("Running @ops-batch-teardown");
    }

    @When("I call the batches endpoint with expected status of {int}")
    public void simpleBatchesApiCallWithNoHeader(int expectedStatus) {
        callBatchesEndpoint(expectedStatus, BaseStepDef.batchesEndpointQueryParam);
    }

    @Then("I am able to parse batch paginated response into DTO")
    public void parseBatchPaginatedDto() {
        assertThat(BaseStepDef.response).isNotNull();
        parseBatchesResponse(BaseStepDef.response);
    }

    @And("I add the query param key: {string} value: {string}")
    public void updateQueryParam(String key, Object value) {
        updateQueryParam(BaseStepDef.batchesEndpointQueryParam, key, value);
    }

    @And("I add batchId query param")
    public void addBatchIdQueryParam() {
        assertNotNull(BaseStepDef.batchId);
        updateQueryParam(BaseStepDef.batchesEndpointQueryParam, "batchId", BaseStepDef.batchId);
    }

    @Then("I am able to assert only {int} totalBatches")
    public void assertTotalSubBatches(int count) {
        assertThat(BaseStepDef.batchesResponse.getTotalBatches()).isEqualTo(count);
    }

    private void batchDbSetup() {
		// todo define the setup step/task in this method
        assertThat(1).isEqualTo(1);
    }

    private void batchDbTearDown() {
        // todo define the teardown step/task in this method
        assertThat(1).isEqualTo(1);
    }

    private void updateQueryParam(Map<String, Object> queryParam, String key, Object object) {
        queryParam.put(key, object);
    }

    private void callBatchesEndpoint(int expectedStatusCode, Map<String, Object> queryParams) {
        RequestSpecification requestSpec = Utils.getDefaultSpec(BaseStepDef.tenant);
        if (authEnabled) {
            requestSpec.header("Authorization", "Bearer " + BaseStepDef.accessToken);
        }
        if (queryParams != null) {
            queryParams.forEach(requestSpec::queryParam);
        }

        BaseStepDef.response = RestAssured.given(requestSpec).baseUri(operationsAppConfig.operationAppContactPoint)
                .expect()
                .spec(new ResponseSpecBuilder().expectStatusCode(expectedStatusCode).build())
                .when()
                .get(operationsAppConfig.batchesEndpoint).andReturn().asString();

        logger.info("Batch Summary Response: " + BaseStepDef.response);
    }

    private void parseBatchesResponse(String response) {
        try {
            BaseStepDef.batchesResponse = objectMapper.readValue(response, BatchPaginatedResponse.class);
        } catch (Exception e) {
            log.error(e.getLocalizedMessage());
        }
    }

    private void assertBatchPaginatedResponse(BatchPaginatedResponse response) {

    }

}
