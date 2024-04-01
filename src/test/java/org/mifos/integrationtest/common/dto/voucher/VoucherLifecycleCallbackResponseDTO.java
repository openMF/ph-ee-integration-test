package org.mifos.integrationtest.common.dto.voucher;

import java.util.List;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
public class VoucherLifecycleCallbackResponseDTO {

    private String requestID;
    private String registerRequestID;
    private Integer numberFailedCases;
    private List<FailedCaseDTO> failedCases;
}
