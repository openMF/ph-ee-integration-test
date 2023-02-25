package org.mifos.integrationtest.common.dto.savings;

import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import lombok.ToString;

@Getter
@Setter
@NoArgsConstructor
@ToString
public class SavingsAccountDeposit {
    public String dateFormat;
    public String locale;
    public int paymentTypeId;
    public int transactionAmount;
    public String transactionDate;

    public SavingsAccountDeposit(String dateFormat, String locale, int paymentTypeId, int transactionAmount, String transactionDate) {
        this.dateFormat = dateFormat;
        this.locale = locale;
        this.paymentTypeId = paymentTypeId;
        this.transactionAmount = transactionAmount;
        this.transactionDate = transactionDate;
    }
}
