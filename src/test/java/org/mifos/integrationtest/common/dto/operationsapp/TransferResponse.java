package org.mifos.integrationtest.common.dto.operationsapp;

import com.fasterxml.jackson.annotation.JsonFormat;
import lombok.Getter;
import lombok.Setter;
import org.mifos.connector.common.channel.dto.PhErrorDTO;
import org.mifos.connector.common.operations.type.TransferStatus;

import java.math.BigDecimal;
import java.util.Date;

@Getter
@Setter
public class TransferResponse {

    private int id;
    private Long workflowInstanceKey;
    private String transactionId;
    private Date startedAt;
    private Date completedAt;
    private TransferStatus status;
    private String statusDetail;
    private String payeeDfspId;
    private String payeePartyId;
    private String payeePartyIdType;
    private BigDecimal payeeFee;
    private String payeeFeeCurrency;
    private String payeeQuoteCode;
    private String payerDfspId;
    private String payerPartyId;
    private String payerPartyIdType;
    private BigDecimal payerFee;
    private String payerFeeCurrency;
    private String payerQuoteCode;
    @JsonFormat(shape = JsonFormat.Shape.STRING)
    private BigDecimal amount;
    private String currency;
    private String direction;
    private PhErrorDTO errorInformation;
    private String batchId;
    private String clientCorrelationId;
}
