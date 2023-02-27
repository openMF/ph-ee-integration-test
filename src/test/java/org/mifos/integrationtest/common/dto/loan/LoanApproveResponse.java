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
public class LoanApproveResponse {
    public int officeId;
    public int clientId;
    public int loanId;
    public int resourceId;
    public ChangesResponse changes;
}

