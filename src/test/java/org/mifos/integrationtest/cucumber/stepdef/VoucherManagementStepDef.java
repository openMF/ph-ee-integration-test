package org.mifos.integrationtest.cucumber.stepdef;

import static com.github.tomakehurst.wiremock.client.WireMock.equalTo;
import static com.github.tomakehurst.wiremock.client.WireMock.getAllServeEvents;
import static com.github.tomakehurst.wiremock.client.WireMock.matchingJsonPath;
import static com.github.tomakehurst.wiremock.client.WireMock.putRequestedFor;
import static com.github.tomakehurst.wiremock.client.WireMock.urlEqualTo;
import static com.github.tomakehurst.wiremock.client.WireMock.verify;
import static com.google.common.truth.Truth.assertThat;
import static java.util.concurrent.TimeUnit.SECONDS;
import static org.awaitility.Awaitility.await;
import static org.junit.jupiter.api.Assertions.assertTrue;
import static org.mifos.integrationtest.common.HttpMethod.PUT;

import com.github.tomakehurst.wiremock.client.VerificationException;
import com.github.tomakehurst.wiremock.stubbing.ServeEvent;
import com.opencsv.CSVWriter;
import io.cucumber.core.internal.com.fasterxml.jackson.core.JsonProcessingException;
import io.cucumber.core.internal.com.fasterxml.jackson.databind.JsonNode;
import io.cucumber.core.internal.com.fasterxml.jackson.databind.ObjectMapper;
import io.cucumber.core.internal.com.fasterxml.jackson.databind.node.ObjectNode;
import io.cucumber.java.After;
import io.cucumber.java.Before;
import io.cucumber.java.en.And;
import io.cucumber.java.en.Given;
import io.cucumber.java.en.Then;
import io.cucumber.java.en.When;
import io.restassured.RestAssured;
import io.restassured.builder.ResponseSpecBuilder;
import io.restassured.specification.RequestSpecification;
import java.io.File;
import java.io.FileWriter;
import java.io.IOException;
import java.math.BigDecimal;
import java.util.ArrayList;
import java.util.List;
import java.util.Random;
import org.mifos.integrationtest.common.Utils;
import org.mifos.integrationtest.common.dto.ErrorDetails;
import org.mifos.integrationtest.common.dto.voucher.RedeemVoucherRequestDTO;
import org.mifos.integrationtest.common.dto.voucher.RequestDTO;
import org.mifos.integrationtest.common.dto.voucher.VoucherInstruction;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;

public class VoucherManagementStepDef extends BaseStepDef {

    @Autowired
    MockServerStepDef mockServerStepDef;

    @Autowired
    ScenarioScopeState scenarioScopeState;

    Logger logger = LoggerFactory.getLogger(VoucherManagementStepDef.class);

    @Given("I can create an VoucherRequestDTO for voucher creation")
    public void iCreateAnIdentityMapperDTOForRegisterBeneficiary() {
        scenarioScopeState.requestId = generateUniqueNumber(12);
        RequestDTO voucherDTO = new RequestDTO();
        voucherDTO.setRequestID(scenarioScopeState.requestId);
        scenarioScopeState.batchId = generateUniqueNumber(10);
        voucherDTO.setBatchID(scenarioScopeState.batchId);

        VoucherInstruction voucherInstruction = new VoucherInstruction();
        voucherInstruction.setInstructionID(generateUniqueNumber(16));
        voucherInstruction.setGroupCode("021");
        voucherInstruction.setCurrency("SGD");
        voucherInstruction.setAmount(BigDecimal.valueOf(9000));
        voucherInstruction.setPayeeFunctionalID("63310590322288932682");
        voucherInstruction.setNarration("Social Support Payment for the Month of Jan");

        ArrayList<VoucherInstruction> voucherInstructions = new ArrayList<>();
        voucherInstructions.add(voucherInstruction);

        voucherDTO.setVoucherInstructions(voucherInstructions);

        ObjectMapper objectMapper = new ObjectMapper();
        try {
            scenarioScopeState.createVoucherBody = objectMapper.writeValueAsString(voucherDTO);
        } catch (JsonProcessingException e) {
            logger.error("Unable to convert the DTO : {}", e);
        }
    }

