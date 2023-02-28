package org.mifos.integrationtest.cucumber;

import io.cucumber.core.internal.com.fasterxml.jackson.core.JsonProcessingException;
import io.cucumber.core.internal.com.fasterxml.jackson.databind.JsonMappingException;
import io.cucumber.java.en.And;
import io.cucumber.java.en.Given;
import io.cucumber.java.en.Then;
import io.cucumber.java.en.When;
import io.restassured.RestAssured;
import io.restassured.builder.ResponseSpecBuilder;
import io.restassured.specification.RequestSpecification;
import org.apache.commons.lang3.StringEscapeUtils;
import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;
import org.mifos.connector.common.channel.dto.PhErrorDTO;
import org.mifos.connector.common.channel.dto.TransactionChannelRequestDTO;
import org.mifos.connector.common.gsma.dto.*;
import org.mifos.integrationtest.common.GSMATransferHelper;
import org.mifos.integrationtest.common.Utils;
import org.springframework.beans.factory.annotation.Value;

import static com.google.common.truth.Truth.assertThat;

public class ErrorCodeStepDef extends BaseStepDef{

    @Value("${max-retry-count}")
    private int maxRetryCount;
    @Value("${retry-interval}")
    private int retryInterval;
    public static TransactionChannelRequestDTO mockTransactionChannelRequestDTO = null;
    public String transactionId;
    public static String randomTransactionId;
    public static GSMATransaction gsmaTransaction = null;
    public static PhErrorDTO errorInformation = null;

    @When("I call the GSMATransfer endpoint with expected status of {int}")
    public void iCallTheGSMATransferEndpointWithExpectedStatusOf(int expectedStatus) {
        RequestSpecification requestSpec = Utils.getDefaultSpec(BaseStepDef.tenant);
        logger.info("body: {}", gsmaTransaction.toString());
        logger.info("url: {}", channelConnectorConfig.gsmaP2PEndpoint);
        BaseStepDef.response = RestAssured.given(requestSpec)
                .baseUri(channelConnectorConfig.channelConnectorContactPoint)
                .body(gsmaTransaction)
                .expect()
                .spec(new ResponseSpecBuilder().expectStatusCode(expectedStatus).build())
                .when()
                .post(channelConnectorConfig.gsmaP2PEndpoint)
                .andReturn().asString();


        logger.info("GSMA transfer Response: {}", BaseStepDef.response);
    }

    @When("I call the transfer query endpoint with transactionId and expected status of {int}")
    public void iCallTheTransferQueryEndpointWithTransactionIdAndExpectedStatusOf(int expectedStatus) {
        RequestSpecification requestSpec = Utils.getDefaultSpec(BaseStepDef.tenant);
        String endPoint = operationsAppConfig.transfersEndpoint ;
        requestSpec.header("Authorization", "Bearer " + BaseStepDef.accessToken);
        requestSpec.queryParam("size", 10);
        requestSpec.queryParam("page",0);
        requestSpec.queryParam("transactionId", transactionId);
        logger.info("Transfer query Response: {}", endPoint);
        logger.info("TxnId : {}", transactionId);
        BaseStepDef.response = RestAssured.given(requestSpec)
                .baseUri(operationsAppConfig.operationAppContactPoint)
                .expect()
                .spec(new ResponseSpecBuilder().expectStatusCode(expectedStatus).build())
                .when()
                .get(endPoint)
                .andReturn().asString();

        logger.info("Transfer query Response: {}", BaseStepDef.response);
    }

    @Then("I should poll the transfer query endpoint with transactionId until errorInformation is populated for the transactionId")
    public void iShouldPollTheTransferQueryEndpointWithTransactionIdUntilErrorInformationIsPopulatedForTheTransactionId() {
        RequestSpecification requestSpec = Utils.getDefaultSpec(BaseStepDef.tenant);
        String endPoint = operationsAppConfig.transfersEndpoint ;
        //requestSpec.header("Authorization", "Bearer " + BaseStepDef.accessToken);
        requestSpec.queryParam("size", 10);
        requestSpec.queryParam("page",0);
        requestSpec.queryParam("transactionId", transactionId);
        logger.info("Transfer query Response: {}", endPoint);
        logger.info("TxnId : {}", transactionId);
        int retryCount=0;
        while (errorInformation == null && retryCount<maxRetryCount ) {
            try {
                iWillSleepForSecs(retryInterval);
            } catch (InterruptedException e) {
                throw new RuntimeException(e);
            }
            BaseStepDef.response = RestAssured.given(requestSpec)
                    .baseUri(operationsAppConfig.operationAppContactPoint)
                    .expect()
                    .spec(new ResponseSpecBuilder().expectStatusCode(200).build())
                    .when()
                    .get(endPoint)
                    .andReturn().asString();

            logger.info("Transfer query Response: {}", BaseStepDef.response);
            checkForCallback();
            retryCount++;
        }
    }


