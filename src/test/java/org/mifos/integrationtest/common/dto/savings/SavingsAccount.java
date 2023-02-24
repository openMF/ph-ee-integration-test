package org.mifos.integrationtest.common.dto.savings;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import lombok.ToString;

import java.util.ArrayList;

@Getter
@Setter
@AllArgsConstructor
@NoArgsConstructor
@ToString
public class SavingsAccount {
    public String productId;
    public int nominalAnnualInterestRate;
    public boolean withdrawalFeeForTransfers;
    public boolean allowOverdraft;
    public boolean lienAllowed;
    public boolean enforceMinRequiredBalance;
    public boolean withHoldTax;
    public int interestCompoundingPeriodType;
    public int interestPostingPeriodType;
    public int interestCalculationType;
    public int interestCalculationDaysInYearType;
    public String submittedOnDate;
    public String locale;
    public String dateFormat;
    public String monthDayFormat;
    public ArrayList<Object> charges;
    public String clientId;
    public String externalId;
}
