package org.mifos.integrationtest.common.dto;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
public class BillRTPReqDTO {

    private String clientCorrelationId;
    private String billID;
    private String requestType;
    private PayerFSPDetail payerFspDetails;
    private Alias alias;
    private Bill billDetails;

    public BillRTPReqDTO(String clientCorrelationId, String billId, String requestType, PayerFSPDetail payerFSPDetail, Bill bill) {
        this.clientCorrelationId = clientCorrelationId;
        this.billDetails = bill;
        this.billID = billId;
        this.requestType = requestType;
        this.payerFspDetails = payerFSPDetail;
    }

    public BillRTPReqDTO(String clientCorrelationId, String billId, String requestType, Alias alias, Bill bill) {
        this.clientCorrelationId = clientCorrelationId;
        this.billDetails = bill;
        this.billID = billId;
        this.requestType = requestType;
        this.alias = alias;
    }

}
