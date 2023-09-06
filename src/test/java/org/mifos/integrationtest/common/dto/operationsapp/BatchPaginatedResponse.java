package org.mifos.integrationtest.common.dto.operationsapp;

import lombok.Getter;
import lombok.Setter;
import java.util.List;

@Getter
@Setter
public class BatchPaginatedResponse {

    long totalBatches, totalTransactions, totalAmount, totalApprovedCount,
            totalApprovedAmount, totalSubBatchesCreated;
    List<Batch> data;
}
