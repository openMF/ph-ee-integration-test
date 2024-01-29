package org.mifos.integrationtest.cucumber.stepdef;

import static com.github.tomakehurst.wiremock.client.WireMock.getAllServeEvents;
import static com.google.common.truth.Truth.assertThat;

import com.github.tomakehurst.wiremock.stubbing.ServeEvent;
import com.google.gson.JsonElement;
import com.google.gson.JsonObject;
import com.google.gson.JsonParser;
import io.cucumber.core.internal.com.fasterxml.jackson.core.JsonProcessingException;
import io.cucumber.java.en.And;
import io.cucumber.java.en.Given;
import io.cucumber.java.en.Then;
import io.cucumber.java.en.When;
import io.restassured.RestAssured;
import io.restassured.builder.ResponseSpecBuilder;
import io.restassured.specification.RequestSpecification;
import java.io.IOException;
import java.time.LocalDateTime;
import java.time.ZoneId;
import java.time.format.DateTimeFormatter;
import java.util.Date;
import java.util.List;
import java.util.UUID;
import org.apache.fineract.client.models.PostSavingsAccountsResponse;
import org.json.JSONException;
import org.mifos.connector.common.mojaloop.type.TransferState;
import org.mifos.integrationtest.common.CsvHelper;
import org.mifos.integrationtest.common.HttpMethod;
import org.mifos.integrationtest.common.TransferHelper;
import org.mifos.integrationtest.common.Utils;
import org.mifos.integrationtest.config.MojaloopConfig;
import org.mifos.integrationtest.config.PayerFundTransferConfig;
import org.springframework.beans.factory.annotation.Autowired;

public class PayerFundTransferStepDef extends BaseStepDef {

    @Autowired
    PayerFundTransferDef fundTransferDef;

    private static String payer_identifier;

    private static String payee_identifier;

    private static String quoteId;

    private static String quotationCallback;

    @Autowired
    PayerFundTransferConfig transferConfig;

    @Autowired
    MojaloopConfig mojaloopConfig;

    @Autowired
    MockServerStepDef mockServerStepDef;

    @Autowired
    CsvHelper csvHelper;

    DateTimeFormatter formatter = DateTimeFormatter.ofPattern("EEE, dd MMM yyyy HH:mm:ss 'GMT'");

    @Given("I have Fineract-Platform-TenantId for {string}")
    public void setTenantForPayer(String client) {
        String tenant;
        logger.info(client);
        if (client.equals("payer")) {
            tenant = transferConfig.payerTenant;
            fundTransferDef.setPayerTenant(tenant);
            scenarioScopeState.tenant = tenant;
        } else {
            tenant = transferConfig.payeeTenant;
            fundTransferDef.setPayeeTenant(tenant);
        }
        assertThat(tenant).isNotEmpty();
        fundTransferDef.setTenant(tenant);
        logger.info(tenant);
    }

    @When("I call the create client endpoint for {string}")
    public void callCreateClientEndpoint(String client) throws JsonProcessingException {
        RequestSpecification requestSpec = Utils.getDefaultSpec();
        requestSpec = fundTransferDef.setHeaders(requestSpec);

        logger.info(client);

        fundTransferDef.createClientBody = fundTransferDef.setBodyClient(client);
        // Calling savings product endpoint
        String clientResponse = RestAssured.given(requestSpec).baseUri(transferConfig.clientBaseUrl).body(fundTransferDef.createClientBody)
                .expect().spec(new ResponseSpecBuilder().expectStatusCode(200).build()).when().post(transferConfig.clientEndpoint)
                .andReturn().asString();

        if (client.equals("payer")) {
            fundTransferDef.responsePayerClient = clientResponse;
            assertThat(fundTransferDef.responsePayerClient).isNotEmpty();
        } else {
            fundTransferDef.responsePayeeClient = clientResponse;
            assertThat(fundTransferDef.responsePayeeClient).isNotEmpty();
        }
        logger.info("Create {} Client Response: {}", client, clientResponse);
    }

