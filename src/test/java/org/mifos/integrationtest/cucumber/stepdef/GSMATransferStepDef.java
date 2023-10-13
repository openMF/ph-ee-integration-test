package org.mifos.integrationtest.cucumber.stepdef;

import static com.github.tomakehurst.wiremock.client.WireMock.*;
import static com.google.common.truth.Truth.assertThat;
import static org.mifos.integrationtest.common.Utils.CONTENT_TYPE;
import static org.mifos.integrationtest.common.Utils.CONTENT_TYPE_VALUE;
import static org.mifos.integrationtest.common.Utils.X_CORRELATIONID;

import com.github.tomakehurst.wiremock.stubbing.ServeEvent;
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
import org.apache.fineract.client.models.PostSavingsAccountsResponse;
import org.mifos.connector.common.identityaccountmapper.dto.AccountMapperRequestDTO;
import org.mifos.connector.common.identityaccountmapper.dto.BeneficiaryDTO;
import org.mifos.integrationtest.common.Utils;
import org.apache.fineract.client.models.PostSelfLoansLoanIdResponse;
import org.mifos.integrationtest.config.GsmaConfig;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;

import java.util.ArrayList;
import java.util.List;
import java.util.Random;

public class GSMATransferStepDef extends BaseStepDef{

    @Autowired
    GsmaConfig gsmaConfig;
    @Autowired
    GSMATransferDef gsmaTransferDef;
    @Autowired
    ObjectMapper objectMapper;
    Logger logger = LoggerFactory.getLogger(this.getClass());
    private static String payer_identifier;
    private static String payeeIdentity;
    private static String requestId;
    private static AccountMapperRequestDTO registerBeneficiaryBody = null;
    private static String registeringInstitutionId = "SocialWelfare";
    private static String callbackBody;

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
        PostSavingsAccountsResponse savingsAccountResponse = objectMapper.readValue(
                gsmaTransferDef.responseSavingsAccount, PostSavingsAccountsResponse.class);
        payer_identifier = savingsAccountResponse.getSavingsId().toString();
        gsmaConfig.interopIdentifierEndpoint = gsmaConfig.interopIdentifierEndpoint.replaceAll("\\{\\{payer_identifierType\\}\\}", "MSISDN");
        gsmaConfig.interopIdentifierEndpoint = gsmaConfig.interopIdentifierEndpoint.replaceAll("\\{\\{payer_identifier\\}\\}", payer_identifier);
        // Calling Interop Identifier endpoint
        gsmaTransferDef.responseInteropIdentifier = RestAssured.given(requestSpec)
                .baseUri(gsmaConfig.savingsBaseUrl)
                .body(gsmaTransferDef.interopIdentifierBody)
                .expect()
                .spec(new ResponseSpecBuilder().expectStatusCode(200).build())
                .when()
                .post(gsmaConfig.interopIdentifierEndpoint)
                .andReturn().asString();

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
        PostSavingsAccountsResponse savingsAccountResponse = objectMapper.readValue(
                gsmaTransferDef.responseSavingsAccount, PostSavingsAccountsResponse.class);
        gsmaConfig.savingsApproveEndpoint = gsmaConfig.savingsApproveEndpoint.replaceAll("\\{\\{savingsAccId\\}\\}", savingsAccountResponse.getSavingsId().toString());
        // Calling create loan account endpoint
        gsmaTransferDef.responseSavingsApprove = RestAssured.given(requestSpec).baseUri(gsmaConfig.savingsBaseUrl)
                .body(gsmaTransferDef.savingsApproveBody).expect().spec(new ResponseSpecBuilder().expectStatusCode(200).build()).when()
                .post(gsmaConfig.savingsApproveEndpoint).andReturn().asString();

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
        PostSavingsAccountsResponse savingsAccountResponse = objectMapper.readValue(
                gsmaTransferDef.responseSavingsAccount, PostSavingsAccountsResponse.class);
        gsmaConfig.savingsActivateEndpoint = gsmaConfig.savingsActivateEndpoint.replaceAll("\\{\\{savingsAccId\\}\\}", savingsAccountResponse.getSavingsId().toString());
        // Calling create loan account endpoint
        gsmaTransferDef.responseSavingsActivate = RestAssured.given(requestSpec).baseUri(gsmaConfig.savingsBaseUrl)
                .body(gsmaTransferDef.savingsActivateBody).expect().spec(new ResponseSpecBuilder().expectStatusCode(200).build()).when()
                .post(gsmaConfig.savingsActivateEndpoint).andReturn().asString();

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
        PostSavingsAccountsResponse savingsAccountResponse = objectMapper.readValue(
                gsmaTransferDef.responseSavingsAccount, PostSavingsAccountsResponse.class);
        gsmaConfig.savingsDepositAccountEndpoint = gsmaConfig.savingsDepositAccountEndpoint.replaceAll("\\{\\{savingsAccId\\}\\}", savingsAccountResponse.getSavingsId().toString());

