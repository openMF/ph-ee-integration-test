package org.mifos.integrationtest.cucumber;

import io.cucumber.core.internal.com.fasterxml.jackson.core.JsonProcessingException;
import io.cucumber.core.internal.com.fasterxml.jackson.databind.ObjectMapper;
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
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;

import static com.google.common.truth.Truth.assertThat;

public class GSMATransferStepDef {

    @Autowired
    GsmaConfig gsmaConfig;
    @Autowired
    GSMATransferDef gsmaTransferDef;
    @Autowired
    ObjectMapper objectMapper;
    Logger logger = LoggerFactory.getLogger(this.getClass());

    @Given("I have Fineract-Platform-TenantId as {string}")
    public void setTenantLoan(String tenant) {
        // Setting tenant
        assertThat(tenant).isNotEmpty();
        gsmaTransferDef.setTenant(tenant);
    }

    @When("I call the create payer client endpoint")
    public void callCreatePayerClientEndpoint() throws JsonProcessingException {
        RequestSpecification requestSpec = Utils.getDefaultSpec();
        requestSpec = gsmaTransferDef.setHeaders(requestSpec);
        gsmaTransferDef.createPayerClientBody = gsmaTransferDef.setBodyPayerClient();
        // Calling savings product endpoint
        gsmaTransferDef.responsePayerClient = RestAssured.given(requestSpec)
                .baseUri(gsmaConfig.payerClientBaseUrl)
                .body(gsmaTransferDef.createPayerClientBody)
                .expect()
                .spec(new ResponseSpecBuilder().expectStatusCode(200).build())
                .when()
                .post(gsmaConfig.payerClientEndpoint)
                .andReturn().asString();

        logger.info("Create Payer Client Response: " + gsmaTransferDef.responsePayerClient);
        assertThat(gsmaTransferDef.responsePayerClient).isNotEmpty();
    }

    @Then("I call the create savings product endpoint")
    public void callCreateSavingsProductEndpoint() throws JsonProcessingException {
        // Setting headers and body
        RequestSpecification requestSpec = Utils.getDefaultSpec();
        requestSpec = gsmaTransferDef.setHeaders(requestSpec);
        gsmaTransferDef.savingsProductBody = gsmaTransferDef.setBodySavingsProduct();
        // Calling savings product endpoint
        gsmaTransferDef.responseSavingsProduct = RestAssured.given(requestSpec)
                .baseUri(gsmaConfig.savingsBaseUrl)
                .body(gsmaTransferDef.savingsProductBody)
                .expect()
                .spec(new ResponseSpecBuilder().expectStatusCode(200).build())
                .when()
                .post(gsmaConfig.savingsProductEndpoint)
                .andReturn().asString();

        logger.info("Savings Product Response: " + gsmaTransferDef.responseSavingsProduct);
        assertThat(gsmaTransferDef.responseSavingsProduct).isNotEmpty();
    }

    @When("I call the create savings account endpoint")
    public void callCreateSavingsAccountEndpoint() throws JsonProcessingException {
        // Setting headers and body
        RequestSpecification requestSpec = Utils.getDefaultSpec();
        requestSpec = gsmaTransferDef.setHeaders(requestSpec);
        gsmaTransferDef.savingsAccountBody = gsmaTransferDef.setBodySavingsAccount();
        // Calling savings product endpoint
        gsmaTransferDef.responseSavingsAccount = RestAssured.given(requestSpec)
                .baseUri(gsmaConfig.savingsBaseUrl)
                .body(gsmaTransferDef.savingsAccountBody)
                .expect()
                .spec(new ResponseSpecBuilder().expectStatusCode(200).build())
                .when()
                .post(gsmaConfig.savingsAccountEndpoint)
                .andReturn().asString();

        logger.info("Savings Account Response: " + gsmaTransferDef.responseSavingsAccount);
        assertThat(gsmaTransferDef.responseSavingsAccount).isNotEmpty();
    }

