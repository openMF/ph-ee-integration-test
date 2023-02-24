package org.mifos.integrationtest.common.dto.loan;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import lombok.ToString;

import java.util.ArrayList;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@ToString
public class LoanAccountData {
    public String clientId;
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
    public boolean isEqualAmortization;
    public int interestType;
    public int interestCalculationPeriodType;
    public boolean allowPartialPeriodInterestCalcualtion;
    public int graceOnArrearsAgeing;
    public String transactionProcessingStrategyCode;
    public ArrayList<Object> rates;
    public String repaymentsStartingFromDate;
    public String locale;
    public String dateFormat;
    public String loanType;
    public String expectedDisbursementDate;
    public String submittedOnDate;
}