package org.mifos.integrationtest.cucumber.stepdef;

import io.cucumber.core.internal.com.fasterxml.jackson.core.JsonProcessingException;
import io.cucumber.core.internal.com.fasterxml.jackson.databind.ObjectMapper;
import io.restassured.RestAssured;
import io.restassured.builder.ResponseSpecBuilder;
import io.restassured.specification.RequestSpecification;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.UUID;
import org.apache.fineract.client.models.InteropIdentifierRequestData;
import org.apache.fineract.client.models.PostClientsRequest;
import org.apache.fineract.client.models.PostClientsResponse;
import org.apache.fineract.client.models.PostLoanProductsRequest;
import org.apache.fineract.client.models.PostLoanProductsResponse;
import org.apache.fineract.client.models.PostLoansLoanIdTransactionsTransactionIdRequest;
import org.apache.fineract.client.models.PostRecurringDepositAccountsRecurringDepositAccountIdTransactionsRequest;
import org.apache.fineract.client.models.PostSavingsAccountsAccountIdRequest;
import org.apache.fineract.client.models.PostSavingsAccountsRequest;
import org.apache.fineract.client.models.PostSavingsAccountsResponse;
import org.apache.fineract.client.models.PostSavingsProductsRequest;
import org.apache.fineract.client.models.PostSavingsProductsResponse;
import org.apache.fineract.client.models.PostSelfLoansLoanIdApprove;
import org.apache.fineract.client.models.PostSelfLoansLoanIdDisburse;
import org.apache.fineract.client.models.PostSelfLoansLoanIdResponse;
import org.apache.fineract.client.models.PostSelfLoansRequest;
import org.json.JSONException;
import org.json.JSONObject;
import org.mifos.connector.common.gsma.dto.CustomData;
import org.mifos.connector.common.gsma.dto.GsmaTransfer;
import org.mifos.connector.common.gsma.dto.Party;
import org.mifos.integrationtest.common.Utils;
import org.mifos.integrationtest.config.GsmaConfig;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;

@Component
public class GSMATransferDef extends GsmaConfig {

    public String gsmaTransferBody;
    public String loanProductBody;
    public String amsName;
    public int amount;
    public String accountId;
    public String acccountHoldingInstitutionId;
    public String gsmaTransactionResponse;
    public String responseLoanProduct;
    public String tenant;
    public String currentDate;
    protected String loanAccountBody;
    protected String responseLoanAccount;
    protected String loanApproveBody;
    protected String responseLoanApprove;
    protected String loanRepaymentBody;
    protected String responseLoanRepayment;
    protected String savingsProductBody;
    protected String responseSavingsProduct;
    protected String savingsApproveBody;
    protected String responseSavingsApprove;
    protected String savingsAccountBody;
    protected String interopIdentifierBody;
    protected String responseSavingsAccount;
    protected String responseInteropIdentifier;
    protected String savingsActivateBody;
    protected String responseSavingsActivate;
    protected String savingsDepositAccountBody;
    protected String responseSavingsDepositAccount;
    protected String createPayerClientBody;
    protected String responsePayerClient;
    protected String payerClientBody;
    protected String loanDisburseBody;
    protected String responseLoanDisburse;
    protected String externalId;
    @Autowired
    ObjectMapper objectMapper;

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

    protected void setcurrentDate(String date) {
        currentDate = date;
    }

    protected RequestSpecification setHeaders(RequestSpecification requestSpec) {
        requestSpec.header("Fineract-Platform-TenantId", tenant);
        requestSpec.header("Authorization", authorizationToken);
        requestSpec.header("Content-Type", "application/json;charset=UTF-8");
        return requestSpec;
    }

