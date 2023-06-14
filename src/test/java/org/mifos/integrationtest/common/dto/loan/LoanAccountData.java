package org.mifos.integrationtest.common.dto.loan;

import io.cucumber.core.internal.com.fasterxml.jackson.annotation.JsonProperty;
import java.util.ArrayList;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import lombok.ToString;

@Setter
@NoArgsConstructor
@ToString
public class LoanAccountData {

    @Getter
    public String clientId;
    @Getter
    public int productId;
    public ArrayList<Object> disbursementData;
    public int principal;
    public int loanTermFrequency;
    public int loanTermFrequencyType;
    public int numberOfRepayments;
    public int repaymentEvery;
    public int repaymentFrequencyType;
    public double interestRatePerPeriod;
    public int amortizationType;
    @JsonProperty(value = "isEqualAmortization")
    public boolean isEqualAmortization;
    public int interestType;
    public int interestCalculationPeriodType;
    public boolean allowPartialPeriodInterestCalcualtion;
    public int graceOnArrearsAgeing;
    public Integer transactionProcessingStrategyId;
    public ArrayList<Object> rates;
    public String repaymentsStartingFromDate;
    public String locale;
    public String dateFormat;
    public String loanType;
    public String expectedDisbursementDate;
    public String submittedOnDate;

    public LoanAccountData(String clientId, int productId, ArrayList<Object> disbursementData, int principal, int loanTermFrequency,
            int loanTermFrequencyType, int numberOfRepayments, int repaymentEvery, int repaymentFrequencyType, double interestRatePerPeriod,
            int amortizationType, boolean isEqualAmortization, int interestType, int interestCalculationPeriodType,
            boolean allowPartialPeriodInterestCalcualtion, int graceOnArrearsAgeing, Integer transactionProcessingStrategyId,
            ArrayList<Object> rates, String repaymentsStartingFromDate, String locale, String dateFormat, String loanType,
            String expectedDisbursementDate, String submittedOnDate) {
        this.clientId = clientId;
        this.productId = productId;
        this.disbursementData = disbursementData;
        this.principal = principal;
        this.loanTermFrequency = loanTermFrequency;
        this.loanTermFrequencyType = loanTermFrequencyType;
        this.numberOfRepayments = numberOfRepayments;
        this.repaymentEvery = repaymentEvery;
        this.repaymentFrequencyType = repaymentFrequencyType;
        this.interestRatePerPeriod = interestRatePerPeriod;
        this.amortizationType = amortizationType;
        this.isEqualAmortization = isEqualAmortization;
        this.interestType = interestType;
        this.interestCalculationPeriodType = interestCalculationPeriodType;
        this.allowPartialPeriodInterestCalcualtion = allowPartialPeriodInterestCalcualtion;
        this.graceOnArrearsAgeing = graceOnArrearsAgeing;
        this.transactionProcessingStrategyId = transactionProcessingStrategyId;
        this.rates = rates;
        this.repaymentsStartingFromDate = repaymentsStartingFromDate;
        this.locale = locale;
        this.dateFormat = dateFormat;
        this.loanType = loanType;
        this.expectedDisbursementDate = expectedDisbursementDate;
        this.submittedOnDate = submittedOnDate;
    }
}
