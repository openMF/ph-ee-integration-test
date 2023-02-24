package org.mifos.integrationtest.common.dto.savings;

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
public class SavingsAccountDeposit {
    public String dateFormat;
    public String locale;
    public int paymentTypeId;
    public int transactionAmount;
    public String transactionDate;
}