    @Then("I call the create savings product endpoint for {string}")
    public void callCreateSavingsProductEndpoint(String client) throws JsonProcessingException {
        // Setting headers and body
        RequestSpecification requestSpec = Utils.getDefaultSpec();
        requestSpec = fundTransferDef.setHeaders(requestSpec);
        fundTransferDef.savingsProductBody = fundTransferDef.setBodySavingsProduct();
        // Calling savings product endpoint
        fundTransferDef.responseSavingsProduct = RestAssured.given(requestSpec).baseUri(transferConfig.savingsBaseUrl)
                .body(fundTransferDef.savingsProductBody).expect().spec(new ResponseSpecBuilder().expectStatusCode(200).build()).when()
                .post(transferConfig.savingsProductEndpoint).andReturn().asString();

        logger.info("Savings Product Response: " + fundTransferDef.responseSavingsProduct);
        assertThat(fundTransferDef.responseSavingsProduct).isNotEmpty();
    }

    @When("I call the create savings account endpoint for {string}")
    public void callCreateSavingsAccountEndpoint(String client) throws JsonProcessingException {
        // Setting headers and body
        RequestSpecification requestSpec = Utils.getDefaultSpec();
        requestSpec = fundTransferDef.setHeaders(requestSpec);
        fundTransferDef.savingsAccountBody = fundTransferDef.setBodySavingsAccount(client);
        // Calling savings product endpoint
        String responseSavingsAccount = RestAssured.given(requestSpec).baseUri(transferConfig.savingsBaseUrl)
                .body(fundTransferDef.savingsAccountBody).expect().spec(new ResponseSpecBuilder().expectStatusCode(200).build()).when()
                .post(transferConfig.savingsAccountEndpoint).andReturn().asString();

        logger.info("Savings Account Response: " + responseSavingsAccount);

        if (client.equals("payer")) {
            fundTransferDef.responseSavingsAccountPayer = responseSavingsAccount;
            assertThat(fundTransferDef.responseSavingsAccountPayer).isNotEmpty();
        } else {
            fundTransferDef.responseSavingsAccountPayee = responseSavingsAccount;
            assertThat(fundTransferDef.responseSavingsAccountPayee).isNotEmpty();
        }
    }

    @Then("I call the interop identifier endpoint for {string}")
    public void callCreateInteropIdentifierEndpoint(String client) throws JsonProcessingException {
        // Setting headers and body
        RequestSpecification requestSpec = Utils.getDefaultSpec();
        requestSpec = fundTransferDef.setHeaders(requestSpec);
        fundTransferDef.interopIdentifierBody = fundTransferDef.setBodyInteropIdentifier();
        // Setting account ID in path

        String responseSavingsAccount = client.equals("payer") ? fundTransferDef.responseSavingsAccountPayer
                : fundTransferDef.responseSavingsAccountPayee;

        PostSavingsAccountsResponse savingsAccountResponse = objectMapper.readValue(responseSavingsAccount,
                PostSavingsAccountsResponse.class);
        String identifier = savingsAccountResponse.getSavingsId().toString();

        if (client.equals("payer")) {
            payer_identifier = identifier;
            scenarioScopeState.payerIdentifier = identifier;
        } else {
            payee_identifier = identifier;
            scenarioScopeState.payeeIdentifier = identifier;
        }

        String endpoint = transferConfig.interopIdentifierEndpoint;
        endpoint = endpoint.replaceAll("\\{\\{identifierType\\}\\}", "MSISDN");
        endpoint = endpoint.replaceAll("\\{\\{identifier\\}\\}", identifier);

        // Calling Interop Identifier endpoint
        fundTransferDef.responseInteropIdentifier = RestAssured.given(requestSpec).baseUri(transferConfig.savingsBaseUrl)
                .body(fundTransferDef.interopIdentifierBody).expect().spec(new ResponseSpecBuilder().expectStatusCode(200).build()).when()
                .post(endpoint).andReturn().asString();

        logger.info("Interop Identifier Response: " + fundTransferDef.responseInteropIdentifier);
        assertThat(fundTransferDef.responseInteropIdentifier).isNotEmpty();
    }

