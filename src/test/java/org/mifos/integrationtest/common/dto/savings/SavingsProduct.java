package org.mifos.integrationtest.common.dto.savings;

import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import lombok.ToString;

import java.util.ArrayList;

@Getter
@Setter
@NoArgsConstructor
@ToString
public class SavingsProduct {

    public String currencyCode;
    public int digitsAfterDecimal;
    public int interestCompoundingPeriodType;
    public int interestPostingPeriodType;
    public int interestCalculationType;
    public int interestCalculationDaysInYearType;
    public String accountingRule;
    public String name;
    public String shortName;
    public String inMultiplesOf;
    public int nominalAnnualInterestRate;
    public ArrayList<Object> paymentChannelToFundSourceMappings;
    public ArrayList<Object> feeToIncomeAccountMappings;
    public ArrayList<Object> penaltyToIncomeAccountMappings;
    public ArrayList<Object> charges;
    public String locale;

    public SavingsProduct(String currencyCode, int digitsAfterDecimal, int interestCompoundingPeriodType, int interestPostingPeriodType,
            int interestCalculationType, int interestCalculationDaysInYearType, String accountingRule, String name, String shortName,
            String inMultiplesOf, int nominalAnnualInterestRate, ArrayList<Object> paymentChannelToFundSourceMappings,
            ArrayList<Object> feeToIncomeAccountMappings, ArrayList<Object> penaltyToIncomeAccountMappings, ArrayList<Object> charges,
            String locale) {
        this.currencyCode = currencyCode;
        this.digitsAfterDecimal = digitsAfterDecimal;
        this.interestCompoundingPeriodType = interestCompoundingPeriodType;
        this.interestPostingPeriodType = interestPostingPeriodType;
        this.interestCalculationType = interestCalculationType;
        this.interestCalculationDaysInYearType = interestCalculationDaysInYearType;
        this.accountingRule = accountingRule;
        this.name = name;
        this.shortName = shortName;
        this.inMultiplesOf = inMultiplesOf;
        this.nominalAnnualInterestRate = nominalAnnualInterestRate;
        this.paymentChannelToFundSourceMappings = paymentChannelToFundSourceMappings;
        this.feeToIncomeAccountMappings = feeToIncomeAccountMappings;
        this.penaltyToIncomeAccountMappings = penaltyToIncomeAccountMappings;
        this.charges = charges;
        this.locale = locale;
    }
}