    protected String setBodyLoanProduct() throws JsonProcessingException {
        // Generating product name and shortname
        String name = new StringBuilder().append(getAlphaNumericString(4)).append(tenant).toString();
        String shortName = getAlphaNumericString(4);
        PostLoanProductsRequest loanProductsRequest = new PostLoanProductsRequest();
        loanProductsRequest.setCurrencyCode("USD");
        loanProductsRequest.setInMultiplesOf(2);
        loanProductsRequest.setDigitsAfterDecimal(2);
        loanProductsRequest.setDaysInYearType(1);
        loanProductsRequest.setInterestCalculationPeriodType(0);
        loanProductsRequest.setIsInterestRecalculationEnabled(false);
        loanProductsRequest.setName(name);
        loanProductsRequest.setShortName(shortName);
        loanProductsRequest.setInMultiplesOf(12);
        loanProductsRequest.setPrincipal((double) 3000);
        loanProductsRequest.setNumberOfRepayments(36);
        loanProductsRequest.setRepaymentFrequencyType(2);
        loanProductsRequest.setInterestType(1);
        loanProductsRequest.setInterestRatePerPeriod(19.0);
        loanProductsRequest.setRepaymentEvery(1);
        loanProductsRequest.setTransactionProcessingStrategyCode("mifos-standard-strategy");
        loanProductsRequest.setAmortizationType(1);
        loanProductsRequest.setAccountingRule(1);
        loanProductsRequest.setInterestRateFrequencyType(2);
        loanProductsRequest.setDaysInMonthType(1);
        loanProductsRequest.setLocale("en");
        loanProductsRequest.setDateFormat("dd MMMM yyyy");

        return objectMapper.writeValueAsString(loanProductsRequest);
    }

    protected String setBodyLoanAccount() throws JsonProcessingException {

        String date = getCurrentDate();
        setcurrentDate(date);
        PostClientsResponse createPayerClientResponse = objectMapper.readValue(responsePayerClient, PostClientsResponse.class);
        PostLoanProductsResponse loanProductResponse = objectMapper.readValue(responseLoanProduct, PostLoanProductsResponse.class);
        Integer clientId = createPayerClientResponse.getClientId();
        Integer resourceId = loanProductResponse.getResourceId();

        PostSelfLoansRequest loanAccountData = new PostSelfLoansRequest();
        loanAccountData.setClientId(clientId);
        loanAccountData.setProductId(resourceId);
        loanAccountData.setDisbursementData(null);
        loanAccountData.setPrincipal(7800.00);
        loanAccountData.setLoanTermFrequency(12);
        loanAccountData.setLoanTermFrequencyType(2);
        loanAccountData.setNumberOfRepayments(12);
        loanAccountData.setRepaymentEvery(1);
        loanAccountData.setRepaymentFrequencyType(2);
        loanAccountData.setInterestRatePerPeriod(8.9);
        loanAccountData.setAmortizationType(1);
        loanAccountData.setInterestType(0);
        loanAccountData.setInterestCalculationPeriodType(0);
        loanAccountData.setTransactionProcessingStrategyCode("mifos-standard-strategy");
        loanAccountData.setLocale("en");
        loanAccountData.setDateFormat("dd MMMM yyyy");
        loanAccountData.setLoanType("individual");
        loanAccountData.setExpectedDisbursementDate(currentDate);
        loanAccountData.setSubmittedOnDate(currentDate);
        return objectMapper.writeValueAsString(loanAccountData);
    }

    private String getCurrentDate() {
        Date date = new Date();
        return new SimpleDateFormat("dd MMMM yyyy").format(date);
    }

    protected String setBodyLoanApprove(int amount) throws JsonProcessingException {
        PostSelfLoansLoanIdApprove loanApprove = new PostSelfLoansLoanIdApprove();
        loanApprove.setApprovedOnDate(currentDate);
        loanApprove.setApprovedLoanAmount(amount);
        loanApprove.setLocale("en");
        loanApprove.setDateFormat("dd MMMM yyyy");
        return objectMapper.writeValueAsString(loanApprove);
    }

