package org.mifos.integrationtest.cucumber.stepdef;

import com.github.tomakehurst.wiremock.client.WireMock;
import com.github.tomakehurst.wiremock.matching.RequestPatternBuilder;
import io.cucumber.java.en.And;
import io.cucumber.java.en.Then;
import io.cucumber.java.en.When;
import io.restassured.RestAssured;
import io.restassured.builder.ResponseSpecBuilder;
import io.restassured.specification.RequestSpecification;
import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;
import org.mifos.connector.common.channel.dto.TransactionChannelRequestDTO;
import org.mifos.connector.common.identityaccountmapper.dto.AccountMapperRequestDTO;
import org.mifos.connector.common.identityaccountmapper.dto.BeneficiaryDTO;
import org.mifos.connector.common.mojaloop.dto.MoneyData;
import org.mifos.connector.common.mojaloop.dto.Party;
import org.mifos.connector.common.mojaloop.dto.PartyIdInfo;
import org.mifos.connector.common.mojaloop.type.IdentifierType;
import org.mifos.integrationtest.common.HttpMethod;
import org.mifos.integrationtest.common.HttpMethod;
import org.mifos.integrationtest.common.Utils;


import java.util.ArrayList;
import java.util.List;

import static com.github.tomakehurst.wiremock.client.WireMock.*;
import static com.github.tomakehurst.wiremock.client.WireMock.urlEqualTo;
import static com.google.common.truth.Truth.assertThat;


public class IdentityMapperStepDef extends BaseStepDef{

    private static String identityMapperBody = null;
    private static AccountMapperRequestDTO registerBeneficiaryBody = null;
    private static AccountMapperRequestDTO addPaymentModalityBody = null;
    private static AccountMapperRequestDTO updatePaymentModalityBody = null;
    private static String payeeIdentity = "69028769626982342711";
    private static int getApiCount = 0;
    private static String fieldName = "numberFailedCases";
    private static String fieldValue = "0";
    private static final String requstId = "915251236706";
    private static final String payeeIdentityAccountLookup = "76032553265657618183";
    private static TransactionChannelRequestDTO transactionChannelRequestDTO = new TransactionChannelRequestDTO();
    private static String transactionId;
    private static String tenant;
    private static String payeeDfspId;

    @When("I call the register beneficiary API with expected status of {int} and stub {string}")
    public void iCallTheRegisterBeneficiaryAPIWithExpectedStatusOf(int expectedStatus, String stub) {
        BaseStepDef.response = RestAssured.given()
                .header("Content-Type", "application/json")
                .header("X-CallbackURL", mockServer.getBaseUri()+stub)
                .baseUri(identityMapperConfig.identityMapperContactPoint)
                .body(registerBeneficiaryBody)
                .expect()
                .spec(new ResponseSpecBuilder().expectStatusCode(expectedStatus).build())
                .when()
                .post(identityMapperConfig.registerBeneficiaryEndpoint)
                .andReturn().asString();


        logger.info("Identity Mapper Response: {}", BaseStepDef.response);
    }

    @When("I call the add payment modality API with expected status of {int} and stub {string}")
    public void iCallTheAddPaymentModalityAPIWithExpectedStatusOf(int expectedStatus, String stub) {
        BaseStepDef.response = RestAssured.given()
                .header("Content-Type", "application/json")
                .header("X-CallbackURL", mockServer.getBaseUri()+"/test")
                .baseUri(identityMapperConfig.identityMapperContactPoint)
                .body(addPaymentModalityBody)
                .expect()
                .spec(new ResponseSpecBuilder().expectStatusCode(expectedStatus).build())
                .when()
                .post(identityMapperConfig.addPaymentModalityEndpoint)
                .andReturn().asString();


        logger.info("Identity Mapper Response: {}", BaseStepDef.response);
    }

    @When("I call the update payment modality API with expected status of {int} and stub {string}")
    public void iCallTheUpdatePaymentModalityAPIWithExpectedStatusOf(int expectedStatus, String stub) {
        BaseStepDef.response = RestAssured.given()
                .header("Content-Type", "application/json")
                .header("X-CallbackURL", mockServer.getBaseUri()+stub)
                .baseUri(identityMapperConfig.identityMapperContactPoint)
                .body(updatePaymentModalityBody)
                .expect()
                .spec(new ResponseSpecBuilder().expectStatusCode(expectedStatus).build())
                .when()
                .post(identityMapperConfig.updatePaymentModalityEndpoint)
                .andReturn().asString();


        logger.info("Identity Mapper Response: {}", BaseStepDef.response);
    }