    @Then("I approve the deposit with command {string}")
    public void callApproveSavingsEndpoint(String command) throws JsonProcessingException {
        // Setting headers and body
        RequestSpecification requestSpec = Utils.getDefaultSpec();
        requestSpec = gsmaTransferDef.setHeaders(requestSpec);
        requestSpec.queryParam("command", command);
        gsmaTransferDef.savingsApproveBody = gsmaTransferDef.setBodySavingsApprove();
        // Setting account ID in path
        SavingsAccountResponse savingsAccountResponse = objectMapper.readValue(
                gsmaTransferDef.responseSavingsAccount, SavingsAccountResponse.class);
        gsmaConfig.savingsApproveEndpoint = gsmaConfig.savingsApproveEndpoint.replaceAll("\\{\\{savingsAccId\\}\\}", savingsAccountResponse.savingsId);
        // Calling create loan account endpoint
        gsmaTransferDef.responseSavingsApprove = RestAssured.given(requestSpec)
                .baseUri(gsmaConfig.savingsBaseUrl)
                .body(gsmaTransferDef.savingsApproveBody)
                .expect()
                .spec(new ResponseSpecBuilder().expectStatusCode(200).build())
                .when()
                .post(gsmaConfig.savingsApproveEndpoint)
                .andReturn().asString();

        logger.info("Savings Approve Response: " + gsmaTransferDef.responseSavingsApprove);
        assertThat(gsmaTransferDef.responseSavingsApprove).isNotEmpty();
    }

    @When("I activate the account with command {string}")
    public void callSavingsActivateEndpoint(String command) throws JsonProcessingException {
        // Setting headers and body
        RequestSpecification requestSpec = Utils.getDefaultSpec();
        requestSpec = gsmaTransferDef.setHeaders(requestSpec);
        requestSpec.queryParam("command", command);
        gsmaTransferDef.savingsActivateBody = gsmaTransferDef.setBodySavingsActivate();
        //Setting account ID
        SavingsAccountResponse savingsAccountResponse = objectMapper.readValue(
                gsmaTransferDef.responseSavingsAccount, SavingsAccountResponse.class);
        gsmaConfig.savingsActivateEndpoint = gsmaConfig.savingsActivateEndpoint.replaceAll("\\{\\{savingsAccId\\}\\}", savingsAccountResponse.savingsId);
        // Calling create loan account endpoint
        gsmaTransferDef.responseSavingsActivate = RestAssured.given(requestSpec)
                .baseUri(gsmaConfig.savingsBaseUrl)
                .body(gsmaTransferDef.savingsActivateBody)
                .expect()
                .spec(new ResponseSpecBuilder().expectStatusCode(200).build())
                .when()
                .post(gsmaConfig.savingsActivateEndpoint)
                .andReturn().asString();

        logger.info("Savings Activate Response: " + gsmaTransferDef.responseSavingsActivate);
        assertThat(gsmaTransferDef.responseSavingsActivate).isNotEmpty();
    }

    @Then("I call the deposit account endpoint with command {string} for amount {int}")
    public void callDepositAccountEndpoint(String command, int amount) throws JsonProcessingException {
        // Setting headers and body
        RequestSpecification requestSpec = Utils.getDefaultSpec();
        requestSpec = gsmaTransferDef.setHeaders(requestSpec);
        requestSpec.queryParam("command", command);
        gsmaTransferDef.savingsDepositAccountBody = gsmaTransferDef.setSavingsDepositAccount(amount);
        //Setting account ID
        SavingsAccountResponse savingsAccountResponse = objectMapper.readValue(
                gsmaTransferDef.responseSavingsAccount, SavingsAccountResponse.class);
        gsmaConfig.savingsDepositAccountEndpoint = gsmaConfig.savingsDepositAccountEndpoint.replaceAll("\\{\\{savingsAccId\\}\\}", savingsAccountResponse.savingsId);

        // Calling create loan account endpoint
        gsmaTransferDef.responseSavingsDepositAccount = RestAssured.given(requestSpec)
                .baseUri(gsmaConfig.savingsBaseUrl)
                .body(gsmaTransferDef.savingsDepositAccountBody)
                .expect()
                .spec(new ResponseSpecBuilder().expectStatusCode(200).build())
                .when()
                .post(gsmaConfig.savingsDepositAccountEndpoint)
                .andReturn().asString();

        logger.info("Savings Deposit Response: " + gsmaTransferDef.responseSavingsDepositAccount);
        assertThat(gsmaTransferDef.responseSavingsDepositAccount).isNotEmpty();
    }

