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
public class LoanProduct {
    private String currencyCode;
    private String includeInBorrowerCycle;
    private boolean useBorrowerCycle;
    private String digitsAfterDecimal;
    private String inMultiplesOf;
    private float repaymentFrequencyType;
    private float interestRateFrequencyType;
    private float amortizationType;
    private float interestType;
    private float interestCalculationPeriodType;
    ArrayList<Object> principalVariationsForBorrowerCycle;
    ArrayList<Object> interestRateVariationsForBorrowerCycle;
    ArrayList<Object> numberOfRepaymentVariationsForBorrowerCycle;
    private boolean multiDisburseLoan;
    private String accountingRule;
    private float daysInYearType;
    private float daysInMonthType;
    private boolean isInterestRecalculationEnabled;
    private float interestRecalculationCompoundingMethod;
    private float rescheduleStrategyMethod;
    private float preClosureInterestCalculationStrategy;
    private boolean isLinkedToFloatingInterestRates;
    private boolean allowVariableInstallments;
    private String name;
    private String shortName;
    private String installmentAmountInMultiplesOf;
    private String minPrincipal;
    private String principal;
    private String maxPrincipal;
    private String minNumberOfRepayments;
    private String numberOfRepayments;
    private String maxNumberOfRepayments;
    private String minInterestRatePerPeriod;
    private float interestRatePerPeriod;
    private String maxInterestRatePerPeriod;
    private String repaymentEvery;
    private boolean canDefineInstallmentAmount;
    private boolean canUseForTopup;
    private String graceOnArrearsAgeing;
    private String overdueDaysForNPA;
    private float recalculationRestFrequencyType;
    ArrayList<Object> paymentChannelToFundSourceMappings;
    ArrayList<Object> feeToIncomeAccountMappings;
    ArrayList<Object> penaltyToIncomeAccountMappings;
    ArrayList<Object> charges;
    private String transactionProcessingStrategyCode;
    AllowAttributeOverrides AllowAttributeOverridesObject;
    private String locale;
    private String dateFormat;
    ArrayList<Object> rates;
    private boolean allowPartialPeriodInterestCalcualtion;
    private String fixedPrincipalPercentagePerInstallment;
}