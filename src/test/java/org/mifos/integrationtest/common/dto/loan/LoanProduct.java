package org.mifos.integrationtest.common.dto.loan;

import io.cucumber.core.internal.com.fasterxml.jackson.annotation.JsonProperty;
import java.util.ArrayList;
import lombok.NoArgsConstructor;
import lombok.Setter;
import lombok.ToString;

@Setter
@NoArgsConstructor
@ToString
public class LoanProduct {

    public String currencyCode;
    public String includeInBorrowerCycle;
    public boolean useBorrowerCycle;
    public String digitsAfterDecimal;
    public String inMultiplesOf;
    public int repaymentFrequencyType;
    public int interestRateFrequencyType;
    public int amortizationType;
    public int interestType;
    public int interestCalculationPeriodType;
    ArrayList<Object> principalVariationsForBorrowerCycle;
    ArrayList<Object> interestRateVariationsForBorrowerCycle;
    ArrayList<Object> numberOfRepaymentVariationsForBorrowerCycle;
    public boolean multiDisburseLoan;
    public String accountingRule;
    public int daysInYearType;
    public int daysInMonthType;
    @JsonProperty(value = "isInterestRecalculationEnabled")
    public Boolean isInterestRecalculationEnabled;
    public int interestRecalculationCompoundingMethod;
    public int rescheduleStrategyMethod;
    public int preClosureInterestCalculationStrategy;
    @JsonProperty(value = "isLinkedToFloatingInterestRates")
    public boolean isLinkedToFloatingInterestRates;

    public LoanProduct(String currencyCode, String includeInBorrowerCycle, boolean useBorrowerCycle, String digitsAfterDecimal,
            String inMultiplesOf, int repaymentFrequencyType, int interestRateFrequencyType, int amortizationType, int interestType,
            int interestCalculationPeriodType, ArrayList<Object> principalVariationsForBorrowerCycle,
            ArrayList<Object> interestRateVariationsForBorrowerCycle, ArrayList<Object> numberOfRepaymentVariationsForBorrowerCycle,
            boolean multiDisburseLoan, String accountingRule, int daysInYearType, int daysInMonthType,
            boolean isInterestRecalculationEnabled, int interestRecalculationCompoundingMethod, int rescheduleStrategyMethod,
            int preClosureInterestCalculationStrategy, boolean isLinkedToFloatingInterestRates, boolean allowVariableInstallments,
            String name, String shortName, String installmentAmountInMultiplesOf, String minPrincipal, String principal,
            String maxPrincipal, String minNumberOfRepayments, String numberOfRepayments, String maxNumberOfRepayments,
            String minInterestRatePerPeriod, int interestRatePerPeriod, String maxInterestRatePerPeriod, String repaymentEvery,
            boolean canDefineInstallmentAmount, boolean canUseForTopup, String graceOnArrearsAgeing, String overdueDaysForNPA,
            int recalculationRestFrequencyType, ArrayList<Object> paymentChannelToFundSourceMappings,
            ArrayList<Object> feeToIncomeAccountMappings, ArrayList<Object> penaltyToIncomeAccountMappings, ArrayList<Object> charges,
            Integer transactionProcessingStrategyId, AllowAttributeOverrides allowAttributeOverridesObject, String locale,
            String dateFormat, ArrayList<Object> rates, boolean allowPartialPeriodInterestCalcualtion,
            String fixedPrincipalPercentagePerInstallment) {
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
        this.transactionProcessingStrategyId = transactionProcessingStrategyId;
        AllowAttributeOverridesObject = allowAttributeOverridesObject;
        this.locale = locale;
        this.dateFormat = dateFormat;
        this.rates = rates;
        this.allowPartialPeriodInterestCalcualtion = allowPartialPeriodInterestCalcualtion;
        this.fixedPrincipalPercentagePerInstallment = fixedPrincipalPercentagePerInstallment;
    }

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
    public int interestRatePerPeriod;
    public String maxInterestRatePerPeriod;
    public String repaymentEvery;
    public boolean canDefineInstallmentAmount;
    public boolean canUseForTopup;
    public String graceOnArrearsAgeing;
    public String overdueDaysForNPA;
    public int recalculationRestFrequencyType;
    ArrayList<Object> paymentChannelToFundSourceMappings;
    ArrayList<Object> feeToIncomeAccountMappings;
    ArrayList<Object> penaltyToIncomeAccountMappings;
    ArrayList<Object> charges;
    public Integer transactionProcessingStrategyId;
    AllowAttributeOverrides AllowAttributeOverridesObject;
    public String locale;
    public String dateFormat;
    ArrayList<Object> rates;
    public boolean allowPartialPeriodInterestCalcualtion;
    public String fixedPrincipalPercentagePerInstallment;
}
