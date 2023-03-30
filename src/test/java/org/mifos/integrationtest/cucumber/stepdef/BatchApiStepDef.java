package org.mifos.integrationtest.cucumber.stepdef;

import io.cucumber.core.internal.com.fasterxml.jackson.core.JsonProcessingException;
import io.cucumber.java.en.And;
import io.cucumber.java.en.Given;
import io.cucumber.java.en.Then;
import io.cucumber.java.en.When;
import io.restassured.RestAssured;
import io.restassured.builder.ResponseSpecBuilder;
import io.restassured.specification.RequestSpecification;
import org.mifos.integrationtest.common.Utils;
import org.mifos.integrationtest.common.dto.BatchApiResponseDTO;

import java.io.File;
import java.util.UUID;

import static com.google.common.truth.Truth.assertThat;

public class BatchApiStepDef extends BaseStepDef {

    @Given("I have a batch id from previous scenario")
    public void setBatchId() {
        // todo fix this
        if (BaseStepDef.batchId == null || BaseStepDef.batchId.isEmpty()) {
            BaseStepDef.batchId = "f9fb953b-9619-4cc3-97fe-e8456f90ee82";
        }
        assertThat(BaseStepDef.batchId).isNotNull();
    }

    @Given("I have the demo csv file {string}")
    public void setFilename(String filename) {
        BaseStepDef.filename = filename;
        assertThat(BaseStepDef.filename).isNotEmpty();
    }

    @And("I have tenant as {string}")
    public void setTenant(String tenant) {
        BaseStepDef.tenant = tenant;
        assertThat(BaseStepDef.tenant).isNotEmpty();
    }

    @When("I call the batch summary API with expected status of {int}")
    public void callBatchSummaryAPI(int expectedStatus) {
        RequestSpecification requestSpec = Utils.getDefaultSpec(BaseStepDef.tenant);
        if (authEnabled) {
            requestSpec.header("Authorization", "Bearer " + BaseStepDef.accessToken);
        }
        requestSpec.queryParam("batchId", BaseStepDef.batchId);
        logger.info("Calling with batch id: {}", BaseStepDef.batchId);

        BaseStepDef.response = RestAssured.given(requestSpec)
                .baseUri(operationsAppConfig.operationAppContactPoint)
                .expect()
                .spec(new ResponseSpecBuilder().expectStatusCode(expectedStatus).build())
                .when()
                .get(operationsAppConfig.batchSummaryEndpoint)
                .andReturn().asString();

        logger.info("Batch Summary Response: " + BaseStepDef.response);
    }

    @When("I call the batch details API with expected status of {int}")
    public void callBatchDetailsAPI(int expectedStatus) {
        RequestSpecification requestSpec = Utils.getDefaultSpec(BaseStepDef.tenant);
        if (authEnabled) {
            requestSpec.header("Authorization", "Bearer " + BaseStepDef.accessToken);
        }
        requestSpec.queryParam("batchId", BaseStepDef.batchId);

        BaseStepDef.response = RestAssured.given(requestSpec)
                .baseUri(operationsAppConfig.operationAppContactPoint)
                .expect()
                .spec(new ResponseSpecBuilder().expectStatusCode(expectedStatus).build())
                .when()
                .get(operationsAppConfig.batchDetailsEndpoint)
                .andReturn().asString();

        logger.info("Batch Details Response: " + BaseStepDef.response);
    }

    @When("I call the batch transactions endpoint with expected status of {int}")
    public void callBatchTransactionsEndpoint(int expectedStatus) {
        RequestSpecification requestSpec = Utils.getDefaultSpec(BaseStepDef.tenant);
        requestSpec.header("filename", BaseStepDef.filename);
        requestSpec.header("X-CorrelationID", UUID.randomUUID().toString());
        requestSpec.queryParam("type", "CSV");
        BaseStepDef.response = RestAssured.given(requestSpec)
                .baseUri(bulkProcessorConfig.bulkProcessorContactPoint)
                .contentType("multipart/form-data")
                .multiPart("data", Utils.getAbsoluteFilePathToResource(BaseStepDef.filename))
                .expect()
                .spec(new ResponseSpecBuilder().expectStatusCode(expectedStatus).build())
                .when()
                .post(bulkProcessorConfig.bulkTransactionEndpoint)
                .andReturn().asString();

        logger.info("Batch Details Response: " + BaseStepDef.response);
    }

    @Then("I should get non empty response")
    public void nonEmptyResponseCheck() {
        assertThat(BaseStepDef.response).isNotNull();
    }

    public static void main(String[] args) {
        String name = "ph-ee-bulk-demo-6.csv";
        File file = new File(Utils.getAbsoluteFilePathToResource(name));
        System.out.println(file.exists());
    }

    @And("I should have {string} and {string} in response")
    public void iShouldHaveAndInResponse(String pollingpath, String suggestedcallback) {
        assertThat(BaseStepDef.response).contains(pollingpath);
        assertThat(BaseStepDef.response).contains(suggestedcallback);

    }
}