    @Then("I approve the deposit with command {string} for {string}")
    public void callApproveSavingsEndpoint(String command, String client) throws JsonProcessingException {
        // Setting headers and body
        RequestSpecification requestSpec = Utils.getDefaultSpec();
        requestSpec = fundTransferDef.setHeaders(requestSpec);
        requestSpec.queryParam("command", command);
        fundTransferDef.savingsApproveBody = fundTransferDef.setBodySavingsApprove();
        String endpoint = transferConfig.savingsApproveEndpoint;

        if (client.equals("payer")) {
            endpoint = endpoint.replaceAll("\\{\\{savingsAccId\\}\\}", payer_identifier);
        } else {
            endpoint = endpoint.replaceAll("\\{\\{savingsAccId\\}\\}", payee_identifier);
        }

        // Calling create loan account endpoint
        fundTransferDef.responseSavingsApprove = RestAssured.given(requestSpec).baseUri(transferConfig.savingsBaseUrl)
                .body(fundTransferDef.savingsApproveBody).expect().spec(new ResponseSpecBuilder().expectStatusCode(200).build()).when()
                .post(endpoint).andReturn().asString();

        logger.info("Savings Approve Response: " + fundTransferDef.responseSavingsApprove);
        assertThat(fundTransferDef.responseSavingsApprove).isNotEmpty();
    }

    @When("I activate the account with command {string} for {string}")
    public void callSavingsActivateEndpoint(String command, String client) throws JsonProcessingException {
        // Setting headers and body
        RequestSpecification requestSpec = Utils.getDefaultSpec();
        requestSpec = fundTransferDef.setHeaders(requestSpec);
        requestSpec.queryParam("command", command);
        fundTransferDef.savingsActivateBody = fundTransferDef.setBodySavingsActivate();

        String endpoint = transferConfig.savingsActivateEndpoint;
        if (client.equals("payer")) {
            endpoint = endpoint.replaceAll("\\{\\{savingsAccId\\}\\}", payer_identifier);
        } else {
            endpoint = endpoint.replaceAll("\\{\\{savingsAccId\\}\\}", payee_identifier);
        }
        // Calling create loan account endpoint
        fundTransferDef.responseSavingsActivate = RestAssured.given(requestSpec).baseUri(transferConfig.savingsBaseUrl)
                .body(fundTransferDef.savingsActivateBody).expect().spec(new ResponseSpecBuilder().expectStatusCode(200).build()).when()
                .post(endpoint).andReturn().asString();

        logger.info("Savings Activate Response: " + fundTransferDef.responseSavingsActivate);
        assertThat(fundTransferDef.responseSavingsActivate).isNotEmpty();
    }

    @Then("I call the deposit account endpoint with command {string} for amount {int} for {string}")
    public void callDepositAccountEndpoint(String command, int amount, String client) throws JsonProcessingException {
        // Setting headers and body
        RequestSpecification requestSpec = Utils.getDefaultSpec();
        requestSpec = fundTransferDef.setHeaders(requestSpec);
        requestSpec.queryParam("command", command);
        fundTransferDef.savingsDepositAccountBody = fundTransferDef.setSavingsDepositAccount(amount);
        // Setting account ID
        PostSavingsAccountsResponse savingsAccountResponse;
        if (client.equals("payer")) {
            savingsAccountResponse = objectMapper.readValue(fundTransferDef.responseSavingsAccountPayer, PostSavingsAccountsResponse.class);
        } else {
            savingsAccountResponse = objectMapper.readValue(fundTransferDef.responseSavingsAccountPayee, PostSavingsAccountsResponse.class);
        }

        String endpoint = transferConfig.savingsDepositAccountEndpoint.replaceAll("\\{\\{savingsAccId\\}\\}",
                savingsAccountResponse.getSavingsId().toString());

        // Calling create loan account endpoint
        fundTransferDef.responseSavingsDepositAccount = RestAssured.given(requestSpec).baseUri(transferConfig.savingsBaseUrl)
                .body(fundTransferDef.savingsDepositAccountBody).expect().spec(new ResponseSpecBuilder().expectStatusCode(200).build())
                .when().post(endpoint).andReturn().asString();

        logger.info("Savings Deposit Response: " + fundTransferDef.responseSavingsDepositAccount);
        assertThat(fundTransferDef.responseSavingsDepositAccount).isNotEmpty();
    }