    @When("I call the create voucher API with expected status of {int} and stub {string}")
    public void iCallTheVoucherCreateAPIWithExpectedStatusOf(int expectedStatus, String stub) {
        RequestSpecification requestSpec = Utils.getDefaultSpec();
        scenarioScopeState.registeringInstitutionId = "SocialWelfare";
        scenarioScopeState.response = RestAssured.given(requestSpec).header("Content-Type", "application/json")
                .header("X-CallbackURL", identityMapperConfig.callbackURL + stub)
                .header("X-Registering-Institution-ID", scenarioScopeState.registeringInstitutionId)
                .baseUri(voucherManagementConfig.voucherManagementContactPoint).body(scenarioScopeState.createVoucherBody).expect()
                .spec(new ResponseSpecBuilder().expectStatusCode(expectedStatus).build()).when()
                .post(voucherManagementConfig.createVoucherEndpoint).andReturn().asString();

        logger.info("Create Voucher Response: {}", scenarioScopeState.response);
    }

    public static String generateUniqueNumber(int length) {
        Random rand = new Random();
        long timestamp = System.currentTimeMillis();
        long randomLong = rand.nextLong(100000000);
        String uniqueNumber = timestamp + "" + randomLong;
        return uniqueNumber.substring(0, length);
    }

    @When("I can create an VoucherRequestDTO for voucher activation")
    public void iCanCreateAnVoucherRequestDTOForVoucherActivation() {

        VoucherInstruction voucherInstruction = new VoucherInstruction();
        voucherInstruction.setSerialNumber(scenarioScopeState.serialNumber);
        voucherInstruction.setStatus("02");

        ArrayList<VoucherInstruction> voucherInstructions = new ArrayList<>();
        voucherInstructions.add(voucherInstruction);

        RequestDTO requestDTO = new RequestDTO(scenarioScopeState.requestId, scenarioScopeState.batchId, voucherInstructions);
        ObjectMapper objectMapper = new ObjectMapper();
        try {
            scenarioScopeState.activateVoucherBody = objectMapper.writeValueAsString(requestDTO);
        } catch (JsonProcessingException e) {
            logger.error("Unable to convert the DTO : {}", e);
        }
    }

    @When("I call the activate voucher API with expected status of {int} and stub {string}")
    public void iCallTheActivateVoucherAPIWithExpectedStatusOfAndStub(int expectedStatus, String stub) {
        RequestSpecification requestSpec = Utils.getDefaultSpec();
        scenarioScopeState.response = RestAssured.given(requestSpec).header("Content-Type", "application/json")
                .header("X-CallbackURL", identityMapperConfig.callbackURL + stub)
                .header("X-Registering-Institution-ID", scenarioScopeState.registeringInstitutionId).header("X-Program-ID", "")
                .queryParam("command", "activate").baseUri(voucherManagementConfig.voucherManagementContactPoint)
                .body(scenarioScopeState.activateVoucherBody).expect()
                .spec(new ResponseSpecBuilder().expectStatusCode(expectedStatus).build()).when()
                .put(voucherManagementConfig.voucherLifecycleEndpoint).andReturn().asString();

        logger.info("Activate Voucher Response: {}", scenarioScopeState.response);
    }

    @When("I can create an VoucherRequestDTO for voucher cancellation")
    public void iCanCreateAnVoucherRequestDTOForVoucherCancellation() {
        VoucherInstruction voucherInstruction = new VoucherInstruction();
        voucherInstruction.setSerialNumber(scenarioScopeState.serialNumber);
        voucherInstruction.setStatus("03");

        ArrayList<VoucherInstruction> voucherInstructions = new ArrayList<>();
        voucherInstructions.add(voucherInstruction);

        RequestDTO requestDTO = new RequestDTO(scenarioScopeState.requestId, scenarioScopeState.batchId, voucherInstructions);
        ObjectMapper objectMapper = new ObjectMapper();
        try {
            scenarioScopeState.cancelVoucherBody = objectMapper.writeValueAsString(requestDTO);
        } catch (JsonProcessingException e) {
            logger.error("Unable to convert the DTO : {}", e);
        }
    }

