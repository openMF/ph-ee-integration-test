package org.mifos.integrationtest.cucumber.stepdef;

import io.cucumber.core.internal.com.fasterxml.jackson.core.JsonProcessingException;
import io.cucumber.core.internal.com.fasterxml.jackson.databind.ObjectMapper;
import io.restassured.specification.RequestSpecification;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.UUID;
import org.apache.fineract.client.models.InteropIdentifierRequestData;
import org.apache.fineract.client.models.PostClientsRequest;
import org.apache.fineract.client.models.PostClientsResponse;
import org.apache.fineract.client.models.PostRecurringDepositAccountsRecurringDepositAccountIdTransactionsRequest;
import org.apache.fineract.client.models.PostSavingsAccountsAccountIdRequest;
import org.apache.fineract.client.models.PostSavingsAccountsRequest;
import org.apache.fineract.client.models.PostSavingsAccountsResponse;
import org.apache.fineract.client.models.PostSavingsProductsRequest;
import org.apache.fineract.client.models.PostSavingsProductsResponse;
import org.mifos.connector.common.mojaloop.dto.MoneyData;
import org.mifos.connector.common.mojaloop.dto.Party;
import org.mifos.connector.common.mojaloop.dto.PartyIdInfo;
import org.mifos.connector.common.mojaloop.dto.QuoteSwitchRequestDTO;
import org.mifos.connector.common.mojaloop.dto.TransactionType;
import org.mifos.connector.common.mojaloop.dto.TransferSwitchRequestDTO;
import org.mifos.connector.common.mojaloop.type.AmountType;
import org.mifos.connector.common.mojaloop.type.IdentifierType;
import org.mifos.connector.common.mojaloop.type.InitiatorType;
import org.mifos.connector.common.mojaloop.type.Scenario;
import org.mifos.connector.common.mojaloop.type.TransactionRole;
import org.mifos.integrationtest.config.MojaloopConfig;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;

@Component
public class PayerFundTransferDef {

    public String amsName;
    public int amount;
    public String accountId;
    public String acccountHoldingInstitutionId;

    public String tenant;
    public String payerTenant;
    public String payeeTenant;
    public String currentDate;
    protected String savingsProductBody;
    protected String responseSavingsProduct;
    protected String savingsApproveBody;
    protected String responseSavingsApprove;
    protected String savingsAccountBody;
    protected String interopIdentifierBody;
    protected String responseSavingsAccountPayer;
    protected String responseSavingsAccountPayee;
    protected String responseInteropIdentifier;
    protected String savingsActivateBody;
    protected String responseSavingsActivate;
    protected String savingsDepositAccountBody;
    protected String responseSavingsDepositAccount;
    protected String createClientBody;
    protected String responsePayerClient;
    protected String responsePayeeClient;
    protected String externalId;
    @Autowired
    ObjectMapper objectMapper;

    @Autowired
    MojaloopConfig mojaloopConfig;

    @Value("${defaults.authorization}")
    public String authorizationToken;

    Logger logger = LoggerFactory.getLogger(this.getClass());

    public void setAccountId(String acId) {
        accountId = acId;
    }

    public void setHeadersMifos(String ams, String aHIId, int amnt) {
        amsName = ams;
        acccountHoldingInstitutionId = aHIId;
        amount = amnt;
    }

    protected void setTenant(String tenantName) {
        tenant = tenantName;
    }

    public void setPayerTenant(String payerTenant) {
        this.payerTenant = payerTenant;
    }

    public void setPayeeTenant(String payeeTenant) {
        this.payeeTenant = payeeTenant;
    }

    protected void setcurrentDate(String date) {
        currentDate = date;
    }

    protected RequestSpecification setHeaders(RequestSpecification requestSpec) {
        requestSpec.header("Fineract-Platform-TenantId", tenant);
        requestSpec.header("Authorization", authorizationToken);
        requestSpec.header("Content-Type", "application/json;charset=UTF-8");
        return requestSpec;
    }

    private String getCurrentDate() {
        Date date = new Date();
        return new SimpleDateFormat("dd MMMM yyyy").format(date);
    }

