package org.mifos.integrationtest.cucumber.stepdef;

import io.cucumber.core.internal.com.fasterxml.jackson.core.JsonProcessingException;
import io.cucumber.core.internal.com.fasterxml.jackson.databind.ObjectMapper;
import io.restassured.RestAssured;
import io.restassured.builder.ResponseSpecBuilder;
import io.restassured.specification.RequestSpecification;
import org.json.JSONException;
import org.json.JSONObject;
import org.mifos.connector.common.ams.dto.LoanRepaymentDTO;
import org.mifos.connector.common.gsma.dto.CustomData;
import org.mifos.connector.common.gsma.dto.GsmaTransfer;
import org.mifos.connector.common.gsma.dto.Party;
import org.mifos.integrationtest.common.Utils;
import org.mifos.integrationtest.common.dto.loan.AllowAttributeOverrides;
import org.mifos.integrationtest.common.dto.loan.CreatePayerClient;
import org.mifos.integrationtest.common.dto.loan.CreatePayerClientResponse;
import org.mifos.integrationtest.common.dto.loan.LoanAccountData;
import org.mifos.integrationtest.common.dto.loan.LoanAccountResponse;
import org.mifos.integrationtest.common.dto.loan.LoanApprove;
import org.mifos.integrationtest.common.dto.loan.LoanDisburse;
import org.mifos.integrationtest.common.dto.loan.LoanProduct;
import org.mifos.integrationtest.common.dto.loan.LoanProductResponse;
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
    protected String responseSavingsAccount;
    protected String savingsActivateBody;
    protected String responseSavingsActivate;
    protected String savingsDepositAccountBody;
    protected String responseSavingsDepositAccount;
    protected String createPayerClientBody;
    protected String responsePayerClient;
    protected String payerClientBody;
    protected String loanDisburseBody;
    protected String responseLoanDisburse;
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

        AllowAttributeOverrides allowAttributeOverrides = new AllowAttributeOverrides(true, true, "interest-principal-penalties-fees-order-strategy", true, true, true, true, true);

        LoanProduct loanProduct = new LoanProduct("USD", "false", false, "2", "2", 2, 3, 1, 0, 0, new ArrayList<>(), new ArrayList<>(), new ArrayList<>(), false, "1", 1, 1, true, 0, 2, 1, false, false, name, shortName, "12", "100", "3000", "21000", "3", "36", "60", "5.9", 19, "35.9", "1", true, true, "7", "90", 1, new ArrayList<>(), new ArrayList<>(), new ArrayList<>(), new ArrayList<>(), "interest-principal-penalties-fees-order-strategy", allowAttributeOverrides, "en", "dd MMMM yyyy", new ArrayList<>(), false, null);

        return objectMapper.writeValueAsString(loanProduct);
    }


    protected String setBodyLoanAccount() throws JsonProcessingException {

        String date = getCurrentDate();
        setcurrentDate(date);

        CreatePayerClientResponse createPayerClientResponse = objectMapper.readValue(
                responsePayerClient, CreatePayerClientResponse.class);
        LoanProductResponse loanProductResponse = objectMapper.readValue(
                responseLoanProduct, LoanProductResponse.class);
        String clientId = createPayerClientResponse.getClientId();
        int resourceId = Integer.parseInt(loanProductResponse.getResourceId());

        LoanAccountData loanAccountData = new LoanAccountData(clientId, resourceId, new ArrayList<>(), 7800, 12, 2, 12, 1, 2, 8.9, 1, false, 0, 0, false, 7, "interest-principal-penalties-fees-order-strategy", new ArrayList<>(), currentDate, "en", "dd MMMM yyyy", "individual", currentDate, currentDate);

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

        SavingsAccount savingsAccount = new SavingsAccount(savingsProductResponse.getResourceId(), 5, false, false, false, false, false, 1, 4, 1, 365, currentDate, "en", "dd MMMM yyyy", "dd MMM", new ArrayList<>(), createPayerClientResponse.getClientId(), "");

        return objectMapper.writeValueAsString(savingsAccount);
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
        LoanDisburse loanDisburse = new LoanDisburse(1, amount, date, "en", "dd MMMM yyyy");
        return objectMapper.writeValueAsString(loanDisburse);
    }

    protected String setGsmaTransactionBody(String prefix) throws JsonProcessingException {
        String accountId = getAccountId(prefix);
        ArrayList<CustomData> customData = new ArrayList<>();
        CustomData customDataObj = new CustomData();
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
        LoanAccountResponse loanAccountResponse = objectMapper.readValue(
                responseLoanAccount, LoanAccountResponse.class);
        String loanId = Integer.toString(loanAccountResponse.getLoanId());
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