    @And("I should be able to parse transactionId from response")
    public void parseTransactionId() {
        try {
            JSONObject jsonObject = new JSONObject(BaseStepDef.response);
            transactionId = jsonObject.getString("transactionId");
            logger.info("Inbound transfer Id: {}", transactionId);
        } catch (JSONException e) {
            e.printStackTrace();
            assertThat(false).isTrue();
            return;
        }
        assertThat(transactionId).isNotNull();
        assertThat(transactionId).isNotEmpty();
    }
    public void checkForCallback(){
        String responseError;
        try {
            JSONObject jsonObject = new JSONObject(BaseStepDef.response);
            JSONArray content = jsonObject.getJSONArray("content");
            if (content.getJSONObject(0).has("errorInformation")) {
                responseError = content.getJSONObject(0).getString("errorInformation");
                errorInformation = objectMapper.readValue(StringEscapeUtils.unescapeJava(responseError), PhErrorDTO.class);
            }
        } catch (JsonMappingException e) {
            throw new RuntimeException(e);
        } catch (JSONException e) {
            throw new RuntimeException(e);
        } catch (JsonProcessingException e) {
            throw new RuntimeException(e);
        }
    }

    @And("I should be able to parse {string} Error Code from response")
    public void iShouldBeAbleToParseErrorCodeFromResponse(String errorCode) {

        assertThat(errorInformation.getErrorCode()).isNotNull();
        assertThat(errorInformation.getErrorCode()).matches(errorCode);
    }

    @And("I will sleep for {int} millisecond")
    public void iWillSleepForSecs(int time) throws InterruptedException {
        Thread.sleep(time);
    }

    @And("I should be able to parse {string} Error Code from GSMA Transfer response")
    public void iShouldBeAbleToParseErrorCodeFromGSMATransferResponse(String errorCode) {
        PhErrorDTO errorInformation;
        try {
            JSONObject jsonObject = new JSONObject(BaseStepDef.response);
            errorInformation = objectMapper.readValue(jsonObject.toString(), PhErrorDTO.class);

        } catch (JSONException | JsonProcessingException e) {
            throw new RuntimeException(e);
        }
        assertThat(errorInformation.getErrorCode()).isNotNull();
        assertThat(errorInformation.getErrorCode()).matches(errorCode);
    }

    @Given("I can create GSMATransferDTO with missing currency details")
    public void iCanCreateGSMATransactionDTOWithMissingCurrency() {
        GSMATransferHelper gsmaTransferHelper = new GSMATransferHelper();
        Fee fee = gsmaTransferHelper.feeHelper("11", "USD", "string");
        GsmaParty debitParty = gsmaTransferHelper.gsmaPartyHelper("msisdn", "449999999");
        GsmaParty creditParty = gsmaTransferHelper.gsmaPartyHelper("msisdn", "+449999112");
        InternationalTransferInformation internationalTransferInformation = gsmaTransferHelper.internationalTransferInformationHelper("string","string", "directtoaccount", "USA", "USA","USA", "USA");
        IdDocument idDocument = gsmaTransferHelper.idDocumentHelper("passport","string", "USA","2022-09-28T12:51:19.260+00:00","2022-09-28T12:51:19.260+00:00","string","string");
        PostalAddress postalAddress = gsmaTransferHelper.postalAddressHelper("string","string","string","string","USA","string","string");
        SubjectName subjectName = gsmaTransferHelper.subjectNameHelper("string","string","string","string","string");
        Kyc senderKyc = gsmaTransferHelper.kycHelper("USA","2000-11-20", "string", "string", "string", 'm', idDocument,"USA","string", postalAddress, subjectName);
        Kyc receiverKyc = gsmaTransferHelper.kycHelper("USA","2000-11-20", "string", "string", "string", 'm', idDocument,"USA","string", postalAddress, subjectName);
        try {
            gsmaTransaction = gsmaTransferHelper.gsmaTransactionRequestBodyHelper("11",debitParty, creditParty, "", "string","string", "string", "transfer","string",fee,"37.423825,-122.082900",
                    internationalTransferInformation,"string",receiverKyc,senderKyc,"string","2023-01-12T12:51:19.260+00:00");
        } catch (JSONException e) {
            throw new RuntimeException(e);
        }
    }

