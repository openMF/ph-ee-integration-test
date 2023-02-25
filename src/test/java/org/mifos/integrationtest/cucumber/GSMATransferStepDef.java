package org.mifos.integrationtest.cucumber;

import io.cucumber.core.internal.com.fasterxml.jackson.core.JsonProcessingException;
import io.cucumber.java.en.And;
import io.cucumber.java.en.Given;
import io.cucumber.java.en.Then;
import io.cucumber.java.en.When;
import io.restassured.RestAssured;
import io.restassured.builder.ResponseSpecBuilder;
import io.restassured.specification.RequestSpecification;
import org.mifos.integrationtest.common.Utils;
import org.mifos.integrationtest.common.dto.loan.LoanAccountResponse;
import org.mifos.integrationtest.common.dto.savings.SavingsAccountResponse;
import org.mifos.integrationtest.config.GsmaConfig;

import static com.google.common.truth.Truth.assertThat;

public class GSMATransferStepDef extends GSMATransferDef {

    @Given("I have Fineract-Platform-TenantId as {string}")
    public void setTenantLoan(String tenant) {
        // Setting tenant
        assertThat(tenant).isNotEmpty();
        GSMATransferDef.setTenant(tenant);
    }

    @When("I call the create payer client endpoint")
    public void callCreatePayerClientEndpoint() {
        RequestSpecification requestSpec = Utils.getDefaultSpec();
        requestSpec = GSMATransferDef.setHeadersClient(requestSpec);
        GSMATransferDef.createPayerClientBody = GSMATransferDef.setBodyPayerClient();
        // Calling savings product endpoint
        GSMATransferDef.responsePayerClient = RestAssured.given(requestSpec)
                .baseUri(GsmaConfig.payerClientBaseUrl)
                .body(GSMATransferDef.payerClientBody)
                .expect()
                .spec(new ResponseSpecBuilder().expectStatusCode(200).build())
                .when()
                .post(GsmaConfig.payerClientEndpoint)
                .andReturn().asString();

        logger.info("Create Payer Client Response: " + GSMATransferDef.responsePayerClient);
        assertThat(GSMATransferDef.responsePayerClient).isNotEmpty();
    }

    @Then("I call the create savings product endpoint")
    public void callCreateSavingsProductEndpoint() {
        // Setting headers and body
        RequestSpecification requestSpec = Utils.getDefaultSpec();
        requestSpec = GSMATransferDef.setHeadersSavings(requestSpec);
        GSMATransferDef.savingsProductBody = GSMATransferDef.setBodySavingsProduct();
        // Calling savings product endpoint
        GSMATransferDef.responseSavingsProduct = RestAssured.given(requestSpec)
                .baseUri(GsmaConfig.savingsBaseUrl)
                .body(GSMATransferDef.savingsProductBody)
                .expect()
                .spec(new ResponseSpecBuilder().expectStatusCode(200).build())
                .when()
                .post(GsmaConfig.savingsProductEndpoint)
                .andReturn().asString();

        logger.info("Savings Product Response: " + GSMATransferDef.responseSavingsProduct);
        assertThat(GSMATransferDef.responseSavingsProduct).isNotEmpty();
    }

    @When("I call the create savings account endpoint")
    public void callCreateSavingsAccountEndpoint() throws JsonProcessingException {
        // Setting headers and body
        RequestSpecification requestSpec = Utils.getDefaultSpec();
        requestSpec = GSMATransferDef.setHeadersSavings(requestSpec);
        GSMATransferDef.savingsAccountBody = GSMATransferDef.setBodySavingsAccount();
        // Calling savings product endpoint
        GSMATransferDef.responseSavingsAccount = RestAssured.given(requestSpec)
                .baseUri(GsmaConfig.savingsBaseUrl)
                .body(GSMATransferDef.savingsAccountBody)
                .expect()
                .spec(new ResponseSpecBuilder().expectStatusCode(200).build())
                .when()
                .post(GsmaConfig.savingsAccountEndpoint)
                .andReturn().asString();

        logger.info("Savings Account Response: " + GSMATransferDef.responseSavingsAccount);
        assertThat(GSMATransferDef.responseSavingsAccount).isNotEmpty();
    }

