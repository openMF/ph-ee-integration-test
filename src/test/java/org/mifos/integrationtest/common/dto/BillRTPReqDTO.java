package org.mifos.integrationtest.common.dto;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import java.io.Serializable;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
public class BillRTPReqDTO {
    private String clientCorrelationId;
    private String billId;
    private String requestType;
    private PayerFSPDetail payerFspDetail;
    private Alias alias;
    private Bill bill;

    public BillRTPReqDTO(String clientCorrelationId, String billId, String requestType, PayerFSPDetail payerFSPDetail, Bill bill){
        this.clientCorrelationId = clientCorrelationId;
        this.bill= bill;
        this.billId = billId;
        this.requestType = requestType;
        this.payerFspDetail = payerFSPDetail;
    }

}