    @Then("I can register the stub for callback endpoint of party lookup")
    public void registerStubForPartyLookup() {
        String endpoint = "parties/MSISDN/" + scenarioScopeState.payeeIdentifier;
        mockServerStepDef.startStub(endpoint, HttpMethod.PUT, 200);
    }

    @Then("I can register the stub for callback endpoint of quotation")
    public void registerStubForQuotation() {
        quoteId = UUID.randomUUID().toString();
        String endpoint = "quotes/" + quoteId;
        mockServerStepDef.startStub(endpoint, HttpMethod.POST, 200);
    }

    @Then("I can register the stub for callback endpoint of transfer")
    public void registerStubForTransfer() {
        quoteId = UUID.randomUUID().toString();
        String endpoint = "transfers/\\{id\\}";
        mockServerStepDef.startStub(endpoint, HttpMethod.PUT, 200);
    }

    @Then("I call the get parties api in ml connector for {string}")
    public void callGetPartiesApi(String client) {
        RequestSpecification requestSpec = Utils.getDefaultSpec();
        requestSpec.header("Accept", "application/vnd.interoperability.participants+json;version=1.0");
        requestSpec.header("Content-Type", "application/vnd.interoperability.parties+json;version=1.0");
        requestSpec.header("Date", new Date());
        requestSpec.header("Fspiop-Destination", mojaloopConfig.payeeFspId);
        requestSpec.header("Fspiop-Source", mojaloopConfig.payerFspId);
        requestSpec.header("partyId", scenarioScopeState.payeeIdentifier);
        requestSpec.header("partyIdType", "MSISDN");
        requestSpec.header("Traceparent", UUID.randomUUID());
        requestSpec.header("X-Lookup-Callback-Url", transferConfig.callbackURL);

        String identifier;
        if (client.equals("payer")) {
            identifier = scenarioScopeState.payerIdentifier;
        } else {
            identifier = scenarioScopeState.payeeIdentifier;
        }

        String endpoint = mojaloopConfig.mlConnectorGetPartyEndpoint;
        endpoint = endpoint.replaceAll("\\{\\{identifierType\\}\\}", "MSISDN");
        endpoint = endpoint.replaceAll("\\{\\{identifier\\}\\}", identifier);

        scenarioScopeState.response = RestAssured.given(requestSpec).baseUri("https://" + mojaloopConfig.mlConnectorHost).expect()
                .spec(new ResponseSpecBuilder().expectStatusCode(202).build()).when().get(endpoint).andReturn().asString();

        assertThat(scenarioScopeState.response).isNotNull();
    }