    @Then("I should be able to verify that the {string} method to {string} endpoint received a request with required parameter in cancel voucher callback body")
    public void iShouldBeAbleToVerifyThatTheMethodToEndpointReceivedARequestWithRequiredParameterInCancelVoucherCallbackBody(String arg0,
            String endpoint) {
        verify(putRequestedFor(urlEqualTo(endpoint))
                .withRequestBody(matchingJsonPath("$.requestID", equalTo(scenarioScopeState.requstId))));

    }

    @When("I call the cancel voucher API with expected status of {int} and stub {string}")
    public void iCallTheCancelVoucherAPIWithExpectedStatusOfAndStub(int expectedStatus, String stub) {
        RequestSpecification requestSpec = Utils.getDefaultSpec();
        scenarioScopeState.response = RestAssured.given(requestSpec).header("Content-Type", "application/json")
                .header("X-CallbackURL", identityMapperConfig.callbackURL + stub)
                .header("X-Registering-Institution-ID", scenarioScopeState.registeringInstitutionId).header("X-Program-ID", "")
                .queryParam("command", "cancel").baseUri(voucherManagementConfig.voucherManagementContactPoint)
                .body(scenarioScopeState.cancelVoucherBody).expect()
                .spec(new ResponseSpecBuilder().expectStatusCode(expectedStatus).build()).when()
                .post(voucherManagementConfig.voucherLifecycleEndpoint).andReturn().asString();

        logger.info("Voucher Response: {}", scenarioScopeState.response);
    }

    @Then("I should be able to verify that the {string} method to {string} endpoint received a request with required parameter in redeem voucher callback body")
    public void iShouldBeAbleToVerifyThatTheMethodToEndpointReceivedARequestWithRequiredParameterInRedeemVoucherCallbackBody(String arg0,
            String endpoint) {
        verify(putRequestedFor(urlEqualTo(endpoint))
                .withRequestBody(matchingJsonPath("$.requestID", equalTo(scenarioScopeState.requstId))));
    }

    @Then("I should be able to verify that the {string} method to {string} endpoint received a request with required parameter in suspend voucher callback body")
    public void iShouldBeAbleToVerifyThatTheMethodToEndpointReceivedARequestWithRequiredParameterInSuspendVoucherCallbackBody(String arg0,
            String endpoint) {
        verify(putRequestedFor(urlEqualTo(endpoint))
                .withRequestBody(matchingJsonPath("$.requestID", equalTo(scenarioScopeState.requstId))));
    }

    @Then("I should be able to extract response body from callback")
    public void iShouldBeAbleToExtractResponseBodyFromCallback() {
        await().atMost(awaitMost, SECONDS).untilAsserted(() -> {
            List<ServeEvent> allServeEvents = getAllServeEvents();

            for (int i = 0; i < allServeEvents.size(); i++) {
                ServeEvent request = allServeEvents.get(i);

                if (!(request.getRequest().getBodyAsString()).isEmpty()) {
                    JsonNode rootNode = null;
                    try {
                        rootNode = objectMapper.readTree(request.getRequest().getBodyAsString());
                    } catch (JsonProcessingException e) {
                        throw new RuntimeException(e);
                    }
                    String requestID = null;
                    if (rootNode.has("requestID")) {
                        requestID = rootNode.get("requestID").asText();
                    }
                    if (rootNode.has("batchID")) {
                        scenarioScopeState.batchId = rootNode.get("batchID").asText();
                    }
                    if (scenarioScopeState.requestId.equals(requestID)) {
                        scenarioScopeState.callbackBody = request.getRequest().getBodyAsString();
                    }

                }
            }

            try {
                // ObjectMapper objectMapper = new ObjectMapper();
                JsonNode rootNode = objectMapper.readTree(scenarioScopeState.callbackBody);

                JsonNode voucherInstructionsNode = rootNode.get("voucherInstructions");
                if (voucherInstructionsNode.isArray()) {
                    for (JsonNode voucherNode : voucherInstructionsNode) {
                        scenarioScopeState.serialNumber = voucherNode.get("serialNumber").asText();
                        scenarioScopeState.voucherNumber = voucherNode.get("voucherNumber").asText();
                    }
                }
            } catch (Exception e) {
                logger.debug(e.getMessage());
            }
            assertThat(scenarioScopeState.serialNumber).isNotEmpty();
        });
    }