    @Then("I approve the deposit with command {string}")
    public void callApproveSavingsEndpoint(String command) throws JsonProcessingException {
        // Setting headers and body
        RequestSpecification requestSpec = Utils.getDefaultSpec();
        requestSpec = GSMATransferDef.setHeadersSavings(requestSpec);
        requestSpec.queryParam("command", command);
        GSMATransferDef.savingsApproveBody = GSMATransferDef.setBodySavingsApprove();
        // Setting account ID in path
        SavingsAccountResponse savingsAccountResponse = objectMapper.readValue(
                responseSavingsAccount, SavingsAccountResponse.class);
        GsmaConfig.savingsApproveEndpoint.replace("{savingsAccId}", savingsAccountResponse.savingsId);
        // Calling create loan account endpoint
        GSMATransferDef.responseSavingsApprove = RestAssured.given(requestSpec)
                .baseUri(GsmaConfig.savingsBaseUrl)
                .body(GSMATransferDef.savingsApproveBody)
                .expect()
                .spec(new ResponseSpecBuilder().expectStatusCode(200).build())
                .when()
                .post(GsmaConfig.savingsApproveEndpoint)
                .andReturn().asString();

        logger.info("Savings Approve Response: " + GSMATransferDef.responseSavingsApprove);
        assertThat(GSMATransferDef.responseSavingsApprove).isNotEmpty();
    }

    @When("I activate the account with command {string}")
    public void callSavingsActivateEndpoint(String command) throws JsonProcessingException {
        // Setting headers and body
        RequestSpecification requestSpec = Utils.getDefaultSpec();
        requestSpec = GSMATransferDef.setHeadersSavings(requestSpec);
        requestSpec.queryParam("command", command);
        GSMATransferDef.savingsActivateBody = GSMATransferDef.setBodySavingsActivate();
        //Setting account ID
        SavingsAccountResponse savingsAccountResponse = objectMapper.readValue(
                responseSavingsAccount, SavingsAccountResponse.class);
        GsmaConfig.savingsActivateEndpoint.replace("{savingsAccId}", savingsAccountResponse.savingsId);
        // Calling create loan account endpoint
        GSMATransferDef.responseSavingsActivate = RestAssured.given(requestSpec)
                .baseUri(GsmaConfig.savingsBaseUrl)
                .body(GSMATransferDef.savingsActivateBody)
                .expect()
                .spec(new ResponseSpecBuilder().expectStatusCode(200).build())
                .when()
                .post(GsmaConfig.savingsActivateEndpoint)
                .andReturn().asString();

        logger.info("Savings Activate Response: " + GSMATransferDef.responseSavingsActivate);
        assertThat(GSMATransferDef.responseSavingsActivate).isNotEmpty();
    }

    @Then("I call the deposit account endpoint for amount {int}")
    public void callDepositAccountEndpoint(int amount) throws JsonProcessingException {
        // Setting headers and body
        RequestSpecification requestSpec = Utils.getDefaultSpec();
        requestSpec = GSMATransferDef.setHeadersSavings(requestSpec);
        GSMATransferDef.savingsDepositAccountBody = GSMATransferDef.setSavingsDepositAccount(amount);
        //Setting account ID
        SavingsAccountResponse savingsAccountResponse = objectMapper.readValue(
                responseSavingsAccount, SavingsAccountResponse.class);
        GsmaConfig.savingsDepositAccountEndpoint.replace("{savingsAccId}", savingsAccountResponse.savingsId);
        // Calling create loan account endpoint
        GSMATransferDef.responseSavingsDepositAccount = RestAssured.given(requestSpec)
                .baseUri(GsmaConfig.savingsBaseUrl)
                .body(GSMATransferDef.savingsDepositAccountBody)
                .expect()
                .spec(new ResponseSpecBuilder().expectStatusCode(200).build())
                .when()
                .post(GsmaConfig.savingsDepositAccountEndpoint)
                .andReturn().asString();

        logger.info("Savings Deposit Response: " + GSMATransferDef.responseSavingsDepositAccount);
        assertThat(GSMATransferDef.responseSavingsDepositAccount).isNotEmpty();
    }

