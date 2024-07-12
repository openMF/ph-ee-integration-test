package org.mifos.integrationtest.cucumber.stepdef;

import static java.util.concurrent.TimeUnit.SECONDS;
import static org.junit.Assert.fail;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import io.cucumber.java.en.And;
import io.cucumber.java.en.Given;
import io.cucumber.java.en.Then;
import io.cucumber.java.en.When;
import io.restassured.RestAssured;
import io.restassured.builder.ResponseSpecBuilder;
import io.restassured.specification.RequestSpecification;
import java.util.ArrayList;
import java.util.List;
import org.apache.hc.core5.http.HttpStatus;
import org.awaitility.Awaitility;
import org.hamcrest.MatcherAssert;
import org.hamcrest.Matchers;
import org.junit.Assert;
import org.mifos.integrationtest.common.Utils;
import org.mifos.integrationtest.common.dto.operationsapp.ReportParameter;
import org.mifos.integrationtest.common.dto.operationsapp.ReportRequestDTO;
import org.mifos.integrationtest.config.OperationsAppConfig;
import org.mifos.integrationtest.config.TenantConfig;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;


public class ReportManagementSteps extends BaseStepDef {

    @Autowired
    TenantConfig tenantConfig;
    @Autowired
    ScenarioScopeState scenarioScopeState;

    Logger logger = LoggerFactory.getLogger(this.getClass());

    @Autowired
    OperationsAppConfig operationsAppConfig;

    @Given("Have tenant as {string}")
    public void iHaveTenannt(String tenant) {
        scenarioScopeState.tenant = tenantConfig.getTenant(tenant.toLowerCase());
    }

    @When("I call the get list of reports API with expected status of {int}")
    public void iCallTheTransferAPIWithExpectedStatusOf(int expectedStatus) {
        RequestSpecification requestSpec = Utils.getDefaultSpec(scenarioScopeState.tenant);
        scenarioScopeState.response = RestAssured.given(requestSpec).baseUri(operationsAppConfig.operationAppContactPoint).expect()
                .spec(new ResponseSpecBuilder().expectStatusCode(expectedStatus).build()).when().get(operationsAppConfig.reportEndpoint)
                .andReturn().asString();

        logger.info("ReportGet Response: {}", scenarioScopeState.response);
    }

    @Then("the response should contain a list of reports")
    public void theResponseShouldContainListOfReports() {
        Assert.assertNotNull(scenarioScopeState.response);
    }

    @When("I call the create report API with valid data with expected status of {int}")
    public void iCallCreateReportAPIWithValidData(int expectedStatus) {

        ObjectMapper objectMapper = new ObjectMapper();
        ReportRequestDTO reportDTO = new ReportRequestDTO();

        reportDTO.setReportName("Sample Report");
        reportDTO.setReportType("Financial");
        reportDTO.setReportSubType("Monthly");
        reportDTO.setReportCategory("Operational");
        reportDTO.setDescription("Financial summary for the month of June");
        reportDTO.setReportSql("SELECT * FROM transactions WHERE date >= '2024-06-01' AND date <= '2024-06-30'");
        List<ReportParameter> reportParameters = new ArrayList<>();

        ReportParameter parameter1 = new ReportParameter();
        parameter1.setParameterKey("Param1");
        parameter1.setParameterValue("Value1");

        ReportParameter parameter2 = new ReportParameter();
        parameter2.setParameterKey("Param2");
        parameter2.setParameterValue("Value2");

        reportParameters.add(parameter1);
        reportParameters.add(parameter2);

        reportDTO.setReportParameters(reportParameters);

        try {
            scenarioScopeState.createReportBody = objectMapper.writeValueAsString(reportDTO);
        } catch (JsonProcessingException e) {
            logger.error("Unable to convert the DTO : {}", e);
        }

        Awaitility.await().atMost(awaitMost, SECONDS).pollDelay(pollDelay, SECONDS).pollInterval(pollInterval, SECONDS).untilAsserted(() -> {
            RequestSpecification requestSpec = Utils.getDefaultSpec();
            scenarioScopeState.response = RestAssured.given(requestSpec).header("Content-Type", "application/json")
                    .header("Platform-TenantId", scenarioScopeState.tenant).baseUri(operationsAppConfig.operationAppContactPoint)
                    .body(scenarioScopeState.createReportBody).expect().statusCode(expectedStatus).when()
                    .post(operationsAppConfig.reportCreate).andReturn().asString();

            logger.info("Create Report Response: {}", scenarioScopeState.response);

            ObjectMapper responseMapper = new ObjectMapper();
            try {
                JsonNode jsonResponse = responseMapper.readTree(scenarioScopeState.response);
                scenarioScopeState.reportId = jsonResponse.get("id").asText();
            } catch (Exception e) {
                logger.error("Error parsing JSON response to get report ID: {}", e.getMessage());
                fail("Error parsing JSON response to get report ID");
            }
        });
    }