    @Then("I add voucher serial number and voucher number in csv file")
    public void addVoucherSerialNumberandVoucherNumberInCSV(String file) {
        createOrAppendCSVFile(file, new String[] { "voucherNumber", "serialNumber" },
                new String[] { scenarioScopeState.voucherNumber, scenarioScopeState.serialNumber });
    }

    public void createOrAppendCSVFile(String filePath, String[] header, String[] data) {
        File file = new File(filePath);

        try (CSVWriter writer = new CSVWriter(new FileWriter(file, true))) {
            // If the file doesn't exist, write the header
            if (!file.exists() || file.length() == 0) {
                writer.writeNext(header);
            }

            // Write data
            writer.writeNext(data);
        } catch (IOException e) {
            logger.debug("Error: {}", e.getMessage());
        }
    }

    @Then("I call the create, Activate voucher API and store it in {string}")
    public void createActivateVoucherInBulkAndCreateCSVFile(String file) {
        logger.info("Creating and activating {} vouchers", totalVouchers);
        for (int i = 0; i < totalVouchers; i++) {
            iCreateAnIdentityMapperDTOForRegisterBeneficiary();
            iCallTheVoucherCreateAPIWithExpectedStatusOf(202, "/createVoucher");
            try {
                Thread.sleep(2000);
            } catch (InterruptedException e) {
                throw new RuntimeException(e);
            }
            iShouldBeAbleToExtractResponseBodyFromCallback();
            addVoucherSerialNumberandVoucherNumberInCSV(file);
            iCanCreateAnVoucherRequestDTOForVoucherActivation();
            iCallTheActivateVoucherAPIWithExpectedStatusOfAndStub(202, "/activateVoucher");
        }
    }

    @Given("I can create an RedeemVoucherRequestDTO for voucher redemption")
    public void iCanCreateAnRedeemVoucherRequestDTOForVoucherRedemption() {
        scenarioScopeState.agentId = generateUniqueNumber(10);

        RedeemVoucherRequestDTO requestDTO = new RedeemVoucherRequestDTO(scenarioScopeState.requestId, scenarioScopeState.agentId,
                scenarioScopeState.serialNumber, scenarioScopeState.voucherNumber);

        ObjectMapper objectMapper = new ObjectMapper();
        try {
            scenarioScopeState.redeemVoucherBody = objectMapper.writeValueAsString(requestDTO);
        } catch (JsonProcessingException e) {
            logger.error("Unable to convert the DTO : {}", e);
        }
    }

    @When("I call the redeem voucher API with expected status of {int}")
    public void iCallTheRedeemVoucherAPIWithExpectedStatusOf(int responseCode) {
        RequestSpecification requestSpec = Utils.getDefaultSpec();
        scenarioScopeState.response = RestAssured.given(requestSpec).header("Content-Type", "application/json")
                .queryParam("command", "redeem").header("X-Registering-Institution-ID", scenarioScopeState.registeringInstitutionId)
                .header("X-CallbackURL", "").header("X-Program-ID", "").baseUri(voucherManagementConfig.voucherManagementContactPoint)
                .body(scenarioScopeState.redeemVoucherBody).expect().spec(new ResponseSpecBuilder().expectStatusCode(responseCode).build())
                .when().post(voucherManagementConfig.voucherLifecycleEndpoint).andReturn().asString();

        scenarioScopeState.redeemVoucherResponseBody = scenarioScopeState.response;
        logger.info("Redeem Voucher Response: {}", scenarioScopeState.response);
    }

