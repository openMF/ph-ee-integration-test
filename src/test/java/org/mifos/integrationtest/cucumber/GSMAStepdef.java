package org.mifos.integrationtest.cucumber;

import io.cucumber.java.en.*;
import io.restassured.*;
import io.restassured.builder.*;
import io.restassured.specification.*;
import org.mifos.connector.common.gsma.dto.*;
import org.mifos.integrationtest.common.*;

import static com.google.common.truth.Truth.assertThat;

public class GSMAStepdef extends BaseStepDef{

    GSMATransaction gsmaTransaction = new GSMATransaction();

    @Given("I can create GSMATransactionDTO")
    public void iCanCreateGSMATransactionDTO() {
        gsmaTransaction.setAmount("11.00");
        gsmaTransaction.setCurrency("USD");
        GsmaParty payer = new GsmaParty();
        payer.setKey("msisdn");
        payer.setValue("+4499999");
        GsmaParty payee = new GsmaParty();
        payee.setKey("msisdn");
        payee.setValue("+449999112");
        GsmaParty[] debitParty = new GsmaParty[1];
        GsmaParty[] creditParty = new GsmaParty[1];
        debitParty[0] = payer;
        creditParty[0] = payee;
        gsmaTransaction.setDebitParty(debitParty);
        gsmaTransaction.setCreditParty(creditParty);
        gsmaTransaction.setRequestingOrganisationTransactionReference("string");
        gsmaTransaction.setSubType("string");
        gsmaTransaction.setType("transfer");
        gsmaTransaction.setDescriptionText("string");
        Fee fees = new Fee();
        fees.setFeeAmount("11");
        fees.setFeeCurrency("USD");
        fees.setFeeType("string");
        Fee[] fee = new Fee[1];
        fee[0] = fees;
        gsmaTransaction.setFees(fee);
        gsmaTransaction.setGeoCode("37.423825,-122.082900");
        InternationalTransferInformation internationalTransferInformation =
                new InternationalTransferInformation();
        internationalTransferInformation.setQuotationReference("string");
        internationalTransferInformation.setQuoteId("string");
        internationalTransferInformation.setDeliveryMethod("directtoaccount");
        internationalTransferInformation.setOriginCountry("USA");
        internationalTransferInformation.setReceivingCountry("USA");
        internationalTransferInformation.setRelationshipSender("string");
        internationalTransferInformation.setRemittancePurpose("string");
        gsmaTransaction.setInternationalTransferInformation(internationalTransferInformation);
        gsmaTransaction.setOneTimeCode("string");
        IdDocument idDocument = new IdDocument();
        idDocument.setIdType("passport");
        idDocument.setIdNumber("string");
        idDocument.setIssuerCountry("USA");
        idDocument.setExpiryDate("2022-09-28T12:51:19.260+00:00");
        idDocument.setIssueDate("2022-09-28T12:51:19.260+00:00");
        idDocument.setIssuer("string");
        idDocument.setIssuerPlace("string");
        IdDocument[] idDocuments = new IdDocument[1];
        idDocuments[0] = idDocument;
        PostalAddress postalAddress = new PostalAddress();
        postalAddress.setAddressLine1("string");
        postalAddress.setAddressLine2("string");
        postalAddress.setAddressLine3("string");
        postalAddress.setCity("string");
        postalAddress.setCountry("USA");
        postalAddress.setPostalCode("string");
        postalAddress.setStateProvince("string");
        SubjectName subjectName = new SubjectName();
        subjectName.setFirstName("string");
        subjectName.setLastName("string");
        subjectName.setMiddleName("string");
        subjectName.setTitle("string");
        subjectName.setNativeName("string");
        Kyc recieverKyc = new Kyc();
        recieverKyc.setBirthCountry("USA");
        recieverKyc.setDateOfBirth("2000-11-20");
        recieverKyc.setContactPhone("string");
        recieverKyc.setEmailAddress("string");
        recieverKyc.setEmployerName("string");
        recieverKyc.setGender('m');
        recieverKyc.setIdDocument(idDocuments);
        recieverKyc.setNationality("USA");
        recieverKyc.setOccupation("string");
        recieverKyc.setPostalAddress(postalAddress);
        recieverKyc.setSubjectName(subjectName);
        Kyc senderKyc = new Kyc();
        senderKyc.setBirthCountry("USA");
        senderKyc.setDateOfBirth("2000-11-20");
        senderKyc.setContactPhone("string");
        senderKyc.setEmailAddress("string");
        senderKyc.setEmployerName("string");
        senderKyc.setGender('m');
        senderKyc.setIdDocument(idDocuments);
        senderKyc.setNationality("USA");
        senderKyc.setOccupation("string");
        senderKyc.setPostalAddress(postalAddress);
        senderKyc.setSubjectName(subjectName);
        gsmaTransaction.setReceiverKyc(recieverKyc);
        gsmaTransaction.setSenderKyc(senderKyc);
        gsmaTransaction.setServicingIdentity("string");
        gsmaTransaction.setRequestDate("2023-01-12T12:51:19.260+00:00");
    }


    @When("I call the GSMATransaction endpoint with expected status of {int}")
    public void iCallTheGSMATransactionEndpointWithExpectedStatusOf(int expectedStatus) {
        RequestSpecification requestSpec = Utils.getDefaultSpec(BaseStepDef.tenant);
        logger.info("body: {}", gsmaTransaction.toString());
        logger.info("url: {}", channelConnectorConfig.internationalRemittanceEndpoint);
        BaseStepDef.response = RestAssured.given(requestSpec)
                .baseUri(channelConnectorConfig.channelConnectorContactPoint)
                .body(gsmaTransaction)
                .expect()
                .spec(new ResponseSpecBuilder().expectStatusCode(expectedStatus).build())
                .when()
                .post(channelConnectorConfig.internationalRemittanceEndpoint)
                .andReturn().asString();

        logger.info("GSMA transfer Response: {}", BaseStepDef.response);
    }
}
