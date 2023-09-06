package org.mifos.integrationtest.common.dto.operationsapp;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import java.util.Date;

@Getter
@Setter
@AllArgsConstructor
@NoArgsConstructor
public class Batch {

    private String batchId, subBatchId, requestId, requestFile,
            result_file, note, paymentMode, registeringInstitutionId,
            payerFsp, correlationId;

    private Long totalTransactions, ongoing, failed, completed,
            totalAmount, ongoingAmount, failedAmount, completedAmount,
            workflowKey, workflowInstanceKey, approvedAmount, approvedCount;;

    private Date resultGeneratedAt, startedAt, completedAt;
}
