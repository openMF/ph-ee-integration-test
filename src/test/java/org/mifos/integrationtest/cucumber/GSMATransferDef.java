package org.mifos.integrationtest.cucumber;

import io.cucumber.core.internal.com.fasterxml.jackson.core.JsonProcessingException;
import io.cucumber.core.internal.com.fasterxml.jackson.databind.ObjectMapper;
import io.restassured.specification.RequestSpecification;
import org.mifos.connector.common.ams.dto.LoanRepaymentDTO;
import org.mifos.connector.common.gsma.dto.CustomData;
import org.mifos.connector.common.gsma.dto.GsmaTransfer;
import org.mifos.connector.common.gsma.dto.Party;
import org.mifos.integrationtest.common.dto.loan.AllowAttributeOverrides;
import org.mifos.integrationtest.common.dto.loan.CreatePayerClient;
import org.mifos.integrationtest.common.dto.loan.CreatePayerClientResponse;
import org.mifos.integrationtest.common.dto.loan.LoanAccountData;
import org.mifos.integrationtest.common.dto.loan.LoanApprove;
import org.mifos.integrationtest.common.dto.loan.LoanDisburse;
import org.mifos.integrationtest.common.dto.loan.LoanProduct;
import org.mifos.integrationtest.common.dto.loan.LoanProductResponse;
import org.mifos.integrationtest.common.dto.savings.SavingsAccount;
import org.mifos.integrationtest.common.dto.savings.SavingsAccountDeposit;
import org.mifos.integrationtest.common.dto.savings.SavingsActivate;
import org.mifos.integrationtest.common.dto.savings.SavingsApprove;
import org.mifos.integrationtest.common.dto.savings.SavingsProduct;
import org.mifos.integrationtest.common.dto.savings.SavingsProductResponse;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;

@Component
public class GSMATransferDef {
    public static String gsmaTransferBody;
    public static String loanProductBody;
    public static String amsName;
    public static int amount;
    public static String accountId;
    public static String acccountHoldingInstitutionId;

    public static String gsmaTransactionResponse;
    public static String responseLoanProduct;
    public static String tenant;
    public static String currentDate;
    protected static String loanAccountBody;
    protected static String responseLoanAccount;
    protected static String loanApproveBody;
    protected static String responseLoanApprove;
    protected static String loanRepaymentBody;
    protected static String responseLoanRepayment;
    protected static String savingsProductBody;
    protected static String responseSavingsProduct;
    protected static String savingsApproveBody;
    protected static String responseSavingsApprove;
    protected static String savingsAccountBody;
    protected static String responseSavingsAccount;
    protected static String savingsActivateBody;
    protected static String responseSavingsActivate;
    protected static String savingsDepositAccountBody;
    protected static String responseSavingsDepositAccount;
    protected static String createPayerClientBody;
    protected static String responsePayerClient;
    protected static String payerClientBody;
    protected static String loanDisburseBody;
    protected static String responseLoanDisburse;
    protected static String gsmaTransfer;

    @Autowired
    static ObjectMapper objectMapper;

    Logger logger = LoggerFactory.getLogger(this.getClass());


    public static void setAccountId(String acId) {
        accountId = acId;
    }

    public static void setHeadersMifos(String ams, String aHIId, int amnt) {
        amsName = ams;
        acccountHoldingInstitutionId = aHIId;
        amount = amnt;
    }

    protected static void setTenant(String tenantName) {
        tenant = tenantName;
    }

    protected static void setcurrentDate(String date) {
        currentDate = date;
    }

    protected static RequestSpecification setHeadersLoan(RequestSpecification requestSpec) {
        requestSpec.header("Fineract-Platform-TenantId", tenant);
        requestSpec.header("Accept", "application/json, text/plain, */*");
        requestSpec.header("Accept-Language", "en-GB,en-US;q=0.9,en;q=0.8");
        requestSpec.header("Accept-Encoding", "gzip, deflate, br");
        requestSpec.header("Authorization", "Basic bWlmb3M6cGFzc3dvcmQ=");
        requestSpec.header("Origin", "https://65.0.42.17:8443");
        requestSpec.header("Referer", "https://65.0.42.17:8443/");
        requestSpec.header("Sec-Fetch-Dest", "empty");
        requestSpec.header("Sec-Fetch-Mode", "cors");
        requestSpec.header("Sec-Fetch-Site", "cross-site");
        requestSpec.header("sec-ch-ua-mobile", "?0");
        requestSpec.header("sec-ch-ua-platform", "sec-ch-ua-platform");
        requestSpec.header("sec-ch-ua", "\"Google Chrome\";v=\"105\", \"Not)A;Brand\";v=\"8\", \"Chromium\";v=\"105\"");
        requestSpec.header("User-Agent", "Mozilla/5.0 (Macintosh; Intel Mac OS X 10_15_7) AppleWebKit/537.36 (KHTML, like Gecko) Chrome/105.0.0.0 Safari/537.36");
        requestSpec.header("Content-Type", "application/json;charset=UTF-8");
        return requestSpec;
    }

