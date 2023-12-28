package org.mifos.integrationtest.cucumber.stepdef;

import com.github.tomakehurst.wiremock.stubbing.ServeEvent;
import io.cucumber.core.internal.com.fasterxml.jackson.core.JsonProcessingException;
import io.cucumber.core.internal.com.fasterxml.jackson.databind.JsonNode;

import io.cucumber.java.After;
import io.cucumber.java.Before;
import io.cucumber.java.en.And;
import io.cucumber.java.en.Given;
import io.cucumber.java.en.Then;
import io.cucumber.java.en.When;
import io.restassured.RestAssured;
import io.restassured.builder.ResponseSpecBuilder;
import io.restassured.response.Response;
import io.restassured.specification.RequestSpecification;
import org.mifos.integrationtest.common.Utils;
import org.springframework.beans.factory.annotation.Autowired;

import java.util.List;
import java.util.Random;

import static com.github.tomakehurst.wiremock.client.WireMock.*;
import static com.google.common.truth.Truth.assertThat;
import static org.mifos.integrationtest.common.HttpMethod.PUT;

public class VoucherManagementStepDef extends BaseStepDef{
    private static String requstId;
    private static String createVoucherBody;
    private static String activateVoucherBody;
    private static String redeemVoucherBody;
    private static String redeemVoucherResponseBody;
    private static  String callbackBody;
    private static String serialNumber;
    private static String voucherNumber;
    private static String cancelVoucherBody;
    private static String suspendVoucherBody;
    private static String registeringInstitutionId = "SocialWelfare";
    private static String requestId;
    private static String agentId;
    private static String fetchVoucherResponseBody;
    @Autowired
    MockServerStepDef mockServerStepDef;


    @Given("I can create an VoucherRequestDTO for voucher creation")
    public void iCreateAnIdentityMapperDTOForRegisterBeneficiary() {
        requestId= generateUniqueNumber(16);
        StringBuilder sb = new StringBuilder();
        sb.append("{\n");
        sb.append("    \"requestID\": \"").append(requestId).append("\",\n");
        sb.append("    \"batchID\": \"").append(generateUniqueNumber(10)).append("\",\n");
        sb.append("    \"voucherInstructions\": [\n");
        sb.append("        {\n");
        sb.append("            \"instructionID\": \"").append(generateUniqueNumber(18)).append("\",\n");
        sb.append("            \"groupCode\": \"021\",\n");
        sb.append("            \"currency\": \"SGD\",\n");
        sb.append("            \"amount\": 3000,\n");
        sb.append("            \"payeeFunctionalID\": \"08187379158030059976\",\n");
        sb.append("            \"narration\": \"Social Support Payment for the Month of January 2023\"\n");
        sb.append("        }\n");
        sb.append("    ]\n");
        sb.append("}");
        createVoucherBody = sb.toString();
    }

    @When("I call the create voucher API with expected status of {int} and stub {string}")
    public void iCallTheVoucherCreateAPIWithExpectedStatusOf(int expectedStatus, String stub) {
        RequestSpecification requestSpec = Utils.getDefaultSpec();
        BaseStepDef.response = RestAssured.given(requestSpec)
                .header("Content-Type", "application/json")
                .header("X-CallbackURL",identityMapperConfig.callbackURL+ stub)
                .header("X-Registering-Institution-ID", registeringInstitutionId)
                .baseUri(voucherManagementConfig.voucherManagementContactPoint)
                .body(createVoucherBody)
                .expect()
                .spec(new ResponseSpecBuilder().expectStatusCode(expectedStatus).build())
                .when()
                .post(voucherManagementConfig.createVoucherEndpoint)
                .andReturn().asString();


        logger.info("Voucher Response: {}", BaseStepDef.response);
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
        requestId= generateUniqueNumber(16);
        batchId = generateUniqueNumber(14);
        StringBuilder sb = new StringBuilder();
        sb.append("{\n");
        sb.append("    \"requestID\": \"").append(requestId).append("\",\n");
        sb.append("    \"batchID\": \"").append(batchId).append("\",\n"); // Replaced "045155518258" with batchId variable
        sb.append("    \"voucherInstructions\": [\n");
        sb.append("        {\n");
        sb.append("            \"serialNumber\": \"").append(serialNumber).append("\",\n");
        sb.append("            \"status\": \"02\"\n");
        sb.append("        }\n");
        sb.append("    ]\n");
        sb.append("}");

        activateVoucherBody = sb.toString();
    }

