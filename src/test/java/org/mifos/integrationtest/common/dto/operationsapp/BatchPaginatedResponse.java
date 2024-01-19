package org.mifos.integrationtest.common.dto.operationsapp;

import java.util.List;
import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
public class BatchPaginatedResponse {

    long totalBatches;
    long totalTransactions;
    long totalAmount;
    long totalApprovedCount;
    long totalApprovedAmount;
    long totalSubBatchesCreated;
    List<Batch> data;
}