    @Then("I can assert that redemption was successful by asserting the status in response")
    public void iCanAssertThatRedemptionWasSuccessfulByAssertingTheStatusInResponse() {
        await().atMost(awaitMost, SECONDS).pollDelay(pollDelay, SECONDS).pollInterval(pollInterval, SECONDS).untilAsserted(() -> {

            try {
                JsonNode rootNode = objectMapper.readTree(scenarioScopeState.redeemVoucherResponseBody);

                String status = rootNode.get("status").asText();
                logger.info("Status {}", status);
                assertThat(status).isEqualTo("01");
                logger.info("Response for successful redemption {}:", scenarioScopeState.redeemVoucherResponseBody);
            } catch (Exception e) {
                logger.debug(e.getMessage());
            }

        });
    }

    public void assertUnsuccessfulRedemption() {
        try {
            JsonNode rootNode = objectMapper.readTree(scenarioScopeState.redeemVoucherResponseBody);

            String status = rootNode.get("status").asText();
            assertThat(status).isEqualTo("00");
        } catch (Exception e) {
            logger.debug(e.getMessage());
        }
    }

    @Before("@createAndActivateVoucher")
    public void createAndActivateVoucher() {
        iCreateAnIdentityMapperDTOForRegisterBeneficiary();
        mockServerStepDef.checkIfMockServerIsInjected();
        mockServerStepDef.startMockServer();
        mockServerStepDef.startStub("/createVoucher", PUT, 200);
        mockServerStepDef.startStub("/activateVoucher", PUT, 200);
        iCallTheVoucherCreateAPIWithExpectedStatusOf(202, "/createVoucher");
        iShouldBeAbleToExtractResponseBodyFromCallback();
        iCanCreateAnVoucherRequestDTOForVoucherActivation();
        iCallTheActivateVoucherAPIWithExpectedStatusOfAndStub(202, "/activateVoucher");
    }

    @Before("@createVoucher")
    public void createVoucher() {
        iCreateAnIdentityMapperDTOForRegisterBeneficiary();
        mockServerStepDef.checkIfMockServerIsInjected();
        mockServerStepDef.startMockServer();
        mockServerStepDef.startStub("/createVoucher", PUT, 200);
        mockServerStepDef.startStub("/activateVoucher", PUT, 200);
        iCallTheVoucherCreateAPIWithExpectedStatusOf(202, "/createVoucher");
        iShouldBeAbleToExtractResponseBodyFromCallback();
    }

    @After("@redeemVoucherFailure")
    public void redeemVoucherFailure() {
        iCanCreateAnRedeemVoucherRequestDTOForVoucherRedemption();
        iCallTheRedeemVoucherAPIWithExpectedStatusOf(200);
        assertUnsuccessfulRedemption();
    }

    @After("@redeemVoucherSuccess")
    public void redeemVoucherSuccess() {
        iCanCreateAnRedeemVoucherRequestDTOForVoucherRedemption();
        iCallTheRedeemVoucherAPIWithExpectedStatusOf(200);
        iCanAssertThatRedemptionWasSuccessfulByAssertingTheStatusInResponse();
    }

    @Given("I can create an VoucherRequestDTO for voucher suspension")
    public void iCanCreateAnVoucherRequestDTOForVoucherSuspension() {
        VoucherInstruction voucherInstruction = new VoucherInstruction();
        voucherInstruction.setSerialNumber(scenarioScopeState.serialNumber);
        voucherInstruction.setStatus("06");

        ArrayList<VoucherInstruction> voucherInstructions = new ArrayList<>();
        voucherInstructions.add(voucherInstruction);

        RequestDTO requestDTO = new RequestDTO(scenarioScopeState.requestId, scenarioScopeState.batchId, voucherInstructions);
        ObjectMapper objectMapper = new ObjectMapper();
        try {
            scenarioScopeState.suspendVoucherBody = objectMapper.writeValueAsString(requestDTO);
        } catch (JsonProcessingException e) {
            logger.error("Unable to convert the DTO : {}", e);
        }
    }