    @Then("the response should contain the created report details")
    public void theResponseShouldContainCreatedReportDetails() {
        ObjectMapper objectMapper = new ObjectMapper();
        try {
            JsonNode jsonResponse = objectMapper.readTree(scenarioScopeState.response);

            MatcherAssert.assertThat(jsonResponse.get("reportName").asText(), Matchers.equalTo("Sample Report"));
            MatcherAssert.assertThat(jsonResponse.get("reportType").asText(), Matchers.equalTo("Financial"));
            MatcherAssert.assertThat(jsonResponse.get("reportSubType").asText(), Matchers.equalTo("Monthly"));
            MatcherAssert.assertThat(jsonResponse.get("description").asText(), Matchers.equalTo("Financial summary for the month of June"));
            MatcherAssert.assertThat(jsonResponse.get("reportSql").asText(),
                    Matchers.equalTo("SELECT * FROM transactions WHERE date >= '2024-06-01' AND date <= '2024-06-30'"));

            if (jsonResponse.has("reportParameters")) {
                JsonNode reportParameters = jsonResponse.get("reportParameters");
                MatcherAssert.assertThat(reportParameters.size(), Matchers.equalTo(2)); // Assuming two parameters were added

                MatcherAssert.assertThat(reportParameters.get(0).get("parameterKey").asText(), Matchers.equalTo("Param1"));
                MatcherAssert.assertThat(reportParameters.get(0).get("parameterValue").asText(), Matchers.equalTo("Value1"));
                MatcherAssert.assertThat(reportParameters.get(1).get("parameterKey").asText(), Matchers.equalTo("Param2"));
                MatcherAssert.assertThat(reportParameters.get(1).get("parameterValue").asText(), Matchers.equalTo("Value2"));
            }
        } catch (Exception e) {
            logger.error("Error parsing JSON response: {}", e.getMessage());
            fail("Error parsing JSON response");
        }
    }

    @And("the response should contain a unique report ID")
    public void theResponseShouldContainUniqueReportID() {
        MatcherAssert.assertThat(scenarioScopeState.response, Matchers.containsString("id"));
    }

    @Given("I have a report ID")
    public void iHaveAReportID() {

    }

    @When("I call the update report API with valid data with expected status of {int}")
    public void iCallUpdateReportAPIWithValidData(int expectedStatus) {
        Assert.assertNotNull(scenarioScopeState.reportId);

        ObjectMapper objectMapper = new ObjectMapper();
        ReportRequestDTO reportDTO = new ReportRequestDTO();

        reportDTO.setReportName("Updated Report Name");
        reportDTO.setReportType("Financial");
        reportDTO.setReportSubType("Monthly");
        reportDTO.setReportCategory("Operational");
        reportDTO.setDescription("Updated description");
        reportDTO.setReportSql("SELECT * FROM transactions WHERE date >= '2024-06-01' AND date <= '2024-06-30'");
        List<ReportParameter> reportParameters = new ArrayList<>();

        ReportParameter parameter1 = new ReportParameter();
        parameter1.setParameterKey("Param1");
        parameter1.setParameterValue("Value1");

        ReportParameter parameter2 = new ReportParameter();
        parameter2.setParameterKey("Param2");
        parameter2.setParameterValue("Value2");

        reportParameters.add(parameter1);
        reportParameters.add(parameter2);

        reportDTO.setReportParameters(reportParameters);

        try {
            String updateReportBody = objectMapper.writeValueAsString(reportDTO);
            scenarioScopeState.updateReportBody = updateReportBody;
        } catch (JsonProcessingException e) {
            logger.error("Unable to convert the DTO : {}", e);
        }
        String updateUrl = "/reports/" + scenarioScopeState.reportId;

        logger.info("Update URL: {}", updateUrl);

        Awaitility.await().atMost(awaitMost, SECONDS).pollDelay(pollDelay, SECONDS).pollInterval(pollInterval, SECONDS).untilAsserted(() -> {
            RequestSpecification requestSpec = Utils.getDefaultSpec();
            scenarioScopeState.response = RestAssured.given(requestSpec).header("Content-Type", "application/json")
                    .header("Platform-TenantId", scenarioScopeState.tenant).baseUri(operationsAppConfig.operationAppContactPoint)
                    .body(scenarioScopeState.updateReportBody).expect().statusCode(expectedStatus).when().put(updateUrl).andReturn()
                    .asString();

            logger.info("Update Report Response: {}", scenarioScopeState.response);
        });
    }

