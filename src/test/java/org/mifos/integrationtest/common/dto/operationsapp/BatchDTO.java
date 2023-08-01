package org.mifos.integrationtest.common.dto.operationsapp;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import java.math.BigDecimal;

@Getter
@Setter
@AllArgsConstructor
@NoArgsConstructor
public class BatchDTO {

    private String batch_id, request_id, file, notes, created_at, status,
            modes, purpose, failPercentage, successPercentage;
    private Long total, ongoing, failed, successful;
    private BigDecimal totalAmount, successfulAmount, pendingAmount, failedAmount;
}