    @And("I create an IdentityMapperDTO for Register Beneficiary")
    public void iCreateAnIdentityMapperDTOForRegisterBeneficiary() {
        List<BeneficiaryDTO> beneficiaryDTOList = new ArrayList<>();

        BeneficiaryDTO beneficiaryDTO =new BeneficiaryDTO("94049169714828912115",null, null, null);
        beneficiaryDTOList.add(beneficiaryDTO);
        registerBeneficiaryBody = new AccountMapperRequestDTO(requstId, "467028349179", beneficiaryDTOList);

    }

    @And("I create an IdentityMapperDTO for Add Payment Modality")
    public void iCreateAnIdentityMapperDTOForAddPaymentModality() {
        List<BeneficiaryDTO> beneficiaryDTOList = new ArrayList<>();

        BeneficiaryDTO beneficiaryDTO =new BeneficiaryDTO("94049169714828912114","00", "12345678", null);
        beneficiaryDTOList.add(beneficiaryDTO);
        addPaymentModalityBody = new AccountMapperRequestDTO(requstId, "467028349179", beneficiaryDTOList);

    }

    @And("I create an IdentityMapperDTO for Update Payment Modality")
    public void iCreateAnIdentityMapperDTOForUpdatePaymentModality() {
        List<BeneficiaryDTO> beneficiaryDTOList = new ArrayList<>();

        BeneficiaryDTO beneficiaryDTO =new BeneficiaryDTO("94049169714828912115","00", "LB28369763644714781256435714", null);
        beneficiaryDTOList.add(beneficiaryDTO);
        updatePaymentModalityBody = new AccountMapperRequestDTO(requstId, "467028349179", beneficiaryDTOList);
    }

    @Then("I call the account lookup API with expected status of {int} and stub {string}")
    public void iCallTheAccountLookupAPIWithExpectedStatusOf(int expectedStatus, String stub) {
        BaseStepDef.response = RestAssured.given()
                .header("Content-Type", "application/json")
                .header("X-CallbackURL", mockServer.getBaseUri()+stub)
                .queryParam("payeeIdentity", payeeIdentity)
                .queryParam("paymentModality","00")
                .baseUri(identityMapperConfig.identityMapperContactPoint)
                .expect()
                .spec(new ResponseSpecBuilder().expectStatusCode(expectedStatus).build())
                .when()
                .get(identityMapperConfig.accountLookupEndpoint)
                .andReturn().asString();


        logger.info("Identity Mapper Response: {}", BaseStepDef.response);
    }

    @Then("I call the account lookup API {int} times with expected status of {int} and stub {string}")
    public void iCallTheAccountLookupAPITimesWithExpectedStatusOf(int count, int expectedStatus, String endpoint) {
        getApiCount=count;
        //int i=0;
        for(int i=1;i<count;i++) {

            BaseStepDef.response = RestAssured.given()
                    .header("Content-Type", "application/json")
                    .header("X-CallbackURL", mockServer.getBaseUri() + endpoint)
                    .queryParam("payeeIdentity", payeeIdentity)
                    .queryParam("paymentModality", "00")
                    .baseUri(identityMapperConfig.identityMapperContactPoint)
                    .expect()
                    .spec(new ResponseSpecBuilder().expectStatusCode(expectedStatus).build())
                    .when()
                    .get(identityMapperConfig.accountLookupEndpoint)
                    .andReturn().asString();

        }
    }

    @Then("I should be able to verify that the {string} method to {string} endpoint received a request with required parameter in body")
    public void iShouldBeAbleToVerifyThatTheMethodToEndpointReceivedRequestWithASpecificBody(String httpmethod, String endpoint) {
        verify(postRequestedFor(urlEqualTo(endpoint))
                .withRequestBody(matchingJsonPath("$.registerRequestID", equalTo(requstId))));
    }

    @Then("I should be able to verify that the {string} method to {string} endpoint received a request with same payeeIdentity")
    public void iShouldBeAbleToVerifyThatTheMethodToEndpointReceivedARequestWithSamePayeeIdentity(String arg0, String endpoint) {
        verify(postRequestedFor(urlEqualTo(endpoint))
                .withRequestBody(matchingJsonPath("$.payeeIdentity", equalTo(payeeIdentity))));
    }