    @And("I call the create loan product endpoint")
    public void callLoanProductEndpoint() {
        // Setting headers and body
        RequestSpecification requestSpec = Utils.getDefaultSpec();
        requestSpec = GSMATransferDef.setHeadersLoan(requestSpec);
        GSMATransferDef.loanProductBody = GSMATransferDef.setBodyLoanProduct();
        // Calling loan product endpoint
        GSMATransferDef.responseLoanProduct = RestAssured.given(requestSpec)
                .baseUri(GsmaConfig.loanBaseUrl)
                .body(GSMATransferDef.loanProductBody)
                .expect()
                .spec(new ResponseSpecBuilder().expectStatusCode(200).build())
                .when()
                .post(GsmaConfig.loanProductEndpoint)
                .andReturn().asString();

        logger.info("Loan Product Response: " + GSMATransferDef.responseLoanProduct);
        assertThat(GSMATransferDef.responseLoanProduct).isNotEmpty();
    }

    @When("I call the create loan account")
    public void callCreateLoanAccountEndpoint() throws JsonProcessingException {
        // Setting headers and body
        RequestSpecification requestSpec = Utils.getDefaultSpec();
        requestSpec = GSMATransferDef.setHeadersLoan(requestSpec);
        GSMATransferDef.loanAccountBody = GSMATransferDef.setBodyLoanAccount();
        // Calling create loan account endpoint
        GSMATransferDef.responseLoanAccount = RestAssured.given(requestSpec)
                .baseUri(GsmaConfig.loanBaseUrl)
                .body(GSMATransferDef.loanAccountBody)
                .expect()
                .spec(new ResponseSpecBuilder().expectStatusCode(200).build())
                .when()
                .post(GsmaConfig.loanAccountEndpoint)
                .andReturn().asString();

        logger.info("Loan Account Response: " + GSMATransferDef.responseLoanAccount);
        assertThat(GSMATransferDef.responseLoanAccount).isNotEmpty();
    }

    @Then("I approve the loan account with command {string} for amount {int}")
    public void callApproveLoanEndpoint(String command, int amount) throws JsonProcessingException {
        // Setting headers and body
        RequestSpecification requestSpec = Utils.getDefaultSpec();
        requestSpec = GSMATransferDef.setHeadersLoan(requestSpec);
        requestSpec.queryParam("command", command);
        GSMATransferDef.loanApproveBody = GSMATransferDef.setBodyLoanApprove(amount);
        //Setting account ID
        LoanAccountResponse loanAccountResponse = objectMapper.readValue(
                responseLoanAccount, LoanAccountResponse.class);
        String loanAccountId = String.valueOf(loanAccountResponse.getLoanId());
        GsmaConfig.loanApproveEndpoint.replace("{loanAccId}", loanAccountId);
        // Calling create loan account endpoint
        GSMATransferDef.responseLoanApprove = RestAssured.given(requestSpec)
                .baseUri(GsmaConfig.loanBaseUrl)
                .body(GSMATransferDef.loanApproveBody)
                .expect()
                .spec(new ResponseSpecBuilder().expectStatusCode(200).build())
                .when()
                .post(GsmaConfig.loanApproveEndpoint)
                .andReturn().asString();

        logger.info("Loan Approve Response: " + GSMATransferDef.responseLoanApprove);
        assertThat(GSMATransferDef.responseLoanApprove).isNotEmpty();
    }