    @Then("I call the get quotation api in ml connector for {string}")
    public void callGetQuoteApi(String client) throws JsonProcessingException {
        RequestSpecification requestSpec = Utils.getDefaultSpec();
        requestSpec.header("Date", new Date());
        requestSpec.header("Traceparent", UUID.randomUUID());
        requestSpec.header("X-Quote-Callback-Url", transferConfig.callbackURL);

        String quoteRequestBody;
        if (client.equals("payer")) {
            quoteRequestBody = fundTransferDef.setBodyPayeeQuoteRequest(scenarioScopeState.payerIdentifier, "1234", "1", quoteId);
        } else {
            quoteRequestBody = fundTransferDef.setBodyPayeeQuoteRequest("1234", scenarioScopeState.payeeIdentifier, "1", quoteId);
        }

        scenarioScopeState.response = RestAssured.given(requestSpec).baseUri("https://" + mojaloopConfig.mlConnectorHost)
                .body(quoteRequestBody).expect().spec(new ResponseSpecBuilder().expectStatusCode(202).build()).when()
                .post(mojaloopConfig.mlConnectorGetQuoteEndpoint).andReturn().asString();

    }

    @Then("I call the transfer api in ml connector for {string}")
    public void callTransferApi(String client) throws JsonProcessingException {
        RequestSpecification requestSpec = Utils.getDefaultSpec();
        requestSpec.header("Date", LocalDateTime.now(ZoneId.of("GMT")).minusMinutes(2).format(formatter));
        requestSpec.header("Traceparent", UUID.randomUUID());
        requestSpec.header("X-Transfer-Callback-Url", transferConfig.callbackURL);

        JsonObject jsonObject = JsonParser.parseString(quotationCallback).getAsJsonObject();
        String ilpPacket = jsonObject.get("ilpPacket").getAsString();
        String condition = jsonObject.get("condition").getAsString();
        String transferRequestBody = fundTransferDef.setBodyPayeeTransferRequest("1", ilpPacket, condition);

        scenarioScopeState.response = RestAssured.given(requestSpec).baseUri("https://" + mojaloopConfig.mlConnectorHost)
                .body(transferRequestBody).expect().spec(new ResponseSpecBuilder().expectStatusCode(202).build()).when()
                .post(mojaloopConfig.mlConnectorTransferEndpoint).andReturn().asString();

    }

    @Then("I should be able to verify the callback for lookup")
    public void verifyGetPartyCallback() {
        List<ServeEvent> serveEvents = getAllServeEvents();
        logger.info(String.valueOf(serveEvents.size()));
        assertThat(serveEvents.size()).isGreaterThan(0);
        serveEvents.subList(0, 1).forEach(serveEvent -> {
            if (!serveEvent.getRequest().getBodyAsString().isEmpty()) {
                logger.info(serveEvent.getRequest().getBodyAsString());
            }
            JsonObject jsonObject = JsonParser.parseString(serveEvent.getRequest().getBodyAsString()).getAsJsonObject();
            String firstName = jsonObject.getAsJsonObject("party").getAsJsonObject("personalInfo").getAsJsonObject("complexName")
                    .get("firstName").getAsString();
            assertThat(firstName).isNotNull();
        });
    }

    @Then("I should be able to verify the callback for quotation")
    public void verifyGetQuotationCallback() {
        List<ServeEvent> serveEvents = getAllServeEvents();
        logger.info(String.valueOf(serveEvents.size()));
        assertThat(serveEvents.size()).isGreaterThan(0);
        serveEvents.subList(0, 1).forEach(serveEvent -> {
            if (!serveEvent.getRequest().getBodyAsString().isEmpty()) {
                logger.info(serveEvent.getRequest().getBodyAsString());
            }
            quotationCallback = serveEvent.getRequest().getBodyAsString();
            JsonObject jsonObject = JsonParser.parseString(serveEvent.getRequest().getBodyAsString()).getAsJsonObject();
            String amount = jsonObject.getAsJsonObject("payeeReceiveAmount").get("amount").getAsString();
            assertThat(amount).isEqualTo("1");
        });
    }

