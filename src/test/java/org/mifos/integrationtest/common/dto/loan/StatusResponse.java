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
public class StatusResponse {

    public int id;
    public String code;
    public String value;
    public boolean pendingApproval;
    public boolean waitingForDisbursal;
    public boolean active;
    public boolean closedObligationsMet;
    public boolean closedWrittenOff;
    public boolean closedRescheduled;
    public boolean closed;
    public boolean overpaid;
}
