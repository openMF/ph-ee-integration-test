package org.mifos.integrationtest.cucumber.stepdef;

import io.cucumber.core.internal.com.fasterxml.jackson.core.JsonProcessingException;
import io.cucumber.core.internal.com.fasterxml.jackson.databind.ObjectMapper;
import io.restassured.RestAssured;
import io.restassured.builder.ResponseSpecBuilder;
import io.restassured.specification.RequestSpecification;
import org.apache.fineract.client.models.InteropIdentifierRequestData;
import org.apache.fineract.client.models.PostClientsRequest;
import org.apache.fineract.client.models.PostClientsResponse;
import org.apache.fineract.client.models.PostRecurringDepositAccountsRecurringDepositAccountIdTransactionsRequest;
import org.apache.fineract.client.models.PostSavingsAccountsAccountIdRequest;
import org.apache.fineract.client.models.PostSavingsAccountsRequest;
import org.apache.fineract.client.models.PostSavingsAccountsResponse;
import org.apache.fineract.client.models.PostSavingsProductsRequest;
import org.apache.fineract.client.models.PostSavingsProductsResponse;
import org.json.JSONException;
import org.json.JSONObject;
import org.mifos.connector.common.ams.dto.LoanRepaymentDTO;
import org.mifos.connector.common.gsma.dto.CustomData;
import org.mifos.connector.common.gsma.dto.GsmaTransfer;
import org.mifos.connector.common.gsma.dto.Party;
import org.mifos.integrationtest.common.Utils;
import org.mifos.integrationtest.common.dto.loan.AllowAttributeOverrides;
import org.mifos.integrationtest.common.dto.loan.LoanAccountData;
import org.mifos.integrationtest.common.dto.loan.LoanAccountResponse;
import org.mifos.integrationtest.common.dto.loan.LoanApprove;
import org.mifos.integrationtest.common.dto.loan.LoanDisburse;
import org.mifos.integrationtest.common.dto.loan.LoanProduct;
import org.mifos.integrationtest.common.dto.loan.LoanProductResponse;
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

        AllowAttributeOverrides allowAttributeOverrides = new AllowAttributeOverrides(true, true,
                "interest-principal-penalties-fees-order-strategy", true, true, true, true, true);

        LoanProduct loanProduct = new LoanProduct("USD", "false", false, "2", "2", 2, 3, 1, 0, 0, new ArrayList<>(), new ArrayList<>(),
                new ArrayList<>(), false, "1", 1, 1, true, 0, 2, 1, false, false, name, shortName, "12", "100", "3000", "21000", "3", "36",
                "60", "5.9", 19, "35.9", "1", true, true, "7", "90", 1, new ArrayList<>(), new ArrayList<>(), new ArrayList<>(),
                new ArrayList<>(), 1, allowAttributeOverrides, "en", "dd MMMM yyyy", new ArrayList<>(), false, null);

        return objectMapper.writeValueAsString(loanProduct);
    }

    protected String setBodyLoanAccount() throws JsonProcessingException {

        String date = getCurrentDate();
        setcurrentDate(date);
        PostClientsResponse createPayerClientResponse = objectMapper.readValue(
                responsePayerClient, PostClientsResponse.class);
        LoanProductResponse loanProductResponse = objectMapper.readValue(
                responseLoanProduct, LoanProductResponse.class);
        String clientId = createPayerClientResponse.getClientId().toString();
        int resourceId = Integer.parseInt(loanProductResponse.getResourceId());

        LoanAccountData loanAccountData = new LoanAccountData(clientId, resourceId, new ArrayList<>(), 7800, 12, 2, 12, 1, 2, 8.9, 1, false,
                0, 0, false, 7, 1, new ArrayList<>(), currentDate, "en", "dd MMMM yyyy", "individual", currentDate, currentDate);

        return objectMapper.writeValueAsString(loanAccountData);
    }

    private String getCurrentDate() {
        Date date = new Date();
        return new SimpleDateFormat("dd MMMM yyyy").format(date);
    }

    protected String setBodyLoanApprove(int amount) throws JsonProcessingException {
        LoanApprove loanApprove = new LoanApprove(currentDate, amount, currentDate, new ArrayList<>(), "en", "dd MMMM yyyy");
        return objectMapper.writeValueAsString(loanApprove);
    }

    protected String setBodyLoanRepayment(String amount) throws JsonProcessingException {
        LoanRepaymentDTO loanRepaymentDTO = new LoanRepaymentDTO(currentDate, "1", amount, "en", "dd MMMM yyyy");
        return objectMapper.writeValueAsString(loanRepaymentDTO);
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
        PostClientsResponse createPayerClientResponse = objectMapper.readValue(
                responsePayerClient, PostClientsResponse.class);
        PostSavingsProductsResponse savingsProductResponse = objectMapper.readValue(
                responseSavingsProduct, PostSavingsProductsResponse.class
        );
        String date = getCurrentDate();
        setcurrentDate(date);
        PostSavingsAccountsRequest savingsAccountsRequest = new PostSavingsAccountsRequest();
        savingsAccountsRequest.setClientId(createPayerClientResponse.getClientId());
        savingsAccountsRequest.setProductId(savingsProductResponse.getResourceId());
        savingsAccountsRequest.setDateFormat("dd MMMM yyyy");
        savingsAccountsRequest.setLocale("en");
        savingsAccountsRequest.setSubmittedOnDate(currentDate);

        return objectMapper.writeValueAsString(savingsAccountsRequest);
    }
    protected String setBodyInteropIdentifier() throws  JsonProcessingException {
        PostSavingsAccountsResponse savingsAccountResponse = objectMapper.readValue(
                responseSavingsProduct, PostSavingsAccountsResponse.class
        );
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
        LoanDisburse loanDisburse = new LoanDisburse(1, amount, date, "en", "dd MMMM yyyy");
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
        PostSavingsAccountsResponse savingsAccountResponse = objectMapper.readValue(
                responseSavingsAccount, PostSavingsAccountsResponse.class);
        return savingsAccountResponse.getSavingsId().toString();
    }

    private String getLoanAccountId() throws JsonProcessingException {
        LoanAccountResponse loanAccountResponse = objectMapper.readValue(responseLoanAccount, LoanAccountResponse.class);
        String loanId = Integer.toString(loanAccountResponse.getLoanId());
        return getLoanIdFromFineract(loanId);
    }

    private String getLoanIdFromFineract(String loanId) {
        RequestSpecification requestSpecification = Utils.getDefaultSpec();
        requestSpecification = setHeaders(requestSpecification);
        loanGetAccountIdEndpoint = loanGetAccountIdEndpoint.replaceAll("\\{\\{loanAccId\\}\\}", loanId);
        String response = RestAssured.given(requestSpecification).baseUri(loanBaseUrl).expect()
                .spec(new ResponseSpecBuilder().expectStatusCode(200).build()).when().get(loanGetAccountIdEndpoint).andReturn().asString();
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
