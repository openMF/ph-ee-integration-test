package org.mifos.integrationtest.common.dto.loan;

import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import lombok.ToString;

import java.util.ArrayList;

@Getter
@Setter
@NoArgsConstructor
@ToString
public class LoanProduct {
    public String currencyCode;
    public String includeInBorrowerCycle;
    public boolean useBorrowerCycle;
    public String digitsAfterDecimal;
    public String inMultiplesOf;
    public float repaymentFrequencyType;
    public float interestRateFrequencyType;
    public float amortizationType;
    public float interestType;
    public float interestCalculationPeriodType;
    ArrayList<Object> principalVariationsForBorrowerCycle;
    ArrayList<Object> interestRateVariationsForBorrowerCycle;
    ArrayList<Object> numberOfRepaymentVariationsForBorrowerCycle;
    public boolean multiDisburseLoan;
    public String accountingRule;
    public float daysInYearType;
    public float daysInMonthType;
    public boolean isInterestRecalculationEnabled;
    public float interestRecalculationCompoundingMethod;
    public float rescheduleStrategyMethod;
    public float preClosureInterestCalculationStrategy;
    public boolean isLinkedToFloatingInterestRates;
    public boolean allowVariableInstallments;
    public String name;
    public String shortName;
    public String installmentAmountInMultiplesOf;
    public String minPrincipal;
    public String principal;
    public String maxPrincipal;
    public String minNumberOfRepayments;
    public String numberOfRepayments;
    public String maxNumberOfRepayments;
    public String minInterestRatePerPeriod;
    public float interestRatePerPeriod;
    public String maxInterestRatePerPeriod;
    public String repaymentEvery;
    public boolean canDefineInstallmentAmount;
    public boolean canUseForTopup;
    public String graceOnArrearsAgeing;
    public String overdueDaysForNPA;
    public float recalculationRestFrequencyType;
    ArrayList<Object> paymentChannelToFundSourceMappings;
    ArrayList<Object> feeToIncomeAccountMappings;
    ArrayList<Object> penaltyToIncomeAccountMappings;
    ArrayList<Object> charges;
    public String transactionProcessingStrategyCode;
    AllowAttributeOverrides AllowAttributeOverridesObject;
    public String locale;
    public String dateFormat;
    ArrayList<Object> rates;

    public LoanProduct(String currencyCode, String includeInBorrowerCycle, boolean useBorrowerCycle, String digitsAfterDecimal, String inMultiplesOf, float repaymentFrequencyType, float interestRateFrequencyType, float amortizationType, float interestType, float interestCalculationPeriodType, ArrayList<Object> principalVariationsForBorrowerCycle, ArrayList<Object> interestRateVariationsForBorrowerCycle, ArrayList<Object> numberOfRepaymentVariationsForBorrowerCycle, boolean multiDisburseLoan, String accountingRule, float daysInYearType, float daysInMonthType, boolean isInterestRecalculationEnabled, float interestRecalculationCompoundingMethod, float rescheduleStrategyMethod, float preClosureInterestCalculationStrategy, boolean isLinkedToFloatingInterestRates, boolean allowVariableInstallments, String name, String shortName, String installmentAmountInMultiplesOf, String minPrincipal, String principal, String maxPrincipal, String minNumberOfRepayments, String numberOfRepayments, String maxNumberOfRepayments, String minInterestRatePerPeriod, float interestRatePerPeriod, String maxInterestRatePerPeriod, String repaymentEvery, boolean canDefineInstallmentAmount, boolean canUseForTopup, String graceOnArrearsAgeing, String overdueDaysForNPA, float recalculationRestFrequencyType, ArrayList<Object> paymentChannelToFundSourceMappings, ArrayList<Object> feeToIncomeAccountMappings, ArrayList<Object> penaltyToIncomeAccountMappings, ArrayList<Object> charges, String transactionProcessingStrategyCode, AllowAttributeOverrides allowAttributeOverridesObject, String locale, String dateFormat, ArrayList<Object> rates, boolean allowPartialPeriodInterestCalcualtion, String fixedPrincipalPercentagePerInstallment) {
        this.currencyCode = currencyCode;
        this.includeInBorrowerCycle = includeInBorrowerCycle;
        this.useBorrowerCycle = useBorrowerCycle;
        this.digitsAfterDecimal = digitsAfterDecimal;
        this.inMultiplesOf = inMultiplesOf;
        this.repaymentFrequencyType = repaymentFrequencyType;
        this.interestRateFrequencyType = interestRateFrequencyType;
        this.amortizationType = amortizationType;
        this.interestType = interestType;
        this.interestCalculationPeriodType = interestCalculationPeriodType;
        this.principalVariationsForBorrowerCycle = principalVariationsForBorrowerCycle;
        this.interestRateVariationsForBorrowerCycle = interestRateVariationsForBorrowerCycle;
        this.numberOfRepaymentVariationsForBorrowerCycle = numberOfRepaymentVariationsForBorrowerCycle;
        this.multiDisburseLoan = multiDisburseLoan;
        this.accountingRule = accountingRule;
        this.daysInYearType = daysInYearType;
        this.daysInMonthType = daysInMonthType;
        this.isInterestRecalculationEnabled = isInterestRecalculationEnabled;
        this.interestRecalculationCompoundingMethod = interestRecalculationCompoundingMethod;
        this.rescheduleStrategyMethod = rescheduleStrategyMethod;
        this.preClosureInterestCalculationStrategy = preClosureInterestCalculationStrategy;
        this.isLinkedToFloatingInterestRates = isLinkedToFloatingInterestRates;
        this.allowVariableInstallments = allowVariableInstallments;
        this.name = name;
        this.shortName = shortName;
        this.installmentAmountInMultiplesOf = installmentAmountInMultiplesOf;
        this.minPrincipal = minPrincipal;
        this.principal = principal;
        this.maxPrincipal = maxPrincipal;
        this.minNumberOfRepayments = minNumberOfRepayments;
        this.numberOfRepayments = numberOfRepayments;
        this.maxNumberOfRepayments = maxNumberOfRepayments;
        this.minInterestRatePerPeriod = minInterestRatePerPeriod;
        this.interestRatePerPeriod = interestRatePerPeriod;
        this.maxInterestRatePerPeriod = maxInterestRatePerPeriod;
        this.repaymentEvery = repaymentEvery;
        this.canDefineInstallmentAmount = canDefineInstallmentAmount;
        this.canUseForTopup = canUseForTopup;
        this.graceOnArrearsAgeing = graceOnArrearsAgeing;
        this.overdueDaysForNPA = overdueDaysForNPA;
        this.recalculationRestFrequencyType = recalculationRestFrequencyType;
        this.paymentChannelToFundSourceMappings = paymentChannelToFundSourceMappings;
        this.feeToIncomeAccountMappings = feeToIncomeAccountMappings;
        this.penaltyToIncomeAccountMappings = penaltyToIncomeAccountMappings;
        this.charges = charges;
        this.transactionProcessingStrategyCode = transactionProcessingStrategyCode;
        AllowAttributeOverridesObject = allowAttributeOverridesObject;
        this.locale = locale;
        this.dateFormat = dateFormat;
        this.rates = rates;
        this.allowPartialPeriodInterestCalcualtion = allowPartialPeriodInterestCalcualtion;
        this.fixedPrincipalPercentagePerInstallment = fixedPrincipalPercentagePerInstallment;
    }

    public boolean allowPartialPeriodInterestCalcualtion;
    public String fixedPrincipalPercentagePerInstallment;
}