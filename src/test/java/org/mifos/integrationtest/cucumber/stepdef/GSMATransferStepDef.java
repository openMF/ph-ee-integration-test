package org.mifos.integrationtest.cucumber.stepdef;

import static com.github.tomakehurst.wiremock.client.WireMock.getAllServeEvents;
import static com.google.common.truth.Truth.assertThat;
import static java.util.concurrent.TimeUnit.SECONDS;
import static org.awaitility.Awaitility.await;
import static org.mifos.integrationtest.common.Utils.CONTENT_TYPE;
import static org.mifos.integrationtest.common.Utils.CONTENT_TYPE_VALUE;
import static org.mifos.integrationtest.common.Utils.X_CORRELATIONID;
import static org.mifos.integrationtest.common.Utils.X_CallbackURL;

import com.github.tomakehurst.wiremock.stubbing.ServeEvent;
import com.google.gson.JsonObject;
import com.google.gson.JsonParser;
import io.cucumber.core.internal.com.fasterxml.jackson.core.JsonProcessingException;
import io.cucumber.core.internal.com.fasterxml.jackson.databind.JsonNode;
import io.cucumber.core.internal.com.fasterxml.jackson.databind.ObjectMapper;
import io.cucumber.java.en.And;
import io.cucumber.java.en.Given;
import io.cucumber.java.en.Then;
import io.cucumber.java.en.When;
import io.restassured.RestAssured;
import io.restassured.builder.ResponseSpecBuilder;
import io.restassured.specification.RequestSpecification;
import java.io.File;
import java.io.FileNotFoundException;
import java.util.ArrayList;
import java.util.List;
import java.util.Random;
import java.util.Scanner;
import org.apache.fineract.client.models.PostSavingsAccountsResponse;
import org.apache.fineract.client.models.PostSelfLoansLoanIdResponse;
import org.mifos.connector.common.ams.dto.InteropAccountDTO;
import org.mifos.connector.common.identityaccountmapper.dto.AccountMapperRequestDTO;
import org.mifos.connector.common.identityaccountmapper.dto.BeneficiaryDTO;
import org.mifos.integrationtest.common.Utils;
import org.mifos.integrationtest.config.GsmaConfig;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;

public class GSMATransferStepDef extends BaseStepDef {

    String debitParty = "";
    String creditParty = "";
    int balance;
    @Value("${ams.base-url}")
    String amsBaseUrl;
    @Value("${ams.balance-endpoint}")
    String amsBalanceEndpoint;

    @Autowired
    GsmaConfig gsmaConfig;

    @Autowired
    GSMATransferDef gsmaTransferDef;
    @Autowired
    ObjectMapper objectMapper;
    @Autowired
    ScenarioScopeState scenarioScopeState;
    Logger logger = LoggerFactory.getLogger(this.getClass());

    private static AccountMapperRequestDTO registerBeneficiaryBody = null;
    private static String registeringInstitutionId = "SocialWelfare";

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
        gsmaTransferDef.responsePayerClient = RestAssured.given(requestSpec).baseUri(gsmaConfig.payerClientBaseUrl)
                .body(gsmaTransferDef.createPayerClientBody).expect().spec(new ResponseSpecBuilder().expectStatusCode(200).build()).when()
                .post(gsmaConfig.payerClientEndpoint).andReturn().asString();

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
        gsmaTransferDef.responseSavingsProduct = RestAssured.given(requestSpec).baseUri(gsmaConfig.savingsBaseUrl)
                .body(gsmaTransferDef.savingsProductBody).expect().spec(new ResponseSpecBuilder().expectStatusCode(200).build()).when()
                .post(gsmaConfig.savingsProductEndpoint).andReturn().asString();

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
        gsmaTransferDef.responseSavingsAccount = RestAssured.given(requestSpec).baseUri(gsmaConfig.savingsBaseUrl)
                .body(gsmaTransferDef.savingsAccountBody).expect().spec(new ResponseSpecBuilder().expectStatusCode(200).build()).when()
                .post(gsmaConfig.savingsAccountEndpoint).andReturn().asString();

