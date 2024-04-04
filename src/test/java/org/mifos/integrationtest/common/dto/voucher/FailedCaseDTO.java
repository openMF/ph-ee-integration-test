package org.mifos.integrationtest.common.dto.voucher;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
public class FailedCaseDTO {

    private String serialNumber;
    private String failureReason;
}
