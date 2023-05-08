package org.mifos.integrationtest.cucumber.stepdef;

import io.cucumber.core.internal.com.fasterxml.jackson.databind.ObjectMapper;
import io.cucumber.java.en.And;
import io.cucumber.java.en.Given;
import io.cucumber.java.en.Then;
import io.cucumber.java.en.When;
import io.restassured.RestAssured;
import io.restassured.builder.ResponseSpecBuilder;
import io.restassured.specification.RequestSpecification;
import org.mifos.integrationtest.common.Utils;
import org.mifos.integrationtest.common.dto.paybill.PayBillRequestDTO;
import org.mifos.integrationtest.config.PaybillConfig;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;

import static com.google.common.truth.Truth.assertThat;
import static org.mifos.integrationtest.common.Utils.CONTENT_TYPE;
import static org.mifos.integrationtest.common.Utils.CONTENT_TYPE_VALUE;

public class PaybillApiStepDef {
    @Autowired
    PaybillStepDef paybillStepDef;
    @Autowired
    PaybillConfig paybillConfig;
    Logger logger = LoggerFactory.getLogger(this.getClass());
    @Autowired
    ObjectMapper objectMapper;

    @Given("The mpesaValidateUrl is not null")
    public void nonEmptympesaValidateUrl() {
        assertThat(paybillConfig.mpesaValidateUrl).isNotNull();
    }

    @Given("The mpesaSettlementUrl is not null")
    public void nonEmptympesaSettlementUrl() {
        assertThat(paybillConfig.mpesaSettlementUrl).isNotNull();
    }

    @Given("I have businessShortCode {string} with transactionId {string}")
    public void setShortCodeAndTxnId(String shortCode, String transactionId) {
        paybillStepDef.businessShortCode = shortCode;
        paybillStepDef.transactionId = transactionId;
        assertThat(paybillStepDef.businessShortCode).isNotEmpty();
        assertThat(paybillStepDef.transactionId).isNotEmpty();
    }

    @And("I have MSISDN {string} and BillRefNo {string} for amount {string}")
    public void setMsisdnBillRefAmount(String msisdn, String billRefNo, String amount) {
        paybillStepDef.msisdn = msisdn;
        paybillStepDef.billRefNo = billRefNo;
        paybillStepDef.amount = amount;
        assertThat(paybillStepDef.msisdn).isNotEmpty();
        assertThat(paybillStepDef.billRefNo).isNotEmpty();
        assertThat(paybillStepDef.amount).isNotEmpty();
    }

    @When("I call the mpesa-connector validate webhook api with expected status code of {int}")
    public void callMpesaConnector(int expectedStatus) {
        RequestSpecification requestSpecification = Utils.getDefaultSpec();
        // Paybill Request DTO for Validation
        PayBillRequestDTO payBillRequestDTO = paybillStepDef.setPaybillRequestDTO();
        requestSpecification.body(payBillRequestDTO);
        requestSpecification.header(CONTENT_TYPE, CONTENT_TYPE_VALUE);
        paybillStepDef.response = RestAssured.given(requestSpecification)
                .contentType(CONTENT_TYPE_VALUE)
                .expect()
                .spec(new ResponseSpecBuilder().expectStatusCode(expectedStatus).build())
                .when()
                .post(paybillConfig.mpesaValidateUrl)
                .andReturn().asString();
        logger.info("Mpesa Validation Response : {}", paybillStepDef.response);
    }

    @Then("I call the confirmation webhook API with expected status of {int}")
    public void callConfirmationWebhook(int expectedStatus) {
        RequestSpecification requestSpecification = Utils.getDefaultSpec();
        // Paybill Request DTO for Settlement
        PayBillRequestDTO payBillRequestDTO = paybillStepDef.setPaybillRequestDTO();
        requestSpecification.body(payBillRequestDTO);
        requestSpecification.header(CONTENT_TYPE, CONTENT_TYPE_VALUE);

        paybillStepDef.response = RestAssured.given(requestSpecification)
                .contentType(CONTENT_TYPE_VALUE)
                .expect()
                .spec(new ResponseSpecBuilder().expectStatusCode(expectedStatus).build())
                .when()
                .post(paybillConfig.mpesaSettlementUrl)
                .andReturn().asString();
        logger.info("Paybill Settlement Response: {}", paybillStepDef.response);
    }
}