    @Given("I can create GSMATransferDTO with same payer and payee")
    public void iCanCreateGSMATransactionDTOWithSamePayerAndPayee() {
        GSMATransferHelper gsmaTransferHelper = new GSMATransferHelper();
        Fee fee = gsmaTransferHelper.feeHelper("11", "USD", "string");
        GsmaParty debitParty = gsmaTransferHelper.gsmaPartyHelper("msisdn", "+449999112");
        GsmaParty creditParty = gsmaTransferHelper.gsmaPartyHelper("msisdn", "+449999112");
        InternationalTransferInformation internationalTransferInformation = gsmaTransferHelper.internationalTransferInformationHelper("string","string", "directtoaccount", "USA", "USA","USA", "USA");
        IdDocument idDocument = gsmaTransferHelper.idDocumentHelper("passport","string", "USA","2022-09-28T12:51:19.260+00:00","2022-09-28T12:51:19.260+00:00","string","string");
        PostalAddress postalAddress = gsmaTransferHelper.postalAddressHelper("string","string","string","string","USA","string","string");
        SubjectName subjectName = gsmaTransferHelper.subjectNameHelper("string","string","string","string","string");
        Kyc senderKyc = gsmaTransferHelper.kycHelper("USA","2000-11-20", "string", "string", "string", 'm', idDocument,"USA","string", postalAddress, subjectName);
        Kyc receiverKyc = gsmaTransferHelper.kycHelper("USA","2000-11-20", "string", "string", "string", 'm', idDocument,"USA","string", postalAddress, subjectName);
        try {
            gsmaTransaction = gsmaTransferHelper.gsmaTransactionRequestBodyHelper("11",debitParty, creditParty, "USD", "string","string", "string", "transfer","string",fee,"37.423825,-122.082900",
                    internationalTransferInformation,"string",receiverKyc,senderKyc,"string","2023-01-12T12:51:19.260+00:00");
        } catch (JSONException e) {
            throw new RuntimeException(e);
        }
    }
    @Given("I can create GSMATransferDTO with Negative Amount")
    public void iCanCreateGSMATransactionDTOWithNegativeAmount() {
        GSMATransferHelper gsmaTransferHelper = new GSMATransferHelper();
        Fee fee = gsmaTransferHelper.feeHelper("11", "USD", "string");
        GsmaParty debitParty = gsmaTransferHelper.gsmaPartyHelper("msisdn", "+449999999");
        GsmaParty creditParty = gsmaTransferHelper.gsmaPartyHelper("msisdn", "+449999112");
        InternationalTransferInformation internationalTransferInformation = gsmaTransferHelper.internationalTransferInformationHelper("string","string", "directtoaccount", "USA", "USA","USA", "USA");
        IdDocument idDocument = gsmaTransferHelper.idDocumentHelper("passport","string", "USA","2022-09-28T12:51:19.260+00:00","2022-09-28T12:51:19.260+00:00","string","string");
        PostalAddress postalAddress = gsmaTransferHelper.postalAddressHelper("string","string","string","string","USA","string","string");
        SubjectName subjectName = gsmaTransferHelper.subjectNameHelper("string","string","string","string","string");
        Kyc senderKyc = gsmaTransferHelper.kycHelper("USA","2000-11-20", "string", "string", "string", 'm', idDocument,"USA","string", postalAddress, subjectName);
        Kyc receiverKyc = gsmaTransferHelper.kycHelper("USA","2000-11-20", "string", "string", "string", 'm', idDocument,"USA","string", postalAddress, subjectName);
        try {
            gsmaTransaction = gsmaTransferHelper.gsmaTransactionRequestBodyHelper("-11",debitParty, creditParty, "USD", "string","string", "string", "transfer","string",fee,"37.423825,-122.082900",
                    internationalTransferInformation,"string",receiverKyc,senderKyc,"string","2023-01-12T12:51:19.260+00:00");
        } catch (JSONException e) {
            throw new RuntimeException(e);
        }

    }

    @Given("I can create GSMATransferDTO with invalid amount format")
    public void iCanCreateGSMATransactionDTOWithInvalidAmountFormat() {
        GSMATransferHelper gsmaTransferHelper = new GSMATransferHelper();
        Fee fee = gsmaTransferHelper.feeHelper("11", "USD", "string");
        GsmaParty debitParty = gsmaTransferHelper.gsmaPartyHelper("msisdn", "449999999");
        GsmaParty creditParty = gsmaTransferHelper.gsmaPartyHelper("msisdn", "+449999112");
        InternationalTransferInformation internationalTransferInformation = gsmaTransferHelper.internationalTransferInformationHelper("string","string", "directtoaccount", "USA", "USA","USA", "USA");
        IdDocument idDocument = gsmaTransferHelper.idDocumentHelper("passport","string", "USA","2022-09-28T12:51:19.260+00:00","2022-09-28T12:51:19.260+00:00","string","string");
        PostalAddress postalAddress = gsmaTransferHelper.postalAddressHelper("string","string","string","string","USA","string","string");
        SubjectName subjectName = gsmaTransferHelper.subjectNameHelper("string","string","string","string","string");
        Kyc senderKyc = gsmaTransferHelper.kycHelper("USA","2000-11-20", "string", "string", "string", 'm', idDocument,"USA","string", postalAddress, subjectName);
        Kyc receiverKyc = gsmaTransferHelper.kycHelper("USA","2000-11-20", "string", "string", "string", 'm', idDocument,"USA","string", postalAddress, subjectName);
        try {
            gsmaTransaction = gsmaTransferHelper.gsmaTransactionRequestBodyHelper("ab",debitParty, creditParty, "USD", "string","string", "string", "transfer","string",fee,"37.423825,-122.082900",
                    internationalTransferInformation,"string",receiverKyc,senderKyc,"string","2023-01-12T12:51:19.260+00:00");
        } catch (JSONException e) {
            throw new RuntimeException(e);
        }
    }