    @When("I call the suspend voucher API with expected status of {int} and stub {string}")
    public void iCallTheSuspendVoucherAPIWithExpectedStatusOfAndStub(int responseCode, String stub) {
        RequestSpecification requestSpec = Utils.getDefaultSpec();
        scenarioScopeState.response = RestAssured.given(requestSpec).header("Content-Type", "application/json")
                .queryParam("command", "suspend").header("X-Registering-Institution-ID", scenarioScopeState.registeringInstitutionId)
                .header("X-CallbackURL", identityMapperConfig.callbackURL + stub).header("X-Program-ID", "")
                .baseUri(voucherManagementConfig.voucherManagementContactPoint).body(scenarioScopeState.suspendVoucherBody).expect()
                .spec(new ResponseSpecBuilder().expectStatusCode(responseCode).build()).when()
                .put(voucherManagementConfig.voucherLifecycleEndpoint).andReturn().asString();

        scenarioScopeState.redeemVoucherResponseBody = scenarioScopeState.response;
        logger.info("Suspend Voucher Response: {}", scenarioScopeState.response);
    }

    @And("I can create an VoucherRequestDTO for voucher reactivation")
    public void iCanCreateAnVoucherRequestDTOForVoucherReactivation() {
        StringBuilder sb = new StringBuilder();
        sb.append("{\n");
        sb.append("    \"requestID\": \"").append(scenarioScopeState.requestId).append("\",\n");
        sb.append("    \"batchID\": \"").append(scenarioScopeState.batchId).append("\",\n"); // Replaced "045155518258"
                                                                                             // with batchId
        // variable
        sb.append("    \"voucherInstructions\": [\n");
        sb.append("        {\n");
        sb.append("            \"serialNumber\": \"").append(scenarioScopeState.serialNumber).append("\",\n");
        sb.append("            \"status\": \"02\"\n");
        sb.append("        }\n");
        sb.append("    ]\n");
        sb.append("}");

        scenarioScopeState.suspendVoucherBody = sb.toString();
    }

    @When("I call the validity check API with expected status of {int} and stub {string}")
    public void iCallTheValidityCheckAPIWithExpectedStatusOfAndStub(int responseCode, String stub) {
        RequestSpecification requestSpec = Utils.getDefaultSpec();
        scenarioScopeState.response = RestAssured.given(requestSpec).header("Content-Type", "application/json")
                .queryParam("serialNumber", scenarioScopeState.serialNumber).queryParam("isValid", "true")
                .header("X-CallbackURL", identityMapperConfig.callbackURL + stub)
                .header("X-Registering-Institution-ID", scenarioScopeState.registeringInstitutionId)
                .baseUri(voucherManagementConfig.voucherManagementContactPoint).expect()
                .spec(new ResponseSpecBuilder().expectStatusCode(responseCode).build()).when()
                .get(voucherManagementConfig.voucherValidityEndpoint).andReturn().asString();

        scenarioScopeState.redeemVoucherResponseBody = scenarioScopeState.response;
        logger.info("Validity Voucher Response: {}", scenarioScopeState.response);
    }

    @And("I can extract result from validation callback and assert if validation is successful on {string}")
    public void iCanExtractResultFromValidationCallbackAndAssertIfValidationIsSuccessful(String endpoint) {
        await().atMost(awaitMost, SECONDS).pollDelay(pollDelay, SECONDS).pollInterval(pollInterval, SECONDS).untilAsserted(() -> {

            // (putRequestedFor(urlEqualTo(endpoint)).withRequestBody(matchingJsonPath("$.isValid", equalTo("true"))));
            List<ServeEvent> allServeEvents = getAllServeEvents();
            String serialNo = null;
            String isValid = null;
            for (int i = 0; i < allServeEvents.size(); i++) {
                ServeEvent request = allServeEvents.get(i);

                if (!(request.getRequest().getBodyAsString()).isEmpty()) {
                    JsonNode rootNode = null;
                    try {
                        rootNode = objectMapper.readTree(request.getRequest().getBodyAsString());
                    } catch (JsonProcessingException e) {
                        throw new RuntimeException(e);
                    }

                    if (rootNode.has("serialNumber")) {
                        serialNo = rootNode.get("serialNumber").asText();
                        isValid = rootNode.get("isValid").asText();
                    }
                }
            }
            assertThat(isValid).isEqualTo("true");
        });
    }

