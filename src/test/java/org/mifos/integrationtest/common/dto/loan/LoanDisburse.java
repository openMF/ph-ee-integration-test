package org.mifos.integrationtest.common.dto.loan;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import lombok.ToString;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@ToString
public class LoanDisburse {
    public int paymentTypeId;
    public int transactionAmount;
    public String actualDisbursementDate;
    public String locale;
    public String dateFormat;
}