    @And("I call the create loan product endpoint")
    public void callLoanProductEndpoint() throws JsonProcessingException {
        // Setting headers and body
        RequestSpecification requestSpec = Utils.getDefaultSpec();
        requestSpec = gsmaTransferDef.setHeaders(requestSpec);
        gsmaTransferDef.loanProductBody = gsmaTransferDef.setBodyLoanProduct();
        // Calling loan product endpoint
        gsmaTransferDef.responseLoanProduct = RestAssured.given(requestSpec)
                .baseUri(gsmaConfig.loanBaseUrl)
                .body(gsmaTransferDef.loanProductBody)
                .expect()
                .spec(new ResponseSpecBuilder().expectStatusCode(200).build())
                .when()
                .post(gsmaConfig.loanProductEndpoint)
                .andReturn().asString();

        logger.info("Loan Product Response: " + gsmaTransferDef.responseLoanProduct);
        assertThat(gsmaTransferDef.responseLoanProduct).isNotEmpty();
    }

    @When("I call the create loan account")
    public void callCreateLoanAccountEndpoint() throws JsonProcessingException {
        // Setting headers and body
        RequestSpecification requestSpec = Utils.getDefaultSpec();
        requestSpec = gsmaTransferDef.setHeaders(requestSpec);
        gsmaTransferDef.loanAccountBody = gsmaTransferDef.setBodyLoanAccount();
        // Calling create loan account endpoint
        gsmaTransferDef.responseLoanAccount = RestAssured.given(requestSpec)
                .baseUri(gsmaConfig.loanBaseUrl)
                .body(gsmaTransferDef.loanAccountBody)
                .expect()
                .spec(new ResponseSpecBuilder().expectStatusCode(200).build())
                .when()
                .post(gsmaConfig.loanAccountEndpoint)
                .andReturn().asString();

        logger.info("Loan Account Response: " + gsmaTransferDef.responseLoanAccount);
        assertThat(gsmaTransferDef.responseLoanAccount).isNotEmpty();
    }

    @Then("I approve the loan account with command {string} for amount {int}")
    public void callApproveLoanEndpoint(String command, int amount) throws JsonProcessingException {
        // Setting headers and body
        RequestSpecification requestSpec = Utils.getDefaultSpec();
        requestSpec = gsmaTransferDef.setHeaders(requestSpec);
        requestSpec.queryParam("command", command);
        gsmaTransferDef.loanApproveBody = gsmaTransferDef.setBodyLoanApprove(amount);
        //Setting account ID
        LoanAccountResponse loanAccountResponse = objectMapper.readValue(
                gsmaTransferDef.responseLoanAccount, LoanAccountResponse.class);
        String loanAccountId = String.valueOf(loanAccountResponse.getLoanId());
        gsmaConfig.loanApproveEndpoint = gsmaConfig.loanApproveEndpoint.replaceAll("\\{\\{loanAccId\\}\\}", loanAccountId);
        // Calling create loan account endpoint
        gsmaTransferDef.responseLoanApprove = RestAssured.given(requestSpec)
                .baseUri(gsmaConfig.loanBaseUrl)
                .body(gsmaTransferDef.loanApproveBody)
                .expect()
                .spec(new ResponseSpecBuilder().expectStatusCode(200).build())
                .when()
                .post(gsmaConfig.loanApproveEndpoint)
                .andReturn().asString();

        logger.info("Loan Approve Response: " + gsmaTransferDef.responseLoanApprove);
        assertThat(gsmaTransferDef.responseLoanApprove).isNotEmpty();
    }

    @When("I call the loan disburse endpoint with command {string} for amount {int}")
    public void callLoanDisburseEndpoint(String command, int amount) throws JsonProcessingException {
        RequestSpecification requestSpec = Utils.getDefaultSpec();
        requestSpec = gsmaTransferDef.setHeaders(requestSpec);
        requestSpec.queryParam("command", command);
        gsmaTransferDef.loanDisburseBody = gsmaTransferDef.setBodyLoanDisburse(amount);
        //Setting account ID
        LoanAccountResponse loanAccountResponse = objectMapper.readValue(
                gsmaTransferDef.responseLoanAccount, LoanAccountResponse.class);
        String loanAccountId = String.valueOf(loanAccountResponse.getLoanId());
        gsmaConfig.loanDisburseEndpoint = gsmaConfig.loanDisburseEndpoint.replaceAll("\\{\\{loanAccId\\}\\}", loanAccountId);
        // Calling create loan account endpoint
        gsmaTransferDef.responseLoanDisburse = RestAssured.given(requestSpec)
                .baseUri(gsmaConfig.loanBaseUrl)
                .body(gsmaTransferDef.loanDisburseBody)
                .expect()
                .spec(new ResponseSpecBuilder().expectStatusCode(200).build())
                .when()
                .post(gsmaConfig.loanDisburseEndpoint)
                .andReturn().asString();

        logger.info("Loan Approve Response: " + gsmaTransferDef.responseLoanDisburse);
        assertThat(gsmaTransferDef.responseLoanDisburse).isNotEmpty();
    }