        // Calling create loan account endpoint
        gsmaTransferDef.responseSavingsDepositAccount = RestAssured.given(requestSpec).baseUri(gsmaConfig.savingsBaseUrl)
                .body(gsmaTransferDef.savingsDepositAccountBody).expect().spec(new ResponseSpecBuilder().expectStatusCode(200).build())
                .when().post(gsmaConfig.savingsDepositAccountEndpoint).andReturn().asString();

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
        //Setting account ID
        PostSelfLoansLoanIdResponse loanAccountResponse = objectMapper.readValue(
                gsmaTransferDef.responseLoanAccount, PostSelfLoansLoanIdResponse.class);
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
        //Setting account ID
        PostSelfLoansLoanIdResponse loanAccountResponse = objectMapper.readValue(
                gsmaTransferDef.responseLoanAccount, PostSelfLoansLoanIdResponse.class);
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
        //Setting account ID
        PostSelfLoansLoanIdResponse loanAccountResponse = objectMapper.readValue(
                gsmaTransferDef.responseLoanAccount, PostSelfLoansLoanIdResponse.class);
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

    @Then("I call the channel connector API for savings account with expected status of {int}")
    public void sendRequestToGSMAEndpointSavings(int status) throws JsonProcessingException {
        RequestSpecification requestSpec = Utils.getDefaultSpec();
        requestSpec.header("amsName", gsmaTransferDef.amsName);
        requestSpec.header("accountHoldingInstitutionId", gsmaTransferDef.acccountHoldingInstitutionId);
        requestSpec.header(X_CORRELATIONID, "123456789");
        requestSpec.header(CONTENT_TYPE, CONTENT_TYPE_VALUE);

        gsmaTransferDef.gsmaTransferBody = gsmaTransferDef.setGsmaTransactionBody("S");

        gsmaTransferDef.gsmaTransactionResponse = RestAssured.given(requestSpec).baseUri(gsmaConfig.channelConnectorBaseUrl)
                .body(gsmaTransferDef.gsmaTransferBody).expect().spec(new ResponseSpecBuilder().expectStatusCode(status).build()).when()
                .post(gsmaConfig.gsmaEndpoint).andReturn().asString();

        logger.info("GSMA Transaction Response: " + gsmaTransferDef.gsmaTransactionResponse);
        assertThat(gsmaTransferDef.gsmaTransactionResponse).isNotEmpty();
    }

    @Then("I call the channel connector API for loan account with expected status of {int}")
    public void sendRequestToGSMAEndpointLoan(int status) throws JsonProcessingException {
        RequestSpecification requestSpec = Utils.getDefaultSpec();
        requestSpec.header("amsName", gsmaTransferDef.amsName);
        requestSpec.header("accountHoldingInstitutionId", gsmaTransferDef.acccountHoldingInstitutionId);
        requestSpec.header(X_CORRELATIONID, "123456789");
        requestSpec.header(CONTENT_TYPE, CONTENT_TYPE_VALUE);

        gsmaTransferDef.gsmaTransferBody = gsmaTransferDef.setGsmaTransactionBody("L");

        gsmaTransferDef.gsmaTransactionResponse = RestAssured.given(requestSpec).baseUri(gsmaConfig.channelConnectorBaseUrl)
                .body(gsmaTransferDef.gsmaTransferBody).expect().spec(new ResponseSpecBuilder().expectStatusCode(status).build()).when()
                .post(gsmaConfig.gsmaEndpoint).andReturn().asString();

        logger.info("GSMA Transaction Response: " + gsmaTransferDef.gsmaTransactionResponse);
        assertThat(gsmaTransferDef.gsmaTransactionResponse).isNotEmpty();
    }

