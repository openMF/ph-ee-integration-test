package org.mifos.integrationtest.common.dto.savings;

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
}
