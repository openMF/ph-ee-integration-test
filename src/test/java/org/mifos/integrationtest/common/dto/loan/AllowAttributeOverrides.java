package org.mifos.integrationtest.common.dto.loan;

import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import lombok.ToString;

@Getter
@Setter
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

    public AllowAttributeOverrides(boolean amortizationType, boolean interestType, String transactionProcessingStrategyCode, boolean interestCalculationPeriodType, boolean inArrearsTolerance, boolean repaymentEvery, boolean graceOnPrincipalAndInterestPayment, boolean graceOnArrearsAgeing) {
        this.amortizationType = amortizationType;
        this.interestType = interestType;
        this.transactionProcessingStrategyCode = transactionProcessingStrategyCode;
        this.interestCalculationPeriodType = interestCalculationPeriodType;
        this.inArrearsTolerance = inArrearsTolerance;
        this.repaymentEvery = repaymentEvery;
        this.graceOnPrincipalAndInterestPayment = graceOnPrincipalAndInterestPayment;
        this.graceOnArrearsAgeing = graceOnArrearsAgeing;
    }
}