    String getAlphaNumericString(int size) {
        String AlphaNumericString = "ABCDEFGHIJKLMNOPQRSTUVWXYZ" + "0123456789" + "abcdefghijklmnopqrstuvxyz";
        StringBuilder sb = new StringBuilder(size);
        for (int i = 0; i < size; i++) {
            int index = (int) (AlphaNumericString.length() * Math.random());
            sb.append(AlphaNumericString.charAt(index));
        }
        return sb.toString();
    }

    protected String setBodySavingsProduct() throws JsonProcessingException {
        // Generating product name and shortname
        String name = new StringBuilder().append(getAlphaNumericString(4)).toString();
        String shortName = getAlphaNumericString(4);

        PostSavingsProductsRequest savingsProductsRequest = new PostSavingsProductsRequest();
        savingsProductsRequest.setCurrencyCode("USD");
        savingsProductsRequest.setDigitsAfterDecimal(2);
        savingsProductsRequest.setInterestCompoundingPeriodType(1);
        savingsProductsRequest.setInterestPostingPeriodType(4);
        savingsProductsRequest.setInterestCalculationType(1);
        savingsProductsRequest.setInterestCalculationDaysInYearType(365);
        savingsProductsRequest.setAccountingRule(1);
        savingsProductsRequest.setName(name);
        savingsProductsRequest.setShortName(shortName);
        savingsProductsRequest.setInMultiplesOf(1);
        savingsProductsRequest.setNominalAnnualInterestRate(5.0);
        savingsProductsRequest.setLocale("en");

        return objectMapper.writeValueAsString(savingsProductsRequest);
    }

    protected String setBodySavingsApprove() throws JsonProcessingException {
        PostSavingsAccountsAccountIdRequest savingsApprove = new PostSavingsAccountsAccountIdRequest();
        savingsApprove.setApprovedOnDate(currentDate);
        savingsApprove.setLocale("en");
        savingsApprove.setDateFormat("dd MMMM yyyy");
        return objectMapper.writeValueAsString(savingsApprove);
    }

    protected String setBodySavingsAccount(String client) throws JsonProcessingException {
        // Getting resourceId and clientId
        PostClientsResponse createPayerClientResponse;

        if (client.equals("payer")) {
            createPayerClientResponse = objectMapper.readValue(responsePayerClient, PostClientsResponse.class);
        } else {
            createPayerClientResponse = objectMapper.readValue(responsePayeeClient, PostClientsResponse.class);
        }

        PostSavingsProductsResponse savingsProductResponse = objectMapper.readValue(responseSavingsProduct,
                PostSavingsProductsResponse.class);
        String date = getCurrentDate();
        setcurrentDate(date);
        externalId = UUID.randomUUID().toString();
        PostSavingsAccountsRequest savingsAccountsRequest = new PostSavingsAccountsRequest();
        savingsAccountsRequest.setClientId(createPayerClientResponse.getClientId());
        savingsAccountsRequest.setProductId(savingsProductResponse.getResourceId());
        savingsAccountsRequest.setDateFormat("dd MMMM yyyy");
        savingsAccountsRequest.setLocale("en");
        savingsAccountsRequest.setSubmittedOnDate(currentDate);
        savingsAccountsRequest.setExternalId(externalId);

        return objectMapper.writeValueAsString(savingsAccountsRequest);
    }

    protected String setBodyInteropIdentifier() throws JsonProcessingException {
        PostSavingsAccountsResponse savingsAccountResponse = objectMapper.readValue(responseSavingsProduct,
                PostSavingsAccountsResponse.class);
        String date = getCurrentDate();
        setcurrentDate(date);

        InteropIdentifierRequestData interopIdentifier = new InteropIdentifierRequestData();
        interopIdentifier.setAccountId(externalId);

        return objectMapper.writeValueAsString(interopIdentifier);
    }

    protected String setBodySavingsActivate() throws JsonProcessingException {
        PostSavingsAccountsAccountIdRequest savingsActivate = new PostSavingsAccountsAccountIdRequest();
        savingsActivate.setActivatedOnDate(currentDate);
        savingsActivate.setLocale("en");
        savingsActivate.setDateFormat("dd MMMM yyyy");
        return objectMapper.writeValueAsString(savingsActivate);
    }