        logger.info("Savings Account Response: " + gsmaTransferDef.responseSavingsAccount);
        assertThat(gsmaTransferDef.responseSavingsAccount).isNotEmpty();
    }

    @Then("I call the interop identifier endpoint")
    public void callCreateInteropIdentifierEndpoint() throws JsonProcessingException {
        // Setting headers and body
        RequestSpecification requestSpec = Utils.getDefaultSpec();
        requestSpec = gsmaTransferDef.setHeaders(requestSpec);
        gsmaTransferDef.interopIdentifierBody = gsmaTransferDef.setBodyInteropIdentifier();
        // Setting account ID in path
        PostSavingsAccountsResponse savingsAccountResponse = objectMapper.readValue(gsmaTransferDef.responseSavingsAccount,
                PostSavingsAccountsResponse.class);
        scenarioScopeState.payerIdentifier = savingsAccountResponse.getSavingsId().toString();
        gsmaConfig.interopIdentifierEndpoint = gsmaConfig.interopIdentifierEndpoint.replaceAll("\\{\\{identifierType\\}\\}", "MSISDN");
        gsmaConfig.interopIdentifierEndpoint = gsmaConfig.interopIdentifierEndpoint.replaceAll("\\{\\{identifier\\}\\}",
                scenarioScopeState.payerIdentifier);
        // Calling Interop Identifier endpoint
        gsmaTransferDef.responseInteropIdentifier = RestAssured.given(requestSpec).baseUri(gsmaConfig.savingsBaseUrl)
                .body(gsmaTransferDef.interopIdentifierBody).expect().spec(new ResponseSpecBuilder().expectStatusCode(200).build()).when()
                .post(gsmaConfig.interopIdentifierEndpoint).andReturn().asString();

        logger.info("Interop Identifier Response: " + gsmaTransferDef.responseInteropIdentifier);
        assertThat(gsmaTransferDef.responseInteropIdentifier).isNotEmpty();
    }

    @Then("I approve the deposit with command {string}")
    public void callApproveSavingsEndpoint(String command) throws JsonProcessingException {
        // Setting headers and body
        RequestSpecification requestSpec = Utils.getDefaultSpec();
        requestSpec = gsmaTransferDef.setHeaders(requestSpec);
        requestSpec.queryParam("command", command);
        gsmaTransferDef.savingsApproveBody = gsmaTransferDef.setBodySavingsApprove();
        // Setting account ID in path
        PostSavingsAccountsResponse savingsAccountResponse = objectMapper.readValue(gsmaTransferDef.responseSavingsAccount,
                PostSavingsAccountsResponse.class);
        String savingsApproveEndpoint = gsmaConfig.savingsApproveEndpoint;
        savingsApproveEndpoint = savingsApproveEndpoint.replaceAll("\\{\\{savingsAccId\\}\\}",
                savingsAccountResponse.getSavingsId().toString());
        // Calling create loan account endpoint
        gsmaTransferDef.responseSavingsApprove = RestAssured.given(requestSpec).baseUri(gsmaConfig.savingsBaseUrl)
                .body(gsmaTransferDef.savingsApproveBody).expect().spec(new ResponseSpecBuilder().expectStatusCode(200).build()).when()
                .post(savingsApproveEndpoint).andReturn().asString();

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
        // Setting account ID
        PostSavingsAccountsResponse savingsAccountResponse = objectMapper.readValue(gsmaTransferDef.responseSavingsAccount,
                PostSavingsAccountsResponse.class);
        String savingsApproveEndpoint = gsmaConfig.savingsApproveEndpoint;
        savingsApproveEndpoint = savingsApproveEndpoint.replaceAll("\\{\\{savingsAccId\\}\\}",
                savingsAccountResponse.getSavingsId().toString());
        // Calling create loan account endpoint
        gsmaTransferDef.responseSavingsActivate = RestAssured.given(requestSpec).baseUri(gsmaConfig.savingsBaseUrl)
                .body(gsmaTransferDef.savingsActivateBody).expect().spec(new ResponseSpecBuilder().expectStatusCode(200).build()).when()
                .post(savingsApproveEndpoint).andReturn().asString();

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
        // Setting account ID
        PostSavingsAccountsResponse savingsAccountResponse = objectMapper.readValue(gsmaTransferDef.responseSavingsAccount,
                PostSavingsAccountsResponse.class);
        String savingsDepositEndpoint = gsmaConfig.savingsDepositAccountEndpoint;
        savingsDepositEndpoint = savingsDepositEndpoint.replaceAll("\\{\\{savingsAccId\\}\\}",
                savingsAccountResponse.getSavingsId().toString());
        // Calling create loan account endpoint
        gsmaTransferDef.responseSavingsDepositAccount = RestAssured.given(requestSpec).baseUri(gsmaConfig.savingsBaseUrl)
                .body(gsmaTransferDef.savingsDepositAccountBody).expect().spec(new ResponseSpecBuilder().expectStatusCode(200).build())
                .when().post(savingsDepositEndpoint).andReturn().asString();

        logger.info("Savings Deposit Response: " + gsmaTransferDef.responseSavingsDepositAccount);
        assertThat(gsmaTransferDef.responseSavingsDepositAccount).isNotEmpty();

    }

    @Then("I call the deposit account endpoint for {string} with command {string} for amount {int}")
    public void callDepositAccountEndpoint(String client, String command, int amount) throws JsonProcessingException {
        // Setting headers and body
        RequestSpecification requestSpec = Utils.getDefaultSpec();
        requestSpec = gsmaTransferDef.setHeaders(requestSpec);
        requestSpec.queryParam("command", command);
        if (client.equals("payer")) {
            scenarioScopeState.initialBalForPayer = amount;
        } else if (client.equals("payee")) {
            scenarioScopeState.initialBalForPayee = amount;
        }
        gsmaTransferDef.savingsDepositAccountBody = gsmaTransferDef.setSavingsDepositAccount(amount);
        // Setting account ID
        PostSavingsAccountsResponse savingsAccountResponse = objectMapper.readValue(gsmaTransferDef.responseSavingsAccount,
                PostSavingsAccountsResponse.class);
        String savingsDepositEndpoint = gsmaConfig.savingsDepositAccountEndpoint;
        savingsDepositEndpoint = savingsDepositEndpoint.replaceAll("\\{\\{savingsAccId\\}\\}",
                savingsAccountResponse.getSavingsId().toString());
        // Calling create loan account endpoint
        gsmaTransferDef.responseSavingsDepositAccount = RestAssured.given(requestSpec).baseUri(gsmaConfig.savingsBaseUrl)
                .body(gsmaTransferDef.savingsDepositAccountBody).expect().spec(new ResponseSpecBuilder().expectStatusCode(200).build())
                .when().post(savingsDepositEndpoint).andReturn().asString();

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
        gsmaTransferDef.responseLoanProduct = RestAssured.given(requestSpec).baseUri(gsmaConfig.loanBaseUrl)
                .body(gsmaTransferDef.loanProductBody).expect().spec(new ResponseSpecBuilder().expectStatusCode(200).build()).when()
                .post(gsmaConfig.loanProductEndpoint).andReturn().asString();

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
        gsmaTransferDef.responseLoanAccount = RestAssured.given(requestSpec).baseUri(gsmaConfig.loanBaseUrl)
                .body(gsmaTransferDef.loanAccountBody).expect().spec(new ResponseSpecBuilder().expectStatusCode(200).build()).when()
                .post(gsmaConfig.loanAccountEndpoint).andReturn().asString();

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
        // Setting account ID
        PostSelfLoansLoanIdResponse loanAccountResponse = objectMapper.readValue(gsmaTransferDef.responseLoanAccount,
                PostSelfLoansLoanIdResponse.class);
        String loanAccountId = String.valueOf(loanAccountResponse.getLoanId());
        gsmaConfig.loanApproveEndpoint = gsmaConfig.loanApproveEndpoint.replaceAll("\\{\\{loanAccId\\}\\}", loanAccountId);
        // Calling create loan account endpoint
        gsmaTransferDef.responseLoanApprove = RestAssured.given(requestSpec).baseUri(gsmaConfig.loanBaseUrl)
                .body(gsmaTransferDef.loanApproveBody).expect().spec(new ResponseSpecBuilder().expectStatusCode(200).build()).when()
                .post(gsmaConfig.loanApproveEndpoint).andReturn().asString();

        logger.info("Loan Approve Response: " + gsmaTransferDef.responseLoanApprove);
        assertThat(gsmaTransferDef.responseLoanApprove).isNotEmpty();
    }

    @When("I call the loan disburse endpoint with command {string} for amount {int}")
    public void callLoanDisburseEndpoint(String command, int amount) throws JsonProcessingException {
        RequestSpecification requestSpec = Utils.getDefaultSpec();
        requestSpec = gsmaTransferDef.setHeaders(requestSpec);
        requestSpec.queryParam("command", command);
        gsmaTransferDef.loanDisburseBody = gsmaTransferDef.setBodyLoanDisburse(amount);
        // Setting account ID
        PostSelfLoansLoanIdResponse loanAccountResponse = objectMapper.readValue(gsmaTransferDef.responseLoanAccount,
                PostSelfLoansLoanIdResponse.class);
        String loanAccountId = String.valueOf(loanAccountResponse.getLoanId());
        gsmaConfig.loanDisburseEndpoint = gsmaConfig.loanDisburseEndpoint.replaceAll("\\{\\{loanAccId\\}\\}", loanAccountId);
        // Calling create loan account endpoint
        gsmaTransferDef.responseLoanDisburse = RestAssured.given(requestSpec).baseUri(gsmaConfig.loanBaseUrl)
                .body(gsmaTransferDef.loanDisburseBody).expect().spec(new ResponseSpecBuilder().expectStatusCode(200).build()).when()
                .post(gsmaConfig.loanDisburseEndpoint).andReturn().asString();

        logger.info("Loan Approve Response: " + gsmaTransferDef.responseLoanDisburse);
        assertThat(gsmaTransferDef.responseLoanDisburse).isNotEmpty();
    }

    @Then("I call the loan repayment endpoint for amount {int}")
    public void callLoanRepaymentEndpoint(int amount) throws JsonProcessingException {
        // Setting headers and body
        RequestSpecification requestSpec = Utils.getDefaultSpec();
        requestSpec = gsmaTransferDef.setHeaders(requestSpec);
        gsmaTransferDef.loanRepaymentBody = gsmaTransferDef.setBodyLoanRepayment(String.valueOf(amount));
        // Setting account ID
        PostSelfLoansLoanIdResponse loanAccountResponse = objectMapper.readValue(gsmaTransferDef.responseLoanAccount,
                PostSelfLoansLoanIdResponse.class);
        String loanId = Integer.toString(loanAccountResponse.getLoanId());
        String loanAccountId = String.format("%0" + (9 - loanId.length()) + "d%s", 0, loanId);
        gsmaConfig.loanRepaymentEndpoint = gsmaConfig.loanRepaymentEndpoint.replaceAll("\\{\\{loanAccId\\}\\}", loanAccountId);
        // Calling create loan account endpoint
        gsmaTransferDef.responseLoanRepayment = RestAssured.given(requestSpec).baseUri(gsmaConfig.loanBaseUrl)
                .body(gsmaTransferDef.loanRepaymentBody).expect().spec(new ResponseSpecBuilder().expectStatusCode(200).build()).when()
                .post(gsmaConfig.loanRepaymentEndpoint).andReturn().asString();

        logger.info("Loan Repayment Response: " + gsmaTransferDef.responseLoanRepayment);
        assertThat(gsmaTransferDef.responseLoanRepayment).isNotEmpty();
    }

    @When("I have amsName as {string} and acccountHoldingInstitutionId as {string} and amount as {int}")
    public void setHeaders(String amsName, String acccountHoldingInstitutionId, int amount) {
        gsmaTransferDef.setHeadersMifos(amsName, acccountHoldingInstitutionId, amount);
        assertThat(gsmaTransferDef.amsName).isNotEmpty();
        assertThat(gsmaTransferDef.acccountHoldingInstitutionId).isNotEmpty();
        assertThat(gsmaTransferDef.amount).isNotNull();

    }

    @Then("I call the channel connector API for savings account with expected status of {int} and stub {string}")
    public void sendRequestToGSMAEndpointSavings(int status, String stub) throws JsonProcessingException {
        RequestSpecification requestSpec = Utils.getDefaultSpec();
        requestSpec.header("amsName", gsmaTransferDef.amsName);
        requestSpec.header("accountHoldingInstitutionId", gsmaTransferDef.acccountHoldingInstitutionId);
        requestSpec.header(X_CORRELATIONID, "123456789");
        requestSpec.header(X_CallbackURL, gsmaConfig.callbackURL + stub);
        requestSpec.header(CONTENT_TYPE, CONTENT_TYPE_VALUE);

        gsmaTransferDef.gsmaTransferBody = gsmaTransferDef.setGsmaTransactionBody("S");

        gsmaTransferDef.gsmaTransactionResponse = RestAssured.given(requestSpec).baseUri(gsmaConfig.channelConnectorBaseUrl)
                .body(gsmaTransferDef.gsmaTransferBody).expect().spec(new ResponseSpecBuilder().expectStatusCode(status).build()).when()
                .post(gsmaConfig.gsmaEndpoint).andReturn().asString();

        logger.info("GSMA Transaction Response: " + gsmaTransferDef.gsmaTransactionResponse);
        assertThat(gsmaTransferDef.gsmaTransactionResponse).isNotEmpty();
    }

    @Then("I call the channel connector API for loan account with expected status of {int} and stub {string}")
    public void sendRequestToGSMAEndpointLoan(int status, String stub) throws JsonProcessingException {
        RequestSpecification requestSpec = Utils.getDefaultSpec();
        requestSpec.header("amsName", gsmaTransferDef.amsName);
        requestSpec.header("accountHoldingInstitutionId", gsmaTransferDef.acccountHoldingInstitutionId);
        requestSpec.header(X_CORRELATIONID, "123456789");
        requestSpec.header(CONTENT_TYPE, CONTENT_TYPE_VALUE);
        requestSpec.header(X_CallbackURL, gsmaConfig.callbackURL + stub);

        gsmaTransferDef.gsmaTransferBody = gsmaTransferDef.setGsmaTransactionBody("L");

        gsmaTransferDef.gsmaTransactionResponse = RestAssured.given(requestSpec).baseUri(gsmaConfig.channelConnectorBaseUrl)
                .body(gsmaTransferDef.gsmaTransferBody).expect().spec(new ResponseSpecBuilder().expectStatusCode(status).build()).when()
                .post(gsmaConfig.gsmaEndpoint).andReturn().asString();

        logger.info("GSMA Transaction Response: " + gsmaTransferDef.gsmaTransactionResponse);
        assertThat(gsmaTransferDef.gsmaTransactionResponse).isNotEmpty();
    }

    @Then("I call the debit interop identifier endpoint with MSISDN")
    public void callCreateDebitInteropIdentifierEndpointMSISDN() throws JsonProcessingException {
        // Setting headers and body
        RequestSpecification requestSpec = Utils.getDefaultSpec();
        requestSpec = gsmaTransferDef.setHeaders(requestSpec);
        gsmaTransferDef.interopIdentifierBody = gsmaTransferDef.setBodyInteropIdentifier();
        // Setting account ID in path
        PostSavingsAccountsResponse savingsAccountResponse = objectMapper.readValue(gsmaTransferDef.responseSavingsAccount,
                PostSavingsAccountsResponse.class);
        String payer_identifier = debitParty;
        String debitInteropEndpoint = gsmaConfig.interopIdentifierEndpoint;

        debitInteropEndpoint = debitInteropEndpoint.replaceAll("\\{\\{identifierType\\}\\}", "MSISDN");
        debitInteropEndpoint = debitInteropEndpoint.replaceAll("\\{\\{identifier\\}\\}", payer_identifier);
        // Calling Interop Identifier endpoint
        logger.info("Interop Identifier Request: " + debitInteropEndpoint);
        gsmaTransferDef.responseInteropIdentifier = RestAssured.given(requestSpec).baseUri(gsmaConfig.savingsBaseUrl)
                .body(gsmaTransferDef.interopIdentifierBody).expect().spec(new ResponseSpecBuilder().expectStatusCode(200).build()).when()
                .post(debitInteropEndpoint).andReturn().asString();

        logger.info("Interop Identifier Response: " + gsmaTransferDef.responseInteropIdentifier);
        assertThat(gsmaTransferDef.responseInteropIdentifier).isNotEmpty();
    }

    @Then("I call the credit interop identifier endpoint with MSISDN")
    public void callCreateCreditInteropIdentifierEndpointMSISDN() throws JsonProcessingException {
        // Setting headers and body
        RequestSpecification requestSpec = Utils.getDefaultSpec();
        requestSpec = gsmaTransferDef.setHeaders(requestSpec);
        gsmaTransferDef.interopIdentifierBody = gsmaTransferDef.setBodyInteropIdentifier();
        // Setting account ID in path
        PostSavingsAccountsResponse savingsAccountResponse = objectMapper.readValue(gsmaTransferDef.responseSavingsAccount,
                PostSavingsAccountsResponse.class);
        String payer_identifier = creditParty;
        String creditInteropEndpoint = gsmaConfig.interopIdentifierEndpoint;
        creditInteropEndpoint = creditInteropEndpoint.replaceAll("\\{\\{identifierType\\}\\}", "MSISDN");
        creditInteropEndpoint = creditInteropEndpoint.replaceAll("\\{\\{identifier\\}\\}", payer_identifier);
        // Calling Interop Identifier endpoint
        logger.info("Interop Identifier Request: " + creditInteropEndpoint);
        gsmaTransferDef.responseInteropIdentifier = RestAssured.given(requestSpec).baseUri(gsmaConfig.savingsBaseUrl)
                .body(gsmaTransferDef.interopIdentifierBody).expect().spec(new ResponseSpecBuilder().expectStatusCode(200).build()).when()
                .post(creditInteropEndpoint).andReturn().asString();

        logger.info("Interop Identifier Response: " + gsmaTransferDef.responseInteropIdentifier);
        assertThat(gsmaTransferDef.responseInteropIdentifier).isNotEmpty();
    }

    @Then("I call the balance api for payer balance")
    public void iCallTheBalanceApiForPayerBalance() throws JsonProcessingException {
        RequestSpecification requestSpec = Utils.getDefaultSpec(scenarioScopeState.tenant);
        String finalEndpoint = amsBalanceEndpoint;
        finalEndpoint = finalEndpoint.replace("{IdentifierType}", "MSISDN");
        finalEndpoint = finalEndpoint.replace("{IdentifierId}", debitParty.isEmpty() ? scenarioScopeState.payerIdentifier : debitParty);
        logger.info("Endpoint: " + finalEndpoint);
        scenarioScopeState.response = RestAssured.given(requestSpec).baseUri(amsBaseUrl).body("").expect()
                .spec(new ResponseSpecBuilder().expectStatusCode(200).build()).when().get(finalEndpoint).andReturn().asString();
        logger.info("Balance Response: " + scenarioScopeState.response);
        InteropAccountDTO interopAccountDTO = objectMapper.readValue(scenarioScopeState.response, InteropAccountDTO.class);
        assertThat(interopAccountDTO.getAvailableBalance().intValue() <= scenarioScopeState.initialBalForPayer).isTrue();
        scenarioScopeState.gsmaP2PAmtDebit = 0;

    }

    @Then("I call the balance api for payee balance")
    public void iCallTheBalanceApiForPayeeBalance() throws JsonProcessingException {
        RequestSpecification requestSpec = Utils.getDefaultSpec(scenarioScopeState.tenant);
        String finalEndpoint = amsBalanceEndpoint;
        finalEndpoint = finalEndpoint.replace("{IdentifierType}", "MSISDN");
        finalEndpoint = finalEndpoint.replace("{IdentifierId}", creditParty.isEmpty() ? scenarioScopeState.payeeIdentifier : creditParty);
        logger.info("Endpoint: " + finalEndpoint);
        scenarioScopeState.response = RestAssured.given(requestSpec).baseUri(amsBaseUrl).body("").expect()
                .spec(new ResponseSpecBuilder().expectStatusCode(200).build()).when().get(finalEndpoint).andReturn().asString();
        logger.info("Balance Response: " + scenarioScopeState.response);
        InteropAccountDTO interopAccountDTO = objectMapper.readValue(scenarioScopeState.response, InteropAccountDTO.class);
        assertThat(interopAccountDTO.getAvailableBalance().intValue() >= scenarioScopeState.initialBalForPayee).isTrue();

    }

    @Then("I call the balance api for payer balance after debit")
    public void iCallTheBalanceApiForPayerBalanceAfterDebit() throws JsonProcessingException {
        RequestSpecification requestSpec = Utils.getDefaultSpec(scenarioScopeState.tenant);
        String finalEndpoint = amsBalanceEndpoint;
        finalEndpoint = finalEndpoint.replace("{IdentifierType}", "MSISDN");
        finalEndpoint = finalEndpoint.replace("{IdentifierId}", debitParty.isEmpty() ? scenarioScopeState.payerIdentifier : debitParty);
        logger.info("Endpoint: " + finalEndpoint);
        scenarioScopeState.response = RestAssured.given(requestSpec).baseUri(amsBaseUrl).body("").expect()
                .spec(new ResponseSpecBuilder().expectStatusCode(200).build()).when().get(finalEndpoint).andReturn().asString();
        logger.info("Balance Response: " + scenarioScopeState.response);
        InteropAccountDTO interopAccountDTO = objectMapper.readValue(scenarioScopeState.response, InteropAccountDTO.class);
        assertThat(interopAccountDTO.getAvailableBalance().intValue() == scenarioScopeState.initialBalForPayer
                - scenarioScopeState.gsmaP2PAmtDebit).isTrue();

    }

    @Then("I call the balance api for payee balance after credit")
    public void iCallTheBalanceApiForPayeeBalanceAfterCredit() throws JsonProcessingException {
        RequestSpecification requestSpec = Utils.getDefaultSpec(scenarioScopeState.tenant);
        String finalEndpoint = amsBalanceEndpoint;
        finalEndpoint = finalEndpoint.replace("{IdentifierType}", "MSISDN");
        finalEndpoint = finalEndpoint.replace("{IdentifierId}", creditParty.isEmpty() ? scenarioScopeState.payeeIdentifier : creditParty);
        logger.info("Endpoint: " + finalEndpoint);
        scenarioScopeState.response = RestAssured.given(requestSpec).baseUri(amsBaseUrl).body("").expect()
                .spec(new ResponseSpecBuilder().expectStatusCode(200).build()).when().get(finalEndpoint).andReturn().asString();
        logger.info("Balance Response: " + scenarioScopeState.response);
        InteropAccountDTO interopAccountDTO = objectMapper.readValue(scenarioScopeState.response, InteropAccountDTO.class);
        assertThat(interopAccountDTO.getAvailableBalance().intValue() == scenarioScopeState.initialBalForPayee
                + scenarioScopeState.gsmaP2PAmtDebit).isTrue();

    }

    @When("I create a set of debit and credit party")
    public void iCreateASetOfDebitAndCreditParty() {
        Random random = new Random();
        debitParty = String.valueOf(random.nextInt(900000000) + 1000000000);
        creditParty = String.valueOf(random.nextInt(900000000) + 1000000000);
        assertThat(debitParty).isNotEmpty();
        assertThat(creditParty).isNotEmpty();
        assertThat(debitParty).isNotEqualTo(creditParty);

    }

    @Then("I create an IdentityMapperDTO for Register Beneficiary with identifier from previous step")
    public void iCreateAnIdentityMapperDTOForRegisterBeneficiaryWithIdentifierFromPreviousStep() {
        List<BeneficiaryDTO> beneficiaryDTOList = new ArrayList<>();
        scenarioScopeState.payeeIdentity = generateUniqueNumber(16);
        BeneficiaryDTO beneficiaryDTO = new BeneficiaryDTO(scenarioScopeState.payeeIdentity, "01", scenarioScopeState.payerIdentifier,
                "gorilla");
        beneficiaryDTOList.add(beneficiaryDTO);
        scenarioScopeState.requestId = generateUniqueNumber(12);
        registerBeneficiaryBody = new AccountMapperRequestDTO(scenarioScopeState.requestId, "", beneficiaryDTOList);
    }

    public static String generateUniqueNumber(int length) {
        Random rand = new Random();
        long timestamp = System.currentTimeMillis();
        long randomLong = rand.nextLong(100000000);
        String uniqueNumber = timestamp + "" + randomLong;
        return uniqueNumber.substring(0, length);
    }

    @When("I call the register beneficiary API with expected status of {int} and callback stub {string}")
    public void iCallTheRegisterBeneficiaryAPIWithExpectedStatusOfAndCallbackStub(int expectedStatus, String stub) {
        RequestSpecification requestSpec = Utils.getDefaultSpec();
        scenarioScopeState.response = RestAssured.given(requestSpec).header("Content-Type", "application/json")
                .header("X-Registering-Institution-ID", registeringInstitutionId)
                .header("X-CallbackURL", identityMapperConfig.callbackURL + stub).baseUri(identityMapperConfig.identityMapperContactPoint)
                .body(registerBeneficiaryBody).expect().spec(new ResponseSpecBuilder().expectStatusCode(expectedStatus).build()).when()
                .post(identityMapperConfig.registerBeneficiaryEndpoint).andReturn().asString();

        logger.info("Identity Mapper Response: {}", scenarioScopeState.response);
    }

    @Then("I call the account lookup API with expected status of {int} and callback stub {string}")
    public void iCallTheAccountLookupAPIWithExpectedStatusOfAndCallbackStub(int expectedStatus, String stub) {
        await().atMost(awaitMost, SECONDS).untilAsserted(() -> {
            scenarioScopeState.requestId = generateUniqueNumber(10);
            RequestSpecification requestSpec = Utils.getDefaultSpec();
            scenarioScopeState.response = RestAssured.given(requestSpec).header("Content-Type", "application/json")
                    .header("X-Registering-Institution-ID", registeringInstitutionId)
                    .header("X-CallbackURL", identityMapperConfig.callbackURL + stub)
                    .queryParam("payeeIdentity", scenarioScopeState.payeeIdentity).queryParam("paymentModality", "01")
                    .queryParam("requestId", scenarioScopeState.requestId).baseUri(identityMapperConfig.identityMapperContactPoint).expect()
                    .spec(new ResponseSpecBuilder().expectStatusCode(expectedStatus).build()).when()
                    .get(identityMapperConfig.accountLookupEndpoint).andReturn().asString();

            logger.info("Identity Mapper Response: {}", scenarioScopeState.response);
        });
    }

    @And("I should be able to verify that the {string} method to {string} endpoint received a request with validation")
    public void iShouldBeAbleToVerifyThatTheMethodToEndpointReceivedARequestWithValidation(String arg0, String endpoint) {
        await().atMost(awaitMost, SECONDS).untilAsserted(() -> {

            List<ServeEvent> allServeEvents = getAllServeEvents();
            Boolean isValidated = null;

            for (int i = 0; i < allServeEvents.size(); i++) {
                ServeEvent request = allServeEvents.get(i);

                if (!(request.getRequest().getBodyAsString()).isEmpty()) {
                    try {
                        JsonNode rootNode = objectMapper.readTree(request.getRequest().getBodyAsString());
                        String requestID = rootNode.get("requestId").asText();

                        if (scenarioScopeState.requestId.equals(requestID)) {
                            scenarioScopeState.callbackBody = request.getRequest().getBodyAsString();
                        }
                    } catch (Exception e) {
                        logger.debug("{}", e.getMessage());
                    }

                }
            }
            try {
                JsonNode rootNode = objectMapper.readTree(scenarioScopeState.callbackBody);
                isValidated = rootNode.get("isValidated").asBoolean();

            } catch (Exception e) {
                logger.debug("{}", e.getMessage());
            }
            assertThat(isValidated).isTrue();
        });
    }

    @When("I call the AMS Mifos Deposit Mock API with expected status of {int}")
    public void sendRequestToGSMADepositMockEndpoint(int status) throws JsonProcessingException {
        RequestSpecification requestSpec = Utils.getDefaultSpec();
        requestSpec.header(CONTENT_TYPE, CONTENT_TYPE_VALUE);
        String body = "{}";
        RestAssured.given(requestSpec).baseUri(gsmaConfig.amsMifosBasseUrl).body(body).expect()
                .spec(new ResponseSpecBuilder().expectStatusCode(status).build()).when().post(gsmaConfig.savingsDepositAccountMockEndpoint)
                .andReturn().asString();
    }

    @When("I call the AMS Mifos Loan Repayment Mock API with expected status of {int}")
    public void sendRequestToGSMALoanRepaymentMockEndpoint(int status) throws JsonProcessingException {
        RequestSpecification requestSpec = Utils.getDefaultSpec();
        requestSpec.header(CONTENT_TYPE, CONTENT_TYPE_VALUE);
        String body = "{}";
        RestAssured.given(requestSpec).baseUri(gsmaConfig.amsMifosBasseUrl).body(body).expect()
                .spec(new ResponseSpecBuilder().expectStatusCode(status).build()).when().post(gsmaConfig.loanRepaymentMockEndpoint)
                .andReturn().asString();
    }

    @When("I call the savings account endpoint to get the current Balance")
    public void getCurrentBalance() throws JsonProcessingException {
        RequestSpecification requestSpec = Utils.getDefaultSpec();
        requestSpec = gsmaTransferDef.setHeaders(requestSpec);
        // Setting account ID in path
        PostSavingsAccountsResponse savingsAccountResponse = objectMapper.readValue(gsmaTransferDef.responseSavingsAccount,
                PostSavingsAccountsResponse.class);
        gsmaConfig.savingsApproveEndpoint = gsmaConfig.savingsApproveEndpoint.replaceAll("\\{\\{savingsAccId\\}\\}",
                savingsAccountResponse.getSavingsId().toString());

        String responseBody = RestAssured.given(requestSpec).baseUri(gsmaConfig.savingsBaseUrl).expect()
                .spec(new ResponseSpecBuilder().expectStatusCode(200).build()).when().get(gsmaConfig.savingsApproveEndpoint).andReturn()
                .asString();

        JsonObject jsonObject = JsonParser.parseString(responseBody).getAsJsonObject();

        scenarioScopeState.currentBalance = jsonObject.get("summary").getAsJsonObject().get("accountBalance").getAsLong();
        logger.info(String.valueOf(scenarioScopeState.currentBalance));
    }

    @When("I create a set of debit and credit party from file {string}")
    public void iCreateASetOfDebitAndCreditPartyFromFile(String filename) {
        try {
            scenarioScopeState.filename = filename;
            File f = new File(Utils.getAbsoluteFilePathToResource(scenarioScopeState.filename));
            assertThat(f.exists()).isTrue();
            assertThat(scenarioScopeState.filename).isNotEmpty();
            Scanner scanner = new Scanner(f);
            String line = scanner.nextLine();
            while (scanner.hasNextLine() && !line.isEmpty()) {
                String line2 = scanner.nextLine();
                String[] parts = line2.split(",");
                debitParty = parts[4];
                creditParty = parts[6];
                assertThat(debitParty).isNotEmpty();
                assertThat(creditParty).isNotEmpty();
                assertThat(debitParty).isNotEqualTo(creditParty);
            }
            scanner.close();
        } catch (FileNotFoundException e) {
            logger.info("File not found");
        }

    }

    @And("I parse amount to be debited and credited from file {string}")
    public void iParseAmountToBeDebitedAndCreditedFromFile(String filename) {
        try {
            scenarioScopeState.filename = filename;
            File f = new File(Utils.getAbsoluteFilePathToResource(scenarioScopeState.filename));
            assertThat(f.exists()).isTrue();
            assertThat(scenarioScopeState.filename).isNotEmpty();
            Scanner scanner = new Scanner(f);
            String line = scanner.nextLine();
            while (scanner.hasNextLine() && !line.isEmpty()) {
                String line2 = scanner.nextLine();
                String[] parts = line2.split(",");
                scenarioScopeState.gsmaP2PAmtDebit = scenarioScopeState.gsmaP2PAmtDebit + Integer.parseInt(parts[7]);
                assertThat(scenarioScopeState.gsmaP2PAmtDebit).isNotNull();
            }
            scanner.close();
        } catch (FileNotFoundException e) {
            logger.info("File not found");
        }

    }

    @Then("I call the balance api for payee {string} balance")
    public void iCallTheBalanceApiForPayeeBalance(String id) throws JsonProcessingException {
        RequestSpecification requestSpec = Utils.getDefaultSpec(scenarioScopeState.tenant);
        String finalEndpoint = amsBalanceEndpoint;
        if (scenarioScopeState.payeeIdentifierforBatch == null) {
            scenarioScopeState.payeeIdentifierforBatch = new String[4];
        }
        scenarioScopeState.payeeIdentifierforBatch[Integer.parseInt(id)] = scenarioScopeState.payeeIdentifier;
        finalEndpoint = finalEndpoint.replace("{IdentifierType}", "MSISDN");
        finalEndpoint = finalEndpoint.replace("{IdentifierId}", creditParty.isEmpty() ? scenarioScopeState.payeeIdentifier : creditParty);
        logger.info("Endpoint: " + finalEndpoint);
        scenarioScopeState.response = RestAssured.given(requestSpec).baseUri(amsBaseUrl).body("").expect()
                .spec(new ResponseSpecBuilder().expectStatusCode(200).build()).when().get(finalEndpoint).andReturn().asString();
        logger.info("Balance Response: " + scenarioScopeState.response);
        InteropAccountDTO interopAccountDTO = objectMapper.readValue(scenarioScopeState.response, InteropAccountDTO.class);
        assertThat(
                interopAccountDTO.getAvailableBalance().intValue() >= scenarioScopeState.initialBalForPayeeForBatch[Integer.parseInt(id)])
                .isTrue();

    }

    @Then("I call the balance api for payee with id {string} balance after credit")
    public void iCallTheBalanceApiForPayeeBalanceAfterCredit(String id) throws JsonProcessingException {
        RequestSpecification requestSpec = Utils.getDefaultSpec(scenarioScopeState.tenant);
        String finalEndpoint = amsBalanceEndpoint;
        finalEndpoint = finalEndpoint.replace("{IdentifierType}", "MSISDN");
        finalEndpoint = finalEndpoint.replace("{IdentifierId}",
                creditParty.isEmpty() ? scenarioScopeState.payeeIdentifierforBatch[Integer.parseInt(id)] : creditParty);
        logger.info("Endpoint: " + finalEndpoint);
        scenarioScopeState.response = RestAssured.given(requestSpec).baseUri(amsBaseUrl).body("").expect()
                .spec(new ResponseSpecBuilder().expectStatusCode(200).build()).when().get(finalEndpoint).andReturn().asString();
        logger.info("Balance Response: " + scenarioScopeState.response);
        InteropAccountDTO interopAccountDTO = objectMapper.readValue(scenarioScopeState.response, InteropAccountDTO.class);
        assertThat(interopAccountDTO.getAvailableBalance().intValue() == scenarioScopeState.initialBalForPayeeForBatch[Integer.parseInt(id)]
                + scenarioScopeState.gsmaP2PAmtDebitForBatch[Integer.parseInt(id)]).isTrue();

    }
}