    protected String setBodyLoanRepayment(String amount) throws JsonProcessingException {
        PostLoansLoanIdTransactionsTransactionIdRequest loanRepayment = new PostLoansLoanIdTransactionsTransactionIdRequest();
        loanRepayment.setTransactionDate(currentDate);
        loanRepayment.setPaymentTypeId(1);
        loanRepayment.setTransactionAmount(Double.valueOf(amount));
        loanRepayment.setLocale("en");
        loanRepayment.setDateFormat("dd MMMM yyyy");
        return objectMapper.writeValueAsString(loanRepayment);
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

    protected String setBodySavingsAccount() throws JsonProcessingException {
        // Getting resourceId and clientId
        PostClientsResponse createPayerClientResponse = objectMapper.readValue(responsePayerClient, PostClientsResponse.class);
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

    protected String setBodyLoanDisburse(int amount) throws JsonProcessingException {
        String date = getCurrentDate();
        setcurrentDate(date);
        PostSelfLoansLoanIdDisburse loanDisburse = new PostSelfLoansLoanIdDisburse();
        loanDisburse.setPaymentTypeId(1);
        loanDisburse.setTransactionAmount(amount);
        loanDisburse.setActualDisbursementDate(currentDate);
        loanDisburse.setLocale("en");
        loanDisburse.setDateFormat("dd MMMM yyyy");
        return objectMapper.writeValueAsString(loanDisburse);
    }

    protected String setGsmaTransactionBody(String prefix) throws JsonProcessingException {
        String accountId = getAccountId(prefix);
        ArrayList<CustomData> customData = new ArrayList<>();
        CustomData customDataObj = new CustomData();
        customDataObj.setKey("key");
        customDataObj.setValue("value");
        customData.add(customDataObj);

        ArrayList<Party> payer = new ArrayList<>();
        Party payerObject = new Party();
        payerObject.setPartyIdType("MSISDN");
        payerObject.setPartyIdIdentifier("+44999911");
        payer.add(payerObject);

        ArrayList<Party> payee = new ArrayList<>();
        Party payeeObject = new Party();
        payeeObject.setPartyIdType("accountId");
        payeeObject.setPartyIdIdentifier(accountId);
        payee.add(payeeObject);

        String dateFormatGiven = "yyyy-MM-dd'T'HH:mm:ss.SSSXXX";
        String currentDate = new SimpleDateFormat(dateFormatGiven).format(new Date());

        GsmaTransfer gsmaTransfer = new GsmaTransfer("RKTQDM7W6S", "inbound", "transfer", Integer.toString(amount), "USD", "note",
                currentDate, customData, payer, payee);
        return objectMapper.writeValueAsString(gsmaTransfer);
    }

    private String getAccountId(String prefix) throws JsonProcessingException {
        String accountNumber = "";
        if (prefix.equalsIgnoreCase("S")) {
            accountNumber = getSavingsAccountId();
        } else {
            accountNumber = getLoanAccountId();
        }
        String accountId = new StringBuilder().append(prefix).append(accountNumber).toString();
        return accountId;
    }

    private String getSavingsAccountId() throws JsonProcessingException {
        PostSavingsAccountsResponse savingsAccountResponse = objectMapper.readValue(responseSavingsAccount,
                PostSavingsAccountsResponse.class);
        return savingsAccountResponse.getSavingsId().toString();
    }

    private String getLoanAccountId() throws JsonProcessingException {
        PostSelfLoansLoanIdResponse loanAccountResponse = objectMapper.readValue(responseLoanAccount, PostSelfLoansLoanIdResponse.class);
        String loanId = loanAccountResponse.getLoanId().toString();
        return getLoanIdFromFineract(loanId);
    }

    private String getLoanIdFromFineract(String loanId) {
        RequestSpecification requestSpecification = Utils.getDefaultSpec();
        // this is to change the tenant for checking account in different tenant.
        setTenant(acccountHoldingInstitutionId);
        requestSpecification = setHeaders(requestSpecification);
        loanGetAccountIdEndpoint = loanGetAccountIdEndpoint.replaceAll("\\{\\{loanAccId\\}\\}", loanId);
        String response = RestAssured.given(requestSpecification).baseUri(loanBaseUrl).expect()
                .spec(new ResponseSpecBuilder().expectStatusCode(200).build()).when().get(loanGetAccountIdEndpoint).andReturn().asString();
        String accountNp = "";
        // this is to change the tenant again for paymenthub operations to make API call.
        setTenant(tenant);
        try {
            JSONObject jsonResponse = new JSONObject(response);
            accountNp = jsonResponse.getString("accountNo");
        } catch (JSONException e) {
            throw new RuntimeException(e);
        }
        return accountNp;

    }
}