    @When("I call the loan disburse endpoint with command {string} for amount {int}")
    public void callLoanDisburseEndpoint(String command, int amount) throws JsonProcessingException {
        RequestSpecification requestSpec = Utils.getDefaultSpec();
        requestSpec = GSMATransferDef.setHeadersLoan(requestSpec);
        requestSpec.queryParam("command", command);
        GSMATransferDef.loanDisburseBody = GSMATransferDef.setBodyLoanDisburse(amount);
        //Setting account ID
        LoanAccountResponse loanAccountResponse = objectMapper.readValue(
                responseLoanAccount, LoanAccountResponse.class);
        String loanAccountId = String.valueOf(loanAccountResponse.getLoanId());
        GsmaConfig.loanDisburseEndpoint.replace("{loanAccId}", loanAccountId);
        // Calling create loan account endpoint
        GSMATransferDef.responseLoanDisburse = RestAssured.given(requestSpec)
                .baseUri(GsmaConfig.loanBaseUrl)
                .body(GSMATransferDef.loanDisburseBody)
                .expect()
                .spec(new ResponseSpecBuilder().expectStatusCode(200).build())
                .when()
                .post(GsmaConfig.loanDisburseEndpoint)
                .andReturn().asString();

        logger.info("Loan Approve Response: " + GSMATransferDef.responseLoanDisburse);
        assertThat(GSMATransferDef.responseLoanDisburse).isNotEmpty();
    }

    @Then("I call the loan repayment endpoint for amount {int}")
    public void callLoanRepaymentEndpoint(int amount) throws JsonProcessingException {
        // Setting headers and body
        RequestSpecification requestSpec = Utils.getDefaultSpec();
        requestSpec = GSMATransferDef.setHeadersLoan(requestSpec);
        GSMATransferDef.loanRepaymentBody = (String) GSMATransferDef.setBodyLoanRepayment(String.valueOf(amount));
        //Setting account ID
        LoanAccountResponse loanAccountResponse = objectMapper.readValue(
                responseLoanAccount, LoanAccountResponse.class);
        String loanAccountId = String.valueOf(new StringBuilder().append("00000000").append(loanAccountResponse.getLoanId()));
        GsmaConfig.loanDisburseEndpoint.replace("{loanAccId}", loanAccountId);
        // Calling create loan account endpoint
        GSMATransferDef.responseLoanRepayment = RestAssured.given(requestSpec)
                .baseUri(GsmaConfig.loanBaseUrl)
                .body(GSMATransferDef.loanRepaymentBody)
                .expect()
                .spec(new ResponseSpecBuilder().expectStatusCode(200).build())
                .when()
                .post(GsmaConfig.loanRepaymentEndpoint)
                .andReturn().asString();

        logger.info("Loan Repayment Response: " + GSMATransferDef.responseLoanRepayment);
        assertThat(GSMATransferDef.responseLoanRepayment).isNotEmpty();
    }

    @Given("I have accountId {string}")
    public void setGsmaTransferBody(String accountId) {
        GSMATransferDef.setAccountId(accountId);
        assertThat(GSMATransferDef.accountId).isNotEmpty();
    }

    @When("I have amsName as {string} and acccountHoldingInstitutionId as {string} and amount as {int}")
    public void setHeaders(String amsName, String acccountHoldingInstitutionId) {
        GSMATransferDef.setHeadersMifos(amsName, acccountHoldingInstitutionId, amount);
        assertThat(GSMATransferDef.amsName).isNotEmpty();
        assertThat(GSMATransferDef.acccountHoldingInstitutionId).isNotEmpty();
        assertThat(GSMATransferDef.amount).isNotNull();
    }

    @Then("I call the channel connector API with expected status of {int}")
    public void sendRequestToGSMAEndpoint(int status) {
        RequestSpecification requestSpec = Utils.getDefaultSpec();
        requestSpec.header("amsName", GSMATransferDef.amsName);
        requestSpec.header("accountHoldingInstitutionId", GSMATransferDef.acccountHoldingInstitutionId);

        GSMATransferDef.gsmaTransfer = GSMATransferDef.setGsmaTransactionBody();

        GSMATransferDef.gsmaTransactionResponse = RestAssured.given(requestSpec)
                .baseUri(GsmaConfig.channelConnectorBaseUrl)
                .body(GSMATransferDef.gsmaTransfer)
                .expect()
                .spec(new ResponseSpecBuilder().expectStatusCode(status).build())
                .when()
                .post(GsmaConfig.gsmaEndpoint)
                .andReturn().asString();

        logger.info("GSMA Transaction Response: " + GSMATransferDef.gsmaTransactionResponse);
        assertThat(GSMATransferDef.gsmaTransactionResponse).isNotEmpty();
    }

}
