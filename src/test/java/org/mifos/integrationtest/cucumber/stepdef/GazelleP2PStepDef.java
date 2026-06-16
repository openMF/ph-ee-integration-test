package org.mifos.integrationtest.cucumber.stepdef;

import static com.google.common.truth.Truth.assertThat;

import io.cucumber.java.en.And;
import io.cucumber.java.en.Given;
import io.cucumber.java.en.Then;
import io.cucumber.java.en.When;
import io.restassured.RestAssured;
import io.restassured.response.Response;
import io.restassured.specification.RequestSpecification;
import org.mifos.integrationtest.config.GazelleConfig;
import org.springframework.beans.factory.annotation.Autowired;

public class GazelleP2PStepDef extends BaseStepDef {

    @Autowired
    GazelleConfig gazelleConfig;

    private String payerMsisdn;
    private String payeeMsisdn;
    private Response transferResponse;

    // Step 1: Get payer MSISDN from Mifos API
    @Given("I have payer in tenant {string}")
    public void getPayerFromTenant(String tenant) {
        RequestSpecification requestSpec = RestAssured.given()
                .header("Authorization", gazelleConfig.mifosAuth)
                .header("Fineract-Platform-TenantId", tenant)
                .header("Content-Type", "application/json");

        Response response = requestSpec
                .baseUri(gazelleConfig.mifosApiUrl)
                .get("/clients?limit=1&orderBy=id&sortOrder=ASC");

        assertThat(response.statusCode()).isEqualTo(200);

        payerMsisdn = response.jsonPath().getString("pageItems[0].mobileNo");
        assertThat(payerMsisdn).isNotNull();
        logger.info("Payer MSISDN: {}", payerMsisdn);
    }

    // Step 2: Get payee MSISDN from Mifos API
    @And("I have payee in tenant {string}")
    public void getPayeeFromTenant(String tenant) {
        RequestSpecification requestSpec = RestAssured.given()
                .header("Authorization", gazelleConfig.mifosAuth)
                .header("Fineract-Platform-TenantId", tenant)
                .header("Content-Type", "application/json");

        Response response = requestSpec
                .baseUri(gazelleConfig.mifosApiUrl)
                .get("/clients?limit=1&orderBy=id&sortOrder=ASC");

        assertThat(response.statusCode()).isEqualTo(200);

        payeeMsisdn = response.jsonPath().getString("pageItems[0].mobileNo");
        assertThat(payeeMsisdn).isNotNull();
        logger.info("Payee MSISDN: {}", payeeMsisdn);
    }

    // Step 3: Make the transfer via PHEE channel connector
    @When("I make a transfer of amount {string} in {string}")
    public void makeTransfer(String amount, String currency) {
        String body = "{"
                + "\"payer\": {\"partyIdInfo\": {\"partyIdType\": \"MSISDN\", \"partyIdentifier\": \"" + payerMsisdn + "\"}},"
                + "\"payee\": {\"partyIdInfo\": {\"partyIdType\": \"MSISDN\", \"partyIdentifier\": \"" + payeeMsisdn + "\"}},"
                + "\"amount\": {\"amount\": " + amount + ", \"currency\": \"" + currency + "\"}"
                + "}";

        transferResponse = RestAssured.given()
                .baseUri(gazelleConfig.channelConnectorUrl)
                .header("Platform-TenantId", gazelleConfig.payerTenant)
                .header("X-PayeeDFSP-ID", gazelleConfig.payeeTenant)
                .header("Content-Type", "application/json")
                .body(body)
                .post(gazelleConfig.transferEndpoint);

        logger.info("Transfer response status: {}", transferResponse.statusCode());
        logger.info("Transfer response body: {}", transferResponse.asString());
    }

    // Step 4: Verify HTTP 200
    @Then("the transfer response status should be 200")
    public void verifyTransferStatus() {
        assertThat(transferResponse.statusCode()).isEqualTo(200);
    }

    // Step 5: Verify transaction ID exists in response
    @And("the response should contain a valid transaction ID")
    public void verifyTransactionId() {
        String transactionId = transferResponse.jsonPath().getString("transactionId");
        assertThat(transactionId).isNotNull();
        assertThat(transactionId).isNotEmpty();
        logger.info("Transaction ID: {}", transactionId);
    }

}
