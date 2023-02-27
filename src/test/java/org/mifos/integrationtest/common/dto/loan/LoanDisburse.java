package org.mifos.integrationtest.common.dto.loan;

import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import lombok.ToString;

@Getter
@Setter
@NoArgsConstructor
@ToString
public class LoanDisburse {
    public int paymentTypeId;
    public int transactionAmount;
    public String actualDisbursementDate;
    public String locale;
    public String dateFormat;

    public LoanDisburse(int paymentTypeId, int transactionAmount, String actualDisbursementDate, String locale, String dateFormat) {
        this.paymentTypeId = paymentTypeId;
        this.transactionAmount = transactionAmount;
        this.actualDisbursementDate = actualDisbursementDate;
        this.locale = locale;
        this.dateFormat = dateFormat;
    }
}
