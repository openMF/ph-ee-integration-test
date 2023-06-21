package org.mifos.integrationtest.common.dto.loan;

import java.util.ArrayList;
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
public class ChangesResponse {

    public StatusResponse status;
    public int approvedLoanAmount;
    public int principal;
    public int netDisbursalAmount;
    public String locale;
    public String dateFormat;
    public String approvedOnDate;
    public ArrayList<Integer> expectedDisbursementDate;
}