    @Then("I call the loan repayment endpoint for amount {int}")
    public void callLoanRepaymentEndpoint(int amount) throws JsonProcessingException {
        // Setting headers and body
        RequestSpecification requestSpec = Utils.getDefaultSpec();
        requestSpec = gsmaTransferDef.setHeaders(requestSpec);
        gsmaTransferDef.loanRepaymentBody = gsmaTransferDef.setBodyLoanRepayment(String.valueOf(amount));
        //Setting account ID
        LoanAccountResponse loanAccountResponse = objectMapper.readValue(
                gsmaTransferDef.responseLoanAccount, LoanAccountResponse.class);
        String loanId = Integer.toString(loanAccountResponse.getLoanId());
        String loanAccountId = String.format("%0" + (9 - loanId.length()) + "d%s", 0, loanId);
        gsmaConfig.loanRepaymentEndpoint = gsmaConfig.loanRepaymentEndpoint.replaceAll("\\{\\{loanAccId\\}\\}", loanAccountId);
        // Calling create loan account endpoint
        gsmaTransferDef.responseLoanRepayment = RestAssured.given(requestSpec)
                .baseUri(gsmaConfig.loanBaseUrl)
                .body(gsmaTransferDef.loanRepaymentBody)
                .expect()
                .spec(new ResponseSpecBuilder().expectStatusCode(200).build())
                .when()
                .post(gsmaConfig.loanRepaymentEndpoint)
                .andReturn().asString();

        logger.info("Loan Repayment Response: " + gsmaTransferDef.responseLoanRepayment);
        assertThat(gsmaTransferDef.responseLoanRepayment).isNotEmpty();
    }

    @Given("I have accountId {string}")
    public void setGsmaTransferBody(String accountId) {
        gsmaTransferDef.setAccountId(accountId);
        assertThat(gsmaTransferDef.accountId).isNotEmpty();
    }

    @When("I have amsName as {string} and acccountHoldingInstitutionId as {string} and amount as {int}")
    public void setHeaders(String amsName, String acccountHoldingInstitutionId, int amount) {
        gsmaTransferDef.setHeadersMifos(amsName, acccountHoldingInstitutionId, amount);
        assertThat(gsmaTransferDef.amsName).isNotEmpty();
        assertThat(gsmaTransferDef.acccountHoldingInstitutionId).isNotEmpty();
        assertThat(gsmaTransferDef.amount).isNotNull();
    }

    @Then("I call the channel connector API with expected status of {int}")
    public void sendRequestToGSMAEndpoint(int status) throws JsonProcessingException {
        RequestSpecification requestSpec = Utils.getDefaultSpec();
        requestSpec.header("amsName", gsmaTransferDef.amsName);
        requestSpec.header("accountHoldingInstitutionId", gsmaTransferDef.acccountHoldingInstitutionId);

        gsmaTransferDef.gsmaTransferBody = gsmaTransferDef.setGsmaTransactionBody();

        gsmaTransferDef.gsmaTransactionResponse = RestAssured.given(requestSpec)
                .baseUri(gsmaConfig.channelConnectorBaseUrl)
                .body(gsmaTransferDef.gsmaTransferBody)
                .expect()
                .spec(new ResponseSpecBuilder().expectStatusCode(status).build())
                .when()
                .post(gsmaConfig.gsmaEndpoint)
                .andReturn().asString();

        logger.info("GSMA Transaction Response: " + gsmaTransferDef.gsmaTransactionResponse);
        assertThat(gsmaTransferDef.gsmaTransactionResponse).isNotEmpty();
    }

}