    @Given("I can create GSMATransferDTO with incorrect Payer")
    public void iCanCreateGSMATransferDTOWithIncorrectPayerHelper() {
        GSMATransferHelper gsmaTransferHelper = new GSMATransferHelper();
        Fee fee = gsmaTransferHelper.feeHelper("11", "USD", "string");
        GsmaParty debitParty = gsmaTransferHelper.gsmaPartyHelper("msisdn", "449999");
        GsmaParty creditParty = gsmaTransferHelper.gsmaPartyHelper("msisdn", "+449999112");
        InternationalTransferInformation internationalTransferInformation = gsmaTransferHelper.internationalTransferInformationHelper("string","string", "directtoaccount", "USA", "USA","USA", "USA");
        IdDocument idDocument = gsmaTransferHelper.idDocumentHelper("passport","string", "USA","2022-09-28T12:51:19.260+00:00","2022-09-28T12:51:19.260+00:00","string","string");
        PostalAddress postalAddress = gsmaTransferHelper.postalAddressHelper("string","string","string","string","USA","string","string");
        SubjectName subjectName = gsmaTransferHelper.subjectNameHelper("string","string","string","string","string");
        Kyc senderKyc = gsmaTransferHelper.kycHelper("USA","2000-11-20", "string", "string", "string", 'm', idDocument,"USA","string", postalAddress, subjectName);
        Kyc receiverKyc = gsmaTransferHelper.kycHelper("USA","2000-11-20", "string", "string", "string", 'm', idDocument,"USA","string", postalAddress, subjectName);
        try {
            gsmaTransaction = gsmaTransferHelper.gsmaTransactionRequestBodyHelper("11",debitParty, creditParty, "USD", "string","string", "string", "transfer","string",fee,"37.423825,-122.082900",
                    internationalTransferInformation,"string",receiverKyc,senderKyc,"string","2023-01-12T12:51:19.260+00:00");
        } catch (JSONException e) {
            throw new RuntimeException(e);
        }
    }

    @Given("I can create GSMATransferDTO with Payer Insufficient Balance")
    public void iCanCreateGSMATransferDTOWithPayerInsufficientBalance() {
        GSMATransferHelper gsmaTransferHelper = new GSMATransferHelper();
        Fee fee = gsmaTransferHelper.feeHelper("110", "USD", "string");
        GsmaParty debitParty = gsmaTransferHelper.gsmaPartyHelper("msisdn", "+449999999");
        GsmaParty creditParty = gsmaTransferHelper.gsmaPartyHelper("msisdn", "+449999112");
        InternationalTransferInformation internationalTransferInformation = gsmaTransferHelper.internationalTransferInformationHelper("string","string", "directtoaccount", "USA", "USA","USA", "USA");
        IdDocument idDocument = gsmaTransferHelper.idDocumentHelper("passport","string", "USA","2022-09-28T12:51:19.260+00:00","2022-09-28T12:51:19.260+00:00","string","string");
        PostalAddress postalAddress = gsmaTransferHelper.postalAddressHelper("string","string","string","string","USA","string","string");
        SubjectName subjectName = gsmaTransferHelper.subjectNameHelper("string","string","string","string","string");
        Kyc senderKyc = gsmaTransferHelper.kycHelper("USA","2000-11-20", "string", "string", "string", 'm', idDocument,"USA","string", postalAddress, subjectName);
        Kyc receiverKyc = gsmaTransferHelper.kycHelper("USA","2000-11-20", "string", "string", "string", 'm', idDocument,"USA","string", postalAddress, subjectName);
        try {
            gsmaTransaction = gsmaTransferHelper.gsmaTransactionRequestBodyHelper("11000",debitParty, creditParty, "USD", "string","string", "string", "transfer","string",fee,"37.423825,-122.082900",
                    internationalTransferInformation,"string",receiverKyc,senderKyc,"string","2023-01-12T12:51:19.260+00:00");
        } catch (JSONException e) {
            throw new RuntimeException(e);
        }
    }


}