    @When("I call the register beneficiary API with a MSISDN and DFSPID as {string} with expected status of 200")
    public void iCallTheRegisterBeneficiaryAPIWithAMSISDNAndDFSPIDAs(String dfspId, Integer expectedStatus) {
        BaseStepDef.response = RestAssured.given()
                .header("Content-Type", "application/json")
                .header("X-CallbackURL", mockServer.getBaseUri()+"/registerBeneficiary")
                .baseUri(identityMapperConfig.identityMapperContactPoint)
                .body(registerBeneficiaryBody)
                .expect()
                .spec(new ResponseSpecBuilder().expectStatusCode(expectedStatus).build())
                .when()
                .post(identityMapperConfig.registerBeneficiaryEndpoint)
                .andReturn().asString();


        logger.info("Identity Mapper Response: {}", BaseStepDef.response);
    }


    @When("I create an IdentityMapperDTO for registering beneficiary with {string} as DFSPID")
    public void iCreateAnIdentityMapperDTOForRegisteringBeneficiaryWithAsDFSPID(String dfspId) {
        tenant = dfspId;

        List<BeneficiaryDTO> beneficiaryDTOList = new ArrayList<>();

        BeneficiaryDTO beneficiaryDTO =new BeneficiaryDTO(payeeIdentityAccountLookup,"01", "12345678", dfspId);
        beneficiaryDTOList.add(beneficiaryDTO);
        registerBeneficiaryBody = new AccountMapperRequestDTO(requstId, "467028349179", beneficiaryDTOList);
    }

    @Then("I create an Channel Transfer DTO with same payee Identity i registered in account mapper")
    public void iCreateAnChannelTransferDTOWithSamePayeeIdentityIRegisteredInAccountMapper() {

        PartyIdInfo payerPartyId = new PartyIdInfo(IdentifierType.MSISDN, "27710101999");
        PartyIdInfo payeePartyId = new PartyIdInfo(IdentifierType.MSISDN, payeeIdentityAccountLookup);
        Party payer = new Party(payerPartyId);
        Party payee = new Party(payeePartyId);
        MoneyData moneyData = new MoneyData("100", "USD");

        transactionChannelRequestDTO.setPayee(payee);
        transactionChannelRequestDTO.setPayer(payer);
        transactionChannelRequestDTO.setAmount(moneyData);
    }

    @And("I will call the channel transfer API with expected status of {int}")
    public void iWillCallTheChannelTransferAPIWithExpectedStatusOf(int expectedStatus) {
        RequestSpecification requestSpec = Utils.getDefaultSpec(tenant);
        BaseStepDef.response = RestAssured.given(requestSpec)
                .baseUri(channelConnectorConfig.channelConnectorContactPoint)
                .body(transactionChannelRequestDTO)
                .expect()
                .spec(new ResponseSpecBuilder().expectStatusCode(expectedStatus).build())
                .when()
                .post(channelConnectorConfig.transferEndpoint)
                .andReturn().asString();

        logger.info("Inbound transfer Response: {}", BaseStepDef.response);
    }

    @And("I should be able to parse transactionId from transfer response")
    public void iShouldBeAbleToParseTransactionIdFromTransferResponse() {

        try {
            JSONObject jsonObject = new JSONObject(BaseStepDef.response);
            transactionId = jsonObject.getString("transactionId");
        } catch (JSONException e) {
            e.printStackTrace();
            assertThat(false).isTrue();
            return;
        }
        assertThat(transactionId).isNotNull();
        assertThat(transactionId).isNotEmpty();
    }


    @Then("I can call ops app transfer api with expected status of {int}")
    public void iCanCallOpsAppTransferApiWithExpectedStatusOf(int expectedStatus) {
        RequestSpecification requestSpec = Utils.getDefaultSpec(tenant);
        requestSpec.header("transactionId", transactionId);
        BaseStepDef.response = RestAssured.given(requestSpec)
                .baseUri(channelConnectorConfig.channelConnectorContactPoint)
                .body(BaseStepDef.inboundTransferMockReq)
                .expect()
                .spec(new ResponseSpecBuilder().expectStatusCode(expectedStatus).build())
                .when()
                .post(channelConnectorConfig.transferEndpoint)
                .andReturn().asString();

        logger.info("Inbound transfer Response: {}", BaseStepDef.response);
    }

    @And("I can assert the payee DFSPID is same as used to register beneficiary id type from response")
    public void iCanAssertThePayeePartyIdTypeFromResponse() {
        try {
            JSONObject jsonObject = new JSONObject(BaseStepDef.response);
            JSONArray content = jsonObject.getJSONArray("content");
            JSONObject transfer = (JSONObject) content.get(0);
            payeeDfspId = transfer.getString("payeeDfspId");
        } catch (JSONException e) {
            e.printStackTrace();
            assertThat(false).isTrue();
            return;
        }
        assertThat(payeeDfspId.equals(tenant));
    }
}