    @When("I call the activate voucher API with expected status of {int} and stub {string}")
    public void iCallTheActivateVoucherAPIWithExpectedStatusOfAndStub(int expectedStatus, String stub) {
        RequestSpecification requestSpec = Utils.getDefaultSpec();
        BaseStepDef.response = RestAssured.given(requestSpec)
                .header("Content-Type", "application/json")
                .header("X-CallbackURL", identityMapperConfig.callbackURL+stub)
                .header("X-Registering-Institution-ID", registeringInstitutionId)
                .header("X-Program-ID", "")
                .queryParam("command", "activate")
                .baseUri(voucherManagementConfig.voucherManagementContactPoint)
                .body(activateVoucherBody)
                .expect()
                .spec(new ResponseSpecBuilder().expectStatusCode(expectedStatus).build())
                .when()
                .put(voucherManagementConfig.voucherLifecycleEndpoint)
                .andReturn().asString();


        logger.info("Voucher Response: {}", BaseStepDef.response);
    }


    @When("I can create an VoucherRequestDTO for voucher cancellation")
    public void iCanCreateAnVoucherRequestDTOForVoucherCancellation() {
        requestId= generateUniqueNumber(16);
        batchId = generateUniqueNumber(14);
        StringBuilder sb = new StringBuilder();
        sb.append("{\n");
        sb.append("    \"requestID\": \"").append(requestId).append("\",\n");
        sb.append("    \"batchID\": \"").append(batchId).append("\",\n"); // Replaced "045155518258" with batchId variable
        sb.append("    \"voucherInstructions\": [\n");
        sb.append("        {\n");
        sb.append("            \"serialNumber\": \"").append(serialNumber).append("\",\n");
        sb.append("            \"status\": \"03\"\n");
        sb.append("        }\n");
        sb.append("    ]\n");
        sb.append("}");

        cancelVoucherBody = sb.toString();
    }

    @Then("I should be able to verify that the {string} method to {string} endpoint received a request with required parameter in cancel voucher callback body")
    public void iShouldBeAbleToVerifyThatTheMethodToEndpointReceivedARequestWithRequiredParameterInCancelVoucherCallbackBody(String arg0, String endpoint) {
        verify(putRequestedFor(urlEqualTo(endpoint))
                .withRequestBody(matchingJsonPath("$.requestID", equalTo(requstId))));

    }

    @When("I call the cancel voucher API with expected status of {int} and stub {string}")
    public void iCallTheCancelVoucherAPIWithExpectedStatusOfAndStub(int expectedStatus, String stub) {
        RequestSpecification requestSpec = Utils.getDefaultSpec();
        BaseStepDef.response = RestAssured.given(requestSpec)
                .header("Content-Type", "application/json")
                .header("X-CallbackURL", identityMapperConfig.callbackURL+ stub)
                .header("X-Registering-Institution-ID", registeringInstitutionId)
                .header("X-Program-ID", "")
                .queryParam("command", "cancel")
                .baseUri(voucherManagementConfig.voucherManagementContactPoint)
                .body(cancelVoucherBody)
                .expect()
                .spec(new ResponseSpecBuilder().expectStatusCode(expectedStatus).build())
                .when()
                .put(voucherManagementConfig.voucherLifecycleEndpoint)
                .andReturn().asString();


        logger.info("Voucher Response: {}", BaseStepDef.response);
    }

    @Then("I should be able to verify that the {string} method to {string} endpoint received a request with required parameter in redeem voucher callback body")
    public void iShouldBeAbleToVerifyThatTheMethodToEndpointReceivedARequestWithRequiredParameterInRedeemVoucherCallbackBody(String arg0, String endpoint) {
        verify(putRequestedFor(urlEqualTo(endpoint))
                .withRequestBody(matchingJsonPath("$.requestID", equalTo(requstId))));
    }


    @Then("I should be able to verify that the {string} method to {string} endpoint received a request with required parameter in suspend voucher callback body")
    public void iShouldBeAbleToVerifyThatTheMethodToEndpointReceivedARequestWithRequiredParameterInSuspendVoucherCallbackBody(String arg0, String endpoint) {
        verify(putRequestedFor(urlEqualTo(endpoint))
                .withRequestBody(matchingJsonPath("$.requestID", equalTo(requstId))));
    }

