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

    private boolean allowPartialPeriodInterestCalcualtion;
    private String fixedPrincipalPercentagePerInstallment;
}