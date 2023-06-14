package org.mifos.integrationtest.common.dto.loan;

import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import lombok.ToString;

import java.util.ArrayList;

@Getter
@Setter
@NoArgsConstructor
@ToString
public class LoanApprove {

    public String approvedOnDate;
    public int approvedLoanAmount;
    public String expectedDisbursementDate;
    public ArrayList<Object> disbursementData;
    public String locale;
    public String dateFormat;

    public LoanApprove(String approvedOnDate, int approvedLoanAmount, String expectedDisbursementDate, ArrayList<Object> disbursementData,
            String locale, String dateFormat) {
        this.approvedOnDate = approvedOnDate;
        this.approvedLoanAmount = approvedLoanAmount;
        this.expectedDisbursementDate = expectedDisbursementDate;
        this.disbursementData = disbursementData;
        this.locale = locale;
        this.dateFormat = dateFormat;
    }
}
