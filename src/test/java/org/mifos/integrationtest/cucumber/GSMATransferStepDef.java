package org.mifos.integrationtest.cucumber;

import io.cucumber.java.en.And;
import io.cucumber.java.en.Given;
import io.cucumber.java.en.Then;
import io.cucumber.java.en.When;
import io.restassured.RestAssured;
import io.restassured.builder.ResponseSpecBuilder;
import io.restassured.specification.RequestSpecification;
import org.mifos.connector.common.gsma.dto.CustomData;
import org.mifos.connector.common.gsma.dto.GsmaTransfer;
import org.mifos.integrationtest.common.Utils;
import org.mifos.integrationtest.config.GsmaConfig;

import static com.google.common.truth.Truth.assertThat;

public class GSMATransferStepDef extends GSMATransferDef {
    @Given("I have Fineract-Platform-TenantId as {string}")
    public void setTenantLoan(String tenant){
        // Setting tenant
        assertThat(tenant).isNotEmpty();
        GSMATransferDef.setTenant(tenant);
    }
    @When("I call the create payer client endpoint")
    public void callCreatePayerClientEndpoint(){
        RequestSpecification requestSpec = Utils.getDefaultSpec();
        requestSpec=GSMATransferDef.setHeadersClient(requestSpec);
        GSMATransferDef.createPayerClientBody= GSMATransferDef.setBodyPayerClient();
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
    public void callCreateSavingsProductEndpoint(){
        // Setting headers and body
        RequestSpecification requestSpec = Utils.getDefaultSpec();
        requestSpec=GSMATransferDef.setHeadersSavings(requestSpec);
        GSMATransferDef.savingsProductBody= GSMATransferDef.setBodySavingsProduct();
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
    }
    @When("I call the create savings account endpoint")
    public void callCreateSavingsAccountEndpoint(){
        // Setting headers and body
        RequestSpecification requestSpec = Utils.getDefaultSpec();
        requestSpec=GSMATransferDef.setHeadersSavings(requestSpec);
        GSMATransferDef.savingsAccountBody= GSMATransferDef.setBodySavingsAccount();
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
    }

    @Then("I approve the deposit with command {string}")
    public void callApproveSavingsEndpoint(String command){
        // Setting headers and body
        RequestSpecification requestSpec = Utils.getDefaultSpec();
        requestSpec=GSMATransferDef.setHeadersSavings(requestSpec);
        requestSpec.queryParam("command",command);
        GSMATransferDef.savingsApproveBody=GSMATransferDef.setBodySavingsApprove();
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
    }
    @When("I activate the account with command {string}")
    public void callSavingsActivateEndpoint(String command){
        // Setting headers and body
        RequestSpecification requestSpec = Utils.getDefaultSpec();
        requestSpec=GSMATransferDef.setHeadersSavings(requestSpec);
        requestSpec.queryParam("command",command);
        GSMATransferDef.savingsActivateBody=GSMATransferDef.setBodySavingsActivate();
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
    }
    @Then("I call the deposit account endpoint")
    public void callDepositAccountEndpoint(){
        // Setting headers and body
        RequestSpecification requestSpec = Utils.getDefaultSpec();
        requestSpec=GSMATransferDef.setHeadersSavings(requestSpec);
        GSMATransferDef.savingsDepositAccountBody=GSMATransferDef.setSavingsDepositAccount();
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
    }
    @When("I call the create loan product endpoint")
    public void callLoanProductEndpoint(){
        // Setting headers and body
        RequestSpecification requestSpec = Utils.getDefaultSpec();
        requestSpec=GSMATransferDef.setHeadersLoan(requestSpec);
        GSMATransferDef.loanProductBody=GSMATransferDef.setBodyLoanProduct();
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
    @Then("I call the create loan account")
    public void callCreateLoanAccountEndpoint(){
        // Setting headers and body
        RequestSpecification requestSpec = Utils.getDefaultSpec();
        requestSpec=GSMATransferDef.setHeadersLoan(requestSpec);
        GSMATransferDef.loanAccountBody=GSMATransferDef.setBodyLoanAccount();
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
    }
    @When("I approve the loan account with command {string}")
    public void callApproveLoanEndpoint(String command){
        // Setting headers and body
        RequestSpecification requestSpec = Utils.getDefaultSpec();
        requestSpec=GSMATransferDef.setHeadersLoan(requestSpec);
        requestSpec.queryParam("command",command);
        GSMATransferDef.loanApproveBody=GSMATransferDef.setBodyLoanApprove();
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
    }
    @Then("I call the loan repayment endpoint")
    public void callLoanRepaymentEndpoint(){
        // Setting headers and body
        RequestSpecification requestSpec = Utils.getDefaultSpec();
        requestSpec=GSMATransferDef.setHeadersLoan(requestSpec);
        GSMATransferDef.loanRepaymentBody= GSMATransferDef.setBodyLoanRepayment().toString();
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

    }

    @Given("I have a GSMA Transfer payload body with accountId {string}")
    public void setGsmaTransferBody(String accountId){
        GSMATransferDef.setGsmaBody(accountId);
        assertThat(GSMATransferDef.gsmaTransferBody).isNotEmpty();
    }
    @When("I have amsName as {string} and acccountHoldingInstitutionId as {string}")
    public void setHeaders(String amsName,String acccountHoldingInstitutionId){
        GSMATransferDef.setHeadersMifos(amsName,acccountHoldingInstitutionId);
        assertThat(GSMATransferDef.amsName).isNotEmpty();
        assertThat(GSMATransferDef.acccountHoldingInstitutionId).isNotEmpty();
    }
    @Then("I call the channel connector API with expected status of {int}")
    public void sendRequestToGSMAEndpoint(int status) {
        RequestSpecification requestSpec = Utils.getDefaultSpec();
        requestSpec.header("amsName", GSMATransferDef.amsName);
        requestSpec.queryParam("accountHoldingInstitutionId", GSMATransferDef.acccountHoldingInstitutionId);
        CustomData customData=new CustomData();

        GsmaTransfer gsmaTransfer=new GsmaTransfer("RKTQDM7W6S","inbound","transfer","100","USD","note","2022-09-28T12:51:19.260+00:00",customData,);

        GSMATransferDef.response = RestAssured.given(requestSpec)
                .baseUri(GsmaConfig.channelConnectorBaseUrl)
                .body(gsmaTransferBody)
                .expect()
                .spec(new ResponseSpecBuilder().expectStatusCode(status).build())
                .when()
                .post(GsmaConfig.gsmaEndpoint)
                .andReturn().asString();

        logger.info("Batch Summary Response: " + GSMATransferDef.response);
    }

}
