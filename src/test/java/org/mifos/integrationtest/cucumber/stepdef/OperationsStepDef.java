package org.mifos.integrationtest.cucumber.stepdef;

import static com.google.common.truth.Truth.assertThat;

import io.cucumber.java.After;
import io.cucumber.java.Before;
import io.cucumber.java.en.And;
import io.cucumber.java.en.Given;
import io.cucumber.java.en.Then;
import io.cucumber.java.en.When;
import io.restassured.RestAssured;
import io.restassured.builder.ResponseSpecBuilder;
import io.restassured.specification.RequestSpecification;
import java.util.HashMap;
import java.util.Map;
import lombok.extern.slf4j.Slf4j;
import org.mifos.integrationtest.common.Utils;
import org.mifos.integrationtest.common.dto.operationsapp.BatchPaginatedResponse;

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
        log.info("Running @ops-batch-setup");
        batchDbSetup();
    }

    @After("@ops-batch-teardown")
    public void operationsBatchTestTearDown() {
        log.info("Running @ops-batch-teardown");
        batchDbTearDown();
    }

    @When("I call the batches endpoint with expected status of {int}")
    public void simpleBatchesApiCallWithNoHeader(int expectedStatus) {
        log.info("Query params: {}", scenarioScopeDef.batchesEndpointQueryParam);
        callBatchesEndpoint(expectedStatus, scenarioScopeDef.batchesEndpointQueryParam);
    }

    @And("The count of batches should be {int}")
    public void assertCountOfBatches(int expectedCount) {
        assertThat(scenarioScopeDef.batchesResponse).isNotNull();
        assertThat(scenarioScopeDef.batchesResponse.getData()).isNotNull();
        assertThat(scenarioScopeDef.batchesResponse.getData().size()).isEqualTo(expectedCount);
    }

    @Then("I am able to parse batch paginated response into DTO")
    public void parseBatchPaginatedDto() {
        assertThat(scenarioScopeDef.response).isNotNull();
        parseBatchesResponse(scenarioScopeDef.response);
    }

    @And("I add the query param key: {string} value: {string}")
    public void updateQueryParam(String key, Object value) {
        updateQueryParam(scenarioScopeDef.batchesEndpointQueryParam, key, value);
    }

    @And("I add batchId query param")
    public void addBatchIdQueryParam() {
        assertNotNull(scenarioScopeDef.batchId);
        updateQueryParam(scenarioScopeDef.batchesEndpointQueryParam, "batchId", scenarioScopeDef.batchId);
    }

    @And("I add date from filter")
    public void addDateFromFilter() {
        String date = scenarioScopeDef.dateTime;
        updateQueryParam(scenarioScopeDef.batchesEndpointQueryParam, "dateFrom", date);
    }

    @And("I add date to filter")
    public void addDateToFilter() {
        String date = BaseStepDef.getCurrentDateInFormat();
        updateQueryParam(scenarioScopeDef.batchesEndpointQueryParam, "dateTo", date);
    }

    @And("I add limit filter {int}")
    public void addLimitFilter(int limit) {
        updateQueryParam(scenarioScopeDef.batchesEndpointQueryParam, "limit", limit);
    }

    @And("I add offset filter {int}")
    public void addOffsetFilter(int offset) {
        updateQueryParam(scenarioScopeDef.batchesEndpointQueryParam, "offset", offset);
    }

    @Then("I am able to assert {int} totalBatches")
    public void assertTotalSubBatches(int count) {
        assertThat(scenarioScopeDef.batchesResponse.getTotalBatches()).isEqualTo(count);
    }

    private void batchDbSetup() {
        // instantiate the shared query param variable if null
        if (scenarioScopeDef.batchesEndpointQueryParam == null) {
            scenarioScopeDef.batchesEndpointQueryParam = new HashMap<>();
        }
    }

    private void batchDbTearDown() {
        // clearing the query parameter shared variable
        if (scenarioScopeDef.batchesEndpointQueryParam.size() > 0) {
            scenarioScopeDef.batchesEndpointQueryParam.clear();
        }
    }

    private void updateQueryParam(Map<String, Object> queryParam, String key, Object object) {
        queryParam.put(key, object);
    }

    private void callBatchesEndpoint(int expectedStatusCode, Map<String, Object> queryParams) {
        log.info("Tenant I am passing is: {}", scenarioScopeDef.tenant);
        RequestSpecification requestSpec = Utils.getDefaultSpec(scenarioScopeDef.tenant);
        if (authEnabled) {
            requestSpec.header("Authorization", "Bearer " + scenarioScopeDef.accessToken);
        }
        if (queryParams != null) {
            queryParams.forEach(requestSpec::queryParam);
        }

        scenarioScopeDef.response = RestAssured.given(requestSpec).baseUri(operationsAppConfig.operationAppContactPoint).expect()
                .spec(new ResponseSpecBuilder().expectStatusCode(expectedStatusCode).build()).when()
                .get(operationsAppConfig.batchesEndpoint).andReturn().asString();

        logger.info("Batches api Response: " + scenarioScopeDef.response);
    }

    private void parseBatchesResponse(String response) {
        try {
            scenarioScopeDef.batchesResponse = objectMapper.readValue(response, BatchPaginatedResponse.class);
        } catch (Exception e) {
            log.error(e.getLocalizedMessage());
        }
    }

    private void assertBatchPaginatedResponse(BatchPaginatedResponse response) {

    }

}