    @Then("I should be able to extract response body from callback")
    public void iShouldBeAbleToExtractResponseBodyFromCallback() {
        List<ServeEvent> allServeEvents = getAllServeEvents();
        for(int i=0; i< allServeEvents.size();i++) {
            ServeEvent request = allServeEvents.get(i);

            if(!(request.getRequest().getBodyAsString()).isEmpty()) {
                JsonNode rootNode = null;
                try {
                    rootNode = objectMapper.readTree(request.getRequest().getBodyAsString());
                } catch (JsonProcessingException e) {
                    throw new RuntimeException(e);
                }
                String requestID = null;
                if(rootNode.has("requestID")) {
                    requestID = rootNode.get("requestID").asText();
                }

                if (requestId.equals(requestID)) {
                    callbackBody = request.getRequest().getBodyAsString();
                }

            }
        }

        try {
            //ObjectMapper objectMapper = new ObjectMapper();
            JsonNode rootNode = objectMapper.readTree(callbackBody);

            JsonNode voucherInstructionsNode = rootNode.get("voucherInstructions");
            if (voucherInstructionsNode.isArray()) {
                for (JsonNode voucherNode : voucherInstructionsNode) {
                    serialNumber = voucherNode.get("serialNumber").asText();
                    voucherNumber = voucherNode.get("voucherNumber").asText();
                }
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
        assertThat(serialNumber).isNotEmpty();
    }

    @Given("I can create an RedeemVoucherRequestDTO for voucher redemption")
    public void iCanCreateAnRedeemVoucherRequestDTOForVoucherRedemption() {
        requestId = generateUniqueNumber(16);
        agentId = generateUniqueNumber(10);
        StringBuilder sb = new StringBuilder();
        sb.append("{\n");
        sb.append("  \"requestID\": \"").append(requestId).append("\",\n"); // Replaced "849324499155" with requestId variable
        sb.append("  \"agentID\": \"").append(agentId).append("\",\n"); // Replaced "1234567890" with agentId variable
        sb.append("  \"voucherSerialNumber\": \"").append(serialNumber).append("\",\n");
        sb.append("  \"voucherSecretNumber\": \"").append(voucherNumber).append("\"\n");
        sb.append("}");

        redeemVoucherBody = sb.toString();
    }

    @When("I call the redeem voucher API with expected status of {int}")
    public void iCallTheRedeemVoucherAPIWithExpectedStatusOf(int responseCode) {
        RequestSpecification requestSpec = Utils.getDefaultSpec();
        BaseStepDef.response = RestAssured.given(requestSpec)
                .header("Content-Type", "application/json")
                .queryParam("command", "redeem")
                .header("X-Registering-Institution-ID", registeringInstitutionId)
                .header("X-CallbackURL", "")
                .header("X-Program-ID", "")
                .baseUri(voucherManagementConfig.voucherManagementContactPoint)
                .body(redeemVoucherBody)
                .expect()
                .spec(new ResponseSpecBuilder().expectStatusCode(responseCode).build())
                .when()
                .put(voucherManagementConfig.voucherLifecycleEndpoint)
                .andReturn().asString();

        redeemVoucherResponseBody = BaseStepDef.response;
        logger.info("Voucher Response: {}", BaseStepDef.response);
    }

    @Then("I can assert that redemption was successful by asserting the status in response")
    public void iCanAssertThatRedemptionWasSuccessfulByAssertingTheStatusInResponse() {
        try {
            JsonNode rootNode = objectMapper.readTree(redeemVoucherResponseBody);

            String status = rootNode.get("status").asText();
            System.out.println("Status: " + status);
            assertThat(status).isEqualTo("01");
        } catch (Exception e) {
            e.printStackTrace();
        }

    }
    public void assertUnsuccessfulRedemption(){
        try {
            JsonNode rootNode = objectMapper.readTree(redeemVoucherResponseBody);

            String status = rootNode.get("status").asText();
            assertThat(status).isEqualTo("00");
        } catch (Exception e) {
            e.printStackTrace();
        }
    }


    @Before("@createAndActivateVoucher")
    public void createAndActivateVoucher(){
        iCreateAnIdentityMapperDTOForRegisterBeneficiary();
        mockServerStepDef.checkIfMockServerIsInjected();
        mockServerStepDef.startMockServer();
        mockServerStepDef.startStub("/createVoucher", PUT, 200);
        mockServerStepDef.startStub("/activateVoucher", PUT, 200);
        iCallTheVoucherCreateAPIWithExpectedStatusOf(202, "/createVoucher");
        try {
            Thread.sleep(5000);
        } catch (InterruptedException e) {
            throw new RuntimeException(e);
        }
        iShouldBeAbleToExtractResponseBodyFromCallback();
        iCanCreateAnVoucherRequestDTOForVoucherActivation();
        iCallTheActivateVoucherAPIWithExpectedStatusOfAndStub(202, "/activateVoucher");
    }
    @Before("@createVoucher")
    public void createVoucher(){
        iCreateAnIdentityMapperDTOForRegisterBeneficiary();
        mockServerStepDef.checkIfMockServerIsInjected();
        mockServerStepDef.startMockServer();
        mockServerStepDef.startStub("/createVoucher", PUT, 200);
        mockServerStepDef.startStub("/activateVoucher", PUT, 200);
        iCallTheVoucherCreateAPIWithExpectedStatusOf(202, "/createVoucher");
        try {
            Thread.sleep(5000);
        } catch (InterruptedException e) {
            throw new RuntimeException(e);
        }
        iShouldBeAbleToExtractResponseBodyFromCallback();
    }
    @After("@redeemVoucherFailure")
    public void redeemVoucherFailure(){
        iCanCreateAnRedeemVoucherRequestDTOForVoucherRedemption();
        iCallTheRedeemVoucherAPIWithExpectedStatusOf(200);
        assertUnsuccessfulRedemption();
    }
    @After("@redeemVoucherSuccess")
    public void redeemVoucherSuccess(){
        iCanCreateAnRedeemVoucherRequestDTOForVoucherRedemption();
        iCallTheRedeemVoucherAPIWithExpectedStatusOf(200);
        iCanAssertThatRedemptionWasSuccessfulByAssertingTheStatusInResponse();
    }

    @Given("I can create an VoucherRequestDTO for voucher suspension")
    public void iCanCreateAnVoucherRequestDTOForVoucherSuspension() {
        requestId= generateUniqueNumber(16);
        batchId = generateUniqueNumber(14);
        StringBuilder sb = new StringBuilder();
        sb.append("{\n");
        sb.append("    \"requestID\": \"").append(requestId).append("\",\n");
        sb.append("    \"batchID\": \"").append(batchId).append("\",\n"); // Replaced "045155518258" with batchId variable
        sb.append("    \"voucherInstructions\": [\n");
        sb.append("        {\n");
        sb.append("            \"serialNumber\": \"").append(serialNumber).append("\",\n");
        sb.append("            \"status\": \"06\"\n");
        sb.append("        }\n");
        sb.append("    ]\n");
        sb.append("}");

        suspendVoucherBody = sb.toString();
    }

    @When("I call the suspend voucher API with expected status of {int} and stub {string}")
    public void iCallTheSuspendVoucherAPIWithExpectedStatusOfAndStub(int responseCode, String stub) {
        RequestSpecification requestSpec = Utils.getDefaultSpec();
        BaseStepDef.response = RestAssured.given(requestSpec)
                .header("Content-Type", "application/json")
                .queryParam("command", "suspend")
                .header("X-Registering-Institution-ID", registeringInstitutionId)
                .header("X-CallbackURL", identityMapperConfig.callbackURL+stub)
                .header("X-Program-ID", "")
                .baseUri(voucherManagementConfig.voucherManagementContactPoint)
                .body(suspendVoucherBody)
                .expect()
                .spec(new ResponseSpecBuilder().expectStatusCode(responseCode).build())
                .when()
                .put(voucherManagementConfig.voucherLifecycleEndpoint)
                .andReturn().asString();

        redeemVoucherResponseBody = BaseStepDef.response;
        logger.info("Voucher Response: {}", BaseStepDef.response);
    }

    @And("I can create an VoucherRequestDTO for voucher reactivation")
    public void iCanCreateAnVoucherRequestDTOForVoucherReactivation() {
        requestId= generateUniqueNumber(16);
        batchId = generateUniqueNumber(14);
        StringBuilder sb = new StringBuilder();
        sb.append("{\n");
        sb.append("    \"requestID\": \"").append(requestId).append("\",\n");
        sb.append("    \"batchID\": \"").append(batchId).append("\",\n"); // Replaced "045155518258" with batchId variable
        sb.append("    \"voucherInstructions\": [\n");
        sb.append("        {\n");
        sb.append("            \"serialNumber\": \"").append(serialNumber).append("\",\n");
        sb.append("            \"status\": \"02\"\n");
        sb.append("        }\n");
        sb.append("    ]\n");
        sb.append("}");

        suspendVoucherBody = sb.toString();
    }

    @When("I call the validity check API with expected status of {int} and stub {string}")
    public void iCallTheValidityCheckAPIWithExpectedStatusOfAndStub(int responseCode, String stub) {
        RequestSpecification requestSpec = Utils.getDefaultSpec();
        BaseStepDef.response = RestAssured.given(requestSpec)
                .header("Content-Type", "application/json")
                .queryParam("serialNumber", serialNumber)
                .queryParam("isValid", "true")
                .header("X-CallbackURL", identityMapperConfig.callbackURL+stub)
                .header("X-Registering-Institution-ID", registeringInstitutionId)
                .baseUri(voucherManagementConfig.voucherManagementContactPoint)
                .expect()
                .spec(new ResponseSpecBuilder().expectStatusCode(responseCode).build())
                .when()
                .get(voucherManagementConfig.voucherValidityEndpoint)
                .andReturn().asString();

        redeemVoucherResponseBody = BaseStepDef.response;
        logger.info("Voucher Response: {}", BaseStepDef.response);
    }

    @And("I can extract result from validation callback and assert if validation is successful on {string}")
    public void iCanExtractResultFromValidationCallbackAndAssertIfValidationIsSuccessful(String endpoint) {
        //(putRequestedFor(urlEqualTo(endpoint)).withRequestBody(matchingJsonPath("$.isValid", equalTo("true"))));
        List<ServeEvent> allServeEvents = getAllServeEvents();
        String serialNo = null;
        String isValid = null;
        for(int i=0; i< allServeEvents.size();i++) {
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
    }

    @Then("I can assert that redemption was unsuccessful by asserting the status in response")
    public void iCanAssertThatRedemptionWasUnsuccessfulByAssertingTheStatusInResponse() {
        try {
            JsonNode rootNode = objectMapper.readTree(redeemVoucherResponseBody);

            String status = rootNode.get("status").asText();
            assertThat(status).isEqualTo("00");
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    @Then("I should be able to assert response body from callback on {string}")
    public void iShouldBeAbleToAssertResponseBodyFromCallback(String endpoint) {
        verify(putRequestedFor(urlEqualTo(endpoint)).withRequestBody(matchingJsonPath("$.registerRequestId", equalTo(requestId))));
        verify(putRequestedFor(urlEqualTo(endpoint)).withRequestBody(matchingJsonPath("$.numberFailedCases", equalTo("0"))));
    }

    @Then("I will call the fetch voucher API with expected status of {int}")
    public void iWillCallTheFetchVoucherAPIWithExpectedStatusOf(int responseCode) {
        RequestSpecification requestSpec = Utils.getDefaultSpec();
        BaseStepDef.response = RestAssured.given(requestSpec)
                .header("Content-Type", "application/json")
                .header("X-Registering-Institution-ID", registeringInstitutionId)
                .baseUri(voucherManagementConfig.voucherManagementContactPoint)
                .expect()
                .spec(new ResponseSpecBuilder().expectStatusCode(responseCode).build())
                .when()
                .get(voucherManagementConfig.fetchVoucherEndpoint + "/" + serialNumber)
                .andReturn().asString();

         fetchVoucherResponseBody = BaseStepDef.response;
        logger.info("Voucher Response: {}", BaseStepDef.response);
    }

    @And("I will assert the fields from fetch voucher response")
    public void iWillAssertTheFieldsFromFetchVoucherResponse() {
        try {
            JsonNode rootNode = objectMapper.readTree(fetchVoucherResponseBody);

            String serialNumberResponse = rootNode.get("serialNumber").asText();
            String registeringInstitutionIdResponse = rootNode.get("registeringInstitutionId").asText();
            assertThat(serialNumberResponse).isEqualTo(serialNumber);
            assertThat(registeringInstitutionIdResponse).isEqualTo(registeringInstitutionId);
        } catch (Exception e) {
            e.printStackTrace();
        }
    }
}