    @Then("I should be able to verify the callback for transfer")
    public void verifyGetTransferCallback() {
        List<ServeEvent> serveEvents = getAllServeEvents();
        logger.info(String.valueOf(serveEvents.size()));
        assertThat(serveEvents.size()).isGreaterThan(0);
        serveEvents.subList(0, 1).forEach(serveEvent -> {
            if (!serveEvent.getRequest().getBodyAsString().isEmpty()) {
                logger.info(serveEvent.getRequest().getBodyAsString());
            }
            JsonObject jsonObject = JsonParser.parseString(serveEvent.getRequest().getBodyAsString()).getAsJsonObject();
            String transferState = jsonObject.get("transferState").getAsString();
            assertThat(transferState).isEqualTo(TransferState.COMMITTED.toString());
        });
    }

    @Then("I call the payer fund transfer api to transfer amount {string} from payer to payee")
    public void payerFundTransfer(String amount) throws JSONException {

        RequestSpecification requestSpec = Utils.getDefaultSpec(transferConfig.payerTenant);
        requestSpec.header(Utils.X_CORRELATIONID, UUID.randomUUID());
        // requestSpec.header("Platform-TenantId", transferConfig.payerTenant);

        String requestBody = TransferHelper
                .getTransferRequestBody(scenarioScopeState.payerIdentifier, scenarioScopeState.payeeIdentifier, amount).toString();
        scenarioScopeState.response = RestAssured.given(requestSpec).baseUri(channelConnectorConfig.channelConnectorContactPoint)
                .body(requestBody).expect().spec(new ResponseSpecBuilder().expectStatusCode(200).build()).when()
                .post(channelConnectorConfig.transferEndpoint).andReturn().asString();

    }

    @When("I call the transfer API in ops app with transactionId as parameter")
    public void iCallTheTransferAPIWithTransactionId() throws InterruptedException {
        RequestSpecification requestSpec = Utils.getDefaultSpec(transferConfig.payerTenant);
        if (authEnabled) {
            requestSpec.header("Authorization", "Bearer " + scenarioScopeState.accessToken);
        }
        requestSpec.queryParam("transactionId", scenarioScopeState.transactionId);

        scenarioScopeState.response = RestAssured.given(requestSpec).baseUri(operationsAppConfig.operationAppContactPoint).expect()
                .spec(new ResponseSpecBuilder().expectStatusCode(200).build()).when().get(operationsAppConfig.transfersEndpoint).andReturn()
                .asString();

        logger.info(scenarioScopeState.transactionId);
        logger.info("Get Transfer Response: " + scenarioScopeState.response);
    }

    @Then("I check for error related to {}")
    public void checkForError(String action) {
        JsonObject jsonObject = JsonParser.parseString(scenarioScopeState.response).getAsJsonObject();

        JsonElement errorInformation = jsonObject.getAsJsonArray("content").get(0).getAsJsonObject().get("errorInformation");

        boolean actionError = (errorInformation != null) && (errorInformation.isJsonObject() || errorInformation.isJsonArray())
                && errorInformation.getAsString().contains(action);
        assertThat(actionError).isFalse();
    }

    @And("I assert the {} is {}")
    public void checkSubPartStatus(String variable, String expectedValue) {
        RequestSpecification requestSpec = Utils.getDefaultSpec(transferConfig.payerTenant);
        if (authEnabled) {
            requestSpec.header("Authorization", "Bearer " + scenarioScopeState.accessToken);
        }
        String endpoint = operationsAppConfig.variablesEndpoint + "/" + scenarioScopeState.transactionId;
        String response = RestAssured.given(requestSpec).baseUri(operationsAppConfig.operationAppContactPoint).expect()
                .spec(new ResponseSpecBuilder().expectStatusCode(200).build()).when().get(endpoint).andReturn().asString();

        JsonObject jsonObject = JsonParser.parseString(response).getAsJsonObject();
        assertThat(jsonObject.get(variable)).isNotNull();

        if (jsonObject.get(variable) != null) {
            String status = jsonObject.get(variable).getAsString();
            assertThat(status).isEqualTo(expectedValue);
        }
    }

