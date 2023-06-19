package org.mifos.integrationtest.cucumber.stepdef;

import io.cucumber.core.internal.com.fasterxml.jackson.core.JsonProcessingException;
import io.cucumber.core.internal.com.fasterxml.jackson.databind.ObjectMapper;
import io.cucumber.java.sl.In;
import io.restassured.RestAssured;
import io.restassured.builder.ResponseSpecBuilder;
import io.restassured.specification.RequestSpecification;
import org.apache.fineract.client.models.PostClientsResponse;
import org.apache.fineract.client.models.PostLoanProductsRequest;
import org.apache.fineract.client.models.PostLoanProductsResponse;
import org.apache.fineract.client.models.PostLoansLoanIdTransactionsTransactionIdRequest;
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
import org.mifos.integrationtest.common.dto.loan.CreatePayerClient;
import org.mifos.integrationtest.common.dto.loan.CreatePayerClientResponse;
import org.mifos.integrationtest.common.dto.savings.InteropIdentifier;
import org.mifos.integrationtest.common.dto.savings.SavingsAccount;
import org.mifos.integrationtest.common.dto.savings.SavingsAccountDeposit;
import org.mifos.integrationtest.common.dto.savings.SavingsAccountResponse;
import org.mifos.integrationtest.common.dto.savings.SavingsActivate;
import org.mifos.integrationtest.common.dto.savings.SavingsApprove;
import org.mifos.integrationtest.common.dto.savings.SavingsProduct;
import org.mifos.integrationtest.common.dto.savings.SavingsProductResponse;
import org.mifos.integrationtest.config.GsmaConfig;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;

import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.UUID;

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
        loanProductsRequest.setPrincipal((double)3000);
        loanProductsRequest.setNumberOfRepayments(36);
        loanProductsRequest.setRepaymentFrequencyType(2);
        loanProductsRequest.setInterestType(1);
        loanProductsRequest.setInterestRatePerPeriod(19.0);
        loanProductsRequest.setRepaymentEvery(1);
        loanProductsRequest.setTransactionProcessingStrategyId(1);
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
        PostClientsResponse createPayerClientResponse = objectMapper.readValue(
                responsePayerClient, PostClientsResponse.class);
        PostLoanProductsResponse loanProductResponse = objectMapper.readValue(
                responseLoanProduct, PostLoanProductsResponse.class);
        Integer clientId = createPayerClientResponse.getClientId();
        Integer resourceId = loanProductResponse.getResourceId();

        PostSelfLoansRequest loanAccountData  = new PostSelfLoansRequest();
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
        loanAccountData.setTransactionProcessingStrategyId(1);
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
        String AlphaNumericString = "ABCDEFGHIJKLMNOPQRSTUVWXYZ"
                + "0123456789"
                + "abcdefghijklmnopqrstuvxyz";
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

        SavingsProduct savingsProduct = new SavingsProduct("USD", 2, 1, 4, 1, 365, "1", name, shortName, "1", 5, new ArrayList<>(), new ArrayList<>(), new ArrayList<>(), new ArrayList<>(), "en");

        return objectMapper.writeValueAsString(savingsProduct);
    }

    protected String setBodySavingsApprove() throws JsonProcessingException {
        SavingsApprove savingsApprove = new SavingsApprove(currentDate, "en", "dd MMMM yyyy");
        return objectMapper.writeValueAsString(savingsApprove);
    }

    protected String setBodySavingsAccount() throws JsonProcessingException {
        // Getting resourceId and clientId
        CreatePayerClientResponse createPayerClientResponse = objectMapper.readValue(
                responsePayerClient, CreatePayerClientResponse.class);
        SavingsProductResponse savingsProductResponse = objectMapper.readValue(
                responseSavingsProduct, SavingsProductResponse.class
        );
        String date = getCurrentDate();
        setcurrentDate(date);
        externalId = UUID.randomUUID().toString();
        SavingsAccount savingsAccount = new SavingsAccount(savingsProductResponse.getResourceId(), 5, false, false, false, false, false, 1, 4, 1, 365, currentDate, "en", "dd MMMM yyyy", "dd MMM", new ArrayList<>(), createPayerClientResponse.getClientId(), externalId);

        return objectMapper.writeValueAsString(savingsAccount);
    }
    protected String setBodyInteropIdentifier() throws  JsonProcessingException {
        SavingsAccountResponse savingsAccountResponse = objectMapper.readValue(
                responseSavingsProduct, SavingsAccountResponse.class
        );
        String date = getCurrentDate();
        setcurrentDate(date);

        InteropIdentifier interopIdentifier = new InteropIdentifier(externalId);

        return objectMapper.writeValueAsString(interopIdentifier);
    }

    protected String setBodySavingsActivate() throws JsonProcessingException {
        SavingsActivate savingsActivate = new SavingsActivate(currentDate, "en", "dd MMMM yyyy");
        return objectMapper.writeValueAsString(savingsActivate);
    }

    protected String setSavingsDepositAccount(int amount) throws JsonProcessingException {
        SavingsAccountDeposit savingsAccountDeposit = new SavingsAccountDeposit("dd MMMM yyyy", "en", 1, amount, currentDate);
        return objectMapper.writeValueAsString(savingsAccountDeposit);
    }

    protected String setBodyPayerClient() throws JsonProcessingException {
        String date = getCurrentDate();
        CreatePayerClient createPayerClient = new CreatePayerClient(new ArrayList<>(), new ArrayList<>(), 1, 1, "John", "Wick", true, "en", "dd MMMM yyyy", date, date, null);
        return objectMapper.writeValueAsString(createPayerClient);
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

        GsmaTransfer gsmaTransfer = new GsmaTransfer("RKTQDM7W6S", "inbound", "transfer", Integer.toString(amount), "USD", "note", currentDate, customData, payer, payee);
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
        SavingsAccountResponse savingsAccountResponse = objectMapper.readValue(
                responseSavingsAccount, SavingsAccountResponse.class);
        return savingsAccountResponse.getSavingsId();
    }

    private String getLoanAccountId() throws JsonProcessingException {
        PostSelfLoansLoanIdResponse loanAccountResponse = objectMapper.readValue(
                responseLoanAccount, PostSelfLoansLoanIdResponse.class);
        String loanId = loanAccountResponse.getLoanId().toString();
        return getLoanIdFromFineract(loanId);
    }

    private String getLoanIdFromFineract(String loanId) {
        RequestSpecification requestSpecification = Utils.getDefaultSpec();
        requestSpecification = setHeaders(requestSpecification);
        loanGetAccountIdEndpoint = loanGetAccountIdEndpoint.replaceAll("\\{\\{loanAccId\\}\\}", loanId);
        String response = RestAssured.given(requestSpecification)
                .baseUri(loanBaseUrl)
                .expect()
                .spec(new ResponseSpecBuilder().expectStatusCode(200).build())
                .when()
                .get(loanGetAccountIdEndpoint)
                .andReturn().asString();
        String accountNp = "";

        try {
            JSONObject jsonResponse = new JSONObject(response);
            accountNp = jsonResponse.getString("accountNo");
        } catch (JSONException e) {
            throw new RuntimeException(e);
        }
        return accountNp;

    }
}
