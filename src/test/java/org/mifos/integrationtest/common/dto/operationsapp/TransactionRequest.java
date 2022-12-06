package org.mifos.integrationtest.common.dto.operationsapp;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
public class TransactionRequest {
    private int id;
    private String workflowInstanceKey;
    private String transactionId;
    private long startedAt;
    private Object completedAt;
    private String state;
    private Object payeeDfspId;
    private String payeePartyId;
    private String payeePartyIdType;
    private Object payeeFee;
    private Object payeeQuoteCode;
    private Object payerDfspId;
    private String payerPartyId;
    private String payerPartyIdType;
    private Object payerFee;
    private Object payerQuoteCode;
    private int amount;
    private String currency;
    private String direction;
    private Object authType;
    private String initiatorType;
    private String scenario;
    private String externalId;
}