    @Then("I assert {string} balance to be {long}")
    public void getCurrentBalance(String client, Long amount) throws JsonProcessingException {
        RequestSpecification requestSpec = Utils.getDefaultSpec();
        fundTransferDef.tenant = fundTransferDef.payerTenant;
        requestSpec = fundTransferDef.setHeaders(requestSpec);
        // Setting account ID in path
        PostSavingsAccountsResponse savingsAccountResponse;
        if (client.equals("payer")) {
            savingsAccountResponse = objectMapper.readValue(fundTransferDef.responseSavingsAccountPayer, PostSavingsAccountsResponse.class);
        } else {
            savingsAccountResponse = objectMapper.readValue(fundTransferDef.responseSavingsAccountPayee, PostSavingsAccountsResponse.class);
        }
        transferConfig.savingsApproveEndpoint = transferConfig.savingsApproveEndpoint.replaceAll("\\{\\{savingsAccId\\}\\}",
                savingsAccountResponse.getSavingsId().toString());

        logger.info(transferConfig.savingsApproveEndpoint);
        String responseBody = RestAssured.given(requestSpec).baseUri(transferConfig.savingsBaseUrl).expect()
                .spec(new ResponseSpecBuilder().expectStatusCode(200).build()).when().get(transferConfig.savingsApproveEndpoint).andReturn()
                .asString();

        JsonObject jsonObject = JsonParser.parseString(responseBody).getAsJsonObject();

        scenarioScopeState.currentBalance = jsonObject.get("summary").getAsJsonObject().get("accountBalance").getAsLong();
        logger.info(String.valueOf(scenarioScopeState.currentBalance));
        assertThat(scenarioScopeState.currentBalance).isEqualTo(amount);
    }

    @When("I create and setup a {string} with account balance of {int}")
    public void consolidatedPayerCreationSteps(String client, int amount) throws JsonProcessingException {
        setTenantForPayer(client);
        callCreateClientEndpoint(client);
        callCreateSavingsProductEndpoint(client);
        callCreateSavingsAccountEndpoint(client);
        callCreateInteropIdentifierEndpoint(client);
        callApproveSavingsEndpoint("approve", client);
        callSavingsActivateEndpoint("activate", client);
        callDepositAccountEndpoint("deposit", amount, client);
    }

    @Then("Create a csv file with file name {string}")
    public void createCsvWithHeaders(String fileName) throws IOException {
        String filePath = Utils.getAbsoluteFilePathToResource(fileName);
        String[] header = { "id", "request_id", "payment_mode", "payer_identifier_type", "payer_identifier", "payee_identifier_type",
                "payee_identifier", "amount", "currency", "note" };
        scenarioScopeState.filename = fileName;
        csvHelper.createCsvFileWithHeaders(filePath, header);
    }

    @Then("add row to csv with current payer and payee and transfer amount {int} and id {int}")
    public void addRowToCsvFile(int transferAmount, int id) throws IOException {

        String[] row = { String.valueOf(id), UUID.randomUUID().toString(), "mojaloop", "msisdn", scenarioScopeState.payerIdentifier, "msisdn",
                scenarioScopeState.payeeIdentifier, String.valueOf(transferAmount), "USD", "Test Payee Payment" };
        String filePath = Utils.getAbsoluteFilePathToResource(scenarioScopeState.filename);
        csvHelper.addRow(filePath, row);
    }

    @Then("add last row to csv with current payer and payee and transfer amount {int} and id {int}")
    public void addLastRowToCsvFile(int transferAmount, int id) throws IOException {

        String[] row = { String.valueOf(id), UUID.randomUUID().toString(), "mojaloop", "msisdn", scenarioScopeState.payerIdentifier, "msisdn",
                scenarioScopeState.payeeIdentifier, String.valueOf(transferAmount), "USD", "Test Payee Payment" };
        String filePath = Utils.getAbsoluteFilePathToResource(scenarioScopeState.filename);
        csvHelper.addLastRow(filePath, row);
    }

}
