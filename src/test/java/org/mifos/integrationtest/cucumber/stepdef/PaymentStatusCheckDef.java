package org.mifos.integrationtest.cucumber.stepdef;

import static com.google.common.truth.Truth.assertThat;

import io.cucumber.java.en.And;
import io.cucumber.java.en.Given;
import io.cucumber.java.en.When;
import io.restassured.RestAssured;
import io.restassured.builder.ResponseSpecBuilder;
import io.restassured.specification.RequestSpecification;
import org.mifos.integrationtest.common.Utils;
import org.mifos.integrationtest.common.dto.PaymentStatusCheckReqDto;
import org.mifos.integrationtest.config.PaymentStatusCheckConfig;
import org.springframework.beans.factory.annotation.Autowired;

public class PaymentStatusCheckDef extends BaseStepDef {

    @Autowired
    PaymentStatusCheckConfig paymentStatusCheckConfig;

    PaymentStatusCheckReqDto paymentStatusCheckReqDto = new PaymentStatusCheckReqDto();

    @And("I extracted clientCorrelationId from response")
    public void iExtractedClientCorrelationIdFromResponse() {
        assertThat(BaseStepDef.clientCorrelationId).isNotNull();
        paymentStatusCheckConfig.requestIds.add(BaseStepDef.clientCorrelationId);
    }

    @When("I should have clean request id list")
    public void iShouldHaveCleanRequestIdList() {
        paymentStatusCheckConfig.requestIds.clear();
        assertThat(paymentStatusCheckConfig.requestIds).isEmpty();
    }

    @Given("I can create a mock request body from above clientCorrelationIds")
    public void iCanCreateAMockRequestBodyFromAboveClientCorrelationIds() {
        assertThat(paymentStatusCheckConfig.requestIds).isNotEmpty();
        paymentStatusCheckReqDto.setRequestIds(paymentStatusCheckConfig.requestIds);
        StringBuilder jsonBuilder = new StringBuilder();
        jsonBuilder.append("{");
        jsonBuilder.append("\"requestIds\": [").append("\"" + "\\" + "\"" + paymentStatusCheckConfig.requestIds.get(0) + "\\" + "\"" + "\"")
                .append(",").append("\"" + "\\" + "\"" + paymentStatusCheckConfig.requestIds.get(1) + "\\" + "\"" + "\"").append("],");
        jsonBuilder.append("\"payeePartyIds\": []");
        jsonBuilder.append("}");
        String jsonString = jsonBuilder.toString();
        BaseStepDef.paymentStatusCheckReqDto = jsonString;
        assertThat(BaseStepDef.paymentStatusCheckReqDto).isNotNull();
        logger.info("Payment Status Check Request Body: " + BaseStepDef.paymentStatusCheckReqDto);

    }

    @When("I call the payment status check endpoint with expected status {int}")
    public void iCallThePaymentStatusCheckEndpointWithExpectedStatus(int expectedStatus) {
        RequestSpecification requestSpec = Utils.getDefaultSpec(BaseStepDef.tenant);
        if (authEnabled) {
            requestSpec.header("Authorization", "Bearer " + BaseStepDef.accessToken);
        }
        BaseStepDef.response = RestAssured.given(requestSpec).baseUri(operationsAppConfig.operationAppContactPoint)
                .body(BaseStepDef.paymentStatusCheckReqDto).expect()
                .spec(new ResponseSpecBuilder().expectStatusCode(expectedStatus).build()).when().post(operationsAppConfig.transfersEndpoint)
                .andReturn().asString();

        logger.info("Batch Details Response: " + BaseStepDef.response);
    }

    @And("I extracted clientCorrelationId from the demo csv file {string}")
    public void iExtractedClientCorrelationIdFromTheDemoCsvFile(String filename) {
        assertThat(filename).isNotNull();
        paymentStatusCheckConfig.requestIds.clear();
        paymentStatusCheckConfig.requestIds = Utils.extractClientCorrelationIdFromCsv(filename, paymentStatusCheckConfig.requestIds);
        assertThat(paymentStatusCheckConfig.requestIds).isNotEmpty();
    }
}