    @Then("I can assert that redemption was unsuccessful by asserting the status in response")
    public void iCanAssertThatRedemptionWasUnsuccessfulByAssertingTheStatusInResponse() {
        try {
            JsonNode rootNode = objectMapper.readTree(scenarioScopeState.redeemVoucherResponseBody);

            String status = rootNode.get("status").asText();
            assertThat(status).isEqualTo("00");
        } catch (Exception e) {
            logger.debug(e.getMessage());
        }
    }

    @Then("I should be able to assert response body from callback on {string}")
    public void iShouldBeAbleToAssertResponseBodyFromCallback(String endpoint) {
        await().atMost(awaitMost, SECONDS).untilAsserted(() -> {
            try {
                verify(putRequestedFor(urlEqualTo(endpoint))
                        .withRequestBody(matchingJsonPath("$.registerRequestID", equalTo(scenarioScopeState.requestId))));
                verify(putRequestedFor(urlEqualTo(endpoint)).withRequestBody(matchingJsonPath("$.numberFailedCases", equalTo("0"))));
                assertTrue(true);// success
            } catch (VerificationException e) {
                assertTrue(false);// failure
            }
        });
    }

    @Then("I will call the fetch voucher API with expected status of {int}")
    public void iWillCallTheFetchVoucherAPIWithExpectedStatusOf(int responseCode) {
        await().atMost(awaitMost, SECONDS).pollDelay(pollDelay, SECONDS).pollInterval(pollInterval, SECONDS).untilAsserted(() -> {
            RequestSpecification requestSpec = Utils.getDefaultSpec();
            scenarioScopeState.response = RestAssured.given(requestSpec).header("Content-Type", "application/json")
                    .header("X-Registering-Institution-ID", scenarioScopeState.registeringInstitutionId)
                    .baseUri(voucherManagementConfig.voucherManagementContactPoint).expect()
                    .spec(new ResponseSpecBuilder().expectStatusCode(responseCode).build()).when()
                    .get(voucherManagementConfig.fetchVoucherEndpoint + "/" + scenarioScopeState.serialNumber).andReturn().asString();

            scenarioScopeState.fetchVoucherResponseBody = scenarioScopeState.response;
            logger.info("Voucher Response: {}", scenarioScopeState.response);
        });
    }

    @And("I will assert the fields from fetch voucher response")
    public void iWillAssertTheFieldsFromFetchVoucherResponse() {
        try {
            JsonNode rootNode = objectMapper.readTree(scenarioScopeState.fetchVoucherResponseBody);

            String serialNumberResponse = rootNode.get("serialNumber").asText();
            String registeringInstitutionIdResponse = rootNode.get("registeringInstitutionId").asText();
            assertThat(serialNumberResponse).isEqualTo(scenarioScopeState.serialNumber);
            assertThat(registeringInstitutionIdResponse).isEqualTo(scenarioScopeState.registeringInstitutionId);

        } catch (Exception e) {
            logger.debug(e.getMessage());
        }
    }

    @Given("I can create an negative VoucherRequestDTO for voucher creation")
    public void createNegativeVoucherRequestDTO() {
        scenarioScopeState.requestId = generateUniqueNumber(18);

        RequestDTO requestDTO = new RequestDTO();
        requestDTO.setRequestID(scenarioScopeState.requestId);
        requestDTO.setBatchID(generateUniqueNumber(10));

        VoucherInstruction voucherInstruction = new VoucherInstruction();
        voucherInstruction.setInstructionID(generateUniqueNumber(16));
        voucherInstruction.setGroupCode("0215");
        voucherInstruction.setCurrency("SGDP");
        voucherInstruction.setAmount(BigDecimal.valueOf(-9000));
        voucherInstruction.setPayeeFunctionalID("6331059032228893278594709682");
        voucherInstruction.setNarration("Social Support Payment for the Month of Jan");

        ArrayList<VoucherInstruction> voucherInstructions = new ArrayList<>();
        voucherInstructions.add(voucherInstruction);
        requestDTO.setVoucherInstructions(voucherInstructions);

        ObjectMapper objectMapper = new ObjectMapper();
        try {
            scenarioScopeState.createVoucherBody = objectMapper.writeValueAsString(requestDTO);
        } catch (JsonProcessingException e) {
            logger.error("Unable to convert the DTO : {}", e);
        }

    }