    protected static String setBodyLoanProduct() {
        // Generating product name and shortname
        String name = new StringBuilder().append("loan").append(getAlphaNumericString(4)).toString();
        String shortName = getAlphaNumericString(4);

        AllowAttributeOverrides allowAttributeOverrides = new AllowAttributeOverrides(true, true, "interest-principal-penalties-fees-order-strategy", true, true, true, true, true);

        LoanProduct loanProduct = new LoanProduct("USD", "false", false, "2", "2", 2, 3, 1, 0, 6, new ArrayList<>(), new ArrayList<>(), new ArrayList<>(), false, "1", 1, 1, true, 0, 2, 1, false, false, name, shortName, "12", "100", "3000", "21000", "3", "36", "60", "5.9", 19, "35.9", "1", true, true, "7", "90", 1, new ArrayList<>(), new ArrayList<>(), new ArrayList<>(), new ArrayList<>(), "interest-principal-penalties-fees-order-strategy", allowAttributeOverrides, "en", "dd MMMM yyyy", new ArrayList<>(), false, null);

        return loanProduct.toString();
    }


    protected static String setBodyLoanAccount() throws JsonProcessingException {

        String date = getCurrentDate();
        setcurrentDate(date);

        CreatePayerClientResponse createPayerClientResponse = objectMapper.readValue(
                responsePayerClient, CreatePayerClientResponse.class);
        LoanProductResponse loanProductResponse = objectMapper.readValue(
                responseLoanProduct, LoanProductResponse.class);

        LoanAccountData loanAccountData = new LoanAccountData(createPayerClientResponse.getClientId(), Integer.parseInt(loanProductResponse.getResourceId()), new ArrayList<>(), 7800, 12, 2, 12, 1, 2, 8.9, 1, false, 0, 0, false, 7, "interest-principal-penalties-fees-order-strategy", new ArrayList<>(), currentDate, "en", "dd MMMM yyyy", "individual", currentDate, currentDate);

        return loanAccountData.toString();
    }

    private static String getCurrentDate() {
        Date date = new Date();
        return new SimpleDateFormat("dd MMMM yyyy").format(date);
    }

    protected static String setBodyLoanApprove(int amount) {
        LoanApprove loanApprove = new LoanApprove(currentDate, amount, currentDate, new ArrayList<>(), "en", "dd MMMM yyyy");
        return loanApprove.toString();
    }

    protected static Object setBodyLoanRepayment(String amount) {
        LoanRepaymentDTO loanRepaymentDTO = new LoanRepaymentDTO(currentDate, "1", amount, "en", "dd MMMM yyyy");
        return loanRepaymentDTO.toString();
    }

    protected static RequestSpecification setHeadersSavings(RequestSpecification requestSpec) {
        requestSpec.header("fineract-platform-tenantid", tenant);
        requestSpec.header("accept", "application/json, text/plain, */*");
        requestSpec.header("accept-language", "en-GB,en-US;q=0.9,en;q=0.8");
        requestSpec.header("authorization", "Basic bWlmb3M6cGFzc3dvcmQ=");
        requestSpec.header("authority", "fynams.mifos.g2pconnect.io");
        requestSpec.header("sec-fetch-dest", "empty");
        requestSpec.header("sec-fetch-mode", "cors");
        requestSpec.header("sec-fetch-site", "cross-site");
        requestSpec.header("sec-ch-ua-mobile", "?0");
        requestSpec.header("sec-ch-ua-platform", "sec-ch-ua-platform");
        requestSpec.header("sec-ch-ua", "\"Google Chrome\";v=\"105\", \"Not)A;Brand\";v=\"8\", \"Chromium\";v=\"105\"");
        requestSpec.header("user-agent", "Mozilla/5.0 (Macintosh; Intel Mac OS X 10_15_7) AppleWebKit/537.36 (KHTML, like Gecko) Chrome/105.0.0.0 Safari/537.36");
        requestSpec.header("content-type", "application/json;charset=UTF-8");
        return requestSpec;
    }