    protected String setSavingsDepositAccount(int amount) throws JsonProcessingException {
        PostRecurringDepositAccountsRecurringDepositAccountIdTransactionsRequest savingsAccountDeposit = new PostRecurringDepositAccountsRecurringDepositAccountIdTransactionsRequest();
        savingsAccountDeposit.setLocale("en");
        savingsAccountDeposit.setDateFormat("dd MMMM yyyy");
        savingsAccountDeposit.setPaymentTypeId(1);
        savingsAccountDeposit.setTransactionAmount((double) amount);
        savingsAccountDeposit.setTransactionDate(currentDate);

        return objectMapper.writeValueAsString(savingsAccountDeposit);
    }

    protected String setBodyPayerClient() throws JsonProcessingException {
        String date = getCurrentDate();
        PostClientsRequest postClientsRequest = new PostClientsRequest();
        postClientsRequest.setOfficeId(1);
        postClientsRequest.setLegalFormId(1);
        postClientsRequest.setFirstname("John");
        postClientsRequest.setLastname("Wick");
        postClientsRequest.setActive(true);
        postClientsRequest.setLocale("en");
        postClientsRequest.setDateFormat("dd MMMM yyyy");
        postClientsRequest.setActivationDate(date);
        postClientsRequest.setSubmittedOnDate(date);
        return objectMapper.writeValueAsString(postClientsRequest);
    }

    protected String setBodyPayeeClient() throws JsonProcessingException {
        String date = getCurrentDate();
        PostClientsRequest postClientsRequest = new PostClientsRequest();
        postClientsRequest.setOfficeId(1);
        postClientsRequest.setLegalFormId(1);
        postClientsRequest.setFirstname("John");
        postClientsRequest.setLastname("Wick");
        postClientsRequest.setActive(true);
        postClientsRequest.setLocale("en");
        postClientsRequest.setDateFormat("dd MMMM yyyy");
        postClientsRequest.setActivationDate(date);
        postClientsRequest.setSubmittedOnDate(date);
        return objectMapper.writeValueAsString(postClientsRequest);
    }

    protected String setBodyClient(String client) throws JsonProcessingException {
        if (client.equals("payer")) {
            return setBodyPayerClient();
        } else if (client.equals("payee")) {
            return setBodyPayeeClient();
        } else if (client.equals("payee2")) {
            return setBodyPayeeClient();
        } else if (client.equals("payee3")) {
            return setBodyPayeeClient();
        }
        return client;
    }

    protected String setBodyPayeeQuoteRequest(String payerIdentifier, String payeeIdentifier, String amount, String quoteId)
            throws JsonProcessingException {
        QuoteSwitchRequestDTO requestDTO = new QuoteSwitchRequestDTO();

        requestDTO.setPayer(getParty(payerIdentifier, mojaloopConfig.payerFspId));
        requestDTO.setPayee(getParty(payeeIdentifier, mojaloopConfig.payeeFspId));
        requestDTO.setAmountType(AmountType.RECEIVE);
        requestDTO.setAmount(new MoneyData(amount, "USD"));
        requestDTO.setTransactionId(UUID.randomUUID().toString());
        requestDTO.setQuoteId(quoteId);
        TransactionType transactionType = new TransactionType();
        transactionType.setInitiatorType(InitiatorType.CONSUMER);
        transactionType.setInitiator(TransactionRole.PAYER);
        transactionType.setScenario(Scenario.TRANSFER);
        requestDTO.setTransactionType(transactionType);
        return objectMapper.writeValueAsString(requestDTO);
    }

    private Party getParty(String identifier, String fspId) {
        PartyIdInfo partyIdInfo = new PartyIdInfo(IdentifierType.MSISDN, identifier);
        partyIdInfo.setFspId(fspId);
        Party party = new Party(partyIdInfo);
        return party;
    }

    protected String setBodyPayeeTransferRequest(String amount, String ilpPacket, String condition) throws JsonProcessingException {
        TransferSwitchRequestDTO requestDTO = new TransferSwitchRequestDTO();
        requestDTO.setTransferId(UUID.randomUUID().toString());
        requestDTO.setPayeeFsp(mojaloopConfig.payerFspId);
        requestDTO.setPayeeFsp(mojaloopConfig.payeeFspId);
        requestDTO.setAmount(new MoneyData(amount, "USD"));
        requestDTO.setIlpPacket(ilpPacket);
        requestDTO.setCondition(condition);
        return objectMapper.writeValueAsString(requestDTO);
    }

}
