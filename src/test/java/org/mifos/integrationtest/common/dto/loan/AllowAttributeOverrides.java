package org.mifos.integrationtest.common.dto.loan;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import lombok.ToString;

@Getter
@Setter
@AllArgsConstructor
@NoArgsConstructor
@ToString
public class AllowAttributeOverrides {
    private boolean amortizationType;
    private boolean interestType;
    private String transactionProcessingStrategyCode;
    private boolean interestCalculationPeriodType;
    private boolean inArrearsTolerance;
    private boolean repaymentEvery;
    private boolean graceOnPrincipalAndInterestPayment;
    private boolean graceOnArrearsAgeing;
}