    @And("I should be able to assert the create voucher validation for negative response")
    public void iWillAssertTheFieldsFromCreateVoucherValidationResponse() {
        try {
            JsonNode rootNode = objectMapper.readTree(scenarioScopeState.response);

            ErrorDetails errorDetails = objectMapper.treeToValue(rootNode, ErrorDetails.class);

            assertThat(errorDetails.getErrorCode()).isEqualTo("error.msg.schema.validation.errors");
            assertThat(errorDetails.getErrorDescription()).isEqualTo("The request is invalid");

        } catch (Exception e) {
            logger.info("An error occurred : {}", e);
        }
    }

    @Given("I can create an negative RedeemVoucherRequestDTO to redeem a voucher")
    public void createNegativeRedeemVoucherRequestDTO() {
        RedeemVoucherRequestDTO requestDTO = new RedeemVoucherRequestDTO();
        requestDTO.setRequestId(generateUniqueNumber(18));
        requestDTO.setAgentId(generateUniqueNumber(15));
        requestDTO.setVoucherSecretNumber(generateUniqueNumber(10));

        ObjectMapper objectMapper = new ObjectMapper();
        try {
            scenarioScopeState.redeemVoucherBody = objectMapper.writeValueAsString(requestDTO);
        } catch (JsonProcessingException e) {
            logger.error("Unable to convert the DTO : {}", e);
        }
    }

    @Then("I will add the required headers")
    public void makeNecessaryHeadersNonNull() {
        scenarioScopeState.registeringInstitutionId = generateUniqueNumber(3);
    }

    @And("I should be able to assert the redeem voucher validation for negative response")
    public void iWillAssertTheFieldsFromRedeemVoucherValidationResponse() {
        try {
            JsonNode rootNode = objectMapper.readTree(scenarioScopeState.response);

            ErrorDetails errorDetails = objectMapper.treeToValue(rootNode, ErrorDetails.class);

            assertThat(errorDetails.getErrorCode()).isEqualTo("error.msg.redeem.voucher.validation.errors");
            assertThat(errorDetails.getErrorDescription()).isEqualTo("Redeem voucher validation failed");

        } catch (Exception e) {
            logger.info("An error occurred : {}", e);
        }
    }

    @Given("I can create an VoucherRequestDTO for voucher creation with unsupported parameter parameter")
    public void iCreateAnIdentityMapperDTOForRegisterBeneficiaryWithUnsupportedParameter() {
        iCreateAnIdentityMapperDTOForRegisterBeneficiary();
        scenarioScopeState.createVoucherBody = addUnsupportedParamsInRequestBody(scenarioScopeState.createVoucherBody);
    }

    @And("I add unsupported parameter in my request body {string}")
    public String addUnsupportedParamsInRequestBody(String requestBody) {
        ObjectMapper objectMapper = new ObjectMapper();

        try {
            // Convert JSON string to JsonNode
            JsonNode jsonNode = objectMapper.readTree(requestBody);

            // Add new key-value pair
            ((ObjectNode) jsonNode).put("abcd", "12345");
            ((ObjectNode) jsonNode).put("efgh", "6789");

            // Convert JsonNode back to JSON string
            requestBody = objectMapper.writeValueAsString(jsonNode);

        } catch (Exception e) {
            logger.info("An error occurred : {}", e);
        }

        return requestBody;
    }

    @After("@voucher-teardown")
    public void voucherTestTearDown() {
        logger.info("Running @voucher-teardown");
        voucherTearDown();
    }

    public void voucherTearDown() {
        scenarioScopeState.requestId = null;
        scenarioScopeState.batchId = null;
        scenarioScopeState.serialNumber = null;
        scenarioScopeState.voucherNumber = null;
    }

}
