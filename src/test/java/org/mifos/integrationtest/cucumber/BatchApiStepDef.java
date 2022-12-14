package org.mifos.integrationtest.cucumber;

import io.cucumber.java.en.And;
import io.cucumber.java.en.Given;
import io.cucumber.java.en.Then;
import io.cucumber.java.en.When;
import io.restassured.RestAssured;
import io.restassured.builder.ResponseSpecBuilder;
import io.restassured.specification.RequestSpecification;
import org.mifos.integrationtest.common.Utils;

import java.io.File;
import java.util.UUID;

import static com.google.common.truth.Truth.assertThat;

public class BatchApiStepDef extends BaseStepDef {

    @Given("I have a batch with id {string}")
    public void setBatchId(String batchId) {
        BaseStepDef.batchId = batchId;
        assertThat(BaseStepDef.batchId).isNotEmpty();
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
        requestSpec.header("Authorization", "Bearer " + BaseStepDef.accessToken);
        requestSpec.queryParam("batchId", BaseStepDef.batchId);

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
        requestSpec.header("Authorization", "Bearer " + BaseStepDef.accessToken);
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
                .post(operationsAppConfig.batchDetailsEndpoint)
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

}
