package org.mifos.integrationtest.common.dto.savings;

import java.util.ArrayList;
import lombok.NoArgsConstructor;
import lombok.ToString;

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

    public SavingsAccount(String productId, int nominalAnnualInterestRate, boolean withdrawalFeeForTransfers, boolean allowOverdraft,
            boolean lienAllowed, boolean enforceMinRequiredBalance, boolean withHoldTax, int interestCompoundingPeriodType,
            int interestPostingPeriodType, int interestCalculationType, int interestCalculationDaysInYearType, String submittedOnDate,
            String locale, String dateFormat, String monthDayFormat, ArrayList<Object> charges, String clientId, String externalId) {
        this.productId = productId;
        this.nominalAnnualInterestRate = nominalAnnualInterestRate;
        this.withdrawalFeeForTransfers = withdrawalFeeForTransfers;
        this.allowOverdraft = allowOverdraft;
        this.lienAllowed = lienAllowed;
        this.enforceMinRequiredBalance = enforceMinRequiredBalance;
        this.withHoldTax = withHoldTax;
        this.interestCompoundingPeriodType = interestCompoundingPeriodType;
        this.interestPostingPeriodType = interestPostingPeriodType;
        this.interestCalculationType = interestCalculationType;
        this.interestCalculationDaysInYearType = interestCalculationDaysInYearType;
        this.submittedOnDate = submittedOnDate;
        this.locale = locale;
        this.dateFormat = dateFormat;
        this.monthDayFormat = monthDayFormat;
        this.charges = charges;
        this.clientId = clientId;
        this.externalId = externalId;
    }

    public String externalId;

    public String getProductId() {
        return productId;
    }

    public void setProductId(String productId) {
        this.productId = productId;
    }

    public int getNominalAnnualInterestRate() {
        return nominalAnnualInterestRate;
    }

    public void setNominalAnnualInterestRate(int nominalAnnualInterestRate) {
        this.nominalAnnualInterestRate = nominalAnnualInterestRate;
    }

    public boolean isWithdrawalFeeForTransfers() {
        return withdrawalFeeForTransfers;
    }

    public void setWithdrawalFeeForTransfers(boolean withdrawalFeeForTransfers) {
        this.withdrawalFeeForTransfers = withdrawalFeeForTransfers;
    }

    public boolean isAllowOverdraft() {
        return allowOverdraft;
    }

    public void setAllowOverdraft(boolean allowOverdraft) {
        this.allowOverdraft = allowOverdraft;
    }

    public boolean isLienAllowed() {
        return lienAllowed;
    }

    public void setLienAllowed(boolean lienAllowed) {
        this.lienAllowed = lienAllowed;
    }

    public boolean isEnforceMinRequiredBalance() {
        return enforceMinRequiredBalance;
    }

    public void setEnforceMinRequiredBalance(boolean enforceMinRequiredBalance) {
        this.enforceMinRequiredBalance = enforceMinRequiredBalance;
    }

    public boolean isWithHoldTax() {
        return withHoldTax;
    }

    public void setWithHoldTax(boolean withHoldTax) {
        this.withHoldTax = withHoldTax;
    }

    public int getInterestCompoundingPeriodType() {
        return interestCompoundingPeriodType;
    }

    public void setInterestCompoundingPeriodType(int interestCompoundingPeriodType) {
        this.interestCompoundingPeriodType = interestCompoundingPeriodType;
    }

    public int getInterestPostingPeriodType() {
        return interestPostingPeriodType;
    }

    public void setInterestPostingPeriodType(int interestPostingPeriodType) {
        this.interestPostingPeriodType = interestPostingPeriodType;
    }

    public int getInterestCalculationType() {
        return interestCalculationType;
    }

    public void setInterestCalculationType(int interestCalculationType) {
        this.interestCalculationType = interestCalculationType;
    }

    public int getInterestCalculationDaysInYearType() {
        return interestCalculationDaysInYearType;
    }

    public void setInterestCalculationDaysInYearType(int interestCalculationDaysInYearType) {
        this.interestCalculationDaysInYearType = interestCalculationDaysInYearType;
    }

    public String getSubmittedOnDate() {
        return submittedOnDate;
    }

    public void setSubmittedOnDate(String submittedOnDate) {
        this.submittedOnDate = submittedOnDate;
    }

    public String getLocale() {
        return locale;
    }

    public void setLocale(String locale) {
        this.locale = locale;
    }

    public String getDateFormat() {
        return dateFormat;
    }

    public void setDateFormat(String dateFormat) {
        this.dateFormat = dateFormat;
    }

    public String getMonthDayFormat() {
        return monthDayFormat;
    }

    public void setMonthDayFormat(String monthDayFormat) {
        this.monthDayFormat = monthDayFormat;
    }

    public ArrayList<Object> getCharges() {
        return charges;
    }

    public void setCharges(ArrayList<Object> charges) {
        this.charges = charges;
    }

    public String getClientId() {
        return clientId;
    }

    public void setClientId(String clientId) {
        this.clientId = clientId;
    }

    public String getExternalId() {
        return externalId;
    }

    public void setExternalId(String externalId) {
        this.externalId = externalId;
    }
}