    @Then("the response should contain the updated report details")
    public void theResponseShouldContainUpdatedReportDetails() {
        ObjectMapper objectMapper = new ObjectMapper();
        try {
            JsonNode jsonResponse = objectMapper.readTree(scenarioScopeState.response);

            MatcherAssert.assertThat(jsonResponse.get("id").asText(), Matchers.equalTo(scenarioScopeState.reportId));

            MatcherAssert.assertThat(jsonResponse.get("reportName").asText(), Matchers.equalTo("Updated Report Name"));
            MatcherAssert.assertThat(jsonResponse.get("description").asText(), Matchers.equalTo("Updated description"));

        } catch (Exception e) {
            // Handle any JSON parsing exceptions
            logger.error("Error parsing JSON response: {}", e.getMessage());
            fail("Error parsing JSON response");
        }
    }

    @When("I call the get single report API with expected status of {int}")
    public void iCallGetSingleReportAPI(int expectedStatus) {
        try {
            RequestSpecification requestSpec = Utils.getDefaultSpec();

            String reportId = scenarioScopeState.reportId;
            String getSingleReportEndpoint = "/reports/{reportId}";

            scenarioScopeState.response = RestAssured.given(requestSpec).header("Platform-TenantId", scenarioScopeState.tenant)
                    .baseUri(operationsAppConfig.operationAppContactPoint).pathParam("reportId", reportId) 
                    .expect().statusCode(expectedStatus).when().get(getSingleReportEndpoint).andReturn().asString();

            logger.info("Get Single Report Response: {}", scenarioScopeState.response);
        } catch (Exception e) {
            logger.error("Error calling get single report API: {}", e.getMessage());
            fail("Error calling get single report API");
        }
    }

    @Then("the response should contain the details of the requested report")
    public void theResponseShouldContainRequestedReportDetails() {
        try {
            ObjectMapper objectMapper = new ObjectMapper();
            JsonNode jsonResponse = objectMapper.readTree(scenarioScopeState.response);

            MatcherAssert.assertThat(jsonResponse.get("id").asText(), Matchers.equalTo(scenarioScopeState.reportId));
            MatcherAssert.assertThat(jsonResponse.get("reportName").asText(), Matchers.equalTo("Updated Report Name"));
            MatcherAssert.assertThat(jsonResponse.get("description").asText(), Matchers.equalTo("Updated description"));

        } catch (Exception e) {
            logger.error("Error parsing JSON response: {}", e.getMessage());
            fail("Error parsing JSON response");
        }
    }

    @When("I call the create report API with invalid data")
    public void iCallCreateReportAPIWithInvalidData() {
        ObjectMapper objectMapper = new ObjectMapper();
        ReportRequestDTO reportDTO = new ReportRequestDTO();

        reportDTO.setReportName(null);
        reportDTO.setReportType("InvalidType");
        reportDTO.setReportSubType("InvalidSubType");
        reportDTO.setReportCategory("Operational");
        reportDTO.setDescription("Invalid description");
        reportDTO.setReportSql("INVALID SQL");
        List<ReportParameter> reportParameters = new ArrayList<>();

        ReportParameter parameter1 = new ReportParameter();
        parameter1.setParameterKey("Param1");
        parameter1.setParameterValue("Value1");

        ReportParameter parameter2 = new ReportParameter();
        parameter2.setParameterKey("Param2");
        parameter2.setParameterValue("Value2");

        reportParameters.add(parameter1);
        reportParameters.add(parameter2);

        reportDTO.setReportParameters(reportParameters);

        try {
            scenarioScopeState.createReportBody = objectMapper.writeValueAsString(reportDTO);
        } catch (JsonProcessingException e) {
            logger.error("Unable to convert the DTO : {}", e);
        }
    }

    @Then("I should receive a response with status {int}")
    public void iShouldReceiveResponseWithStatus(int expectedStatus) {
        Awaitility.await().atMost(awaitMost, SECONDS).pollDelay(pollDelay, SECONDS).pollInterval(pollInterval, SECONDS).untilAsserted(() -> {
            try {
                RequestSpecification requestSpec = Utils.getDefaultSpec();

                if (scenarioScopeState.tenant != null) {
                    requestSpec.header("Platform-TenantId", scenarioScopeState.tenant);
                }

                scenarioScopeState.response = RestAssured.given(requestSpec).header("Content-Type", "application/json")
                        .baseUri(operationsAppConfig.operationAppContactPoint).body(scenarioScopeState.createReportBody).expect()
                        .statusCode(HttpStatus.SC_BAD_REQUEST).when().post(operationsAppConfig.reportCreate).andReturn().asString();

                logger.info("Create Report Response: {}", scenarioScopeState.response);
            } catch (Exception e) {
                logger.error("Error calling create report API: {}", e.getMessage());
                fail("Error calling create report API");
            }
        });
    }
}
