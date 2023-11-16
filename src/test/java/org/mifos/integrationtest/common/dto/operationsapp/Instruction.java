package org.mifos.integrationtest.common.dto.operationsapp;

import lombok.Getter;
import lombok.Setter;
import org.mifos.connector.common.operations.type.TransferStatus;

import java.math.BigDecimal;
import java.util.Date;

@Getter
@Setter
public class Instruction {
    private String instructionId;
    private String payerFsp;
    private String payeeFunctionalId;
    private BigDecimal amount;
    private TransferStatus status;
    private String reason;
    private Date startedAt;
    private Date completedAt;
    private String subBatchId;
}