    static String getAlphaNumericString(int size) {
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

    protected static String setBodySavingsProduct() {
        // Generating product name and shortname
        String name = new StringBuilder().append("product").append(getAlphaNumericString(3)).toString();
        String shortName = getAlphaNumericString(3);

        SavingsProduct savingsProduct = new SavingsProduct("USD", 2, 1, 4, 1, 365, "1", name, shortName, "1", 5, new ArrayList<>(), new ArrayList<>(), new ArrayList<>(), new ArrayList<>(), "en");

        return savingsProduct.toString();
    }

    protected static String setBodySavingsApprove() {
        SavingsApprove savingsApprove = new SavingsApprove(currentDate, "en", "dd MMMM yyyy");
        return savingsApprove.toString();
    }

    protected static String setBodySavingsAccount() throws JsonProcessingException {
        // Getting resourceId and clientId
        CreatePayerClientResponse createPayerClientResponse = objectMapper.readValue(
                responsePayerClient, CreatePayerClientResponse.class);
        SavingsProductResponse savingsProductResponse = objectMapper.readValue(
                responseSavingsProduct, SavingsProductResponse.class
        );

        SavingsAccount savingsAccount = new SavingsAccount(savingsProductResponse.getResourceId(), 5, false, false, false, false, false, 1, 4, 1, 365, currentDate, "en", "dd MMMM yyyy", "dd MMM", new ArrayList<>(), createPayerClientResponse.getClientId(), "");

        return savingsAccount.toString();
    }

    protected static String setBodySavingsActivate() {
        SavingsActivate savingsActivate = new SavingsActivate(currentDate, "en", "dd MMMM yyyy");
        return savingsActivate.toString();
    }

    protected static String setSavingsDepositAccount(int amount) {
        SavingsAccountDeposit savingsAccountDeposit = new SavingsAccountDeposit("dd MMMM yyyy", "en", 1, amount, currentDate);
        return savingsAccountDeposit.toString();
    }

    protected static RequestSpecification setHeadersClient(RequestSpecification requestSpec) {
        requestSpec.header("fineract-platform-tenantid", tenant);
        requestSpec.header("accept", "application/json, text/plain, */*");
        requestSpec.header("accept-language", "en-GB,en-US;q=0.9,en;q=0.8");
        requestSpec.header("authorization", "Basic bWlmb3M6cGFzc3dvcmQ=");
        requestSpec.header("authority", "fynams.mifos.g2pconnect.io");
        requestSpec.header("sec-fetch-dest", "empty");
        requestSpec.header("sec-fetch-mode", "cors");
        requestSpec.header("sec-fetch-site", "cross-site");
        requestSpec.header("sec-ch-ua-mobile", "?0");
        requestSpec.header("sec-ch-ua-platform", "sec-ch-ua-platform");
        requestSpec.header("sec-ch-ua", "\"Google Chrome\";v=\"105\", \"Not)A;Brand\";v=\"8\", \"Chromium\";v=\"105\"");
        requestSpec.header("user-agent", "Mozilla/5.0 (Macintosh; Intel Mac OS X 10_15_7) AppleWebKit/537.36 (KHTML, like Gecko) Chrome/105.0.0.0 Safari/537.36");
        requestSpec.header("content-type", "application/json;charset=UTF-8");
        return requestSpec;
    }

    protected static String setBodyPayerClient() {
        String date = getCurrentDate();
        CreatePayerClient createPayerClient = new CreatePayerClient(new ArrayList<>(), new ArrayList<>(), 1, 1, "John", "Wick", true, "en", "dd MMMM yyyy", date, date, null);
        return createPayerClient.toString();
    }

    protected static String setBodyLoanDisburse(int amount) {
        String date = getCurrentDate();
        setcurrentDate(date);
        LoanDisburse loanDisburse = new LoanDisburse(1, amount, date, "en", "dd MMMM yyyy");
        return loanDisburse.toString();
    }

    protected static String setGsmaTransactionBody() {
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

        GsmaTransfer gsmaTransfer = new GsmaTransfer("RKTQDM7W6S", "inbound", "transfer", Integer.toString(GSMATransferDef.amount), "USD", "note", "2022-09-28T12:51:19.260+00:00", customData, payer, payee);
        return gsmaTransfer.toString();
    }
}