    @Then("I create an IdentityMapperDTO for Register Beneficiary with identifier from previous step")
    public void iCreateAnIdentityMapperDTOForRegisterBeneficiaryWithIdentifierFromPreviousStep() {
        List<BeneficiaryDTO> beneficiaryDTOList = new ArrayList<>();
        payeeIdentity = generateUniqueNumber(16);
        BeneficiaryDTO beneficiaryDTO = new BeneficiaryDTO(payeeIdentity, "01", payer_identifier, "gorilla");
        beneficiaryDTOList.add(beneficiaryDTO);
        requestId = generateUniqueNumber(12);
        registerBeneficiaryBody = new AccountMapperRequestDTO(requestId, "", beneficiaryDTOList);
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
        BaseStepDef.response = RestAssured.given(requestSpec).header("Content-Type", "application/json").header("X-Registering-Institution-ID", registeringInstitutionId)
                .header("X-CallbackURL", identityMapperConfig.callbackURL + stub).baseUri(identityMapperConfig.identityMapperContactPoint)
                .body(registerBeneficiaryBody).expect().spec(new ResponseSpecBuilder().expectStatusCode(expectedStatus).build()).when()
                .post(identityMapperConfig.registerBeneficiaryEndpoint).andReturn().asString();


        logger.info("Identity Mapper Response: {}", BaseStepDef.response);
    }

    @Then("I call the account lookup API with expected status of {int} and callback stub {string}")
    public void iCallTheAccountLookupAPIWithExpectedStatusOfAndCallbackStub(int expectedStatus, String stub) {
        requestId = generateUniqueNumber(10);
        RequestSpecification requestSpec = Utils.getDefaultSpec();
        BaseStepDef.response = RestAssured.given(requestSpec).header("Content-Type", "application/json").header("X-Registering-Institution-ID", registeringInstitutionId)
                .header("X-CallbackURL", identityMapperConfig.callbackURL + stub).queryParam("payeeIdentity", payeeIdentity)
                .queryParam("paymentModality", "01").queryParam("requestId", requestId)
                .baseUri(identityMapperConfig.identityMapperContactPoint).expect()
                .spec(new ResponseSpecBuilder().expectStatusCode(expectedStatus).build()).when()
                .get(identityMapperConfig.accountLookupEndpoint).andReturn().asString();

        logger.info("Identity Mapper Response: {}", BaseStepDef.response);
    }

    @And("I should be able to verify that the {string} method to {string} endpoint received a request with validation")
    public void iShouldBeAbleToVerifyThatTheMethodToEndpointReceivedARequestWithValidation(String arg0, String endpoint) {
        List<ServeEvent> allServeEvents = getAllServeEvents();
        Boolean isValidated = null;

        for (int i = 0; i < allServeEvents.size(); i++) {
            ServeEvent request = allServeEvents.get(i);

            if (!(request.getRequest().getBodyAsString()).isEmpty()) {
                try {
                    JsonNode rootNode = objectMapper.readTree(request.getRequest().getBodyAsString());
                    String requestID = rootNode.get("requestId").asText();

                    if (requestId.equals(requestID)) {
                        callbackBody = request.getRequest().getBodyAsString();
                    }
                } catch (Exception e) {
                    e.printStackTrace();
                }

            }
        }
        try {
            JsonNode rootNode = objectMapper.readTree(callbackBody);
            isValidated = rootNode.get("isValidated").asBoolean();

        } catch (Exception e) {
            e.printStackTrace();
        }
        assertThat(isValidated).isTrue();
    }

    @When("I call the AMS Mifos Deposit Mock API with expected status of {int}")
    public void sendRequestToGSMADepositMockEndpoint(int status) throws JsonProcessingException {
        RequestSpecification requestSpec = Utils.getDefaultSpec();
        requestSpec.header(CONTENT_TYPE, CONTENT_TYPE_VALUE);
        String body = "{}";
        RestAssured.given(requestSpec).baseUri(gsmaConfig.amsMifosBasseUrl)
                .body(body).expect().spec(new ResponseSpecBuilder().expectStatusCode(status).build()).when()
                .post(gsmaConfig.savingsDepositAccountMockEndpoint).andReturn().asString();
    }

    @When("I call the AMS Mifos Loan Repayment Mock API with expected status of {int}")
    public void sendRequestToGSMALoanRepaymentMockEndpoint(int status) throws JsonProcessingException {
        RequestSpecification requestSpec = Utils.getDefaultSpec();
        requestSpec.header(CONTENT_TYPE, CONTENT_TYPE_VALUE);
        String body = "{}";
        RestAssured.given(requestSpec).baseUri(gsmaConfig.amsMifosBasseUrl)
                .body(body).expect().spec(new ResponseSpecBuilder().expectStatusCode(status).build()).when()
                .post(gsmaConfig.loanRepaymentMockEndpoint).andReturn().asString();
    }
}
