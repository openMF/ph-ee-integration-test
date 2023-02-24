package org.mifos.integrationtest.common.dto.loan;

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
public class LoanApprove {
    public String approvedOnDate;
    public int approvedLoanAmount;
    public String expectedDisbursementDate;
    public ArrayList<Object> disbursementData;
    public String locale;
    public String dateFormat;
}
