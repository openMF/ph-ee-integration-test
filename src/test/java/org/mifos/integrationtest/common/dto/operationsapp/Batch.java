package org.mifos.integrationtest.common.dto.operationsapp;

import java.util.Date;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@Getter
@Setter
@AllArgsConstructor
@NoArgsConstructor
@SuppressWarnings("checkstyle:membername")
public class Batch {

    private String batchId;
    private String subBatchId;
    private String requestId;
    private String requestFile;
    private String result_file;
    // Please update this variable name in the batch and remove the suppressWaning annotation
    private String note;
    private String paymentMode;
    private String registeringInstitutionId;
    private String payerFsp;
    private String correlationId;

    private Long totalTransactions;
    private Long ongoing;
    private Long failed;
    private Long completed;
    private Long totalAmount;
    private Long ongoingAmount;
    private Long failedAmount;
    private Long completedAmount;
    private Long workflowKey;
    private Long workflowInstanceKey;
    private Long approvedAmount;
    private Long approvedCount;

    private Date resultGeneratedAt;
    private Date startedAt;
    private Date completedAt;
}
