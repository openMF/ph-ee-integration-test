package org.mifos.integrationtest.common;

import org.json.JSONException;
import org.mifos.connector.common.gsma.dto.Fee;
import org.mifos.connector.common.gsma.dto.GSMATransaction;
import org.mifos.connector.common.gsma.dto.GsmaParty;
import org.mifos.connector.common.gsma.dto.IdDocument;
import org.mifos.connector.common.gsma.dto.InternationalTransferInformation;
import org.mifos.connector.common.gsma.dto.Kyc;
import org.mifos.connector.common.gsma.dto.PostalAddress;
import org.mifos.connector.common.gsma.dto.SubjectName;

public class GSMATransferHelper {

    public GSMATransaction gsmaTransactionRequestBodyHelper(String amount, GsmaParty payer, GsmaParty payee, String currency,
            String requestingOrganisationTransactionReference, String originalTransactionReference, String subType, String type,
            String descriptionText, Fee fees, String geocCode, InternationalTransferInformation internationalTransferInformation,
            String oneTimeCode, Kyc receiverKyc, Kyc senderKyc, String servicingIdentity, String requestDate) throws JSONException {
        GSMATransaction gsmaTransaction = new GSMATransaction();
        gsmaTransaction.setAmount(amount);
        gsmaTransaction.setCurrency(currency);
        gsmaTransaction.setCreditParty(new GsmaParty[] { payee });
        gsmaTransaction.setDebitParty(new GsmaParty[] { payer });
        gsmaTransaction.setRequestingOrganisationTransactionReference(requestingOrganisationTransactionReference);
        gsmaTransaction.setOriginalTransactionReference(originalTransactionReference);
        gsmaTransaction.setType(type);
        gsmaTransaction.setSubType(subType);
        gsmaTransaction.setDescriptionText(descriptionText);
        gsmaTransaction.setFees(new Fee[] { fees });
        gsmaTransaction.setGeoCode(geocCode);
        gsmaTransaction.setInternationalTransferInformation(internationalTransferInformation);
        gsmaTransaction.setOneTimeCode(oneTimeCode);
        gsmaTransaction.setReceiverKyc(receiverKyc);
        gsmaTransaction.setSenderKyc(senderKyc);
        gsmaTransaction.setServicingIdentity(servicingIdentity);
        gsmaTransaction.setRequestDate(requestDate);
        return gsmaTransaction;
    }

    public GsmaParty gsmaPartyHelper(String key, String value) {
        GsmaParty gsmaParty = new GsmaParty();
        gsmaParty.setKey(key);
        gsmaParty.setValue(value);
        return gsmaParty;
    }

    public Fee feeHelper(String amount, String currency, String feeType) {
        Fee fee = new Fee();
        fee.setFeeAmount(amount);
        fee.setFeeCurrency(currency);
        fee.setFeeType(feeType);
        return fee;
    }

    public InternationalTransferInformation internationalTransferInformationHelper(String quotationReference, String quoteId,
            String deliveryMethod, String originCountry, String receivingCountry, String relationshipSender, String remittancePurpose) {
        InternationalTransferInformation internationalTransferInformation = new InternationalTransferInformation();
        internationalTransferInformation.setQuotationReference(quotationReference);
        internationalTransferInformation.setQuoteId(quoteId);
        internationalTransferInformation.setDeliveryMethod(deliveryMethod);
        internationalTransferInformation.setOriginCountry(originCountry);
        internationalTransferInformation.setReceivingCountry(receivingCountry);
        internationalTransferInformation.setRelationshipSender(relationshipSender);
        internationalTransferInformation.setRemittancePurpose(remittancePurpose);
        return internationalTransferInformation;
    }

    public IdDocument idDocumentHelper(String idType, String idNumber, String issuerCountry, String expiryDate, String issueDate,
            String issuer, String issuerPlace) {
        IdDocument idDocument = new IdDocument();
        idDocument.setIdType(idType);
        idDocument.setIdNumber(idNumber);
        idDocument.setIssuerCountry(issuerCountry);
        idDocument.setIssuerCountry(expiryDate);
        idDocument.setIssueDate(issueDate);
        idDocument.setIssuer(issuer);
        idDocument.setIssuerPlace(issuerPlace);
        return idDocument;
    }

    public PostalAddress postalAddressHelper(String addressLine1, String addressLine2, String addressLine3, String city, String country,
            String postalCode, String stateProvince) {
        PostalAddress postalAddress = new PostalAddress();
        postalAddress.setAddressLine1(addressLine1);
        postalAddress.setAddressLine2(addressLine2);
        postalAddress.setAddressLine3(addressLine3);
        postalAddress.setCity(city);
        postalAddress.setCountry(country);
        postalAddress.setPostalCode(postalCode);
        postalAddress.setStateProvince(stateProvince);
        return postalAddress;
    }

    public SubjectName subjectNameHelper(String firstName, String lastName, String middleName, String title, String nativeName) {
        SubjectName subjectName = new SubjectName();
        subjectName.setFirstName(firstName);
        subjectName.setLastName(lastName);
        subjectName.setMiddleName(middleName);
        subjectName.setTitle(title);
        subjectName.setNativeName(nativeName);
        return subjectName;
    }

    public Kyc kycHelper(String birthCountry, String dateOfBirth, String contactPhone, String emailAddress, String employerName,
            char gender, IdDocument idDocument, String nationality, String occupation, PostalAddress postalAddress,
            SubjectName subjectName) {
        Kyc kyc = new Kyc();
        kyc.setIdDocument(new IdDocument[] { idDocument });
        kyc.setBirthCountry(birthCountry);
        kyc.setDateOfBirth(dateOfBirth);
        kyc.setContactPhone(contactPhone);
        kyc.setEmployerName(employerName);
        kyc.setEmailAddress(emailAddress);
        kyc.setGender(gender);
        kyc.setNationality(nationality);
        kyc.setOccupation(occupation);
        kyc.setPostalAddress(postalAddress);
        kyc.setSubjectName(subjectName);
        return kyc;
    }

}
